package equipments.HeatPump;

import equipments.HeatPump.compressor.CompressorCI;
import equipments.HeatPump.connections.HeatPumpActuatorInboundPort;
import equipments.HeatPump.connections.HeatPumpControllerOutboundPort;
import equipments.HeatPump.connections.HeatPumpExternalControlInboundPort;
import equipments.HeatPump.connections.HeatPumpExternalJava4InboundPort;
import equipments.HeatPump.interfaces.*;
import equipments.HeatPump.powerRepartitionPolicy.PowerRepartitionPolicyI;
import equipments.HeatPump.simulations.HeatPumpHeatingModel;
import equipments.HeatPump.simulations.events.*;
import equipments.HeatPump.simulations.sil.HeatPumpStateModel;
import equipments.HeatPump.simulations.sil.LocalSILSimulationArchitectures;
import equipments.HeatPump.temperatureSensor.TemperatureSensorCI;
import fr.sorbonne_u.alasca.physical_data.Measure;
import fr.sorbonne_u.alasca.physical_data.SignalData;
import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import fr.sorbonne_u.components.annotations.RequiredInterfaces;
import fr.sorbonne_u.components.cyphy.ExecutionMode;
import fr.sorbonne_u.components.cyphy.annotations.LocalArchitecture;
import fr.sorbonne_u.components.cyphy.annotations.SIL_Simulation_Architectures;
import fr.sorbonne_u.components.cyphy.interfaces.ModelStateAccessI;
import fr.sorbonne_u.components.cyphy.plugins.devs.AtomicSimulatorPlugin;
import fr.sorbonne_u.components.cyphy.plugins.devs.RTAtomicSimulatorPlugin;
import fr.sorbonne_u.components.cyphy.utils.aclocks.ClocksServerWithSimulation;
import fr.sorbonne_u.components.cyphy.utils.tests.TestScenarioWithSimulation;
import fr.sorbonne_u.components.exceptions.BCMException;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import fr.sorbonne_u.components.hem2025.bases.RegistrationCI;
import fr.sorbonne_u.devs_simulation.architectures.RTArchitecture;
import fr.sorbonne_u.devs_simulation.models.annotations.ModelExternalEvents;
import fr.sorbonne_u.exceptions.PreconditionException;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

/**
 * The class <code>equipments.HeatPump.HeatPumpCyPhy</code>.
 *
 * <p><strong>Description</strong></p>
 *
 * <p>
 *
 * </p>
 *
 * <p><strong>Invariants</strong></p>
 *
 * <pre>
 * </pre>
 *
 * <p>Created on : 2025-10-04</p>
 *
 * @author    <a href="mailto:Rodrigo.Vila@etu.sorbonne-universite.fr">Rodrigo Vila</a>
 * @author    <a href="mailto:Damien.Ribeiro@etu.sorbonne-universite.fr">Damien Ribeiro</a>
 */

@SIL_Simulation_Architectures({
        @LocalArchitecture(
                uri = "HEAT_PUMP_UNIT_TEST_URI",
                rootModelURI = "HEAT_PUMP_COUPLED_MODEL",
                simulatedTimeUnit = TimeUnit.HOURS,
                externalEvents = @ModelExternalEvents()
        ),
        @LocalArchitecture(
                uri = "HEAT_PUMP_INTEGRATION_TEST_URI",
                rootModelURI = "HEAT-PUMP-STATE-MODEL-URI",
                simulatedTimeUnit = TimeUnit.HOURS,
                externalEvents = @ModelExternalEvents(
                        exported = {
                                SwitchOnEvent.class,
                                SwitchOffEvent.class,
                                StartHeatingEvent.class,
                                StartCoolingEvent.class,
                                StopHeatingEvent.class,
                                StopCoolingEvent.class,
                                SetPowerEvent.class
                        }
                )
        )}
)
@RequiredInterfaces(required = {
        TemperatureSensorCI.class,
        CompressorCI.class,
        RegistrationCI.class,
        HeatPumpControllerCI.class
})
@OfferedInterfaces(offered = {
        HeatPumpUserCI.class,
        HeatPumpInternalControlCI.class,
        HeatPumpExternalJava4InboundPort.class,
        HeatPumpActuatorCI.class
})
public class HeatPumpCyPhy extends HeatPump {

