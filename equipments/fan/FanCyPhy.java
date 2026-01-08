package equipments.fan;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import equipments.fan.simulations.events.SetHighFan;
import equipments.fan.simulations.events.SetLowFan;
import equipments.fan.simulations.events.SetMediumFan;
import equipments.fan.simulations.events.SwitchOffFan;
import equipments.fan.simulations.events.SwitchOnFan;
import equipments.fan.simulations.sil.FanStateModel;
import equipments.fan.simulations.sil.LocalSILSimulationArchitectures;
import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import fr.sorbonne_u.components.cyphy.ExecutionMode;
import fr.sorbonne_u.components.cyphy.annotations.LocalArchitecture;
import fr.sorbonne_u.components.cyphy.annotations.SIL_Simulation_Architectures;
import fr.sorbonne_u.components.cyphy.plugins.devs.AtomicSimulatorPlugin;
import fr.sorbonne_u.components.cyphy.plugins.devs.RTAtomicSimulatorPlugin;
import fr.sorbonne_u.components.cyphy.utils.aclocks.ClocksServerWithSimulation;
import fr.sorbonne_u.components.cyphy.utils.tests.TestScenarioWithSimulation;
import fr.sorbonne_u.components.exceptions.BCMException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import fr.sorbonne_u.devs_simulation.architectures.RTArchitecture;
import fr.sorbonne_u.devs_simulation.models.annotations.ModelExternalEvents;
import fr.sorbonne_u.exceptions.PreconditionException;

//-----------------------------------------------------------------------------
/**
* The class <code>FanCyPhy</code> implements the cyber-physical
* component version of the fan.
*
* <p><strong>Description</strong></p>
* 
* <p><strong>Implementation Invariants</strong></p>
* 
* <pre>
* invariant	{@code INITIAL_STATE != null}
* invariant	{@code INITIAL_MODE != null}
* invariant	{@code currentState != null}
* invariant	{@code currentMode != null}
* invariant	{@code NUMBER_OF_STANDARD_THREADS >= 0}
* invariant	{@code NUMBER_OF_SCHEDULABLE_THREADS >= 0}
* invariant	{@code localArchitectureURI == null || !localArchitectureURI.isEmpty() && accelerationFactor > 0.0}
* invariant	{@code asp == null || localArchitectureURI != null}
* </pre>
* 
* <p><strong>Invariants</strong></p>
* 
* <pre>
* invariant	{@code REFLECTION_INBOUND_PORT_URI != null && !REFLECTION_INBOUND_PORT_URI.isEmpty()}
* invariant	{@code INBOUND_PORT_URI != null && !INBOUND_PORT_URI.isEmpty()}
* invariant	{@code UNIT_TEST_ARCHITECTURE_URI != null && !UNIT_TEST_ARCHITECTURE_URI.isEmpty()}
* invariant	{@code INTEGRATION_TEST_ARCHITECTURE_URI != null && !INTEGRATION_TEST_ARCHITECTURE_URI.isEmpty()}
* invariant	{@code HIGH_POWER != null && HIGH_POWER.getData() > 0.0 && HIGH_POWER.getMeasurementUnit().equals(POWER_UNIT)}
* invariant	{@code MEDIUM_POWER != null && MEDIUM_POWER.getData() > 0.0 && MEDIUM_POWER.getMeasurementUnit().equals(POWER_UNIT)}
* invariant	{@code LOW_POWER != null && LOW_POWER.getData() > 0.0 && LOW_POWER.getMeasurementUnit().equals(POWER_UNIT)}
* invariant	{@code TENSION != null && (TENSION.getData() == 110.0 || TENSION.getData() == 220.0) && TENSION.getMeasurementUnit().equals(TENSION_UNIT)}
* invariant	{@code INITIAL_STATE != null && INITIAL_MODE != null}
* invariant	{@code X_RELATIVE_POSITION >= 0}
* invariant	{@code Y_RELATIVE_POSITION >= 0}
* </pre>
* 
* <p>Created on : 2026-01-03</p>
* 
* @author	<a href="mailto:Rodrigo.Vila@etu.sorbonne-universite.fr">Rodrigo Vila</a>
* @author	<a href="mailto:Damien.Ribeiro@etu.sorbonne-universite.fr">Damien Ribeiro</a>
*/

