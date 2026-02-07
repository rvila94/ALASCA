package equipments.oven.simulations.mil;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import equipments.oven.Oven.OvenMode;
import equipments.oven.simulations.OvenCoupledModel;
import equipments.oven.simulations.OvenElectricityModel;
import equipments.oven.simulations.OvenSimulationConfigurationI;
import equipments.oven.simulations.OvenTemperatureModel;
import equipments.oven.simulations.OvenUnitTesterModel;
import equipments.oven.simulations.events.CloseDoorOven;
import equipments.oven.simulations.events.DoNotHeatOven;
import equipments.oven.simulations.events.HeatOven;
import equipments.oven.simulations.events.OpenDoorOven;
import equipments.oven.simulations.events.SetModeOven;
import equipments.oven.simulations.events.SetPowerOven;
import equipments.oven.simulations.events.SetTargetTemperatureOven;
import equipments.oven.simulations.events.SwitchOffOven;
import equipments.oven.simulations.events.SwitchOnOven;
import equipments.oven.simulations.events.SetModeOven.ModeValue;
import equipments.oven.simulations.events.SetTargetTemperatureOven.TargetTemperatureValue;

import java.time.Instant;
import java.util.ArrayList;
import fr.sorbonne_u.components.cyphy.utils.tests.SimulationTestStep;
import fr.sorbonne_u.components.cyphy.utils.tests.TestScenarioWithSimulation;
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
import fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI;
import fr.sorbonne_u.exceptions.VerboseException;

// -----------------------------------------------------------------------------
/**
 * The class <code>RunOvenUnitarySimulation</code> creates a simulator
 * for the Oven and then runs a typical simulation.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p>
 * The simulation architecture for the Oven contains four atomic models
 * composed under a coupled model:
 * </p>
 * <p><img src="../../../../../../../../images/hem-2025-e2/OvenUnitTestArchitecture.png"/></p>
 * <p>
 * The {@code OvenUnitTesterModel} emits events corresponding to actions of
 * a user mainly towards the {@code OvenElectricityModel} keeping track of
 * the state of the Oven and its power consumption in an exported variable
 * {@code currentIntensity} not used here. The @code OvenTemperatureModel} 
 * simulates the oven internal temperature.
 * Internal temperature depends upon the fact that the Oven actually heats or
 * not and with which power, relevant events that changes the status of the
 * Oven are propagated to the {@code OvenTemperatureModel}, which also
 * imports the variable {@code currentHeatingPower} from the
 * {@code OvenElectricityModel} as it influences the quickness of the
 * temperature raise when heating.
 * </p>
 * <p>
 * The code of the {@code main} method shows how to use simulation model
 * descriptors to create the description of a simulation architecture and then
 * create an instance of this architecture by instantiating and connecting the
 * models. Note how models are described by atomic model descriptors and coupled
 * model descriptors and then the connections between coupled models and their
 * submodels as well as exported events and variables to imported ones are
 * described by different maps. In this example, only connections of events and
 * bindings of variables between models within this architecture are necessary,
 * but when creating coupled models, they can also import and export events and
 * variables consumed and produced by their submodels.
 * </p>
 * <p>
 * The architecture object is the root of this description and it provides
 * the method {@code constructSimulator} that instantiate the models and
 * connect them. This method returns the reference on the simulator attached
 * to the root coupled model in the architecture instance, which is then used
 * to perform simulation runs by calling the method
 * {@code doStandAloneSimulation}.
 * </p>
 * <p>
 * The descriptors and maps can be viewed as kinds of nodes in the abstract
 * syntax tree of an architectural language that does not have a concrete
 * syntax yet.
 * </p>
 * 
 * <p><strong>Implementation Invariants</strong></p>
 * 
 * <pre>
 * invariant	{@code true}	// no more invariant
 * </pre>
 * 
 * <p><strong>Invariants</strong></p>
 * 
 * <pre>
 * invariant	{@code true}	// no more invariant
 * </pre>
 * 
 * <p>Created on : 2025-11-13</p>
 * 
 * @author	<a href="mailto:Rodrigo.Vila@etu.sorbonne-universite.fr">Rodrigo Vila</a>
 * @author	<a href="mailto:Damien.Ribeiro@etu.sorbonne-universite.fr">Damien Ribeiro</a>
 */
public class			RunOvenUnitaryMILSimulation
{
	// -------------------------------------------------------------------------
	// Invariants
	// -------------------------------------------------------------------------

	/**
	 * return true if the static invariants are observed, false otherwise.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @return	true if the invariants are observed, false otherwise.
	 */
	public static boolean	staticInvariants()
	{
		boolean ret = true;
		ret &= OvenSimulationConfigurationI.staticInvariants();
		return ret;
	}

	// -------------------------------------------------------------------------
	// Methods
	// -------------------------------------------------------------------------

