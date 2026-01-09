package equipments.fan.simulations.sil;

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

import equipments.fan.simulations.FanCoupledModel;
import equipments.fan.simulations.FanElectricityModel;
import equipments.fan.simulations.FanSimulationConfigurationI;
import equipments.fan.simulations.FanUnitTesterModel;
import equipments.fan.simulations.events.SetHighFan;
import equipments.fan.simulations.events.SetLowFan;
import equipments.fan.simulations.events.SetMediumFan;
import equipments.fan.simulations.events.SwitchOffFan;
import equipments.fan.simulations.events.SwitchOnFan;

import java.time.Instant;
import java.util.*;

/**
 * The class <code>RunFanrUnitarySILSimulator</code> is the main class
 * used to run real time simulations on the software-in-the-loop models of the
 * fan in isolation based on test scenarios.
 *
 * <p><strong>Description</strong></p>
 *
 * <p><strong>Implementation Invariants</strong></p>
 *
 ** <pre>
 * invariant	{@code true}	// no more invariant
 * </pre>
 * 
 * <p>Created on : 2026-01-03</p>
 *
 * @author    <a href="mailto:Rodrigo.Vila@etu.sorbonne-universite.fr">Rodrigo Vila</a>
 * @author    <a href="mailto:Damien.Ribeiro@etu.sorbonne-universite.fr">Damien Ribeiro</a>
 */
