package equipments.fan.mil;

import java.util.concurrent.TimeUnit;

import equipments.fan.mil.events.SetHighFan;
import equipments.fan.mil.events.SetMediumFan;
import equipments.fan.mil.events.SetLowFan;
import equipments.fan.mil.events.SwitchOffFan;
import equipments.fan.mil.events.SwitchOnFan;
import fr.sorbonne_u.components.cyphy.utils.tests.AbstractTestScenarioBasedAtomicModel;
import fr.sorbonne_u.components.cyphy.utils.tests.TestScenarioWithSimulation;
import fr.sorbonne_u.devs_simulation.exceptions.MissingRunParameterException;
import fr.sorbonne_u.devs_simulation.exceptions.NeoSim4JavaException;
import fr.sorbonne_u.devs_simulation.models.annotations.ModelExternalEvents;
import fr.sorbonne_u.devs_simulation.models.interfaces.ModelI;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.AtomicSimulatorI;
import fr.sorbonne_u.devs_simulation.utils.StandardLogger;
import java.util.Map;

// -----------------------------------------------------------------------------
/**
 * The class <code>FanUnitTesterModel</code> implements a unit tester
 * simulation model for the fan which runs test scenarios.
 *
 * <p><strong>Description</strong></p>
 * 
 * <ul>
 * <li>Imported events: none</li>
 * <li>Exported events:
 *   {@code SwitchOnFan},
 *   {@code SwitchOffFan},
 *   {@code SetLowFan},
 *   {@code SetMediumFan},
 *   {@code SetHighFan}</li>
 * </ul>
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
//-----------------------------------------------------------------------------
@ModelExternalEvents(exported = {SwitchOnFan.class,
								 SwitchOffFan.class,
								 SetLowFan.class,
								 SetMediumFan.class,
								 SetHighFan.class})
//-----------------------------------------------------------------------------
public class			FanUnitTesterModel
extends		AbstractTestScenarioBasedAtomicModel
{
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	private static final long serialVersionUID = 1L;
	/** when true, leaves a trace of the execution of the model.			*/
	public static final boolean		VERBOSE = true;
	/** when true, leaves a debugging trace of the execution of the model.	*/
	public static final boolean		DEBUG = false;

	/** single model URI.													*/
	public static final String	URI = "fan-unit-tester-model";
	/**	name of the run parameter for the test scenario to be executed.		*/
	public static final String	TEST_SCENARIO_RP_NAME = "TEST_SCENARIO";

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * create an atomic model with the given URI (if null, one will be
	 * generated) and to be run by the given simulator using the given time unit
	 * for its clock.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code uri == null || !uri.isEmpty()}
	 * pre	{@code simulatedTimeUnit != null}
	 * pre	{@code simulationEngine != null && !simulationEngine.isModelSet()}
	 * pre	{@code simulationEngine instanceof AtomicEngine}
	 * post	{@code !isDebugModeOn()}
	 * post	{@code getURI() != null && !getURI().isEmpty()}
	 * post	{@code uri == null || getURI().equals(uri)}
	 * post	{@code getSimulatedTimeUnit().equals(simulatedTimeUnit)}
	 * post	{@code getSimulationEngine().equals(simulationEngine)}
	 * </pre>
	 *
	 * @param uri				unique identifier of the model.
	 * @param simulatedTimeUnit	time unit used for the simulation clock.
	 * @param simulationEngine	simulation engine enacting the model.
	 */
	public				FanUnitTesterModel(
		String uri,
		TimeUnit simulatedTimeUnit,
		AtomicSimulatorI simulationEngine
		)
	{
		super(uri, simulatedTimeUnit, simulationEngine);

		this.getSimulationEngine().setLogger(new StandardLogger());

		// Invariant checking
		assert	FanUnitTesterModel.implementationInvariants(this) :
				new NeoSim4JavaException(
						"Implementation Invariants violation: "
						+ "FanUnitTesterModel."
						+ "implementationInvariants(this)");
		assert	FanUnitTesterModel.invariants(this) :
				new NeoSim4JavaException(
						"Invariants violation: FanUnitTesterModel."
						+ "invariants(this)");
	}

	// -------------------------------------------------------------------------
	// Methods
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.ModelI#setSimulationRunParameters(java.util.Map)
	 */
	@Override
	public void			setSimulationRunParameters(
		Map<String, Object> simParams
		) throws MissingRunParameterException
	{
		String testScenarioName = ModelI.createRunParameterName(this.getURI(),
														TEST_SCENARIO_RP_NAME);

		// Preconditions checking
		assert	simParams != null :
				new MissingRunParameterException("simParams != null");
		assert	simParams.containsKey(testScenarioName) :
				new MissingRunParameterException(testScenarioName);

		this.setTestScenario((TestScenarioWithSimulation) simParams.get(testScenarioName));
	}
}
// -----------------------------------------------------------------------------
