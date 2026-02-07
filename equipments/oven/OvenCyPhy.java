package equipments.oven;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import equipments.oven.connections.OvenActuatorInboundPort;
import equipments.oven.connections.OvenControllerOutboundPort;
import equipments.oven.connections.OvenExternalControlInboundPort;
import equipments.oven.simulations.events.CancelDelayedStartOven;
import equipments.oven.simulations.events.CloseDoorOven;
import equipments.oven.simulations.events.DelayedStartOven.DelayValue;
import equipments.oven.simulations.events.DoNotHeatOven;
import equipments.oven.simulations.events.HeatOven;
import equipments.oven.simulations.events.OpenDoorOven;
import equipments.oven.simulations.events.SetModeOven;
import equipments.oven.simulations.events.SetModeOven.ModeValue;
import equipments.oven.simulations.events.SetPowerOven;
import equipments.oven.simulations.events.SetPowerOven.PowerValueOven;
import equipments.oven.simulations.events.SetTargetTemperatureOven;
import equipments.oven.simulations.events.SetTargetTemperatureOven.TargetTemperatureValue;
import equipments.oven.simulations.events.SwitchOffOven;
import equipments.oven.simulations.events.SwitchOnOven;
import equipments.oven.simulations.sil.LocalSILSimulationArchitectures;
import equipments.oven.simulations.sil.OvenStateModel;
import fr.sorbonne_u.alasca.physical_data.Measure;
import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import fr.sorbonne_u.components.annotations.RequiredInterfaces;
import fr.sorbonne_u.components.cyphy.ExecutionMode;
import fr.sorbonne_u.components.cyphy.annotations.LocalArchitecture;
import fr.sorbonne_u.components.cyphy.annotations.SIL_Simulation_Architectures;
import fr.sorbonne_u.components.cyphy.plugins.devs.AtomicSimulatorPlugin;
import fr.sorbonne_u.components.cyphy.plugins.devs.RTAtomicSimulatorPlugin;
import fr.sorbonne_u.components.cyphy.utils.aclocks.ClocksServerWithSimulation;
import fr.sorbonne_u.components.cyphy.utils.tests.TestScenarioWithSimulation;
import fr.sorbonne_u.components.exceptions.BCMException;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import fr.sorbonne_u.components.hem2025.bases.RegistrationCI;
import fr.sorbonne_u.components.utils.tests.TestScenario;
import fr.sorbonne_u.devs_simulation.architectures.RTArchitecture;
import fr.sorbonne_u.devs_simulation.models.annotations.ModelExternalEvents;
import fr.sorbonne_u.devs_simulation.models.time.Duration;
import fr.sorbonne_u.exceptions.PostconditionException;
import fr.sorbonne_u.exceptions.PreconditionException;

/**
 * The class <code>Oven</code> implements an oven component.

 *
 * <p><strong>Description</strong></p>
 * 
 * A component simulating an oven device that can operate in several modes:
 * <ul>
 *   <li>{@code CUSTOM} where the user defines the target temperature,</li>
 *   <li>{@code DEFROST} where a gentle fixed temperature is used,</li>
 *   <li>{@code GRILL} where a high fixed temperature is used.</li>
 * </ul>
 * The oven can also start cooking after a delay, represented by the
 * {@code WAITING} state.
 * 
 * <p><strong>Implementation Invariants</strong></p>
 * 
 * <pre>
 * invariant {@code currentState != null}
 * invariant {@code currentMode != null}
 * invariant {@code targetTemperature != null && targetTemperature.getMeasurementUnit().equals(TEMPERATURE_UNIT)}
 * invariant {@code targetTemperature.getData() >= MIN_TARGET_TEMPERATURE.getData() && targetTemperature.getData() <= MAX_TARGET_TEMPERATURE.getData()}
 * invariant {@code currentPowerLevel == null || currentPowerLevel.getMeasure().getMeasurementUnit().equals(POWER_UNIT)}
 * invariant {@code currentPowerLevel == null || currentPowerLevel.getMeasure().getData() >= 0.0 && currentPowerLevel.getMeasure().getData() <= MAX_POWER_LEVEL.getData()}
 * </pre>
 * 
 * <p><strong>Invariants</strong></p>
 * 
 * <pre>
 * invariant {@code REFLECTION_INBOUND_PORT_URI != null && !REFLECTION_INBOUND_PORT_URI.isEmpty()}
 * invariant {@code USER_INBOUND_PORT_URI != null && !USER_INBOUND_PORT_URI.isEmpty()}
 * invariant {@code INTERNAL_CONTROL_INBOUND_PORT_URI != null && !INTERNAL_CONTROL_INBOUND_PORT_URI.isEmpty()}
 * invariant {@code EXTERNAL_CONTROL_INBOUND_PORT_URI != null && !EXTERNAL_CONTROL_INBOUND_PORT_URI.isEmpty()}
 * invariant {@code X_RELATIVE_POSITION >= 0}
 * invariant {@code Y_RELATIVE_POSITION >= 0}
 * </pre>
 * 
 * <p>Created on : 2025-10-10</p>
 * 
 * @author	<a href="mailto:Rodrigo.Vila@etu.sorbonne-universite.fr">Rodrigo Vila</a>
 * @author	<a href="mailto:Damien.Ribeiro@etu.sorbonne-universite.fr">Damien Ribeiro</a>
 */

