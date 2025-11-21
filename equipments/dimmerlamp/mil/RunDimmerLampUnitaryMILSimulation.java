package equipments.dimmerlamp.mil;

import equipments.dimmerlamp.mil.events.LampPowerValue;
import equipments.dimmerlamp.mil.events.SetPowerLampEvent;
import equipments.dimmerlamp.mil.events.SwitchOffLampEvent;
import equipments.dimmerlamp.mil.events.SwitchOnLampEvent;
import fr.sorbonne_u.components.hem2025.tests_utils.SimulationTestStep;
import fr.sorbonne_u.components.hem2025.tests_utils.TestScenario;
import fr.sorbonne_u.devs_simulation.architectures.Architecture;
import fr.sorbonne_u.devs_simulation.architectures.ArchitectureI;
import fr.sorbonne_u.devs_simulation.hioa.architectures.AtomicHIOA_Descriptor;
import fr.sorbonne_u.devs_simulation.models.architectures.AbstractAtomicModelDescriptor;
import fr.sorbonne_u.devs_simulation.models.architectures.AtomicModelDescriptor;
import fr.sorbonne_u.devs_simulation.models.architectures.CoupledModelDescriptor;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.events.EventSink;
import fr.sorbonne_u.devs_simulation.models.events.EventSource;
import fr.sorbonne_u.devs_simulation.models.interfaces.ModelI;
import fr.sorbonne_u.devs_simulation.models.time.Duration;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import fr.sorbonne_u.devs_simulation.simulators.SimulationEngine;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulationReportI;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * The class <code>equipments.dimmerlamp.mil.RunDimmerLampMILSimulation</code>.
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
public class RunDimmerLampUnitaryMILSimulation {

    // -------------------------------------------------------------------------
    // Invariants
    // -------------------------------------------------------------------------

    // TODO

    // -------------------------------------------------------------------------
    // Methods
    // -------------------------------------------------------------------------

