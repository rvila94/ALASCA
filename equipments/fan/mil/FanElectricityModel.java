package equipments.fan.mil;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import equipments.fan.mil.events.AbstractFanEvent;
import equipments.fan.mil.events.SetHighFan;
import equipments.fan.mil.events.SetMediumFan;
import equipments.fan.mil.events.SetLowFan;
import equipments.fan.mil.events.SwitchOffFan;
import equipments.fan.mil.events.SwitchOnFan;
import fr.sorbonne_u.alasca.physical_data.MeasurementUnit;
import equipments.fan.Fan;
import equipments.fan.FanImplementationI.FanMode;
import equipments.fan.FanImplementationI.FanState;
import fr.sorbonne_u.components.hem2025e1.equipments.meter.ElectricMeterImplementationI;
import fr.sorbonne_u.components.hem2025e2.GlobalReportI;
import equipments.fan.mil.FanElectricityModel;
import fr.sorbonne_u.components.hem2025e2.utils.Electricity;
import fr.sorbonne_u.devs_simulation.exceptions.MissingRunParameterException;
import fr.sorbonne_u.devs_simulation.exceptions.NeoSim4JavaException;
import fr.sorbonne_u.devs_simulation.hioa.annotations.ExportedVariable;
import fr.sorbonne_u.devs_simulation.hioa.annotations.ModelExportedVariable;
import fr.sorbonne_u.devs_simulation.hioa.models.AtomicHIOA;
import fr.sorbonne_u.devs_simulation.hioa.models.vars.Value;
import fr.sorbonne_u.devs_simulation.models.annotations.ModelExternalEvents;
import fr.sorbonne_u.devs_simulation.models.events.Event;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.interfaces.ModelI;
import fr.sorbonne_u.devs_simulation.models.time.Duration;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.AtomicSimulatorI;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulationReportI;
import fr.sorbonne_u.devs_simulation.utils.StandardLogger;
import fr.sorbonne_u.exceptions.AssertionChecking;

/**
 * The class <code>FanElectricity_MILModel</code> defines a MIL model
 * of the electricity consumption of a fan.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p>
 * The fan can be switched on and off, and when switched on, it can be
 * either in a low mode, with lower electricity consumption, medium electricity
 * consumptioa or high mode, with a higher electricity consumption.
 * </p>
 * <p>
 * The electricity consumption is represented as a variable of type double that
 * has to be exported towards the electric meter MIL model in order to be summed
 * up to get the global electricity consumption of the house.
 * </p>
 * <p>
 * To model the user actions, four events are defined to be imported and the
 * external transitions upon the reception of these events force the fan
 * electricity model in the corresponding mode with the corresponding
 * electricity consumption.
 * </p>
 * 
 * <ul>
 * <li>Imported events:
 *   {@code SwitchOnFan},
 *   {@code SwitchOffFan},
 *   {@code SetLowFan},
 *   {@code SetMediumFan},  
 *   {@code SetHighFan}</li>
 * <li>Exported events: none</li>
 * <li>Imported variables: none</li>
 * <li>Exported variables:
 *   name = {@code currentIntensity}, type = {@code Double}</li>
 * </ul>
 * 
 * <p><strong>Implementation Invariants</strong></p>
 * 
 * <pre>
 * invariant	{@code lowModeConsumption > 0.0}
 * invariant	{@code mediumModeConsumption > lowModeConsumption}
 * invariant	{@code highModeConsumption > mediumModeConsumption}
 * invariant	{@code totalConsumption >= 0.0}
 * invariant	{@code currentState != null}
 * invariant	{@code !currentIntensity.isInitialised() || currentIntensity.getValue() >= 0.0}
 * </pre>
 * 
 * <p><strong>Invariants</strong></p>
 * 
 * <pre>
 * invariant	{@code URI != null && !URI.isEmpty()}
 * invariant	{@code LOW_MODE_CONSUMPTION_RPNAME != null && !LOW_MODE_CONSUMPTION_RPNAME.isEmpty()}
 * invariant	{@code MEDIUM_MODE_CONSUMPTION_RPNAME != null && !MEDIUM_MODE_CONSUMPTION_RPNAME.isEmpty()}
 * invariant	{@code HIGH_MODE_CONSUMPTION_RPNAME != null && !HIGH_MODE_CONSUMPTION_RPNAME.isEmpty()}
 * invariant	{@code TENSION_RPNAME != null && !TENSION_RPNAME.isEmpty()}
 * </pre>
 * 
 * <p>Created on : 2025-11-11</p>
 * 
 * @author	<a href="mailto:Rodrigo.Vila@etu.sorbonne-universite.fr">Rodrigo Vila</a>
 * @author	<a href="mailto:Damien.Ribeiro@etu.sorbonne-universite.fr">Damien Ribeiro</a>
 */