@SIL_Simulation_Architectures({
    @LocalArchitecture(
            uri = "OVEN_UNIT_TEST_URI",
            rootModelURI = "OVEN_COUPLED_MODEL",
            simulatedTimeUnit = TimeUnit.HOURS,
            externalEvents = @ModelExternalEvents()
    ),
    @LocalArchitecture(
            uri = "OVEN_INTEGRATION_TEST_URI",
            rootModelURI = "OVEN_COUPLED_MODEL",
            simulatedTimeUnit = TimeUnit.HOURS,
            externalEvents = @ModelExternalEvents(
                    exported = {
                            CloseDoorOven.class,
                            DoNotHeatOven.class,
                            HeatOven.class,
                            OpenDoorOven.class,
                            SetModeOven.class,
                            SetPowerOven.class,
                            SetTargetTemperatureOven.class,
                            SwitchOffOven.class,
                            SwitchOnOven.class
                    }
            )
    )}
)
@RequiredInterfaces(required = {RegistrationCI.class, OvenControllerCI.class})
@OfferedInterfaces(offered={
		OvenUserJava4CI.class, 
		OvenInternalControlCI.class, 
		OvenExternalControlJava4CI.class,
		OvenActuatorCI.class,
		OvenControllerCI.class})

public class OvenCyPhy extends Oven{
	
    // -------------------------------------------------------------------------
    // Constants
    // -------------------------------------------------------------------------

    /** */
    public static final String UNIT_TEST_URI = "OVEN_UNIT_TEST_URI";

    /**  */
    public static final String INTEGRATION_TEST_URI = "OVEN_INTEGRATION_TEST_URI";

    // -------------------------------------------------------------------------
    // Variables
    // -------------------------------------------------------------------------

    protected AtomicSimulatorPlugin asp;
    protected String localArchitectureURI;
    
    protected OvenControllerOutboundPort controllerPort;
    protected String controllerCCName;
    protected String controllerURI;
    
    protected OvenActuatorInboundPort actuatorPort;
    protected OvenExternalControlInboundPort controllerExternalPort;
    
	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------
    
    protected OvenCyPhy(
            String userInboundURI,
            String internalInboundURI,
            String externalInboundURI,
            String registrationHEMURI,
            String registrationHEMCcName,
            String actuatorInboundURI,
            String controllerExternalInboundURI,
            String controllerURI,
            String controllerCCName
    ) throws Exception {

        super(
        	userInboundURI,
            internalInboundURI,
            externalInboundURI,
            registrationHEMURI,
            registrationHEMCcName
        );

        this.actuatorPort =
                new OvenActuatorInboundPort(actuatorInboundURI, this);
        this.actuatorPort.publishPort();

        this.controllerExternalPort =
                new OvenExternalControlInboundPort(controllerExternalInboundURI, this);
        this.controllerExternalPort.publishPort();

        this.controllerPort = new OvenControllerOutboundPort(this);
        this.controllerPort.publishPort();

        this.controllerURI = controllerURI;
        this.controllerCCName = controllerCCName;
    }
    