    // -------------------------------------------------------------------------
    // Constants
    // -------------------------------------------------------------------------

    /** */
    protected static final String UNIT_TEST_URI = "HEAT_PUMP_UNIT_TEST_URI";

    /**  */
    protected static final String INTEGRATION_TEST_URI = "HEAT_PUMP_INTEGRATION_TEST_URI";


    // -------------------------------------------------------------------------
    // Variables
    // -------------------------------------------------------------------------

    protected AtomicSimulatorPlugin asp;
    protected String localArchitectureURI;

    protected HeatPumpControllerOutboundPort controller_port;
    protected String controllerCCName;

    protected String controllerURI;
    protected HeatPumpActuatorInboundPort actuator_port;
    protected HeatPumpExternalControlInboundPort controller_external_port;

    protected HeatPumpCyPhy(
            String compressorURI,
            String bufferTankURI,
            String compressorCcName,
            String bufferCcName,
            String userInboundURI,
            String internalInboundURI,
            String externalInboundURI,
            String registrationHEMURI,
            String registrationHEMCcName,
            String actuatorInboundURI,
            String cExternalInboundURI,
            String controllerURI,
            String controllerCCName) throws Exception {

        super(compressorURI,
                bufferTankURI,
                compressorCcName,
                bufferCcName,
                userInboundURI,
                internalInboundURI,
                externalInboundURI,
                registrationHEMURI,
                registrationHEMCcName);

        this.actuator_port = new HeatPumpActuatorInboundPort(actuatorInboundURI, this);
        this.actuator_port.publishPort();

        this.controller_external_port = new HeatPumpExternalControlInboundPort(cExternalInboundURI, this);
        this.controller_external_port.publishPort();

        this.controller_port = new HeatPumpControllerOutboundPort(this);
        this.controller_port.publishPort();

        this.controllerCCName = controllerCCName;
        this.controllerURI = controllerURI;
    }

    protected HeatPumpCyPhy(
            String reflectionInboundPortURI,
            String compressorURI,
            String bufferTankURI,
            String compressorCcName,
            String bufferCcName,
            String userInboundURI,
            String internalInboundURI,
            String externalInboundURI,
            String registrationHEMURI,
            String registrationHEMCcName,
            String cExternalInboundURI,
            String actuatorInboundURI,
            String controllerURI,
            String controllerCCName) throws Exception {
        super(reflectionInboundPortURI,
                compressorURI,
                bufferTankURI,
                compressorCcName,
                bufferCcName,
                userInboundURI,
                internalInboundURI,
                externalInboundURI,
                registrationHEMURI,
                registrationHEMCcName);

        this.actuator_port = new HeatPumpActuatorInboundPort(actuatorInboundURI, this);
        this.actuator_port.publishPort();

        this.controller_external_port = new HeatPumpExternalControlInboundPort(cExternalInboundURI, this);
        this.controller_external_port.publishPort();

        this.controller_port = new HeatPumpControllerOutboundPort(this);
        this.controller_port.publishPort();

        this.controllerCCName = controllerCCName;
        this.controllerURI = controllerURI;
    }

    protected HeatPumpCyPhy(
            String compressorURI,
            String bufferTankURI,
            String compressorCcName,
            String bufferCcName,
            String userInboundURI,
            String internalInboundURI,
            String externalInboundURI,
            String actuatorInboundURI,
            String cExternalInboundURI,
            String controllerURI,
            String controllerCCName) throws Exception {
        super(compressorURI,
                bufferTankURI,
                compressorCcName,
                bufferCcName,
                userInboundURI,
                internalInboundURI,
                externalInboundURI);

        this.actuator_port = new HeatPumpActuatorInboundPort(actuatorInboundURI, this);
        this.actuator_port.publishPort();

        this.controller_external_port = new HeatPumpExternalControlInboundPort(cExternalInboundURI, this);
        this.controller_external_port.publishPort();

        this.controller_port = new HeatPumpControllerOutboundPort(this);
        this.controller_port.publishPort();

        this.controllerCCName = controllerCCName;
        this.controllerURI = controllerURI;
    }

