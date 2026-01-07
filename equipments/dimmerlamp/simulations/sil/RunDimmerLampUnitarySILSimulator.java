package equipments.dimmerlamp.simulations.sil;

import equipments.dimmerlamp.DimmerLamp;
import equipments.dimmerlamp.simulations.DimmerLampCoupledModel;
import equipments.dimmerlamp.simulations.DimmerLampElectricityModel;
import equipments.dimmerlamp.simulations.DimmerLampSimulationConfigurationI;
import equipments.dimmerlamp.simulations.DimmerLampUnitTesterModel;
import equipments.dimmerlamp.simulations.events.LampPowerValue;
import equipments.dimmerlamp.simulations.events.SetPowerLampEvent;
import equipments.dimmerlamp.simulations.events.SwitchOffLampEvent;
import equipments.dimmerlamp.simulations.events.SwitchOnLampEvent;
import fr.sorbonne_u.components.cyphy.utils.tests.SimulationTestStep;
import fr.sorbonne_u.components.cyphy.utils.tests.TestScenarioWithSimulation;
import fr.sorbonne_u.components.hem2025e2.equipments.hairdryer.mil.HairDryerSimulationConfigurationI;
import fr.sorbonne_u.devs_simulation.architectures.ArchitectureI;
import fr.sorbonne_u.devs_simulation.architectures.RTArchitecture;
import fr.sorbonne_u.devs_simulation.hioa.architectures.RTAtomicHIOA_Descriptor;
import fr.sorbonne_u.devs_simulation.models.architectures.AbstractAtomicModelDescriptor;
import fr.sorbonne_u.devs_simulation.models.architectures.CoupledModelDescriptor;
import fr.sorbonne_u.devs_simulation.models.architectures.RTAtomicModelDescriptor;
import fr.sorbonne_u.devs_simulation.models.architectures.RTCoupledModelDescriptor;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.events.EventSink;
import fr.sorbonne_u.devs_simulation.models.events.EventSource;
import fr.sorbonne_u.devs_simulation.models.interfaces.ModelI;
import fr.sorbonne_u.devs_simulation.models.time.Duration;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import fr.sorbonne_u.devs_simulation.simulators.SimulationEngine;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulationReportI;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI;
import org.apache.commons.math3.random.RandomDataGenerator;

import java.time.Instant;
import java.util.*;