    protected OvenCyPhy(
            String userInboundURI,
            String internalInboundURI,
            String externalInboundURI,
            String registrationHEMURI,
            String registrationHEMCcName,
            String actuatorInboundURI,
            String controllerExternalInboundURI,
            String controllerURI,
            String controllerCCName,
            ExecutionMode mode,
            TestScenario testScenario,
            String localArchitectureURI,
            double accelerationFactor
    ) throws Exception {

        super(REFLECTION_INBOUND_PORT_URI, 
        	userInboundURI,
            internalInboundURI,
            externalInboundURI,
            registrationHEMURI,
            registrationHEMCcName,
            mode,
            testScenario,
            accelerationFactor
        );

        this.executionMode = mode;
        this.localArchitectureURI = localArchitectureURI;

        this.actuatorPort =
                new OvenActuatorInboundPort(actuatorInboundURI, this);
        this.actuatorPort.publishPort();

        this.controllerExternalPort =
                new OvenExternalControlInboundPort(controllerExternalInboundURI, this);
        this.controllerExternalPort.publishPort();

        this.controllerPort = new OvenControllerOutboundPort(this);
        this.controllerPort.publishPort();

        this.controllerURI = controllerURI;
        this.controllerCCName = controllerCCName;
    }
    
    protected OvenCyPhy(
            String userInboundURI,
            String internalInboundURI,
            String externalInboundURI,
            String actuatorInboundURI,
            String controllerExternalInboundURI,
            String controllerURI,
            String controllerCCName
    ) throws Exception {

        super(
            userInboundURI,
            internalInboundURI,
            externalInboundURI
        );

        this.actuatorPort =
                new OvenActuatorInboundPort(actuatorInboundURI, this);
        this.actuatorPort.publishPort();

        this.controllerExternalPort =
                new OvenExternalControlInboundPort(controllerExternalInboundURI, this);
        this.controllerExternalPort.publishPort();

        this.controllerPort = new OvenControllerOutboundPort(this);
        this.controllerPort.publishPort();

        this.controllerURI = controllerURI;
        this.controllerCCName = controllerCCName;
    }


    // -------------------------------------------------------------------------
    // Private methods
    // -------------------------------------------------------------------------

    private void tracing(String message) {
        if (OvenCyPhy.VERBOSE) {
            this.traceMessage(message);
        }
    }

    private void triggerExternalEvent(RTAtomicSimulatorPlugin.EventFactoryFI factory) throws Exception {
        if ( this.getExecutionMode().isSILTest() ) {

            ((RTAtomicSimulatorPlugin)asp).triggerExternalEvent(
                    OvenStateModel.URI,
                    factory
            );
        }
    }

    private void setSimulatorPlugin() throws Exception {

        switch (this.getExecutionMode()) {
            case INTEGRATION_TEST_WITH_SIL_SIMULATION:
            case UNIT_TEST_WITH_SIL_SIMULATION:
                RTArchitecture architecture =
                        (RTArchitecture) this.localSimulationArchitectures.
                                get(this.localArchitectureURI);
                this.asp = new RTAtomicSimulatorPlugin();
                this.asp.setPluginURI(architecture.getRootModelURI());
                this.asp.setSimulationArchitecture(architecture);
                this.installPlugin(this.asp);
                this.asp.createSimulator();
                this.asp.setSimulationRunParameters(
                        (TestScenarioWithSimulation) this.testScenario,
                        new HashMap<>()
                );
                break;
            case UNIT_TEST_WITH_HIL_SIMULATION:
            case INTEGRATION_TEST_WITH_HIL_SIMULATION:
                throw new BCMException("HIL simulation are not implemented");
            default:
        }

    }