public class RunFanUnitarySILSimulator {

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
                    FanElectricityModel.URI,
                    RTAtomicHIOA_Descriptor.create(
                            FanElectricityModel.class,
                            FanElectricityModel.URI,
                            FanSimulationConfigurationI.TIME_UNIT,
                            null,
                            ACCELERATION_FACTOR));
            // for atomic model, we use an AtomicModelDescriptor
            atomicModelDescriptors.put(
                    FanStateModel.URI,
                    RTAtomicModelDescriptor.create(
                            FanStateModel.class,
                            FanStateModel.URI,
                            FanSimulationConfigurationI.TIME_UNIT,
                            null,
                            ACCELERATION_FACTOR));
            atomicModelDescriptors.put(
                    FanUnitTesterModel.URI,
                    RTAtomicModelDescriptor.create(
                            FanUnitTesterModel.class,
                            FanUnitTesterModel.URI,
                            FanSimulationConfigurationI.TIME_UNIT,
                            null,
                            ACCELERATION_FACTOR));

            Map<String, CoupledModelDescriptor> coupledModelDescriptors =
                    new HashMap<>();

            // the set of submodels of the coupled model, given by their URIs
            Set<String> submodels = new HashSet<>();
            submodels.add(FanStateModel.URI);
            submodels.add(FanElectricityModel.URI);
            submodels.add(FanUnitTesterModel.URI);

            Map<EventSource, EventSink[]> connections =
                    new HashMap<>();

            add_simple_connection(
                    connections, SwitchOnFan.class,
                    FanStateModel.URI, FanElectricityModel.URI);
            add_simple_connection(
                    connections, SwitchOffFan.class,
                    FanStateModel.URI, FanElectricityModel.URI);
            add_simple_connection(
                    connections, SetHighFan.class,
                    FanStateModel.URI, FanElectricityModel.URI);
            add_simple_connection(
                    connections, SetMediumFan.class,
                    FanStateModel.URI, FanElectricityModel.URI);
            add_simple_connection(
                    connections, SetLowFan.class,
                    FanStateModel.URI, FanElectricityModel.URI);

            add_simple_connection(
                    connections, SwitchOnFan.class,
                    FanUnitTesterModel.URI, FanStateModel.URI);
            add_simple_connection(
                    connections, SwitchOffFan.class,
                    FanUnitTesterModel.URI, FanStateModel.URI);
            add_simple_connection(
                    connections, SetHighFan.class,
                    FanUnitTesterModel.URI, FanStateModel.URI);
            add_simple_connection(
                    connections, SetMediumFan.class,
                    FanUnitTesterModel.URI, FanStateModel.URI);
            add_simple_connection(
                    connections, SetLowFan.class,
                    FanUnitTesterModel.URI, FanStateModel.URI);

            // coupled model descriptor
            coupledModelDescriptors.put(
                    FanCoupledModel.URI,
                    new RTCoupledModelDescriptor(
                            FanCoupledModel.class,
                            FanCoupledModel.URI,
                            submodels,
                            null,
                            null,
                            connections,
                            null,
                            ACCELERATION_FACTOR));

            // simulation architecture
            ArchitectureI architecture =
                    new RTArchitecture(
                            FanCoupledModel.URI,
                            atomicModelDescriptors,
                            coupledModelDescriptors,
                            FanSimulationConfigurationI.TIME_UNIT);
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
            new Time(0.0, FanSimulationConfigurationI.TIME_UNIT);

    // The tests are repeated
    protected static final int REPETITION = 2;

    protected static final String GHERKIN_SPEC_CLASSICAL = "------------------------------------\n" +
            "Classical\n" +
            "   Gherkin specification\n" +
            "       Feature: fan operation\n" +
            "           Scenario: fan switched on\n" +
            "               Given the fan is off\n" +
            "               When it is switched on\n" +
            "               Then it is on\n" +
            "        		Then it is on and low\n" +
            "      		Scenario: fan set high\n" +
			"        		Given a fan that is on\n" +
			"		        When it is set high\n" +
			"       		 Then it is on and high\n" +
			"      		Scenario: fan set medium\n" +
			"        		Given a fan that is on\n" +
			"        		When it is set medium\n" +
			"        		Then it is on and medium\n" +
			"      		Scenario: fan set low\n" +
			"        		Given a fan that is on\n" +
			"        		When it is set low\n" +
			"        		Then it is on and low\n" +
			"      		Scenario: fan switched off\n" +
			"        		Given a fan that is on\n" +
			"        		When it is switched of\n" +
			"        		Then it is off\n" +
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

    protected static SimulationTestStep ScenarioFanSwitchOn(String instant) {
        return new SimulationTestStep(
                FanUnitTesterModel.URI,
                Instant.parse(instant),
                (m, t) -> {
                    ArrayList<EventI> ret = new ArrayList<>();
                    ret.add(new SwitchOnFan(t));
                    return ret;
                },
                (m, t) -> {});
    }

    protected static SimulationTestStep ScenarioFanSetHigh(String instant) {

        return new SimulationTestStep(
                FanUnitTesterModel.URI,
                Instant.parse(instant),
                (m, t) -> {
                    ArrayList<EventI> ret = new ArrayList<>();
                    ret.add(new SetHighFan(t));
                    return ret;
                },
                (m, t) -> {});
    }
    
    protected static SimulationTestStep ScenarioFanSetMedium(String instant) {

        return new SimulationTestStep(
                FanUnitTesterModel.URI,
                Instant.parse(instant),
                (m, t) -> {
                    ArrayList<EventI> ret = new ArrayList<>();
                    ret.add(new SetMediumFan(t));
                    return ret;
                },
                (m, t) -> {});
    }
    
    protected static SimulationTestStep ScenarioFanSetLow(String instant) {

        return new SimulationTestStep(
                FanUnitTesterModel.URI,
                Instant.parse(instant),
                (m, t) -> {
                    ArrayList<EventI> ret = new ArrayList<>();
                    ret.add(new SetLowFan(t));
                    return ret;
                },
                (m, t) -> {});
    }

    protected static SimulationTestStep ScenarioFanSwitchOff(String instant) {
        return new SimulationTestStep(
                FanUnitTesterModel.URI,
                Instant.parse(instant),
                (m, t) -> {
                    ArrayList<EventI> ret = new ArrayList<>();
                    ret.add(new SwitchOffFan(t));
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

        int timeIndex = 1;

        for (int k = 0; k < repetition; ++k) {

            // Switch ON
            testSteps.add(
                ScenarioFanSwitchOn(testInstantClassical(timeIndex++))
            );
            // Set HIGH
            testSteps.add(
                ScenarioFanSetHigh(testInstantClassical(timeIndex++))
            );
            // Set MEDIUM
            testSteps.add(
                ScenarioFanSetMedium(testInstantClassical(timeIndex++))
            );
            // Set LOW
            testSteps.add(
                ScenarioFanSetLow(testInstantClassical(timeIndex++))
            );
            // Switch OFF
            testSteps.add(
                ScenarioFanSwitchOff(testInstantClassical(timeIndex++))
            );
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
                    FanCoupledModel.URI,
                    START_TIME,
                    (ts, simParams) -> {
                        simParams.put(
                                ModelI.createRunParameterName(
                                        FanUnitTesterModel.URI,
                                        FanUnitTesterModel.TEST_SCENARIO_RP_NAME),
                                ts);
                    },
                    testScenariosClassical(REPETITION)
            );

    protected static SimulationTestStep[] testScenariosPriority() {
        ArrayList<SimulationTestStep> testSteps = new ArrayList<>();

        testSteps.add(ScenarioFanSwitchOn("2024-10-20T01:00:00.00Z"));
        testSteps.add(ScenarioFanSetHigh("2024-10-20T01:00:00.00Z"));
        testSteps.add(ScenarioFanSetMedium("2024-10-20T01:00:00.00Z"));
        testSteps.add(ScenarioFanSetLow("2024-10-20T01:00:00.00Z"));
        testSteps.add(ScenarioFanSwitchOff("2024-10-20T01:00:00.00Z"));

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
                    FanCoupledModel.URI,
                    START_TIME,
                    (ts, simParams) -> {
                        simParams.put(
                                ModelI.createRunParameterName(
                                        FanUnitTesterModel.URI,
                                        FanUnitTesterModel.TEST_SCENARIO_RP_NAME),
                                ts);
                    },
                    testScenariosPriority()
            );
}
