package equipments.dimmerlamp;

import equipments.dimmerlamp.interfaces.DimmerLampExternalJava4CI;
import equipments.dimmerlamp.interfaces.DimmerLampUserCI;
import equipments.dimmerlamp.simulations.events.LampPowerValue;
import equipments.dimmerlamp.simulations.events.SetPowerLampEvent;
import equipments.dimmerlamp.simulations.events.SwitchOffLampEvent;
import equipments.dimmerlamp.simulations.events.SwitchOnLampEvent;
import equipments.dimmerlamp.simulations.sil.DimmerLampStateModel;
import equipments.dimmerlamp.simulations.sil.LocalSILSimulationArchitectures;
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
import fr.sorbonne_u.components.hem2025e2.equipments.heater.mil.events.SwitchOnHeater;
import fr.sorbonne_u.components.hem2025e3.equipments.heater.sil.HeaterStateSILModel;
import fr.sorbonne_u.components.utils.tests.TestScenario;
import fr.sorbonne_u.devs_simulation.architectures.RTArchitecture;
import fr.sorbonne_u.devs_simulation.models.annotations.ModelExternalEvents;
import fr.sorbonne_u.exceptions.PreconditionException;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

/**
 * The class <code>equipments.dimmerlamp.DimmerLampCyPhy</code>.
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
                uri = "DIMMER_LAMP_UNIT_TEST_URI",
                rootModelURI = "DIMMER_LAMP_COUPLED_MODEL",
                simulatedTimeUnit = TimeUnit.HOURS,
                externalEvents = @ModelExternalEvents()
        ),
        @LocalArchitecture(
                uri = "silIntegrationTests",
                rootModelURI = "DIMMER-LAMP-STATE-MODEL-URI",
                simulatedTimeUnit = TimeUnit.HOURS,
                externalEvents = @ModelExternalEvents(
                        exported = {
                                SwitchOnLampEvent.class,
                                SwitchOffLampEvent.class,
                                SetPowerLampEvent.class
                        }
                )
        )}
)
@RequiredInterfaces(required = {RegistrationCI.class})
@OfferedInterfaces(offered = {DimmerLampUserCI.class, DimmerLampExternalJava4CI.class})
public class DimmerLampCyPhy
extends DimmerLamp {

    // -------------------------------------------------------------------------
    // Constants
    // -------------------------------------------------------------------------

    /** */
    public static final String UNIT_TEST_URI = "DIMMER_LAMP_UNIT_TEST_URI";

    /**  */
    public static final String INTEGRATION_TEST_URI = "silIntegrationTests";

    // -------------------------------------------------------------------------
    // Variables
    // -------------------------------------------------------------------------

    protected AtomicSimulatorPlugin asp;
    protected String localArchitectureURI;

    // -------------------------------------------------------------------------
    // Invariants
    // -------------------------------------------------------------------------

    // -------------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------------

    protected DimmerLampCyPhy(
            String reflectionInboundPortURI,
            String userInboundPortURI,
            String externalInboundPortURI,
            String registrationHEMURI,
            String registrationHemCcName) throws Exception {
        super(reflectionInboundPortURI,
                userInboundPortURI,
                externalInboundPortURI,
                registrationHEMURI,
                registrationHemCcName);
    }

    protected DimmerLampCyPhy(String registrationHEMURI,
                              String registrationHemCcName,
                              ExecutionMode mode,
                              TestScenario testScenario,
                              String architectureURI,
                              double accelerationFactor) throws Exception {
        super(registrationHEMURI,
                registrationHemCcName,
                mode,
                testScenario,
                accelerationFactor);

        this.executionMode = mode;
        this.localArchitectureURI = architectureURI;
    }

    // -------------------------------------------------------------------------
    // Private methods
    // -------------------------------------------------------------------------

    private void tracing(String message) {
        if (DimmerLamp.VERBOSE) {
            this.traceMessage(message + "\n");
        }
    }

    private void triggerExternalEvent(RTAtomicSimulatorPlugin.EventFactoryFI factory) throws Exception {
        if ( this.getExecutionMode().isSILTest() ) {
            this.tracing("triggered");
            ((RTAtomicSimulatorPlugin)this.asp).triggerExternalEvent(
                    DimmerLampStateModel.URI,
                    factory);
            this.tracing("triggered successfully");
        }
    }

    private void setSimulatorPlugin() throws Exception {

        switch (this.getExecutionMode()) {
            case INTEGRATION_TEST_WITH_SIL_SIMULATION:
            case UNIT_TEST_WITH_SIL_SIMULATION:
                this.tracing(this.localArchitectureURI);
                RTArchitecture architecture =
                        (RTArchitecture) this.localSimulationArchitectures.
                                get(this.localArchitectureURI);
                this.asp = new AtomicSimulatorPlugin();
                this.asp.setPluginURI(architecture.getRootModelURI());
                this.tracing(architecture.getRootModelURI());
                this.asp.setSimulationArchitecture(architecture);
                this.installPlugin(this.asp);
                this.asp.createSimulator();
                this.asp.setSimulationRunParameters(
                        (TestScenarioWithSimulation) this.testScenario,
                        new HashMap<>()
                );
                this.tracing("asp initialised");
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

//        switch (mode){
//            case UNIT_TEST_WITH_SIL_SIMULATION:
//                result = LocalSILSimulationArchitectures.
//                        createDimmerLampSIL_Architecture4UnitTest(
//                                architectureURI,
//                                rootModelURI,
//                                simulatedTimeUnit,
//                                accelerationFactor
//                        );
//                break;
//            case INTEGRATION_TEST_WITH_SIL_SIMULATION:
//                result = LocalSILSimulationArchitectures.
//                        createDimmerLampSIL_Architecture4IntegrationTest(
//                                architectureURI,
//                                rootModelURI,
//                                simulatedTimeUnit,
//                                accelerationFactor
//                        );
//                break;
//            default:
//                throw new BCMException("Unknown local simulation architecture : " + architectureURI);
//        }

        if (architectureURI.equals(DimmerLampCyPhy.UNIT_TEST_URI)) {
            result = LocalSILSimulationArchitectures.
                        createDimmerLampSIL_Architecture4UnitTest(
                                architectureURI,
                                rootModelURI,
                                simulatedTimeUnit,
                                accelerationFactor
                        );
        } else if (architectureURI.equals(DimmerLampCyPhy.INTEGRATION_TEST_URI)) {
            result = LocalSILSimulationArchitectures.
                        createDimmerLampSIL_Architecture4IntegrationTest(
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

    /**
     * @see equipments.dimmerlamp.DimmerLamp#switchOn
     */
    @Override
    public void switchOn() throws Exception {
        this.tracing("super switchOn");

        super.switchOn();

        this.tracing("trigger On");

        this.triggerExternalEvent(SwitchOnLampEvent::new);
    }

    /**
     * @see equipments.dimmerlamp.DimmerLamp#switchOff
     */
    @Override
    public void switchOff() throws Exception {
        super.switchOff();

        this.triggerExternalEvent(SwitchOffLampEvent::new);
    }

    /**
     * @see equipments.dimmerlamp.DimmerLamp#setPower
     */
    @Override
    public void setPower(Measure<Double> variation) throws Exception {
        super.setPower(variation);

        LampPowerValue power_value = new LampPowerValue(variation.getData());
        this.triggerExternalEvent(t -> new SetPowerLampEvent(t, power_value));
    }

    // -------------------------------------------------------------------------
    // BCM methods
    // -------------------------------------------------------------------------


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
    public void shutdown() throws ComponentShutdownException {
        try {
            super.shutdown();
        } catch (Exception e) {
            throw new ComponentShutdownException(e) ;
        }

    }

    @Override
    public void execute() throws Exception
    {

        this.tracing("Dimmer lamp executes.\n");

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
                this.tracing("Starts INTEGRATION_TEST_WITH_SIL_SIMULATION");
                this.initialiseClock4Simulation(
                        ClocksServerWithSimulation.STANDARD_INBOUNDPORT_URI,
                        this.clockURI);
                this.tracing("clock initialised");
                break;
            case UNIT_TEST_WITH_HIL_SIMULATION:
            case INTEGRATION_TEST_WITH_HIL_SIMULATION:
                throw new BCMException("HIL simulation not implemented yet");
            default:
        }
    }

}