    /**
     * @see fr.sorbonne_u.components.cyphy.AbstractCyPhyComponent#createLocalSimulationArchitecture
     */
    @Override
    protected RTArchitecture createLocalSimulationArchitecture(
            String architectureURI,
            String rootModelURI,
            TimeUnit simulatedTimeUnit,
            double accelerationFactor
    ) throws Exception
    {
        assert architectureURI != null && ! architectureURI.isEmpty() :
                new PreconditionException("architectureURI == null || architectureURI.isEmpty()");
        assert rootModelURI != null && ! rootModelURI.isEmpty() :
                new PreconditionException("rootModelURI == null || rootModelURI.isEmpty()");
        assert simulatedTimeUnit != null :
                new PreconditionException("simulatedTimeUnit == null");
        assert accelerationFactor > 0.0 :
                new PreconditionException("accelerationFactor <= 0.0");

        RTArchitecture result = null;

        if (architectureURI.equals(OvenCyPhy.UNIT_TEST_URI)) {
            result = LocalSILSimulationArchitectures.
                    createOvenSIL_Architecture4UnitTest(
                            architectureURI,
                            rootModelURI,
                            simulatedTimeUnit,
                            accelerationFactor
                    );
        } else if (architectureURI.equals(OvenCyPhy.INTEGRATION_TEST_URI)) {
            result = LocalSILSimulationArchitectures.
                    createOvenSIL_Architecture4IntegrationTest(
                            architectureURI,
                            rootModelURI,
                            simulatedTimeUnit,
                            accelerationFactor
                    );
        } else {
            throw new BCMException("Unknown local simulation architecture "
                    + "URI: " + architectureURI);
        }

        return result;
    }
    
    // -------------------------------------------------------------------------
    // Simulation methods
    // -------------------------------------------------------------------------

    @Override
    public void switchOn() throws Exception {
        this.controllerPort.startControlling();

        super.switchOn();

        this.triggerExternalEvent(SwitchOnOven::new);
    }

    @Override
    public void switchOff() throws Exception {
        this.controllerPort.stopControlling();

        super.switchOff();

        this.triggerExternalEvent(SwitchOffOven::new);
    }
    
    @Override
    public void startHeating() throws Exception {
        super.startHeating();

        this.triggerExternalEvent(HeatOven::new);
    }

    @Override
    public void stopHeating() throws Exception {
        super.stopHeating();

        this.triggerExternalEvent(DoNotHeatOven::new);
    }
    
    @Override
	public void openDoor() throws Exception {
    	super.openDoor();
    	
    	this.triggerExternalEvent(OpenDoorOven::new);
    }
    
    @Override
	public void closeDoor() throws Exception {
    	super.closeDoor();
    	
    	this.triggerExternalEvent(CloseDoorOven::new);
    }
      
    @Override
	public void setMode(OvenMode mode) throws Exception {
    	super.setMode(mode);
    	
    	ModeValue modeValue = new ModeValue(mode);
        this.triggerExternalEvent(t -> new SetModeOven(t, modeValue));
    }
    
    @Override
	public void setTargetTemperature(Measure<Double> target) throws Exception {
    	super.setTargetTemperature(target);
    	
    	TargetTemperatureValue temperatureValue = new TargetTemperatureValue(target.getData());
        this.triggerExternalEvent(t -> new SetTargetTemperatureOven(t, temperatureValue));
    }
    
    @Override
    public void setCurrentPowerLevel(Measure<Double> powerLevel) throws Exception {
        super.setCurrentPowerLevel(powerLevel);

        PowerValueOven powerValue = new PowerValueOven(powerLevel.getData());
        this.triggerExternalEvent(t -> new SetPowerOven(t, powerValue));
    }
    
    @Override
    public void startCooking() throws Exception {
        assert this.on() :
            new PreconditionException("on()");
        assert this.getState() != OvenState.HEATING :
            new PreconditionException("getCurrentState() != HEATING");

        if (Oven.VERBOSE) {
            this.traceMessage("Oven starts cooking immediately (SIL).\n");
        }

        if (this.currentState == OvenState.WAITING) {
            this.currentState = OvenState.ON;
            this.triggerExternalEvent(CancelDelayedStartOven::new);
            
        }

        this.startHeating(); // déclenche HeatOven

        assert this.getState() == OvenState.HEATING :
            new PostconditionException("getCurrentState() == HEATING");
    }
    