	public static void main(String[] args)
	{
		staticInvariants();

		try {
			// map that will contain the atomic model descriptors to construct
			// the simulation architecture
			Map<String,AbstractAtomicModelDescriptor> atomicModelDescriptors =
															new HashMap<>();

			// the Oven models simulating its electricity consumption, its
			// temperatures and the external temperature are atomic HIOA models
			// hence we use an AtomicHIOA_Descriptor(s)
			atomicModelDescriptors.put(
					OvenElectricityModel.URI,
					AtomicHIOA_Descriptor.create(
							OvenElectricityModel.class,
							OvenElectricityModel.URI,
							OvenSimulationConfigurationI.TIME_UNIT,
							null));
			atomicModelDescriptors.put(
					OvenTemperatureModel.URI,
					AtomicHIOA_Descriptor.create(
							OvenTemperatureModel.class,
							OvenTemperatureModel.URI,
							OvenSimulationConfigurationI.TIME_UNIT,
							null));
			// the Oven unit tester model only exchanges event, an
			// atomic model hence we use an AtomicModelDescriptor
			atomicModelDescriptors.put(
					OvenUnitTesterModel.URI,
					AtomicModelDescriptor.create(
							OvenUnitTesterModel.class,
							OvenUnitTesterModel.URI,
							OvenSimulationConfigurationI.TIME_UNIT,
							null));

			// map that will contain the coupled model descriptors to construct
			// the simulation architecture
			Map<String,CoupledModelDescriptor> coupledModelDescriptors =
																new HashMap<>();

			// the set of submodels of the coupled model, given by their URIs
			Set<String> submodels = new HashSet<String>();
			submodels.add(OvenElectricityModel.URI);
			submodels.add(OvenTemperatureModel.URI);
			submodels.add(OvenUnitTesterModel.URI);
			
			// event exchanging connections between exporting and importing
			// models
			Map<EventSource,EventSink[]> connections =
										new HashMap<EventSource,EventSink[]>();

			connections.put(
					new EventSource(OvenUnitTesterModel.URI,
									SetPowerOven.class),
					new EventSink[] {
							new EventSink(OvenElectricityModel.URI,
										  SetPowerOven.class)
					});
			connections.put(
					new EventSource(OvenUnitTesterModel.URI,
									SwitchOnOven.class),
					new EventSink[] {
							new EventSink(OvenElectricityModel.URI,
										  SwitchOnOven.class)
					});
			connections.put(
					new EventSource(OvenUnitTesterModel.URI,
									SwitchOffOven.class),
					new EventSink[] {
							new EventSink(OvenElectricityModel.URI,
										  SwitchOffOven.class),
							new EventSink(OvenTemperatureModel.URI,
										  SwitchOffOven.class)
					});
			connections.put(
					new EventSource(OvenUnitTesterModel.URI, HeatOven.class),
					new EventSink[] {
							new EventSink(OvenElectricityModel.URI,
										  HeatOven.class),
							new EventSink(OvenTemperatureModel.URI,
										  HeatOven.class)
					});
			connections.put(
					new EventSource(OvenUnitTesterModel.URI, DoNotHeatOven.class),
					new EventSink[] {
							new EventSink(OvenElectricityModel.URI,
										  DoNotHeatOven.class),
							new EventSink(OvenTemperatureModel.URI,
										  DoNotHeatOven.class)
					});
			connections.put(
			        new EventSource(OvenUnitTesterModel.URI, SetModeOven.class),
			        new EventSink[]{
			                new EventSink(OvenTemperatureModel.URI, 
			                				SetModeOven.class)
			        });

			connections.put(
			        new EventSource(OvenUnitTesterModel.URI, SetTargetTemperatureOven.class),
			        new EventSink[]{
			                new EventSink(OvenTemperatureModel.URI, 
			                				SetTargetTemperatureOven.class)
			        });
			
			connections.put(
			        new EventSource(OvenUnitTesterModel.URI, OpenDoorOven.class),
			        new EventSink[]{
			                new EventSink(OvenTemperatureModel.URI, 
			                				OpenDoorOven.class)
			        });
			connections.put(
			        new EventSource(OvenUnitTesterModel.URI, CloseDoorOven.class),
			        new EventSink[]{
			                new EventSink(OvenTemperatureModel.URI, 
			                				CloseDoorOven.class)
			        });

			// variable bindings between exporting and importing models
			Map<VariableSource,VariableSink[]> bindings =
								new HashMap<VariableSource,VariableSink[]>();

			bindings.put(new VariableSource("currentHeatingPower",
											Double.class,
											OvenElectricityModel.URI),
						 new VariableSink[] {
								 new VariableSink("currentHeatingPower",
										 		  Double.class,
										 		  OvenTemperatureModel.URI)
						 });
			bindings.put(
				    new VariableSource("currentMode",
				            			OvenMode.class,
				            			OvenTemperatureModel.URI),
				    new VariableSink[]{
				    			new VariableSink("currentMode",
				    							OvenMode.class,
				    							OvenElectricityModel.URI)
				    });

			// coupled model descriptor
			coupledModelDescriptors.put(
					OvenCoupledModel.URI,
					new CoupledHIOA_Descriptor(
							OvenCoupledModel.class,
							OvenCoupledModel.URI,
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
							OvenCoupledModel.URI,
							atomicModelDescriptors,
							coupledModelDescriptors,
							OvenSimulationConfigurationI.TIME_UNIT);

			// create the simulator from the simulation architecture
			SimulatorI se = architecture.constructSimulator();
			// this add additional time at each simulation step in
			// standard simulations (useful when debugging)
			SimulationEngine.SIMULATION_STEP_SLEEP_TIME = 0L;

			// run a CLASSICAL test scenario
			TestScenarioWithSimulation classical = classical();
			Map<String, Object> classicalRunParameters =
												new HashMap<String, Object>();
			classical.addToRunParameters(classicalRunParameters);
			se.setSimulationRunParameters(classicalRunParameters);
			Time startTime = classical.getStartTime();
			Duration d = classical.getEndTime().subtract(startTime);
			se.doStandAloneSimulation(startTime.getSimulatedTime(),
									  d.getSimulatedDuration());
			System.exit(0);
		} catch (Exception e) {
			throw new RuntimeException(e) ;
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
	protected static Time		START_TIME = new Time(0.0, TimeUnit.HOURS);

	/** standard test scenario, see Gherkin specification.				 	*/
	protected static TestScenarioWithSimulation	classical() throws VerboseException
	{	    
		return new TestScenarioWithSimulation(
		        "-----------------------------------------------------\n" +
		        "Classical\n\n" +
		        "  Gherkin specification\n\n" +
		        "    Feature: Oven operation\n\n" +
		        "      Scenario: Oven switched on\n" +
		        "        Given a Oven that is off\n" +
		        "        When it is switched on\n" +
		        "        Then it is on but not heating\n" +
		        "      Scenario: Target temperature set to 50\n" +
		        "        Given a Oven that is on and not heating\n" +
		        "        When a target temperature of 50 is set\n" +
		        "        Then its mode is CUSTOM and target temperature is 50\n" +
		        "      Scenario: Oven heats\n" +
		        "        Given a Oven that is on, on CUSTOM mode and not heating\n" +
		        "        When it is asked to heat\n" +
		        "        Then it is on and it heats at max power level\n" +
		        "      Scenario: Oven stops heating\n" +
		        "        Given a Oven that is heating\n" +
		        "        When it is asked not to heat\n" +
		        "        Then it is on but it stops heating\n" +
		        "      Scenario: Mode set to DEFROST\n" +
		        "        Given a Oven that is on\n" +
		        "        When its mode is set to DEFROST\n" +
		        "        Then its target temperature is 50\n" +
		        "      Scenario: Oven heats again\n" +
		        "        Given a Oven that is on and not heating\n" +
		        "        When it is asked to heat\n" +
		        "        Then it is on and it heats at 500 power level\n" +
		        "      Scenario: Oven stops heating\n" +
		        "        Given a Oven that is heating\n" +
		        "        When it is asked not to heat\n" +
		        "        Then it is on but it stops heating\n" +
		        "      Scenario: Mode set to GRILL\n" +
		        "        Given a Oven that is on\n" +
		        "        When its mode is set to GRILL\n" +
		        "        Then its target temperature is 220\n" +
		        "      Scenario: Oven heats again\n" +
		        "        Given a Oven that is on and not heating\n" +
		        "        When it is asked to heat\n" +
		        "        Then it is on and it heats at max power level\n" +
		        "      Scenario: Oven set a different power level\n" +
		        "        Given a Oven that is heating\n" +
		        "        When it is set to a new power level\n" +
		        "        Then it is on and it heats at the new power level\n" +
		        "      Scenario: Oven switched off\n" +
		        "        Given a Oven that is on\n" +
		        "        When it is switched off\n" +
		        "        Then it is off\n" +
		        "	   Scenario: Oven door opened after switch off\r\n" +
		        "        Given a Oven that is off\r\n" +
		        "        When its door is opened\r\n" +
		        "        Then the oven cools faster\n" +
		        "-----------------------------------------------------\n",
		        "\n-----------------------------------------------------\n" +
		        "End Classical\n" +
		        "-----------------------------------------------------",
		        "fake-clock-URI",	// for simulation only test scenario, no clock needed
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
		        new SimulationTestStep[] {
		            // Switch on
		            new SimulationTestStep(
		                OvenUnitTesterModel.URI,
		                Instant.parse("2025-10-20T12:30:00.00Z"),
		                (m, t) -> {
		                    ArrayList<EventI> ret = new ArrayList<>();
		                    ret.add(new SwitchOnOven(t));
		                    return ret;
		                },
		                (m, t) -> {}),

		            // Set target temperature 50
		            new SimulationTestStep(
		                OvenUnitTesterModel.URI,
		                Instant.parse("2025-10-20T12:35:00.00Z"),
		                (m, t) -> {
		                    ArrayList<EventI> ret = new ArrayList<>();
		                    ret.add(new SetTargetTemperatureOven(
		                        t, new TargetTemperatureValue(50.0)));
		                    return ret;
		                },
		                (m, t) -> {}),

		            // Heat
		            new SimulationTestStep(
		                OvenUnitTesterModel.URI,
		                Instant.parse("2025-10-20T13:00:00.00Z"),
		                (m, t) -> {
		                    ArrayList<EventI> ret = new ArrayList<>();
		                    ret.add(new HeatOven(t));
		                    return ret;
		                },
		                (m, t) -> {}),

		            // Stop heating
		            new SimulationTestStep(
		                OvenUnitTesterModel.URI,
		                Instant.parse("2025-10-20T13:30:00.00Z"),
		                (m, t) -> {
		                    ArrayList<EventI> ret = new ArrayList<>();
		                    ret.add(new DoNotHeatOven(t));
		                    return ret;
		                },
		                (m, t) -> {}),

		            // Set DEFROST mode
		            new SimulationTestStep(
		                OvenUnitTesterModel.URI,
		                Instant.parse("2025-10-20T13:40:00.00Z"),
		                (m, t) -> {
		                    ArrayList<EventI> ret = new ArrayList<>();
		                    ret.add(new SetModeOven(t,
		                        new ModeValue(OvenMode.DEFROST)));
		                    return ret;
		                },
		                (m, t) -> {}),

		            // Heat after DEFROST
		            new SimulationTestStep(
		                OvenUnitTesterModel.URI,
		                Instant.parse("2025-10-20T14:00:00.00Z"),
		                (m, t) -> {
		                    ArrayList<EventI> ret = new ArrayList<>();
		                    ret.add(new HeatOven(t));
		                    return ret;
		                },
		                (m, t) -> {}),

		            // Stop heating again
		            new SimulationTestStep(
		                OvenUnitTesterModel.URI,
		                Instant.parse("2025-10-20T14:30:00.00Z"),
		                (m, t) -> {
		                    ArrayList<EventI> ret = new ArrayList<>();
		                    ret.add(new DoNotHeatOven(t));
		                    return ret;
		                },
		                (m, t) -> {}),

		            // Set GRILL mode
		            new SimulationTestStep(
		                OvenUnitTesterModel.URI,
		                Instant.parse("2025-10-20T14:40:00.00Z"),
		                (m, t) -> {
		                    ArrayList<EventI> ret = new ArrayList<>();
		                    ret.add(new SetModeOven(
		                        t, new ModeValue(OvenMode.GRILL)));
		                    return ret;
		                },
		                (m, t) -> {}),

		            // Heat after GRILL
		            new SimulationTestStep(
		                OvenUnitTesterModel.URI,
		                Instant.parse("2025-10-20T15:00:00.00Z"),
		                (m, t) -> {
		                    ArrayList<EventI> ret = new ArrayList<>();
		                    ret.add(new HeatOven(t));
		                    return ret;
		                },
		                (m, t) -> {}),

		            // Change power level
		            new SimulationTestStep(
		                OvenUnitTesterModel.URI,
		                Instant.parse("2025-10-20T15:30:00.00Z"),
		                (m, t) -> {
		                    ArrayList<EventI> ret = new ArrayList<>();
		                    ret.add(new SetPowerOven(
		                        t, new SetPowerOven.PowerValueOven(880.0)));
		                    return ret;
		                },
		                (m, t) -> {}),

		            // Switch off
		            new SimulationTestStep(
		                OvenUnitTesterModel.URI,
		                Instant.parse("2025-10-20T16:30:00.00Z"),
		                (m, t) -> {
		                    ArrayList<EventI> ret = new ArrayList<>();
		                    ret.add(new SwitchOffOven(t));
		                    return ret;
		                },
		                (m, t) -> {}),
		            
		         // Open oven door
		            new SimulationTestStep(
		                OvenUnitTesterModel.URI,
		                Instant.parse("2025-10-20T16:40:00.00Z"),
		                (m, t) -> {
		                    ArrayList<EventI> ret = new ArrayList<>();
		                    ret.add(new OpenDoorOven(t));
		                    return ret;
		                },
		                (m, t) -> {})
		        }
		    );
	}
}
// -----------------------------------------------------------------------------
