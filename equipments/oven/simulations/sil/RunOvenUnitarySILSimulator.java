package equipments.oven.simulations.sil;

import equipments.oven.Oven.OvenMode;
import equipments.oven.simulations.*;
import equipments.oven.simulations.events.*;
import equipments.oven.simulations.events.DelayedStartOven.DelayValue;
import equipments.oven.simulations.events.SetModeOven.ModeValue;
import equipments.oven.simulations.events.SetTargetTemperatureOven.TargetTemperatureValue;
import fr.sorbonne_u.components.cyphy.utils.tests.SimulationTestStep;
import fr.sorbonne_u.components.cyphy.utils.tests.TestScenarioWithSimulation;
import fr.sorbonne_u.devs_simulation.architectures.ArchitectureI;
import fr.sorbonne_u.devs_simulation.architectures.RTArchitecture;
import fr.sorbonne_u.devs_simulation.hioa.architectures.RTAtomicHIOA_Descriptor;
import fr.sorbonne_u.devs_simulation.hioa.architectures.RTCoupledHIOA_Descriptor;
import fr.sorbonne_u.devs_simulation.hioa.models.vars.VariableSink;
import fr.sorbonne_u.devs_simulation.hioa.models.vars.VariableSource;
import fr.sorbonne_u.devs_simulation.models.architectures.*;
import fr.sorbonne_u.devs_simulation.models.events.*;
import fr.sorbonne_u.devs_simulation.models.interfaces.ModelI;
import fr.sorbonne_u.devs_simulation.models.time.Duration;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import fr.sorbonne_u.devs_simulation.simulators.SimulationEngine;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulationReportI;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI;

import java.time.Instant;
import java.util.*;

public class RunOvenUnitarySILSimulator {

    // ---------------------------------------------------------------------
    // Constants
    // ---------------------------------------------------------------------

    /** Acceleration factor for real-time SIL simulation */
    public static final double ACCELERATION_FACTOR = 3600.0;

    // ---------------------------------------------------------------------
    // Utility methods
    // ---------------------------------------------------------------------

    protected static void addSimpleConnection(
            Map<EventSource, EventSink[]> map,
            Class<? extends EventI> eventType,
            String sourceURI,
            String destURI)
    {
        EventSource source = new EventSource(sourceURI, eventType);
        EventSink sink = new EventSink(destURI, eventType);
        map.put(source, new EventSink[]{ sink });
    }

    protected static void addConnectionsAll(
            Map<EventSource, EventSink[]> map,
            Class<? extends EventI> eventType,
            String sourceURI)
    {
        EventSource source = new EventSource(sourceURI, eventType);

        EventSink sinkElectricity =
                new EventSink(OvenElectricityModel.URI, eventType);
        EventSink sinkTemperature =
                new EventSink(OvenTemperatureModel.URI, eventType);

        map.put(source, new EventSink[]{ sinkElectricity, sinkTemperature });
    }

    protected static void addBinding(
            Map<VariableSource, VariableSink[]> map,
            String name,
            Class<?> type,
            String exportingURI,
            String importingURI)
    {
        VariableSource source =
                new VariableSource(name, type, exportingURI);
        VariableSink sink =
                new VariableSink(name, type, importingURI);

        map.put(source, new VariableSink[]{ sink });
    }

    // ---------------------------------------------------------------------
    // Main
    // ---------------------------------------------------------------------