/**
 * The class <code>equipments.dimmerlamp.sil.RunDimmerLampSILSimulator</code>.
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
public class RunDimmerLampUnitarySILSimulator {

    // -------------------------------------------------------------------------
    // Constants
    // -------------------------------------------------------------------------

    /** the acceleration factor used in the real time MIL simulations.	 	*/
    public static final double		ACCELERATION_FACTOR = 3600.0;

    // -------------------------------------------------------------------------
    // Methods
    // -------------------------------------------------------------------------

    protected static void add_simple_connection(
            Map<EventSource, EventSink[]> map,
            Class <? extends EventI> eventType,
            String source_uri,
            String dest_uri) {
        final EventSource source =
                new EventSource(source_uri, eventType);
        final EventSink sink =
                new EventSink(dest_uri, eventType);

        map.put(source, new EventSink[] { sink });
    }

    public static void	main(String[] args)  {
        Time.setPrintPrecision(4);
        Duration.setPrintPrecision(4);

        try {

            Map<String, AbstractAtomicModelDescriptor> atomicModelDescriptors =
                    new HashMap<>();
            atomicModelDescriptors.put(
                    DimmerLampElectricityModel.URI,
                    RTAtomicHIOA_Descriptor.create(
                            DimmerLampElectricityModel.class,
                            DimmerLampElectricityModel.URI,
                            DimmerLampSimulationConfigurationI.TIME_UNIT,
                            null,
                            ACCELERATION_FACTOR));
            // for atomic model, we use an AtomicModelDescriptor
            atomicModelDescriptors.put(
                    DimmerLampStateModel.URI,
                    RTAtomicModelDescriptor.create(
                            DimmerLampStateModel.class,
                            DimmerLampStateModel.URI,
                            DimmerLampSimulationConfigurationI.TIME_UNIT,
                            null,
                            ACCELERATION_FACTOR));
            atomicModelDescriptors.put(
                    DimmerLampUnitTesterModel.URI,
                    RTAtomicModelDescriptor.create(
                            DimmerLampUnitTesterModel.class,
                            DimmerLampUnitTesterModel.URI,
                            DimmerLampSimulationConfigurationI.TIME_UNIT,
                            null,
                            ACCELERATION_FACTOR));

            Map<String, CoupledModelDescriptor> coupledModelDescriptors =
                    new HashMap<>();

            // the set of submodels of the coupled model, given by their URIs
            Set<String> submodels = new HashSet<>();
            submodels.add(DimmerLampStateModel.URI);
            submodels.add(DimmerLampElectricityModel.URI);
            submodels.add(DimmerLampUnitTesterModel.URI);

            Map<EventSource, EventSink[]> connections =
                    new HashMap<>();

            add_simple_connection(
                    connections, SwitchOnLampEvent.class,
                    DimmerLampStateModel.URI, DimmerLampElectricityModel.URI);
            add_simple_connection(
                    connections, SwitchOffLampEvent.class,
                    DimmerLampStateModel.URI, DimmerLampElectricityModel.URI);
            add_simple_connection(
                    connections, SetPowerLampEvent.class,
                    DimmerLampStateModel.URI, DimmerLampElectricityModel.URI);

            add_simple_connection(
                    connections, SwitchOnLampEvent.class,
                    DimmerLampUnitTesterModel.URI, DimmerLampStateModel.URI);
            add_simple_connection(
                    connections, SwitchOffLampEvent.class,
                    DimmerLampUnitTesterModel.URI, DimmerLampStateModel.URI);
            add_simple_connection(
                    connections, SetPowerLampEvent.class,
                    DimmerLampUnitTesterModel.URI, DimmerLampStateModel.URI);

            // coupled model descriptor
            coupledModelDescriptors.put(
                    DimmerLampCoupledModel.URI,
                    new RTCoupledModelDescriptor(
                            DimmerLampCoupledModel.class,
                            DimmerLampCoupledModel.URI,
                            submodels,
                            null,
                            null,
                            connections,
                            null,
                            ACCELERATION_FACTOR));

            // simulation architecture
            ArchitectureI architecture =
                    new RTArchitecture(
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
            Map<String, Object> classicalRunParameters = new HashMap<>();
            CLASSICAL.addToRunParameters(classicalRunParameters);
            se.setSimulationRunParameters(classicalRunParameters);
            Time startTime = CLASSICAL.getStartTime();
            Duration d = CLASSICAL.getEndTime().subtract(startTime);
            long realTimeStart = System.currentTimeMillis() + 200;
            se.startRTSimulation(realTimeStart, startTime.getSimulatedTime(), d.getSimulatedDuration());
            long executionDuration =
                    new Double(
                            HairDryerSimulationConfigurationI.TIME_UNIT.toMillis(1)
                                    * (d.getSimulatedDuration()/ACCELERATION_FACTOR)).
                            longValue();
            Thread.sleep(executionDuration + 2000L);
            SimulationReportI sr = se.getSimulatedModel().getFinalReport();
            System.out.println(sr);

            /** create the simulator from the simulation architecture
             se = architecture.constructSimulator();
             // this add additional time at each simulation step in
             // standard simulations (useful when debugging)
             SimulationEngine.SIMULATION_STEP_SLEEP_TIME = 0L;

             // run a PRIORITY test scenario
             PRIORITY_SCENARIO.setUpSimulator(se);
             startTime = PRIORITY_SCENARIO.getStartTime();
             d = PRIORITY_SCENARIO.getEndTime().subtract(startTime);
             se.doStandAloneSimulation(startTime.getSimulatedTime(),
             d.getSimulatedDuration());
             sr = se.getSimulatedModel().getFinalReport();
             System.out.println(sr);*/

            System.exit(0);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

}

    // -------------------------------------------------------------------------
    // Test scenarios
    // -------------------------------------------------------------------------

    /** the start instant used in the test scenarios.						*/
    protected static Instant START_INSTANT_CLASSICAL =
            Instant.parse("2025-10-20T01:00:00.00Z");
    /** the end instant used in the test scenarios.							*/
    protected static Instant	END_INSTANT_CLASSICAL =
            Instant.parse("2025-10-20T13:00:00.00Z");

    /** the start instant used in the test scenarios.						*/
    protected static Instant	START_INSTANT_PRIORITY =
            Instant.parse("2024-10-20T00:00:00.00Z");
    /** the end instant used in the test scenarios.							*/
    protected static Instant	END_INSTANT_PRIORITY =
            Instant.parse("2024-10-20T04:00:00.00Z");

    /** the start time in simulated time, corresponding to
     *  {@code START_INSTANT}.												*/
    protected static Time		START_TIME =
            new Time(0.0, DimmerLampSimulationConfigurationI.TIME_UNIT);

    // The tests are repeated
    protected static final int REPETITION = 2;

    protected static final RandomDataGenerator rg = new RandomDataGenerator();

    protected static final String GHERKIN_SPEC_CLASSICAL = "------------------------------------\n" +
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
            "           Scenario: The tests are repeated another time\n" +
            "               Given the dimmer lamp has just been switched off\n" +
            "               When the tests are repeated\n" +
            "               Then the behaviour of the tests is still the same\n" +
            "------------------------------------\n";

    protected static final String GHERKIN_SPEC_PRIORITY = "------------------------------------\n" +
            "Classical\n" +
            "   Gherkin specification\n" +
            "       Feature: Priority Test\n" +
            "         Scenario: All the events occur at the same instant\n" +
            "           Given the simulator has been initialised\n" +
            "           When all the events occur at the same time\n" +
            "           Then the simulator does not crash\n" +
            "           and kwh == 0\n" +
            "------------------------------------\n";

    protected static final String END_MESSAGE =
            "End Classical\n------------------------------------\n";

    protected static SimulationTestStep ScenarioLampSwitchOn(String instant) {
        return new SimulationTestStep(
                DimmerLampUnitTesterModel.URI,
                Instant.parse(instant),
                (m, t) -> {
                    ArrayList<EventI> ret = new ArrayList<>();
                    ret.add(new SwitchOnLampEvent(t));
                    return ret;
                },
                (m, t) -> {});
    }

    protected static SimulationTestStep ScenarioLampSetPower(String instant) {

        final double power =
                rg.nextUniform(
                        DimmerLamp.MIN_POWER_VARIATION.getData(),
                        DimmerLamp.MAX_POWER_VARIATION.getData());

        return new SimulationTestStep(
                DimmerLampUnitTesterModel.URI,
                Instant.parse(instant),
                (m, t) -> {
                    ArrayList<EventI> ret = new ArrayList<>();
                    ret.add(new SetPowerLampEvent(t, new LampPowerValue(power)));
                    return ret;
                },
                (m, t) -> {});
    }

    protected static SimulationTestStep ScenarioLampSwitchOff(String instant) {
        return new SimulationTestStep(
                DimmerLampUnitTesterModel.URI,
                Instant.parse(instant),
                (m, t) -> {
                    ArrayList<EventI> ret = new ArrayList<>();
                    ret.add(new SwitchOffLampEvent(t));
                    return ret;
                },
                (m, t) -> {});
    }

    protected static String testInstantClassical(int i) {
        StringBuilder builder = new StringBuilder();

        if (i < 10) {
            builder.append("2025-10-20T0");
        } else {
            builder.append("2025-10-20T");
        }

        builder.append(i);
        builder.append(":00:00.00Z");
        return builder.toString();
    }

    /**
     *
     * Returns a list containing the step of the simulation
     *
     * <p><strong>Contract</strong></p>
     *
     * <pre>
     *  pre {@code true} // no precondition
     *  post {@code true} // no postcondition
     * </pre>
     * @param repetition the number of times we repeat the simulation test will be repeated
     * @return list containing the step of the simulation
     */
    protected static SimulationTestStep[] testScenariosClassical(int repetition) {
        ArrayList<SimulationTestStep> testSteps = new ArrayList<>();

        int i = 1;

        for (int k = 0; k < repetition; ++k) {
            testSteps.add(ScenarioLampSwitchOn(testInstantClassical(i)));
            testSteps.add(ScenarioLampSetPower(testInstantClassical(i + 1)));
            testSteps.add(ScenarioLampSwitchOff(testInstantClassical(i + 2)));
            i += 3;
        }

        SimulationTestStep[] result = new SimulationTestStep[testSteps.size()];
        return testSteps.toArray(result);
    }

    /** standard test scenario, see Gherkin specification.				 	*/
    protected final static TestScenarioWithSimulation CLASSICAL =
            new TestScenarioWithSimulation(
                    GHERKIN_SPEC_CLASSICAL,
                    END_MESSAGE,
                    "clock_uri",
                    START_INSTANT_CLASSICAL,
                    END_INSTANT_CLASSICAL,
                    DimmerLampCoupledModel.URI,
                    START_TIME,
                    (ts, simParams) -> {
                        simParams.put(
                                ModelI.createRunParameterName(
                                        DimmerLampUnitTesterModel.URI,
                                        DimmerLampUnitTesterModel.TEST_SCENARIO_RP_NAME),
                                ts);
                    },
                    testScenariosClassical(REPETITION)
            );

    protected static SimulationTestStep[] testScenariosPriority() {
        ArrayList<SimulationTestStep> testSteps = new ArrayList<>();

        testSteps.add(ScenarioLampSwitchOn("2024-10-20T01:00:00.00Z"));
        testSteps.add(ScenarioLampSetPower("2024-10-20T01:00:00.00Z"));
        testSteps.add(ScenarioLampSwitchOff("2024-10-20T01:00:00.00Z"));

        SimulationTestStep[] result = new SimulationTestStep[testSteps.size()];
        return testSteps.toArray(result);
    }

    /** priority test all the Event are used at the same
     *  We expect to kwh == 0
     */
    protected final static TestScenarioWithSimulation PRIORITY_SCENARIO =
            new TestScenarioWithSimulation(
                    GHERKIN_SPEC_PRIORITY,
                    END_MESSAGE,
                    "clock_uri",
                    START_INSTANT_PRIORITY,
                    END_INSTANT_PRIORITY,
                    DimmerLampCoupledModel.URI,
                    START_TIME,
                    (ts, simParams) -> {
                        simParams.put(
                                ModelI.createRunParameterName(
                                        DimmerLampUnitTesterModel.URI,
                                        DimmerLampUnitTesterModel.TEST_SCENARIO_RP_NAME),
                                ts);
                    },
                    testScenariosPriority()
            );


}