    public static void main(String[] args) {

        try {

            Map<String, AbstractAtomicModelDescriptor> atomicModelDescriptors =
                    new HashMap<>();

            // the dimmer lamp models simulating its electricity consumption is an
            // atomic HIOA model hence we use an AtomicHIOA_Descriptor(s)
            atomicModelDescriptors.put(
                    DimmerLampElectricityModel.URI,
                    AtomicHIOA_Descriptor.create(
                            DimmerLampElectricityModel.class,
                            DimmerLampElectricityModel.URI,
                            DimmerLampSimulationConfigurationI.TIME_UNIT,
                            null));
            // for atomic model, we use an AtomicModelDescriptor
            atomicModelDescriptors.put(
                    DimmerLampUnitTesterModel.URI,
                    AtomicModelDescriptor.create(
                            DimmerLampUnitTesterModel.class,
                            DimmerLampUnitTesterModel.URI,
                            DimmerLampSimulationConfigurationI.TIME_UNIT,
                            null));

            // map that will contain the coupled model descriptors to construct
            // the simulation architecture
            Map<String, CoupledModelDescriptor> coupledModelDescriptors =
                    new HashMap<>();

            // the set of submodels of the coupled model, given by their URIs
            Set<String> submodels = new HashSet<>();
            submodels.add(DimmerLampElectricityModel.URI);
            submodels.add(DimmerLampUnitTesterModel.URI);

            // event exchanging connections between exporting and importing
            // models
            Map<EventSource, EventSink[]> connections =
                    new HashMap<>();

            connections.put(
                    new EventSource(DimmerLampUnitTesterModel.URI,
                            SwitchOnLampEvent.class),
                    new EventSink[] {
                            new EventSink(DimmerLampElectricityModel.URI,
                                    SwitchOnLampEvent.class)
                    }
            );
            connections.put(
                    new EventSource(DimmerLampUnitTesterModel.URI,
                            SetPowerLampEvent.class),
                    new EventSink[] {
                            new EventSink(DimmerLampElectricityModel.URI,
                                    SetPowerLampEvent.class)
                    }
            );

            connections.put(
                    new EventSource(DimmerLampUnitTesterModel.URI,
                            SwitchOffLampEvent.class),
                    new EventSink[] {
                            new EventSink(DimmerLampElectricityModel.URI,
                                    SwitchOffLampEvent.class)
                    }
            );

            // coupled model descriptor
            coupledModelDescriptors.put(
                    DimmerLampCoupledModel.URI,
                    new CoupledModelDescriptor(
                            DimmerLampCoupledModel.class,
                            DimmerLampCoupledModel.URI,
                            submodels,
                            null,
                            null,
                            connections,
                            null));

            // simulation architecture
            ArchitectureI architecture =
                    new Architecture(
                            DimmerLampCoupledModel.URI,
                            atomicModelDescriptors,
                            coupledModelDescriptors,
                            DimmerLampSimulationConfigurationI.TIME_UNIT);

            // create the simulator from the simulation architecture
            SimulatorI se = architecture.constructSimulator();
            // this add additional time at each simulation step in
            // standard simulations (useful when debugging)
            SimulationEngine.SIMULATION_STEP_SLEEP_TIME = 0L;

            // run a CLASSICAL test scenario
            CLASSICAL.setUpSimulator(se);
            Time startTime = CLASSICAL.getStartTime();
            Duration d = CLASSICAL.getEndTime().subtract(startTime);
            se.doStandAloneSimulation(startTime.getSimulatedTime(),
                    d.getSimulatedDuration());
            SimulationReportI sr = se.getSimulatedModel().getFinalReport();
            System.out.println(sr);
            System.exit(0);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    // -------------------------------------------------------------------------
    // Test scenarios
    // -------------------------------------------------------------------------

    /** the start instant used in the test scenarios.						*/
    protected static Instant	START_INSTANT =
            Instant.parse("2025-10-20T12:00:00.00Z");
    /** the end instant used in the test scenarios.							*/
    protected static Instant	END_INSTANT =
            Instant.parse("2025-10-20T18:00:00.00Z");
    /** the start time in simulated time, corresponding to
     *  {@code START_INSTANT}.												*/
    protected static Time		START_TIME =
            new Time(0.0, DimmerLampSimulationConfigurationI.TIME_UNIT);

    protected static final String GHERKIN_SPEC = "------------------------------------\n" +
            "Classical\n" +
            "   Gherkin specification\n" +
            "       Feature: dimmer lamp operation\n" +
            "           Scenario: dimmer lamp switched on\n" +
            "               Given the dimmer lamp is off\n" +
            "               When it is switched on\n" +
            "               Then it is on\n" +
            "               And the power consumption is minimal\n" +
            "           Scenario: dimmer lamp sets power\n" +
            "               Given the dimmer lamp is on\n" +
            "               When the power is set to a valid wattage\n" +
            "               Then the power is equal to the set wattage\n" +
            "           Scenario: dimmer lamp switch off\n" +
            "               Given the dimmer lamp is on\n" +
            "               When the dimmer lamp is switched off\n" +
            "               Then the dimmer lamp is off\n" +
            "------------------------------------\n";

    protected static final String END_MESSAGE =
            "End Classical\n------------------------------------\n";

    protected static SimulationTestStep ScenarioLampSwitchOn() {
        return new SimulationTestStep(
                DimmerLampUnitTesterModel.URI,
                Instant.parse("2025-10-20T13:00:00.00Z"),
                (m, t) -> {
                    ArrayList<EventI> ret = new ArrayList<>();
                    ret.add(new SwitchOnLampEvent(t));
                    return ret;
                },
                (m, t) -> {});
    }

    protected static final double TEST_POWER = 50.; // 50 W

    protected static SimulationTestStep ScenarioLampSetPower() {

        return new SimulationTestStep(
                DimmerLampUnitTesterModel.URI,
                Instant.parse("2025-10-20T13:00:00.00Z"),
                (m, t) -> {
                    ArrayList<EventI> ret = new ArrayList<>();
                    ret.add(new SetPowerLampEvent(t, new LampPowerValue(TEST_POWER)));
                    return ret;
                },
                (m, t) -> {});
    }

    protected static SimulationTestStep ScenarioLampSwitchOff() {
        return new SimulationTestStep(
                DimmerLampUnitTesterModel.URI,
                Instant.parse("2025-10-20T14:00:00.00Z"),
                (m, t) -> {
                    ArrayList<EventI> ret = new ArrayList<>();
                    ret.add(new SwitchOffLampEvent(t));
                    return ret;
                },
                (m, t) -> {});
    }

    /** standard test scenario, see Gherkin specification.				 	*/
    protected final static TestScenario CLASSICAL =
            new TestScenario(
                    GHERKIN_SPEC,
                    END_MESSAGE,
                    START_INSTANT,
                    END_INSTANT,
                    START_TIME,
                    (se, ts) -> {
                        HashMap<String, Object> simParams = new HashMap<>();
                        simParams.put(
                                ModelI.createRunParameterName(
                                        DimmerLampUnitTesterModel.URI,
                                        DimmerLampUnitTesterModel.TEST_SCENARIO_RP_NAME),
                                ts);
                        se.setSimulationRunParameters(simParams);
                    },
                    new SimulationTestStep[]{
                            ScenarioLampSwitchOn(),
                            ScenarioLampSetPower(),
                            ScenarioLampSwitchOff()
                    }
            );
}
