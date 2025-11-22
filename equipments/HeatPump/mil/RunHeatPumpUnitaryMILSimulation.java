package equipments.HeatPump.mil;

import equipments.HeatPump.HeatPump;
import equipments.HeatPump.mil.events.*;
import fr.sorbonne_u.components.hem2025.tests_utils.SimulationTestStep;
import fr.sorbonne_u.components.hem2025.tests_utils.TestScenario;
import fr.sorbonne_u.components.hem2025e2.equipments.heater.mil.ExternalTemperatureModel;
import fr.sorbonne_u.devs_simulation.architectures.Architecture;
import fr.sorbonne_u.devs_simulation.architectures.ArchitectureI;
import fr.sorbonne_u.devs_simulation.hioa.architectures.AtomicHIOA_Descriptor;
import fr.sorbonne_u.devs_simulation.hioa.architectures.CoupledHIOA_Descriptor;
import fr.sorbonne_u.devs_simulation.hioa.models.vars.VariableSink;
import fr.sorbonne_u.devs_simulation.hioa.models.vars.VariableSource;
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
import org.apache.commons.math3.random.RandomDataGenerator;

import java.time.Instant;
import java.util.*;

/**
 * The class <code>equipments.HeatPump.mil.RunHeatPumpUnitaryMILSimulation</code>.
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
public class RunHeatPumpUnitaryMILSimulation {

    public static void main(String[] args) {

        try {

            Map<String, AbstractAtomicModelDescriptor> atomicModelDescriptors =
                    new HashMap<>();

            // the heat pump's models simulate are all
            // atomic HIOA model hence we use an AtomicHIOA_Descriptor(s)
            atomicModelDescriptors.put(
                    HeatPumpElectricityModel.URI,
                    AtomicHIOA_Descriptor.create(
                            HeatPumpElectricityModel.class,
                            HeatPumpElectricityModel.URI,
                            HeatPumpSimulationConfigurationI.TIME_UNIT,
                            null));
            atomicModelDescriptors.put(
                    HeatPumpHeatingModel.URI,
                    AtomicHIOA_Descriptor.create(
                            HeatPumpHeatingModel.class,
                            HeatPumpHeatingModel.URI,
                            HeatPumpSimulationConfigurationI.TIME_UNIT,
                            null
                    ));
            atomicModelDescriptors.put(
                    ExternalTemperatureModel.URI,
                    AtomicHIOA_Descriptor.create(
                            ExternalTemperatureModel.class,
                            ExternalTemperatureModel.URI,
                            HeatPumpSimulationConfigurationI.TIME_UNIT,
                            null));
            // for atomic model, we use an AtomicModelDescriptor
            atomicModelDescriptors.put(
                    HeatPumpUnitTesterModel.URI,
                    AtomicModelDescriptor.create(
                            HeatPumpUnitTesterModel.class,
                            HeatPumpUnitTesterModel.URI,
                            HeatPumpSimulationConfigurationI.TIME_UNIT,
                            null));

            // map that will contain the coupled model descriptors to construct
            // the simulation architecture
            Map<String, CoupledModelDescriptor> coupledModelDescriptors =
                    new HashMap<>();

            // the set of submodels of the coupled model, given by their URIs
            Set<String> submodels = new HashSet<>();
            submodels.add(HeatPumpHeatingModel.URI);
            submodels.add(HeatPumpElectricityModel.URI);
            submodels.add(ExternalTemperatureModel.URI);
            submodels.add(HeatPumpUnitTesterModel.URI);

            // event exchanging connections between exporting and importing
            // models
            Map<EventSource, EventSink[]> connections =
                    new HashMap<>();

            connections.put(
                new EventSource(HeatPumpUnitTesterModel.URI,
                        SwitchOnEvent.class),
                    new EventSink[] {
                            new EventSink(HeatPumpElectricityModel.URI,
                                    SwitchOnEvent.class)
                    }
            );

            connections.put(
                    new EventSource(HeatPumpUnitTesterModel.URI,
                            SwitchOffEvent.class),
                    new EventSink[] {
                            new EventSink(HeatPumpElectricityModel.URI,
                                    SwitchOffEvent.class)
                    }
            );

            connections.put(
                    new EventSource(HeatPumpUnitTesterModel.URI,
                            SetPowerEvent.class),
                    new EventSink[] {
                            new EventSink(HeatPumpElectricityModel.URI,
                                    SetPowerEvent.class)
                    }
            );

            connections.put(
                    new EventSource(HeatPumpUnitTesterModel.URI,
                            StartHeatingEvent.class),
                    new EventSink[] {
                            new EventSink(HeatPumpElectricityModel.URI,
                                    StartHeatingEvent.class),
                            new EventSink(HeatPumpHeatingModel.URI,
                                    StartHeatingEvent.class)
                    }
            );

            connections.put(
                    new EventSource(HeatPumpUnitTesterModel.URI,
                            StopHeatingEvent.class),
                    new EventSink[] {
                            new EventSink(HeatPumpElectricityModel.URI,
                                    StopHeatingEvent.class),
                            new EventSink(HeatPumpHeatingModel.URI,
                                    StopHeatingEvent.class)
                    }
            );

            connections.put(
                    new EventSource(HeatPumpUnitTesterModel.URI,
                            StartCoolingEvent.class),
                    new EventSink[] {
                            new EventSink(HeatPumpElectricityModel.URI,
                                    StartCoolingEvent.class),
                            new EventSink(HeatPumpHeatingModel.URI,
                                    StartCoolingEvent.class)
                    }
            );

            connections.put(
                    new EventSource(HeatPumpUnitTesterModel.URI,
                            StopCoolingEvent.class),
                    new EventSink[] {
                            new EventSink(HeatPumpElectricityModel.URI,
                                    StopCoolingEvent.class),
                            new EventSink(HeatPumpHeatingModel.URI,
                                    StopCoolingEvent.class)
                    }
            );

            // variable bindings between exporting and importing models
            Map<VariableSource, VariableSink[]> bindings =
                    new HashMap<VariableSource,VariableSink[]>();

            bindings.put(new VariableSource("externalTemperature",
                    Double.class,
                    ExternalTemperatureModel.URI),
                    new VariableSink[] {
                            new VariableSink("externalTemperature",
                                    Double.class,
                                    HeatPumpHeatingModel.URI)
                    });

            bindings.put(new VariableSource("currentTemperaturePower",
                    Double.class,
                    HeatPumpElectricityModel.URI),
                    new VariableSink[]{
                            new VariableSink("currentTemperaturePower",
                                    Double.class,
                                    HeatPumpHeatingModel.URI)
                    });

            // coupled model descriptor
            coupledModelDescriptors.put(
                    HeatPumpCoupledModel.URI,
                    new CoupledHIOA_Descriptor(
                            HeatPumpCoupledModel.class,
                            HeatPumpCoupledModel.URI,
                            submodels,
                            null,
                            null,
                            connections,
                            null,
                            null,
                            null,
                            bindings));

            // simulation architecture
            ArchitectureI architecture =
                    new Architecture(
                            HeatPumpCoupledModel.URI,
                            atomicModelDescriptors,
                            coupledModelDescriptors,
                            HeatPumpSimulationConfigurationI.TIME_UNIT);

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
    protected static Instant START_INSTANT =
            Instant.parse("2025-10-20T01:00:00.00Z");
    /** the end instant used in the test scenarios.							*/
    protected static Instant	END_INSTANT =
            Instant.parse("2025-10-20T20:00:00.00Z");
    /** the start time in simulated time, corresponding to
     *  {@code START_INSTANT}.												*/

    protected static Time START_TIME =
            new Time(0.0, HeatPumpSimulationConfigurationI.TIME_UNIT);

    // The tests are repeated
    protected static final int REPETITION = 2;

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
            "               When the power is set to a valid wattage\n" +
            "               Then the power is equal to the set wattage\n" +
            "           Scenario: Heat pump starts heating\n" +
            "               Given the heat pump is on\n" +
            "               When the heat pump starts heating\n" +
            "               Then the heat pump is heating\n" +
            "           Scenario: Set power while heating\n" +
            "               Given the heat pump is heating\n" +
            "               When the power is set to a valid wattage\n" +
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
            "               When the power is set to a valid wattage\n" +
            "               Then the power is equal to the set wattage\n" +
            "           Scenario: Heat pump stops cooling\n" +
            "               Given the heat pump is cooling\n" +
            "               When the heat pump stops cooling\n" +
            "               Then the heat pump is not cooling\n" +
            "               And is still on\n" +
            "           Scenario: Heat pump switches off\n" +
            "               Given the heat pump is on\n" +
            "               When the heat pump is switched off\n" +
            "               Then the heat pump is off\n" +
            "           Scenario: The tests are repeated another time\n" +
            "               Given the heat pump has just been switched off\n" +
            "               When the tests are repeated\n" +
            "               Then the behaviour of the tests is still the same\n" +
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

    protected static SimulationTestStep ScenarioSetPower(String instant) {

        final double power =
                rg.nextUniform(HeatPump.MIN_REQUIRED_POWER_LEVEL.getData(),
                                HeatPump.MAX_POWER_LEVEL.getData());

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
     * @param repetition the number of times we repeat the simulation test will be repeated
     * @return array containing the step of the simulation
     */
    protected static SimulationTestStep[] testScenarios(int repetition) {
        ArrayList<SimulationTestStep> testSteps = new ArrayList<>();

        int i = 1;

        for (int k = 0; k < repetition; ++k) {
            testSteps.add(ScenarioSwitchOn(testInstant(i)));
            testSteps.add(ScenarioSetPower(testInstant(i+1)));
            testSteps.add(ScenarioStartHeating(testInstant(i+2)));
            testSteps.add(ScenarioSetPower(testInstant(i+3)));
            testSteps.add(ScenarioStopHeating(testInstant(i+4)));
            testSteps.add(ScenarioStartCooling(testInstant(i+5)));
            testSteps.add(ScenarioSetPower(testInstant(i+6)));
            testSteps.add(ScenarioStopCooling(testInstant(i+7)));
            testSteps.add(ScenarioSwitchOff(testInstant(i+8)));

            i += 9;
        }

        SimulationTestStep[] result = new SimulationTestStep[testSteps.size()];
        return testSteps.toArray(result);
    }

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
                                        HeatPumpUnitTesterModel.URI,
                                        HeatPumpUnitTesterModel.TEST_SCENARIO_RP_NAME),
                                ts);
                        se.setSimulationRunParameters(simParams);
                    },
                    testScenarios(REPETITION)
            );

}