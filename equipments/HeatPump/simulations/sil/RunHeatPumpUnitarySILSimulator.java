package equipments.HeatPump.simulations.sil;

import equipments.HeatPump.HeatPump;
import equipments.HeatPump.simulations.*;
import equipments.HeatPump.simulations.events.*;
import equipments.HeatPump.simulations.interfaces.HeatPumpSimulationConfigurationI;
import fr.sorbonne_u.components.cyphy.utils.tests.SimulationTestStep;
import fr.sorbonne_u.components.cyphy.utils.tests.TestScenarioWithSimulation;
import fr.sorbonne_u.components.hem2025e3.equipments.heater.sil.ExternalTemperatureSILModel;
import fr.sorbonne_u.devs_simulation.architectures.ArchitectureI;
import fr.sorbonne_u.devs_simulation.architectures.RTArchitecture;
import fr.sorbonne_u.devs_simulation.hioa.architectures.RTAtomicHIOA_Descriptor;
import fr.sorbonne_u.devs_simulation.hioa.architectures.RTCoupledHIOA_Descriptor;
import fr.sorbonne_u.devs_simulation.hioa.models.vars.VariableSink;
import fr.sorbonne_u.devs_simulation.hioa.models.vars.VariableSource;
import fr.sorbonne_u.devs_simulation.models.architectures.*;
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
 * The class <code>equipments.HeatPump.sil.RunHeatPumpUnitarySILSimulation</code>.
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
public class RunHeatPumpUnitarySILSimulator {

    // -------------------------------------------------------------------------
    // Constants
    // -------------------------------------------------------------------------

    /** the acceleration factor used in the real time MIL simulations.	 	*/
    public static final double		ACCELERATION_FACTOR = 3600.0;

    protected static void add_connections_all(
            Map<EventSource, EventSink[]> map,
            Class <? extends EventI> eventType,
            String source_uri) {
        final EventSource source =
                new EventSource(source_uri, eventType);
        final EventSink sink_electricity =
                new EventSink(HeatPumpElectricityModel.URI, eventType);
        final EventSink sink_heating =
                new EventSink(HeatPumpHeatingModel.URI, eventType);

        map.put(source, new EventSink[] { sink_electricity, sink_heating });
    }

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

    public static void add_binding(
            Map<VariableSource, VariableSink[]> map,
            String name,
            Class<?> type,
            String exportingURI,
            String importingURI
    ) {
        final VariableSource source = new VariableSource(name, type, exportingURI);
        final VariableSink sink = new VariableSink(name, type, importingURI);

        map.put(source, new VariableSink[]{ sink });
    }

    // -------------------------------------------------------------------------
    // Methods
    // -------------------------------------------------------------------------