// -----------------------------------------------------------------------------
@ModelExternalEvents(imported = {SwitchOnFan.class,
								 SwitchOffFan.class,
								 SetLowFan.class,
								 SetMediumFan.class,
								 SetHighFan.class})
@ModelExportedVariable(name = "currentIntensity", type = Double.class)
// -----------------------------------------------------------------------------
public class			FanElectricityModel
extends		AtomicHIOA
{
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	private static final long		serialVersionUID = 1L;
	/** when true, leaves a trace of the execution of the model.			*/
	public static boolean			VERBOSE = true;
	/** when true, leaves a debugging trace of the execution of the model.	*/
	public static boolean			DEBUG = false;

	/** URI for an instance model; works as long as only one instance is
	 *  created.															*/
	public static final String		URI = FanElectricityModel.class.
																getSimpleName();

	/** current state (OFF, ON) of the fan.							*/
	protected FanState		currentState = FanState.OFF;
	/** current mode (LOW, MEDIUM, HIGH) of the fan.							*/
	protected FanMode			currentMode = FanMode.LOW;
	/** true when the electricity consumption of the dryer has changed
	 *  after executing an external event; the external event changes the
	 *  value of <code>currentState</code> and then an internal transition
	 *  will be triggered by putting through in this variable which will
	 *  update the variable <code>currentIntensity</code>.					*/
	protected boolean				consumptionHasChanged = false;

	/** power consumption in the LOW mode in the power unit defined by the
	 *  fan.															*/
	protected double				lowModeConsumption;
	/** power consumption in the MEDIUM mode in the power unit defined by the
	 *  fan.															*/
	protected double				mediumModeConsumption;
	/** power consumption in the HIGH mode in the power unit defined by the
	 *  fan.															*/
	protected double				highModeConsumption;

	/** total consumption of the fan during the simulation in kwh.	*/
	protected double				totalConsumption;

	// -------------------------------------------------------------------------
	// HIOA model variables
	// -------------------------------------------------------------------------

	/** current intensity in the power unit defined by the electric meter.	*/
	@ExportedVariable(type = Double.class)
	protected final Value<Double>	currentIntensity = new Value<Double>(this);

	// -------------------------------------------------------------------------
	// Invariants
	// -------------------------------------------------------------------------

	/**
	 * return true if the implementation invariants are observed, false
	 * otherwise.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code instance != null}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param instance	instance to be tested.
	 * @return			true if the implementation invariants are observed, false otherwise.
	 */
	protected static boolean	implementationInvariants(
		FanElectricityModel instance
		)
	{
		assert	instance != null :
				new NeoSim4JavaException("Precondition violation: "
						+ "instance != null");

		boolean ret = true;
		ret &= AssertionChecking.checkImplementationInvariant(
				instance.lowModeConsumption > 0.0,
				FanElectricityModel.class,
				instance,
				"lowModeConsumption > 0.0");
		ret &= AssertionChecking.checkImplementationInvariant(
				instance.mediumModeConsumption > instance.lowModeConsumption,
				FanElectricityModel.class,
				instance,
				"mediumModeConsumption > lowModeConsumption");
		ret &= AssertionChecking.checkImplementationInvariant(
				instance.highModeConsumption > instance.mediumModeConsumption,
				FanElectricityModel.class,
				instance,
				"highModeConsumption > mediumModeConsumption");
		ret &= AssertionChecking.checkImplementationInvariant(
				instance.totalConsumption >= 0.0,
				FanElectricityModel.class,
				instance,
				"totalConsumption >= 0.0");
		ret &= AssertionChecking.checkImplementationInvariant(
				instance.currentState != null,
				FanElectricityModel.class,
				instance,
				"currentState != null");
		ret &= AssertionChecking.checkImplementationInvariant(
				!instance.currentIntensity.isInitialised() ||
									instance.currentIntensity.getValue() >= 0.0,
				FanElectricityModel.class,
				instance,
				"!currentIntensity.isInitialised() || "
				+ "currentIntensity.getValue() >= 0.0");
		return ret;
	}

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
	 * @return	true if the static invariants are observed, false otherwise.
	 */
	public static boolean	staticInvariants()
	{
		boolean ret = true;
		ret &= Fan.staticInvariants();
		ret &= FanSimulationConfigurationI.staticInvariants();
		ret &= AssertionChecking.checkStaticInvariant(
				URI != null && !URI.isEmpty(),
				FanElectricityModel.class,
				"URI != null && !URI.isEmpty()");
		ret &= AssertionChecking.checkStaticInvariant(
				LOW_MODE_CONSUMPTION_RPNAME != null &&
										!LOW_MODE_CONSUMPTION_RPNAME.isEmpty(),
				FanElectricityModel.class,
				"LOW_MODE_CONSUMPTION_RPNAME != null && "
								+ "!LOW_MODE_CONSUMPTION_RPNAME.isEmpty()");
		ret &= AssertionChecking.checkStaticInvariant(
				MEDIUM_MODE_CONSUMPTION_RPNAME != null &&
										!MEDIUM_MODE_CONSUMPTION_RPNAME.isEmpty(),
				FanElectricityModel.class,
				"MEDIUM_MODE_CONSUMPTION_RPNAME != null && "
								+ "!MEDIUM_MODE_CONSUMPTION_RPNAME.isEmpty()");
		ret &= AssertionChecking.checkStaticInvariant(
				HIGH_MODE_CONSUMPTION_RPNAME != null &&
									!HIGH_MODE_CONSUMPTION_RPNAME.isEmpty(),
				FanElectricityModel.class,
				"HIGH_MODE_CONSUMPTION_RPNAME != null && "
							+ "!HIGH_MODE_CONSUMPTION_RPNAME.isEmpty()");
		ret &= AssertionChecking.checkStaticInvariant(
				TENSION_RPNAME != null && !TENSION_RPNAME.isEmpty(),
				FanElectricityModel.class,
				"TENSION_RPNAME != null && !TENSION_RPNAME.isEmpty()");
		return ret;
	}

	/**
	 * return true if the invariants are observed, false otherwise.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code instance != null}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param instance	instance to be tested.
	 * @return			true if the invariants are observed, false otherwise.
	 */
	protected static boolean	invariants(
		FanElectricityModel instance
		)
	{
		assert	instance != null :
				new NeoSim4JavaException(
						"Precondition violation: instance != null");

		boolean ret = true;
		ret &= staticInvariants();
		return ret;
	}

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * create a fan MIL model instance.
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
	 * @param uri				URI of the model.
	 * @param simulatedTimeUnit	time unit used for the simulation time.
	 * @param simulationEngine	simulation engine to which the model is attached.
	 * @throws Exception		<i>to do</i>.
	 */
	public				FanElectricityModel(
		String uri,
		TimeUnit simulatedTimeUnit,
		AtomicSimulatorI simulationEngine
		) throws Exception
	{
		super(uri, simulatedTimeUnit, simulationEngine);


		this.lowModeConsumption = Fan.LOW_POWER.getData();
		this.mediumModeConsumption = Fan.MEDIUM_POWER.getData();
		this.highModeConsumption = Fan.HIGH_POWER.getData();

		this.getSimulationEngine().setLogger(new StandardLogger());

		assert	FanElectricityModel.implementationInvariants(this) :
				new NeoSim4JavaException(
						"FanElectricityModel.implementationInvariants("
						+ "this)");
		assert	FanElectricityModel.invariants(this) :
				new NeoSim4JavaException(
						"FanElectricityModel.invariants(this)");
	}

	// -------------------------------------------------------------------------
	// Methods
	// -------------------------------------------------------------------------

	/**
	 * return the current state of the fan.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code return != null}
	 * </pre>
	 *
	 * @return	the state of the fan.
	 */
	public FanState	getState()
	{
		return this.currentState;
	}

	/**
	 * set the state and mode of the fan.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code s != null}
	 * post	{@code getState() == s}
	 * </pre>
	 *
	 * @param s		the new state.
	 * @param m		the new mode.
	 */
	public void			setStateMode(FanState s, FanMode m)
	{
		this.currentState = s;
		this.currentMode = m;
	}

	/**
	 * return the current mode of the fan.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code return != null}
	 * </pre>
	 *
	 * @return	the state of the fan.
	 */
	public FanMode	getMode()
	{
		return this.currentMode;
	}

	/**
	 * toggle the value of the state of the model telling whether the
	 * electricity consumption level has just changed or not; when it changes
	 * after receiving an external event, an immediate internal transition
	 * is triggered to update the level of electricity consumption.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 */
	public void			toggleConsumptionHasChanged()
	{
		if (this.consumptionHasChanged) {
			this.consumptionHasChanged = false;
		} else {
			this.consumptionHasChanged = true;
		}
	}

	// -------------------------------------------------------------------------
	// DEVS simulation protocol
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.devs_simulation.hioa.models.AtomicHIOA#initialiseState(fr.sorbonne_u.devs_simulation.models.time.Time)
	 */
	@Override
	public void			initialiseState(Time startTime)
	{
		super.initialiseState(startTime);

		// initially the fan is off and its electricity consumption is
		// not about to change.
		this.currentState = FanState.OFF;
		this.currentMode = FanMode.LOW;
		this.consumptionHasChanged = false;
		this.totalConsumption = 0.0;

		if (VERBOSE) {
			this.logMessage("simulation begins.");
		}

		assert	FanElectricityModel.implementationInvariants(this) :
				new NeoSim4JavaException(
						"FanElectricityModel.implementationInvariants("
						+ "this)");
		assert	FanElectricityModel.invariants(this) :
				new NeoSim4JavaException(
						"FanElectricityModel.invariants(this)");
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.hioa.models.interfaces.VariableInitialisationI#initialiseVariables()
	 */
	@Override
	public void			initialiseVariables()
	{
		super.initialiseVariables();

		// initially, the fan is off, so its consumption is zero.
		this.currentIntensity.initialise(0.0);

		assert	FanElectricityModel.implementationInvariants(this) :
				new NeoSim4JavaException(
						"FanElectricityModel.implementationInvariants("
						+ "this)");
		assert	FanElectricityModel.invariants(this) :
				new NeoSim4JavaException(
						"FanElectricityModel.invariants(this)");
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.AtomicModelI#output()
	 */
	@Override
	public ArrayList<EventI>	output()
	{
		// the model does not export events.
		return null;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.ModelI#timeAdvance()
	 */
	@Override
	public Duration		timeAdvance()
	{
		Duration ret = null;
		// to trigger an internal transition after an external transition, the
		// variable consumptionHasChanged is set to true, hence when it is true
		// return a zero delay otherwise return an infinite delay (no internal
		// transition expected)
		if (this.consumptionHasChanged) {
			// after triggering the internal transition, toggle the boolean
			// to prepare for the next internal transition.
			this.toggleConsumptionHasChanged();
			ret = new Duration(0.0, this.getSimulatedTimeUnit());
		} else {
			// after an internal transition, wait until another external
			// events comes in, hence no internal transition is planned
			ret = Duration.INFINITY;
		}

		assert	FanElectricityModel.implementationInvariants(this) :
				new NeoSim4JavaException(
						"FanElectricityModel.implementationInvariants("
						+ "this)");
		assert	FanElectricityModel.invariants(this) :
				new NeoSim4JavaException(
						"FanElectricityModel.invariants(this)");

		return ret;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.AtomicModel#userDefinedInternalTransition(fr.sorbonne_u.devs_simulation.models.time.Duration)
	 */
	@Override
	public void			userDefinedInternalTransition(Duration elapsedTime)
	{
		super.userDefinedInternalTransition(elapsedTime);

		// set the current electricity consumption from the current state
		Time t = this.getCurrentStateTime();
		if (this.currentState == FanState.ON) {
			switch (this.currentMode)
			{
				case LOW :
					this.currentIntensity.
						setNewValue(
							this.lowModeConsumption/Fan.TENSION.getData(),
							t);
					break;
				case MEDIUM :
					this.currentIntensity.
						setNewValue(
							this.mediumModeConsumption/Fan.TENSION.getData(),
							t);
					break;
				case HIGH :
					this.currentIntensity.
						setNewValue(
							this.highModeConsumption/Fan.TENSION.getData(),
							t);
			}
		} else {
			this.currentIntensity.setNewValue(0.0, t);
		}

		// Tracing
		if (VERBOSE) {
			StringBuffer message =
					new StringBuffer("executes an internal transition ");
			message.append("with current consumption ");
			message.append(this.currentIntensity.getValue());
			message.append(" ");
			message.append(ElectricMeterImplementationI.POWER_UNIT);
			message.append(" at ");
			message.append(this.currentIntensity.getTime());
			this.logMessage(message.toString());
		}

		assert	FanElectricityModel.implementationInvariants(this) :
				new NeoSim4JavaException(
						"FanElectricityModel.implementationInvariants("
						+ "this)");
		assert	FanElectricityModel.invariants(this) :
				new NeoSim4JavaException(
						"FanElectricityModel.invariants(this)");
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.AtomicModel#userDefinedExternalTransition(fr.sorbonne_u.devs_simulation.models.time.Duration)
	 */
	@Override
	public void			userDefinedExternalTransition(Duration elapsedTime)
	{
		super.userDefinedExternalTransition(elapsedTime);

		// get the vector of currently received external events
		ArrayList<EventI> currentEvents = this.getStoredEventAndReset();
		// when this method is called, there is at least one external event,
		// and for the current fan model, there must be exactly one by
		// construction.
		assert	currentEvents != null && currentEvents.size() == 1;

		Event ce = (Event) currentEvents.get(0);

		// optional: compute the total consumption (in kwh) for the simulation
		// report.
		if (ElectricMeterImplementationI.POWER_UNIT.equals(MeasurementUnit.WATTS)) {
			this.totalConsumption +=
					Electricity.computeConsumption(
								elapsedTime,
								this.currentIntensity.getValue());
		} else {
			this.totalConsumption +=
					Electricity.computeConsumption(
								elapsedTime,
								Fan.TENSION.getData() *
										this.currentIntensity.getValue());
		}

		// Tracing
		if (VERBOSE) {
			StringBuffer message =
					new StringBuffer("executes an external transition ");
			message.append(ce.toString());
			message.append(")");
			this.logMessage(message.toString());
		}

		assert	ce instanceof AbstractFanEvent :
				new RuntimeException(
						ce + " is not an event that an FanElectricityModel"
						+ " can receive and process.");
		// events have a method execute on to perform their effect on this
		// model
		ce.executeOn(this);

		assert	FanElectricityModel.implementationInvariants(this) :
				new NeoSim4JavaException(
						"FanElectricityModel.implementationInvariants("
						+ "this)");
		assert	FanElectricityModel.invariants(this) :
				new NeoSim4JavaException(
						"FanElectricityModel.invariants(this)");
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.hioa.models.AtomicHIOA#endSimulation(fr.sorbonne_u.devs_simulation.models.time.Time)
	 */
	@Override
	public void			endSimulation(Time endTime)
	{
		Duration d = endTime.subtract(this.getCurrentStateTime());
		if (ElectricMeterImplementationI.POWER_UNIT.equals(MeasurementUnit.WATTS)) {
			this.totalConsumption +=
					Electricity.computeConsumption(
								d,
								this.currentIntensity.getValue());
		} else {
			this.totalConsumption +=
					Electricity.computeConsumption(
								d,
								Fan.TENSION.getData() *
											this.currentIntensity.getValue());
		}

		if (VERBOSE) {
			this.logMessage("simulation ends.");
		}
		super.endSimulation(endTime);
	}

	// -------------------------------------------------------------------------
	// Optional DEVS simulation protocol: simulation run parameters
	// -------------------------------------------------------------------------

	/** run parameter name for {@code LOW_MODE_CONSUMPTION}.				*/
	public static final String		LOW_MODE_CONSUMPTION_RPNAME =
												URI + ":LOW_MODE_CONSUMPTION";
	/** run parameter name for {@code MEDIUM_MODE_CONSUMPTION}.				*/
	public static final String		MEDIUM_MODE_CONSUMPTION_RPNAME =
												URI + ":MEDIUM_MODE_CONSUMPTION";
	/** run parameter name for {@code HIGH_MODE_CONSUMPTION}.				*/
	public static final String		HIGH_MODE_CONSUMPTION_RPNAME =
												URI + ":HIGH_MODE_CONSUMPTION";
	/** run parameter name for {@code TENSION}.								*/
	public static final String		TENSION_RPNAME = URI + ":TENSION";

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.ModelI#setSimulationRunParameters(Map)
	 */
	@Override
	public void			setSimulationRunParameters(
		Map<String, Object> simParams
		) throws MissingRunParameterException
	{
		super.setSimulationRunParameters(simParams);

		String lowName =
			ModelI.createRunParameterName(getURI(),
										  LOW_MODE_CONSUMPTION_RPNAME);
		if (simParams.containsKey(lowName)) {
			this.lowModeConsumption = (double) simParams.get(lowName);
		}
		String mediumName =
				ModelI.createRunParameterName(getURI(),
											  MEDIUM_MODE_CONSUMPTION_RPNAME);
			if (simParams.containsKey(lowName)) {
				this.mediumModeConsumption = (double) simParams.get(mediumName);
			}
		String highName =
			ModelI.createRunParameterName(getURI(),
										  HIGH_MODE_CONSUMPTION_RPNAME);
		if (simParams.containsKey(highName)) {
			this.highModeConsumption = (double) simParams.get(highName);
		}

		assert	FanElectricityModel.implementationInvariants(this) :
				new NeoSim4JavaException(
						"FanElectricityModel.implementationInvariants("
						+ "this)");
		assert	FanElectricityModel.invariants(this) :
				new NeoSim4JavaException(
						"FanElectricityModel.invariants(this)");
	}

	// -------------------------------------------------------------------------
	// Optional DEVS simulation protocol: simulation report
	// -------------------------------------------------------------------------

	/**
	 * The class <code>FanElectricityReport</code> implements the
	 * simulation report for the <code>FanElectricityModel</code>.
	 *
	 * <p><strong>Description</strong></p>
	 * 
	 * <p><strong>Glass-box Invariants</strong></p>
	 * 
	 * <pre>
	 * invariant	{@code true}	// no more invariant
	 * </pre>
	 * 
	 * <p><strong>Black-box Invariants</strong></p>
	 * 
	 * <pre>
	 * invariant	{@code true}	// no more invariant
	 * </pre>
	 * 
	 * <p>Created on : 2023-09-29</p>
	 * 
	 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
	 */
	public static class		FanElectricityReport
	implements	SimulationReportI, GlobalReportI
	{
		private static final long serialVersionUID = 1L;
		protected String	modelURI;
		protected double	totalConsumption; // in kwh

		public				FanElectricityReport(
			String modelURI,
			double totalConsumption
			)
		{
			super();
			this.modelURI = modelURI;
			this.totalConsumption = totalConsumption;
		}

		@Override
		public String		getModelURI()
		{
			return this.modelURI;
		}

		@Override
		public String		printout(String indent)
		{
			StringBuffer ret = new StringBuffer(indent);
			ret.append("---\n");
			ret.append(indent);
			ret.append('|');
			ret.append(this.modelURI);
			ret.append(" report\n");
			ret.append(indent);
			ret.append('|');
			ret.append("total consumption in kwh = ");
			ret.append(this.totalConsumption);
			ret.append(".\n");
			ret.append(indent);
			ret.append("---\n");
			return ret.toString();
		}

		/**
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String	toString()
		{
			return this.printout("");
			
		}
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.Model#getFinalReport()
	 */
	@Override
	public SimulationReportI	getFinalReport()
	{
		return new FanElectricityReport(this.getURI(),
											  this.totalConsumption);
	}
}
// -----------------------------------------------------------------------------
