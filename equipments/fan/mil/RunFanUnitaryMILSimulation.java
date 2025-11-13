package equipments.fan.mil;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import equipments.fan.mil.events.SetHighFan;
import equipments.fan.mil.events.SetLowFan;
import equipments.fan.mil.events.SetMediumFan;
import equipments.fan.mil.events.SwitchOffFan;
import equipments.fan.mil.events.SwitchOnFan;
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
import java.util.ArrayList;

// -----------------------------------------------------------------------------
/**
 * The class <code>RunFanUnitarySimulation</code> is the main class used
 * to run simulations on the example models of the fan in isolation
 * based on test scenarios.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p>
 * The simulation architecture for the fan contains only two atomic
 * models composed under a coupled model:
 * </p>
 * <p><img src="../../../images/hem-2025-e2/FanUnitTestArchitecture.png"/></p> 
 * <p>
 * The code of the {@code main} methods shows how to use simulation model
 * descriptors to create the description of the above simulation architecture
 * and then create an instance of this architecture by instantiating and
 * connecting the model instances. Note how models are described by atomic model
 * descriptors and coupled model descriptors and then the connections between
 * coupled models and their submodels as well as exported events to imported
 * ones are described by different maps. In this example, only connections
 * between models within this architecture are necessary, but when creating
 * coupled models, they can also import and export events consumed and produced
 * by their submodels.
 * </p>
 * <p>
 * The architecture object is the root of this description and it provides
 * the method {@code constructSimulator} that instantiate the models and
 * connect them. This method returns the reference on the simulator attached
 * to the root coupled model in the architecture instance, which is then used
 * to perform simulation runs by calling the method
 * {@code doStandAloneSimulation}
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
 * <p>Created on : 2025-11-11</p>
 * 
 * @author	<a href="mailto:Rodrigo.Vila@etu.sorbonne-universite.fr">Rodrigo Vila</a>
 * @author	<a href="mailto:Damien.Ribeiro@etu.sorbonne-universite.fr">Damien Ribeiro</a>
 */