@SIL_Simulation_Architectures({
    @LocalArchitecture(
            uri = "FAN_UNIT_TEST_URI",
            rootModelURI = "FAN_COUPLED_MODEL",
            simulatedTimeUnit = TimeUnit.HOURS,
            externalEvents = @ModelExternalEvents()
    ),
    @LocalArchitecture(
            uri = "FAN_INTEGRATION_TEST_URI",
            rootModelURI = "FAN-STATE-MODEL-URI",
            simulatedTimeUnit = TimeUnit.HOURS,
            externalEvents = @ModelExternalEvents()
    )}
)
@OfferedInterfaces(offered={FanUserCI.class})
public class FanCyPhy 
extends Fan{
	
	// -------------------------------------------------------------------------
    // Constants
    // -------------------------------------------------------------------------

    protected static final String UNIT_TEST_URI = "FAN_UNIT_TEST_URI";

    protected static final String INTEGRATION_TEST_URI = "FAN_INTEGRATION_TEST_URI";


    // -------------------------------------------------------------------------
    // Variables
    // -------------------------------------------------------------------------

    protected AtomicSimulatorPlugin asp;
    protected String localArchitectureURI;

    // -------------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------------

    protected FanCyPhy(
            String reflectionInboundPortURI,
            String userInboundPortURI) throws Exception {
        super(reflectionInboundPortURI, userInboundPortURI);
    }
    
    // -------------------------------------------------------------------------
    // Private methods
    // -------------------------------------------------------------------------

    private void tracing(String message) {
        if (Fan.VERBOSE) {
            this.traceMessage(message);
        }
    }

    private void triggerExternalEvent(RTAtomicSimulatorPlugin.EventFactoryFI factory) throws Exception {
        if ( this.getExecutionMode().isSILTest() ) {

            ((RTAtomicSimulatorPlugin)asp).triggerExternalEvent(
                    FanStateModel.URI,
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
                this.asp = new AtomicSimulatorPlugin();
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
        ExecutionMode mode = this.getExecutionMode();

        switch (mode){
            case UNIT_TEST_WITH_SIL_SIMULATION:
                result = LocalSILSimulationArchitectures.
                        createFanSIL_Architecture4UnitTest(
                                architectureURI,
                                rootModelURI,
                                simulatedTimeUnit,
                                accelerationFactor
                        );
            case INTEGRATION_TEST_WITH_HIL_SIMULATION:
                result = LocalSILSimulationArchitectures.
                        createFanSIL_Architecture4IntegrationTest(
                                architectureURI,
                                rootModelURI,
                                simulatedTimeUnit,
                                accelerationFactor
                        );
                break;
            default:
                throw new BCMException("Unknown local simulation architecture : " + architectureURI);
        }

        return result;
    }
    
    // -------------------------------------------------------------------------
    // Simulation methods
    // -------------------------------------------------------------------------

    /**
     * @see equipments.fan.Fan#turnOn()
     */
    @Override
    public void turnOn() throws Exception {
        super.turnOn();

        this.triggerExternalEvent(SwitchOnFan::new);
    }

    /**
     * @see equipments.fan.Fan#turnOff()
     */
    @Override
    public void turnOff() throws Exception {
        super.turnOff();

        this.triggerExternalEvent(SwitchOffFan::new);
    }
    
    /**
     * @see equipments.fan.Fan#setHigh()
     */
    @Override
    public void setHigh() throws Exception {
        super.setHigh();

        this.triggerExternalEvent(SetHighFan::new);
    }
    
    /**
     * @see equipments.fan.Fan#setHigh()
     */
    @Override
    public void setMedium() throws Exception {
        super.setMedium();

        this.triggerExternalEvent(SetMediumFan::new);
    }
    
    /**
     * @see equipments.fan.Fan#setLow()
     */
    @Override
    public void setLow() throws Exception {
        super.setLow();

        this.triggerExternalEvent(SetLowFan::new);
    }
    
    @Override
    public void start() throws ComponentStartException {
        super.start();

        // Error wrapping
        try {
            this.setSimulatorPlugin();
        } catch (Exception e) {
            throw new ComponentStartException(e);
        }

    }

    @Override
    public void execute() throws Exception
    {
        super.execute();

        this.tracing("Fan executes.\n");

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