    public static void main(String[] args) {
        Time.setPrintPrecision(4);
        Duration.setPrintPrecision(4);

        try {
            // ------------------------------------------------------------
            // Atomic models
            // ------------------------------------------------------------

            Map<String, AbstractAtomicModelDescriptor> atomicModelDescriptors =
                    new HashMap<>();

            atomicModelDescriptors.put(
                    OvenElectricityModel.URI,
                    RTAtomicHIOA_Descriptor.create(
                            OvenElectricityModel.class,
                            OvenElectricityModel.URI,
                            OvenSimulationConfigurationI.TIME_UNIT,
                            null,
                            ACCELERATION_FACTOR));

            atomicModelDescriptors.put(
                    OvenTemperatureModel.URI,
                    RTAtomicHIOA_Descriptor.create(
                            OvenTemperatureModel.class,
                            OvenTemperatureModel.URI,
                            OvenSimulationConfigurationI.TIME_UNIT,
                            null,
                            ACCELERATION_FACTOR));

            atomicModelDescriptors.put(
                    OvenStateModel.URI,
                    RTAtomicModelDescriptor.create(
                            OvenStateModel.class,
                            OvenStateModel.URI,
                            OvenSimulationConfigurationI.TIME_UNIT,
                            null,
                            ACCELERATION_FACTOR));

            atomicModelDescriptors.put(
                    OvenUnitTesterModel.URI,
                    RTAtomicModelDescriptor.create(
                            OvenUnitTesterModel.class,
                            OvenUnitTesterModel.URI,
                            OvenSimulationConfigurationI.TIME_UNIT,
                            null,
                            ACCELERATION_FACTOR));

            // ------------------------------------------------------------
            // Submodels
            // ------------------------------------------------------------

            Set<String> submodels = new HashSet<>();
            submodels.add(OvenStateModel.URI);
            submodels.add(OvenElectricityModel.URI);
            submodels.add(OvenTemperatureModel.URI);
            submodels.add(OvenUnitTesterModel.URI);

            // ------------------------------------------------------------
            // Event connections
            // ------------------------------------------------------------

            Map<EventSource, EventSink[]> connections = new HashMap<>();

            // From state model
            addSimpleConnection(connections, SwitchOnOven.class,
                    OvenStateModel.URI, OvenElectricityModel.URI);

            addSimpleConnection(connections, SetPowerOven.class,
                    OvenStateModel.URI, OvenElectricityModel.URI);
            
            addSimpleConnection(connections,OpenDoorOven.class,
            		OvenStateModel.URI, OvenTemperatureModel.URI);
            		
            addSimpleConnection(connections,CloseDoorOven.class,
            		OvenStateModel.URI, OvenTemperatureModel.URI);
            
            addConnectionsAll(connections, SwitchOffOven.class,
                    OvenStateModel.URI);
            
            addConnectionsAll(connections, HeatOven.class,
                    OvenStateModel.URI);

            addConnectionsAll(connections, DoNotHeatOven.class,
                    OvenStateModel.URI);

            addSimpleConnection(connections, SetModeOven.class,
                    OvenStateModel.URI, OvenTemperatureModel.URI);

            // From unit tester
            addSimpleConnection(connections, SwitchOnOven.class,
                    OvenUnitTesterModel.URI, OvenStateModel.URI);

            addSimpleConnection(connections, SwitchOffOven.class,
                    OvenUnitTesterModel.URI, OvenStateModel.URI);

            addSimpleConnection(connections, HeatOven.class,
                    OvenUnitTesterModel.URI, OvenStateModel.URI);

            addSimpleConnection(connections, DoNotHeatOven.class,
                    OvenUnitTesterModel.URI, OvenStateModel.URI);

            addSimpleConnection(connections, OpenDoorOven.class,
                    OvenUnitTesterModel.URI, OvenStateModel.URI);

            addSimpleConnection(connections, CloseDoorOven.class,
                    OvenUnitTesterModel.URI, OvenStateModel.URI);

            addSimpleConnection(connections, SetModeOven.class,
                    OvenUnitTesterModel.URI, OvenStateModel.URI);

            addSimpleConnection(connections, SetPowerOven.class,
                    OvenUnitTesterModel.URI, OvenStateModel.URI);

            addSimpleConnection(connections, SetTargetTemperatureOven.class,
                    OvenUnitTesterModel.URI, OvenTemperatureModel.URI);
            
            addSimpleConnection(connections, DelayedStartOven.class,
                    OvenUnitTesterModel.URI, OvenStateModel.URI);

            addSimpleConnection(connections, CancelDelayedStartOven.class,
                    OvenUnitTesterModel.URI, OvenStateModel.URI);

            // ------------------------------------------------------------
            // Variable bindings
            // ------------------------------------------------------------

            Map<VariableSource, VariableSink[]> bindings = new HashMap<>();

            addBinding(bindings,
                    "currentHeatingPower",
                    Double.class,
                    OvenElectricityModel.URI,
                    OvenTemperatureModel.URI);

            addBinding(bindings,
                    "currentMode",
                    OvenMode.class,
                    OvenTemperatureModel.URI,
                    OvenElectricityModel.URI);

            // ------------------------------------------------------------
            // Coupled model
            // ------------------------------------------------------------

            Map<String, CoupledModelDescriptor> coupledModelDescriptors =
                    new HashMap<>();

            coupledModelDescriptors.put(
                    OvenCoupledModel.URI,
                    new RTCoupledHIOA_Descriptor(
                            OvenCoupledModel.class,
                            OvenCoupledModel.URI,
                            submodels,
                            null,
                            null,
                            connections,
                            null,
                            null,
                            null,
                            bindings,
                            ACCELERATION_FACTOR));

            ArchitectureI architecture =
                    new RTArchitecture(
                            OvenCoupledModel.URI,
                            atomicModelDescriptors,
                            coupledModelDescriptors,
                            OvenSimulationConfigurationI.TIME_UNIT);

            // ------------------------------------------------------------
            // Simulator
            // ------------------------------------------------------------

            SimulatorI se = architecture.constructSimulator();
            SimulationEngine.SIMULATION_STEP_SLEEP_TIME = 0L;

            Map<String, Object> simParams = new HashMap<>();
            CLASSICAL.addToRunParameters(simParams);
            se.setSimulationRunParameters(simParams);

            Time startTime = CLASSICAL.getStartTime();
            Duration d = CLASSICAL.getEndTime().subtract(startTime);

            long realTimeStart = System.currentTimeMillis() + 200;
            se.startRTSimulation(
                    realTimeStart,
                    startTime.getSimulatedTime(),
                    d.getSimulatedDuration());

            long executionDuration =
                    (long) (OvenSimulationConfigurationI.TIME_UNIT.toMillis(1)
                            * (d.getSimulatedDuration() / ACCELERATION_FACTOR));

            Thread.sleep(executionDuration + 2000L);

            SimulationReportI sr =
                    se.getSimulatedModel().getFinalReport();
            System.out.println(sr);

            System.exit(0);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    protected static final Instant START_INSTANT =
            Instant.parse("2025-10-20T01:00:00.00Z");
    protected static final Instant END_INSTANT =
            Instant.parse("2025-10-20T10:00:00.00Z");

    protected static final Time START_TIME =
            new Time(0.0, OvenSimulationConfigurationI.TIME_UNIT);
    
    protected static final String GHERKIN_SPEC =
            "------------------------------------\n" +
            "Classical\n\n" +
            "  Gherkin specification\n\n" +
            "    Feature: Oven operation (SIL)\n\n" +

            "      Scenario: Oven switched on\n" +
            "        Given an oven that is off\n" +
            "        When it is switched on\n" +
            "        Then it is on and not heating\n\n" +

            "      Scenario: Target temperature set to 250\n" +
            "        Given an oven that is on and not heating\n" +
            "        When a target temperature of 250 is set\n" +
            "        Then its mode is CUSTOM and target temperature is 250\n\n" +

            "      Scenario: Delayed start programmed\n" +
            "        Given an oven that is on and not heating\n" +
            "        When a delayed start of 1 hour is programmed\n" +
            "        Then the oven is in waiting state\n\n" +

            "      Scenario: Delayed start triggers heating\n" +
            "        Given an oven with a delayed start programmed\n" +
            "        When the delay expires\n" +
            "        Then the oven starts heating at max power level\n\n" +

            "      Scenario: Oven stops heating\n" +
            "        Given an oven that is heating\n" +
            "        When it is asked not to heat\n" +
            "        Then it is on but it stops heating\n\n" +

            "      Scenario: Delayed start programmed again\n" +
            "        Given an oven that is on and not heating\n" +
            "        When a delayed start of 2 hours is programmed\n" +
            "        Then the oven is in waiting state\n\n" +

            "      Scenario: Delayed start cancelled\n" +
            "        Given an oven with a delayed start programmed\n" +
            "        When the delayed start is cancelled before expiration\n" +
            "        Then the delayed start is cancelled and the oven is on\n\n" +

            "      Scenario: Mode set to GRILL\n" +
            "        Given an oven that is on\n" +
            "        When its mode is set to GRILL\n" +
            "        Then its target temperature is 220\n\n" +

            "      Scenario: Oven heats again\n" +
            "        Given an oven that is on, not heating and in GRILL mode\n" +
            "        When it is asked to heat\n" +
            "        Then it is on and it heats at max power level\n\n" +

            "      Scenario: Oven switched off\n" +
            "        Given an oven that is heating\n" +
            "        When it is switched off\n" +
            "        Then it is off\n\n" +

            "      Scenario: Oven door opened while off\n" +
            "        Given an oven that is off\n" +
            "        When its door is opened\n" +
            "        Then the oven cools faster\n\n" +

            "------------------------------------\n";


    protected static final String END_MESSAGE =
            "End Classical\n------------------------------------\n";
    
    protected static SimulationTestStep ScenarioSwitchOn(String instant) {
        return new SimulationTestStep(
                OvenUnitTesterModel.URI,
                Instant.parse(instant),
                (m, t) -> {
                    ArrayList<EventI> ret = new ArrayList<>();
                    ret.add(new SwitchOnOven(t));
                    return ret;
                },
                (m, t) -> {});
    }

    protected static SimulationTestStep ScenarioSwitchOff(String instant) {
        return new SimulationTestStep(
                OvenUnitTesterModel.URI,
                Instant.parse(instant),
                (m, t) -> {
                    ArrayList<EventI> ret = new ArrayList<>();
                    ret.add(new SwitchOffOven(t));
                    return ret;
                },
                (m, t) -> {});
    }
    
    protected static SimulationTestStep ScenarioStartHeating(String instant) {
        return new SimulationTestStep(
                OvenUnitTesterModel.URI,
                Instant.parse(instant),
                (m, t) -> {
                    ArrayList<EventI> ret = new ArrayList<>();
                    ret.add(new HeatOven(t));
                    return ret;
                },
                (m, t) -> {});
    }

    protected static SimulationTestStep ScenarioStopHeating(String instant) {
        return new SimulationTestStep(
                OvenUnitTesterModel.URI,
                Instant.parse(instant),
                (m, t) -> {
                    ArrayList<EventI> ret = new ArrayList<>();
                    ret.add(new DoNotHeatOven(t));
                    return ret;
                },
                (m, t) -> {});
    }
    
    protected static SimulationTestStep ScenarioDelayedStart(
            String instant, Duration delay)
    {
        return new SimulationTestStep(
                OvenUnitTesterModel.URI,
                Instant.parse(instant),
                (m, t) -> {
                    ArrayList<EventI> ret = new ArrayList<>();
                    ret.add(new DelayedStartOven(t, new DelayValue(delay)));
                    return ret;
                },
                (m, t) -> {});
    }

    protected static SimulationTestStep ScenarioCancelDelayedStart(String instant) {
        return new SimulationTestStep(
                OvenUnitTesterModel.URI,
                Instant.parse(instant),
                (m, t) -> {
                    ArrayList<EventI> ret = new ArrayList<>();
                    ret.add(new CancelDelayedStartOven(t));
                    return ret;
                },
                (m, t) -> {});
    }
    
    protected static SimulationTestStep ScenarioSetTargetTemperature(
            String instant, double temp)
    {
        return new SimulationTestStep(
                OvenUnitTesterModel.URI,
                Instant.parse(instant),
                (m, t) -> {
                    ArrayList<EventI> ret = new ArrayList<>();
                    ret.add(new SetTargetTemperatureOven(t, 
                    		new TargetTemperatureValue(temp)));
                    return ret;
                },
                (m, t) -> {});
    }
    
    protected static SimulationTestStep ScenarioSetMode(
            String instant, OvenMode mode)
    {
        return new SimulationTestStep(
                OvenUnitTesterModel.URI,
                Instant.parse(instant),
                (m, t) -> {
                    ArrayList<EventI> ret = new ArrayList<>();
                    ret.add(new SetModeOven(t, 
                    		new ModeValue(mode)));
                    return ret;
                },
                (m, t) -> {});
    }
    
    protected static SimulationTestStep ScenarioOpenDoor(String instant)
    {
    	return new SimulationTestStep(
                OvenUnitTesterModel.URI,
                Instant.parse(instant),
                (m, t) -> {
                    ArrayList<EventI> ret = new ArrayList<>();
                    ret.add(new OpenDoorOven(t));
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
    
    protected static SimulationTestStep[] testScenarios() {
        ArrayList<SimulationTestStep> testSteps = new ArrayList<>();

        // 00h – switch on
        testSteps.add(ScenarioSwitchOn(testInstant(1)));

        // 01h – set target temperature (CUSTOM)
        testSteps.add(
            ScenarioSetTargetTemperature(testInstant(2), 250.0)
        );

        // 02h – delayed start 1h
        testSteps.add(
            ScenarioDelayedStart(
                testInstant(3),
                new Duration(1.0, OvenSimulationConfigurationI.TIME_UNIT)
            )
        );

        // 04h – stop heating after delayed start triggered
        testSteps.add(ScenarioStopHeating(testInstant(5)));

        // 05h – delayed start 2h
        testSteps.add(
            ScenarioDelayedStart(
                testInstant(6),
                new Duration(2.0, OvenSimulationConfigurationI.TIME_UNIT)
            )
        );

        // 06h – cancel delayed start
        testSteps.add(ScenarioCancelDelayedStart(testInstant(7)));

        // 07h – set mode GRILL
        testSteps.add(
            ScenarioSetMode(testInstant(8), OvenMode.GRILL)
        );

        // 08h – heat again
        testSteps.add(ScenarioStartHeating(testInstant(9)));

        // 09h – switch off
        testSteps.add(ScenarioSwitchOff(testInstant(10)));

        // 10h – open door while off
        testSteps.add(ScenarioOpenDoor(testInstant(11)));

        SimulationTestStep[] result =
                new SimulationTestStep[testSteps.size()];
        return testSteps.toArray(result);
    }


    
    protected final static TestScenarioWithSimulation CLASSICAL =
            new TestScenarioWithSimulation(
                    GHERKIN_SPEC,
                    END_MESSAGE,
                    "clock_uri",
                    START_INSTANT,
                    END_INSTANT,
                    OvenCoupledModel.URI,
                    START_TIME,
                    (ts, simParams) -> {
                        simParams.put(
                                ModelI.createRunParameterName(
                                        OvenUnitTesterModel.URI,
                                        OvenUnitTesterModel.TEST_SCENARIO_RP_NAME),
                                ts);
                    },
                    testScenarios()
            );
 
}