    // -------------------------------------------------------------------------
    // Private methods
    // -------------------------------------------------------------------------

    private void tracing(String message) {
        if (HeatPumpCyPhy.VERBOSE) {
            this.traceMessage(message);
        }
    }

    private void triggerExternalEvent(RTAtomicSimulatorPlugin.EventFactoryFI factory) throws Exception {
        if ( this.getExecutionMode().isSILTest() ) {

            ((RTAtomicSimulatorPlugin)asp).triggerExternalEvent(
                    HeatPumpStateModel.URI,
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
                        createHeatPumpSIL_Architecture4UnitTest(
                                architectureURI,
                                rootModelURI,
                                simulatedTimeUnit,
                                accelerationFactor
                        );
            case INTEGRATION_TEST_WITH_HIL_SIMULATION:
                result = LocalSILSimulationArchitectures.
                        createHeatPumpSIL_Architecture4IntegrationTest(
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

    @Override
    public void switchOn() throws Exception {
        this.controller_port.startControlling();

        super.switchOn();

        this.triggerExternalEvent(SwitchOnEvent::new);
    }

    @Override
    public void switchOff() throws Exception {
        this.controller_port.stopControlling();

        super.switchOff();

        this.triggerExternalEvent(SwitchOffEvent::new);
    }

    @Override
    public void startHeating() throws Exception {
        super.startHeating();

        this.triggerExternalEvent(StartHeatingEvent::new);
    }

    @Override
    public void stopHeating() throws Exception {
        super.stopHeating();

        this.triggerExternalEvent(StopHeatingEvent::new);
    }

    @Override
    public void startCooling() throws Exception {
        super.startCooling();

        this.triggerExternalEvent(StartCoolingEvent::new);
    }

    @Override
    public void stopCooling() throws Exception {
        super.stopCooling();

        this.triggerExternalEvent(StopCoolingEvent::new);
    }

    @Override
    public void setCurrentPower(Measure<Double> power) throws Exception {
        System.out.println(power.getData());

        super.setCurrentPower(power);

        HeatPumpPowerValue powerValue = new HeatPumpPowerValue(power.getData());
        this.triggerExternalEvent(t -> new SetPowerEvent(t, powerValue));
    }

    @Override
    public void setCurrentPower(Measure<Double> power, PowerRepartitionPolicyI policy) throws Exception {
        super.setCurrentPower(power, policy);

        HeatPumpPowerValue powerValue = new HeatPumpPowerValue(power.getData());
        this.triggerExternalEvent(t -> new SetPowerEvent(t, powerValue));
    }

    @Override
    public SignalData<Double> getCurrentTemperature() throws Exception {

        if ( this.executionMode.isSILTest() ) {
            Double v = (Double) this.asp
                    .getModelVariableValue(HeatPumpHeatingModel.URI, HeatPumpHeatingModel.currentTemperatureName)
                    .getValue();

            return new SignalData<>(new Measure<>(v));
        } else {
            return super.getCurrentTemperature();
        }

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
                    this.controller_port.getPortURI(),
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
        this.doPortDisconnection(this.controller_port.getPortURI());

        super.finalise();
    }

    @Override
    public synchronized void shutdown() throws ComponentShutdownException
    {
        try {
            this.controller_external_port.unpublishPort();
            this.controller_port.unpublishPort();
            this.actuator_port.unpublishPort();

        } catch (Exception e) {
            throw new ComponentShutdownException(e) ;
        }

        super.shutdown();
    }

    @Override
    public void execute() throws Exception
    {
        super.execute();

        this.tracing("Heat Pump executes.\n");

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