public class			RunFanUnitaryMILSimulation
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
	 * pre	{@code instance != null}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @return	true if the invariants are observed, false otherwise.
	 */
	public static boolean	staticInvariants()
	{
		boolean ret = true;
		ret &= FanSimulationConfigurationI.staticInvariants();
		return ret;
	}

	// -------------------------------------------------------------------------
	// Methods
	// -------------------------------------------------------------------------

	public static void	main(String[] args)
	{
		staticInvariants();
		Time.setPrintPrecision(4);
		Duration.setPrintPrecision(4);

		try {
			// map that will contain the atomic model descriptors to construct
			// the simulation architecture
			Map<String,AbstractAtomicModelDescriptor> atomicModelDescriptors =
																new HashMap<>();

			// the hair dyer model simulating its electricity consumption, an
			// atomic HIOA model hence we use an AtomicHIOA_Descriptor
			atomicModelDescriptors.put(
					FanElectricityModel.URI,
					AtomicHIOA_Descriptor.create(
							FanElectricityModel.class,
							FanElectricityModel.URI,
							FanSimulationConfigurationI.TIME_UNIT,
							null));
			// for atomic model, we use an AtomicModelDescriptor
			atomicModelDescriptors.put(
					FanUnitTesterModel.URI,
					AtomicModelDescriptor.create(
							FanUnitTesterModel.class,
							FanUnitTesterModel.URI,
							FanSimulationConfigurationI.TIME_UNIT,
							null));

			// map that will contain the coupled model descriptors to construct
			// the simulation architecture
			Map<String,CoupledModelDescriptor> coupledModelDescriptors =
																new HashMap<>();

			// the set of submodels of the coupled model, given by their URIs
			Set<String> submodels = new HashSet<String>();
			submodels.add(FanElectricityModel.URI);
			submodels.add(FanUnitTesterModel.URI);

			// event exchanging connections between exporting and importing
			// models
			Map<EventSource,EventSink[]> connections =
										new HashMap<EventSource,EventSink[]>();

			connections.put(
					new EventSource(FanUnitTesterModel.URI,
									SwitchOnFan.class),
					new EventSink[] {
							new EventSink(FanElectricityModel.URI,
										  SwitchOnFan.class)
					});
			connections.put(
					new EventSource(FanUnitTesterModel.URI,
									SwitchOffFan.class),
					new EventSink[] {
							new EventSink(FanElectricityModel.URI,
										  SwitchOffFan.class)
					});
			connections.put(
					new EventSource(FanUnitTesterModel.URI,
									SetHighFan.class),
					new EventSink[] {
							new EventSink(FanElectricityModel.URI,
										  SetHighFan.class)
					});
			connections.put(
			 
					new EventSource(FanUnitTesterModel.URI,
									SetMediumFan.class),
					new EventSink[] {
							new EventSink(FanElectricityModel.URI,
										  SetMediumFan.class)
					});
			connections.put(
					new EventSource(FanUnitTesterModel.URI,
									SetLowFan.class),
					new EventSink[] {
							new EventSink(FanElectricityModel.URI,
										  SetLowFan.class)
					});

			// coupled model descriptor
			coupledModelDescriptors.put(
					FanCoupledModel.URI,
					new CoupledModelDescriptor(
							FanCoupledModel.class,
							FanCoupledModel.URI,
							submodels,
							null,
							null,
							connections,
							null));

			// simulation architecture
			ArchitectureI architecture =
					new Architecture(
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
			CLASSICAL.setUpSimulator(se);
			Time startTime = CLASSICAL.getStartTime();
			Duration d = CLASSICAL.getEndTime().subtract(startTime);
			se.doStandAloneSimulation(startTime.getSimulatedTime(),
									  d.getSimulatedDuration());
			SimulationReportI sr = se.getSimulatedModel().getFinalReport();
			System.out.println(sr);
			System.exit(0);
		} catch (Throwable e) {
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
									Instant.parse("2025-10-20T19:00:00.00Z");
	/** the start time in simulated time, corresponding to
	 *  {@code START_INSTANT}.												*/
	protected static Time		START_TIME =
									new Time(0.0, TimeUnit.HOURS);

	/** standard test scenario, see Gherkin specification.				 	*/
	protected static TestScenario	CLASSICAL =
		new TestScenario(
			"-----------------------------------------------------\n" +
			"Classical\n\n" +
			"  Gherkin specification\n\n" +
			"    Feature: fan operation\n\n" +
			"      Scenario: fan switched on\n" +
			"        Given a fan that is off\n" +
			"        When it is switched on\n" +
			"        Then it is on and low\n" +
			"      Scenario: fan set high\n" +
			"        Given a fan that is on\n" +
			"        When it is set high\n" +
			"        Then it is on and high\n" +
			"      Scenario: fan set medium\n" +
			"        Given a fan that is on\n" +
			"        When it is set medium\n" +
			"        Then it is on and medium\n" +
			"      Scenario: fan set low\n" +
			"        Given a fan that is on\n" +
			"        When it is set low\n" +
			"        Then it is on and low\n" +
			"      Scenario: fan switched off\n" +
			"        Given a fan that is on\n" +
			"        When it is switched of\n" +
			"        Then it is off\n" +
			"-----------------------------------------------------\n",
			"\n-----------------------------------------------------\n" +
			"End Classical\n" +
			"-----------------------------------------------------",
			START_INSTANT,
			END_INSTANT,
			START_TIME,
			(se, ts) -> { 
				HashMap<String, Object> simParams = new HashMap<>();
				simParams.put(
					ModelI.createRunParameterName(
						FanUnitTesterModel.URI,
						FanUnitTesterModel.TEST_SCENARIO_RP_NAME),
					ts);
				se.setSimulationRunParameters(simParams);
			},
			new SimulationTestStep[]{
				new SimulationTestStep(
					FanUnitTesterModel.URI,
					Instant.parse("2025-10-20T13:00:00.00Z"),
					(m, t) -> {
						ArrayList<EventI> ret = new ArrayList<>();
						ret.add(new SwitchOnFan(t));
						return ret;
					},
					(m, t) -> {}),
				new SimulationTestStep(
						FanUnitTesterModel.URI,
						Instant.parse("2025-10-20T14:00:00.00Z"),
						(m, t) -> {
							ArrayList<EventI> ret = new ArrayList<>();
							ret.add(new SetHighFan(t));
							return ret;
						},
						(m, t) -> {}),
				new SimulationTestStep(
						FanUnitTesterModel.URI,
						Instant.parse("2025-10-20T15:00:00.00Z"),
						(m, t) -> {
							ArrayList<EventI> ret = new ArrayList<>();
							ret.add(new SetMediumFan(t));
							return ret;
						},
						(m, t) -> {}),
				new SimulationTestStep(
						FanUnitTesterModel.URI,
						Instant.parse("2025-10-20T16:00:00.00Z"),
						(m, t) -> {
							ArrayList<EventI> ret = new ArrayList<>();
							ret.add(new SetLowFan(t));
							return ret;
						},
						(m, t) -> {}),
				new SimulationTestStep(
						FanUnitTesterModel.URI,
						Instant.parse("2025-10-20T17:00:00.00Z"),
						(m, t) -> {
							ArrayList<EventI> ret = new ArrayList<>();
							ret.add(new SwitchOffFan(t));
							return ret;
						},
						(m, t) -> {})
			});
}
// -----------------------------------------------------------------------------