    public static void	main(String[] args) {
        Time.setPrintPrecision(4);
        Duration.setPrintPrecision(4);

        try {

            Map<String, AbstractAtomicModelDescriptor> atomicModelDescriptors =
                    new HashMap<>();

            atomicModelDescriptors.put(
                    HeatPumpElectricityModel.URI,
                    RTAtomicHIOA_Descriptor.create(
                            HeatPumpElectricityModel.class,
                            HeatPumpElectricityModel.URI,
                            HeatPumpSimulationConfigurationI.TIME_UNIT,
                            null,
                            ACCELERATION_FACTOR));

            atomicModelDescriptors.put(
                    HeatPumpHeatingModel.URI,
                    RTAtomicHIOA_Descriptor.create(
                            HeatPumpHeatingModel.class,
                            HeatPumpHeatingModel.URI,
                            HeatPumpSimulationConfigurationI.TIME_UNIT,
                            null,
                            ACCELERATION_FACTOR
                    ));
            atomicModelDescriptors.put(
                    ExternalTemperatureSILModel.URI,
                    RTAtomicHIOA_Descriptor.create(
                            ExternalTemperatureSILModel.class,
                            ExternalTemperatureSILModel.URI,
                            HeatPumpSimulationConfigurationI.TIME_UNIT,
                            null,
                            ACCELERATION_FACTOR));

            atomicModelDescriptors.put(
                    HeatPumpStateModel.URI,
                    RTAtomicModelDescriptor.create(
                            HeatPumpStateModel.class,
                            HeatPumpStateModel.URI,
                            HeatPumpSimulationConfigurationI.TIME_UNIT,
                            null,
                            ACCELERATION_FACTOR));

            atomicModelDescriptors.put(
                    HeatPumpUnitTesterModel.URI,
                    RTAtomicModelDescriptor.create(
                            HeatPumpUnitTesterModel.class,
                            HeatPumpUnitTesterModel.URI,
                            HeatPumpSimulationConfigurationI.TIME_UNIT,
                            null,
                            ACCELERATION_FACTOR));

            Set<String> submodels = new HashSet<String>();
            submodels.add(HeatPumpStateModel.URI);
            submodels.add(HeatPumpElectricityModel.URI);
            submodels.add(HeatPumpHeatingModel.URI);
            submodels.add(ExternalTemperatureSILModel.URI);
            submodels.add(HeatPumpUnitTesterModel.URI);

            Map<EventSource, EventSink[]> connections =
                    new HashMap<>();

            add_simple_connection(connections, SwitchOnEvent.class, HeatPumpStateModel.URI, HeatPumpElectricityModel.URI);
            add_simple_connection(connections, SwitchOffEvent.class, HeatPumpStateModel.URI, HeatPumpElectricityModel.URI);
            add_simple_connection(connections, SetPowerEvent.class, HeatPumpStateModel.URI, HeatPumpElectricityModel.URI);
            add_connections_all(connections, StartHeatingEvent.class, HeatPumpStateModel.URI);
            add_connections_all(connections, StopHeatingEvent.class, HeatPumpStateModel.URI);
            add_connections_all(connections, StartCoolingEvent.class, HeatPumpStateModel.URI);
            add_connections_all(connections, StopCoolingEvent.class, HeatPumpStateModel.URI);

            add_simple_connection(
                    connections, SwitchOnEvent.class,
                    HeatPumpUnitTesterModel.URI, HeatPumpStateModel.URI
            );
            add_simple_connection(
                    connections, SwitchOffEvent.class,
                    HeatPumpUnitTesterModel.URI, HeatPumpStateModel.URI
            );
            add_simple_connection(
                    connections, SetPowerEvent.class,
                    HeatPumpUnitTesterModel.URI, HeatPumpStateModel.URI
            );
            add_simple_connection(
                    connections, StartHeatingEvent.class,
                    HeatPumpUnitTesterModel.URI, HeatPumpStateModel.URI
            );
            add_simple_connection(
                    connections, StopHeatingEvent.class,
                    HeatPumpUnitTesterModel.URI, HeatPumpStateModel.URI
            );
            add_simple_connection(
                    connections, StartCoolingEvent.class,
                    HeatPumpUnitTesterModel.URI, HeatPumpStateModel.URI
            );
            add_simple_connection(
                    connections, StopCoolingEvent.class,
                    HeatPumpUnitTesterModel.URI, HeatPumpStateModel.URI
            );

            Map<VariableSource, VariableSink[]> bindings =
                    new HashMap<>();

            add_binding(bindings,
                    "externalTemperature", Double.class,
                    ExternalTemperatureSILModel.URI, HeatPumpHeatingModel.URI);
            add_binding(bindings,
                    "currentTemperaturePower", Double.class,
                    HeatPumpElectricityModel.URI, HeatPumpHeatingModel.URI
                    );

            Map<String, CoupledModelDescriptor> coupledModelDescriptors =
                    new HashMap<>();

            coupledModelDescriptors.put(
                    HeatPumpCoupledModel.URI,
                    new RTCoupledHIOA_Descriptor(
                            HeatPumpCoupledModel.class,
                            HeatPumpCoupledModel.URI,
                            submodels,
                            null,
                            null,
                            connections,
                            null,
                            null,
                            null,
                            bindings,
                            ACCELERATION_FACTOR
                    ));

            ArchitectureI architecture =
                    new RTArchitecture(
                            HeatPumpCoupledModel.URI,
                            atomicModelDescriptors,
                            coupledModelDescriptors,
                            HeatPumpSimulationConfigurationI.TIME_UNIT);

            // create the simulator from the simulation architecture
            SimulatorI se = architecture.constructSimulator();
            // this add additional time at each simulation step in
            // standard simulations (useful when debugging)
            SimulationEngine.SIMULATION_STEP_SLEEP_TIME = 0L;

            Map<String, Object> classicalRunParameters = new HashMap<>();
            CLASSICAL.addToRunParameters(classicalRunParameters);
            se.setSimulationRunParameters(classicalRunParameters);
            Time startTime = CLASSICAL.getStartTime();
            Duration d = CLASSICAL.getEndTime().subtract(startTime);
            long realTimeStart = System.currentTimeMillis() + 200;
            se.startRTSimulation(realTimeStart, startTime.getSimulatedTime(), d.getSimulatedDuration());
            long executionDuration =
                    new Double(
                            HeatPumpSimulationConfigurationI.TIME_UNIT.toMillis(1)
                                    * (d.getSimulatedDuration()/ACCELERATION_FACTOR)).
                            longValue();
            Thread.sleep(executionDuration + 2000L);
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
    protected static Instant START_INSTANT =
            Instant.parse("2025-10-20T01:00:00.00Z");
    /** the end instant used in the test scenarios.							*/
    protected static Instant	END_INSTANT =
            Instant.parse("2025-10-20T14:00:00.00Z");
    /** the start time in simulated time, corresponding to
     *  {@code START_INSTANT}.												*/

    protected static Time START_TIME =
            new Time(0.0, HeatPumpSimulationConfigurationI.TIME_UNIT);

    protected static final RandomDataGenerator rg = new RandomDataGenerator();

    protected static final String GHERKIN_SPEC = "------------------------------------\n" +
            "Classical\n" +
            "   Gherkin specification\n" +
            "       Feature: Heat pump operation\n" +
            "           Scenario: Heat pump switched on\n" +
            "               Given the heat pump is off\n" +
            "               When the heat pump is switched on\n" +
            "               Then the heat pump is on\n" +
            "           Scenario: Set power while on\n" +
            "               Given the heat pump is on\n" +
            "               When the power is set to the maximum wattage\n" +
            "               Then the power is equal to the maximum wattage\n" +
            "           Scenario: Heat pump starts heating\n" +
            "               Given the heat pump is on\n" +
            "               When the heat pump starts heating\n" +
            "               Then the heat pump is heating\n" +
            "           Scenario: Set power while heating\n" +
            "               Given the heat pump is heating\n" +
            "               When the power is set to a valid wattage that is not the maximum\n" +
            "               Then the power is equal to the set wattage\n" +
            "           Scenario: Heat pump stops heating\n" +
            "               Given the heat pump is heating\n" +
            "               When the heat pump stops heating\n" +
            "               Then the heat pump is not heating\n" +
            "               And is still on\n" +
            "           Scenario: Heat pump starts cooling\n" +
            "               Given the heat pump is on\n" +
            "               When the heat pump starts cooling\n" +
            "               Then the heat pump is cooling\n" +
            "           Scenario: Set power while cooling\n" +
            "               Given the heat pump is cooling\n" +
            "               When the power is set to the maximum wattage\n" +
            "               Then the power is equal to the maximum wattage\n" +
            "           Scenario: Heat pump stops cooling\n" +
            "               Given the heat pump is cooling\n" +
            "               When the heat pump stops cooling\n" +
            "               Then the heat pump is not cooling\n" +
            "               And is still on\n" +
            "           Scenario: Heat pump switches off\n" +
            "               Given the heat pump is on\n" +
            "               When the heat pump is switched off\n" +
            "               Then the heat pump is off\n" +
            "------------------------------------\n";

    protected static final String END_MESSAGE =
            "End Classical\n------------------------------------\n";

    protected static SimulationTestStep ScenarioSwitchOn(String instant) {
        return new SimulationTestStep(
                HeatPumpUnitTesterModel.URI,
                Instant.parse(instant),
                (m, t) -> {
                    ArrayList<EventI> ret = new ArrayList<>();
                    ret.add(new SwitchOnEvent(t));
                    return ret;
                },
                (m, t) -> {});
    }

    protected static SimulationTestStep ScenarioSwitchOff(String instant) {
        return new SimulationTestStep(
                HeatPumpUnitTesterModel.URI,
                Instant.parse(instant),
                (m, t) -> {
                    ArrayList<EventI> ret = new ArrayList<>();
                    ret.add(new SwitchOffEvent(t));
                    return ret;
                },
                (m, t) -> {});
    }

    protected static SimulationTestStep ScenarioStartHeating(String instant) {
        return new SimulationTestStep(
                HeatPumpUnitTesterModel.URI,
                Instant.parse(instant),
                (m, t) -> {
                    ArrayList<EventI> ret = new ArrayList<>();
                    ret.add(new StartHeatingEvent(t));
                    return ret;
                },
                (m, t) -> {});
    }

    protected static SimulationTestStep ScenarioStopHeating(String instant) {
        return new SimulationTestStep(
                HeatPumpUnitTesterModel.URI,
                Instant.parse(instant),
                (m, t) -> {
                    ArrayList<EventI> ret = new ArrayList<>();
                    ret.add(new StopHeatingEvent(t));
                    return ret;
                },
                (m, t) -> {});
    }

    protected static SimulationTestStep ScenarioStartCooling(String instant) {
        return new SimulationTestStep(
                HeatPumpUnitTesterModel.URI,
                Instant.parse(instant),
                (m, t) -> {
                    ArrayList<EventI> ret = new ArrayList<>();
                    ret.add(new StartCoolingEvent(t));
                    return ret;
                },
                (m, t) -> {});
    }

    protected static SimulationTestStep ScenarioStopCooling(String instant) {
        return new SimulationTestStep(
                HeatPumpUnitTesterModel.URI,
                Instant.parse(instant),
                (m, t) -> {
                    ArrayList<EventI> ret = new ArrayList<>();
                    ret.add(new StopCoolingEvent(t));
                    return ret;
                },
                (m, t) -> {});
    }

    protected static SimulationTestStep ScenarioSetPower(String instant, double power) {

        return new SimulationTestStep(
                HeatPumpUnitTesterModel.URI,
                Instant.parse(instant),
                (m, t) -> {
                    ArrayList<EventI> ret = new ArrayList<>();
                    ret.add(new SetPowerEvent(t, new HeatPumpPowerValue(power)));
                    return ret;
                },
                (m, t) -> {});
    }

    protected static String testInstant(int i) {
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
     * @return array containing the step of the simulation
     */
    protected static SimulationTestStep[] testScenarios() {
        ArrayList<SimulationTestStep> testSteps = new ArrayList<>();

        final double power_hour2 = HeatPump.MAX_POWER_LEVEL.getData();
        final double power_hour4 = 50.;
        final double power_hour9 = HeatPump.MAX_POWER_LEVEL.getData();

        testSteps.add(ScenarioSwitchOn(testInstant(1)));
        testSteps.add(ScenarioSetPower(testInstant(2), power_hour2));
        testSteps.add(ScenarioStartHeating(testInstant(3)));
        testSteps.add(ScenarioSetPower(testInstant(4), power_hour4));
        testSteps.add(ScenarioStopHeating(testInstant(7)));
        testSteps.add(ScenarioStartCooling(testInstant(10)));
        testSteps.add(ScenarioSetPower(testInstant(11), power_hour9));
        testSteps.add(ScenarioStopCooling(testInstant(12)));
        testSteps.add(ScenarioSwitchOff(testInstant(13)));

        SimulationTestStep[] result = new SimulationTestStep[testSteps.size()];
        return testSteps.toArray(result);
    }

    protected final static TestScenarioWithSimulation CLASSICAL =
            new TestScenarioWithSimulation(
                    GHERKIN_SPEC,
                    END_MESSAGE,
                    "clock_uri",
                    START_INSTANT,
                    END_INSTANT,
                    HeatPumpCoupledModel.URI,
                    START_TIME,
                    (ts, simParams) -> {
                        simParams.put(
                                ModelI.createRunParameterName(
                                        HeatPumpUnitTesterModel.URI,
                                        HeatPumpUnitTesterModel.TEST_SCENARIO_RP_NAME),
                                ts);
                    },
                    testScenarios()
            );

}