    @Override
    public void startDelayedCooking(Duration delay) throws Exception {
        assert this.on() :
            new PreconditionException("on()");
        assert this.getState() != OvenState.HEATING :
            new PreconditionException("getCurrentState() != HEATING");
        assert this.getState() != OvenState.WAITING :
            new PreconditionException("getCurrentState() != WAITING");
        assert delay != null :
            new PreconditionException("delay != null");
        assert delay.getSimulatedDuration() > 0.0 :
            new PreconditionException("delay > 0");

        if (Oven.VERBOSE) {
            this.traceMessage(
                "Oven schedules delayed cooking (SIL) after " + delay + ".\n");
        }

        this.currentState = OvenState.WAITING;
        
        DelayValue delayValue = new DelayValue(delay);
        this.triggerExternalEvent(t -> new SetPowerOven(t, delayValue));
    }
    
    @Override
	public void stopCooking() throws Exception {
	    assert this.getState() == OvenState.WAITING ||
	           this.getState() == OvenState.HEATING :
	        new PreconditionException(
	            "getCurrentState() == WAITING || HEATING");

	    if (this.getState() == OvenState.WAITING) {
	    	if (Oven.VERBOSE) {
		        this.traceMessage("Oven cancels delayed start.\n");
		    }
	    	
	        this.currentState = OvenState.ON;
	        this.triggerExternalEvent(CancelDelayedStartOven::new);
	        
	    } else { // HEATING
	    	if (Oven.VERBOSE) {
		        this.traceMessage("Oven stops cooking.\n");
		    }
	        this.stopHeating();
	    }

	    assert this.getState() == OvenState.ON :
	        new PostconditionException("getCurrentState() == ON");
	}
    
    
    
    
    // -------------------------------------------------------------------------
    // BCM methods
    // -------------------------------------------------------------------------

    @Override
    public synchronized void start() throws ComponentStartException {

        super.start();

        try {
            // connection to the controller
            this.doPortConnection(
                    this.controllerPort.getPortURI(),
                    this.controllerURI,
                    this.controllerCCName
            );

            this.setSimulatorPlugin();
        } catch (Exception e) {
            throw new ComponentStartException(e);
        }

    }

    @Override
    public synchronized void finalise() throws Exception
    {
        this.doPortDisconnection(this.controllerPort.getPortURI());

        super.finalise();
    }

    @Override
    public synchronized void shutdown() throws ComponentShutdownException
    {
        try {
            this.controllerExternalPort.unpublishPort();
            this.controllerPort.unpublishPort();
            this.actuatorPort.unpublishPort();

        } catch (Exception e) {
            throw new ComponentShutdownException(e) ;
        }

        super.shutdown();
    }

    @Override
    public void execute() throws Exception
    {
        super.execute();

        this.tracing("Oven executes.\n");

        switch (this.getExecutionMode()) {
            case UNIT_TEST_WITH_SIL_SIMULATION:
                this.initialiseClock4Simulation(
                        ClocksServerWithSimulation.STANDARD_INBOUNDPORT_URI,
                        this.clockURI
                );
                this.asp.initialiseSimulation(
                        this.getClock4Simulation().getSimulatedStartTime(),
                        this.getClock4Simulation().getSimulatedDuration());
                this.asp.startRTSimulation(
                        TimeUnit.NANOSECONDS.toMillis(
                                this.getClock4Simulation().getStartEpochNanos()),
                        this.getClock4Simulation().getSimulatedStartTime().getSimulatedTime(),
                        this.getClock4Simulation().getSimulatedDuration().getSimulatedDuration());

                this.getClock4Simulation().waitUntilStart();
                Thread.sleep(200L);
                this.logMessage(this.asp.getFinalReport().toString());
            case INTEGRATION_TEST_WITH_SIL_SIMULATION:
                break;
            case UNIT_TEST_WITH_HIL_SIMULATION:
            case INTEGRATION_TEST_WITH_HIL_SIMULATION:
                throw new BCMException("HIL simulation not implemented yet");
            default:
        }
    }
	

}