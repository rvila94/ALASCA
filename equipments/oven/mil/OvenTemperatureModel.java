package equipments.oven.mil;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import equipments.oven.mil.events.DoNotHeat;
import equipments.oven.mil.events.Heat;
import equipments.oven.mil.events.OvenEventI;
import equipments.oven.mil.events.SetModeOven;
import equipments.oven.mil.events.SetTargetTemperatureOven;
import equipments.oven.mil.events.SwitchOffOven;
import equipments.oven.Oven;
import equipments.oven.Oven.OvenMode;
import equipments.oven.Oven.OvenState;
import equipments.oven.OvenExternalControlI;
import fr.sorbonne_u.components.hem2025e2.GlobalReportI;
import fr.sorbonne_u.devs_simulation.exceptions.NeoSim4JavaException;
import fr.sorbonne_u.devs_simulation.hioa.annotations.ExportedVariable;
import fr.sorbonne_u.devs_simulation.hioa.annotations.ImportedVariable;
import fr.sorbonne_u.devs_simulation.hioa.annotations.InternalVariable;
import fr.sorbonne_u.devs_simulation.hioa.annotations.ModelExportedVariable;
import fr.sorbonne_u.devs_simulation.hioa.annotations.ModelImportedVariable;
import fr.sorbonne_u.devs_simulation.hioa.models.AtomicHIOA;
import fr.sorbonne_u.devs_simulation.hioa.models.vars.DerivableValue;
import fr.sorbonne_u.devs_simulation.hioa.models.vars.Value;
import fr.sorbonne_u.devs_simulation.models.annotations.ModelExternalEvents;
import fr.sorbonne_u.devs_simulation.models.events.Event;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.time.Duration;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.AtomicSimulatorI;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulationReportI;
import fr.sorbonne_u.devs_simulation.utils.Pair;
import fr.sorbonne_u.devs_simulation.utils.StandardLogger;
import fr.sorbonne_u.devs_simulation.utils.AssertionChecking;

// -----------------------------------------------------------------------------
/**
 * The class <code>OvenTemperatureModel</code> defines a simulation model
 * for the temperature inside the chamber of an oven.
 *
 * <p><strong>Description</strong></p>
 *
 * <p>
 * The model is implemented as an atomic HIOA model. A differential equation
 * defines the temperature variation over time inside the oven chamber.
 * The temperature evolves under the influence of two factors:
 * </p>
 *
 * <ol>
 * <li>the heating power delivered by the oven when it is in the
 *     <code>HEATING</code> state, which pushes the chamber temperature
 *     toward the current <code>targetTemperature</code>;</li>
 *
 * <p>
 * The resulting differential equation is integrated using the Euler method
 * with a predefined integration step.
 * </p>
 *
 * <p>
 * The temperature dynamics depend on the current state (ON, HEATING, etc.)
 * and the current heating power from the electricity model. The
 * <code>Heat</code> and <code>DoNotHeat</code> events update the state to
 * determine whether the oven should actively heat or not.
 * </p>
 *
 * <ul>
 * <li>Imported events:
 *   <code>SwitchOffOven</code>,
 *   <code>Heat</code>,
 *   <code>DoNotHeat</code>,
 *   <code>SetModeOven</code>
 *   <code>SetTargetTemperatureOven</code></li>
 *
 * <li>Exported events: none</li>
 *
 * <li>Imported variables:
 *   <code>currentHeatingPower</code></li>
 *
 * <li>Exported variables:
 *   <code>currentTemperature</code>,
 *   <code>targetTemperature</code></li>
 * </ul>
 * 
 * <p>Created on : 2025-11-13</p>
 * 
 * @author	<a href="mailto:Rodrigo.Vila@etu.sorbonne-universite.fr">Rodrigo Vila</a>
 * @author	<a href="mailto:Damien.Ribeiro@etu.sorbonne-universite.fr">Damien Ribeiro</a>
 */
@ModelExternalEvents(imported = {SwitchOffOven.class,
		 						 Heat.class,
		 						 DoNotHeat.class,
								 SetModeOven.class,
								 SetTargetTemperatureOven.class})
@ModelExportedVariable(name = "targetTemperature", type = Double.class)
@ModelImportedVariable(name = "currentHeatingPower", type = Double.class)
// -----------------------------------------------------------------------------
public class			OvenTemperatureModel
extends		AtomicHIOA
{
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	private static final long		serialVersionUID = 1L;

	// The following variables should be considered constant but can be changed
	// before the first model instance is created to adapt the simulation
	// scenario.

	/** URI for a model; works when only one instance is created.			*/
	public static String		URI = OvenTemperatureModel.class.
															getSimpleName();
	/** when true, leaves a trace of the execution of the model.			*/
	public static boolean		VERBOSE = true;
	/** when true, leaves a debugging trace of the execution of the model.	*/
	public static boolean		DEBUG = false;

	// TODO: define as simulation run parameters
	private static double AMBIENT_TEMPERATURE = 20.0;
	/** temperature when the simulation begins.			*/
	public static double		INITIAL_TEMPERATURE = 22.0;
	/** Represents how quickly the oven loses heat to its environment */
	protected static double 	COOLING_TRANSFER_CONSTANT = 1000.0;
	/** heating transfer constant in the differential equation when the
	 *  heating power is maximal.											*/
	protected static double		MIN_HEATING_TRANSFER_CONSTANT = 1.0;
	/** update tolerance for the temperature <i>i.e.</i>, shortest elapsed
	 *  time since the last update under which the temperature is not
	 *  changed by the update to avoid too large computation errors.		*/
	protected static double		TEMPERATURE_UPDATE_TOLERANCE = 0.0001;
	/** the minimal power under which the temperature derivative must be 0.	*/
	protected static double		POWER_HEAT_TRANSFER_TOLERANCE = 0.0001;
	/** integration step for the differential equation(assumed in hours).	*/
	protected static double		STEP = 60.0/3600.0;	// 60 seconds

	/** current state of the Oven; for the temperature model, only two
	 *  states are relevant: heating, which is {@code OvenState.HEATING},
	 *  or notheating, which assimilates to on or waiting but not heating <i>i.e.</i>,
	 *  {@code OvenState.ON}.												*/
	protected OvenState		currentState = OvenState.ON;
	protected OvenMode 		currentMode  = OvenMode.CUSTOM;

	// Simulation run variables

	/** integration step as a duration, including the time unit.			*/
	protected final Duration	integrationStep;
	/** accumulator to compute the mean external temperature for the
	 *  simulation report.													*/
	protected double			temperatureAcc;
	/** the simulation time of start used to compute the mean temperature.	*/
	protected Time				start;
	/** the mean temperature over the simulation duration for the simulation
	 *  report.																*/
	protected double			meanTemperature;

	// -------------------------------------------------------------------------
	// HIOA model variables
	// -------------------------------------------------------------------------

	/** the current heating power between 0 and
	 *  {@code OvenElectricityModel.MAX_HEATING_POWER}.					*/
	@ImportedVariable(type = Double.class)
	protected Value<Double>					currentHeatingPower;
	/** current temperature in the oven.									*/
	@InternalVariable(type = Double.class)
	protected final DerivableValue<Double>	currentTemperature =
											new DerivableValue<Double>(this);
	@ExportedVariable(type = Double.class)
	protected final Value<Double> targetTemperature = new Value<Double>(this);

	// -------------------------------------------------------------------------
	// Invariants
	// -------------------------------------------------------------------------

	/**
	 * return true if the static implementation invariants are observed, false
	 * otherwise.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @return	true if the static implementation invariants are observed, false otherwise.
	 */
	protected static boolean	staticImplementationInvariants()
	{
		boolean ret = true;
		ret &= AssertionChecking.checkStaticImplementationInvariant(
				TEMPERATURE_UPDATE_TOLERANCE >= 0.0,
				OvenTemperatureModel.class,
				"TEMPERATURE_UPDATE_TOLERANCE >= 0.0");
		ret &= AssertionChecking.checkStaticImplementationInvariant(
				POWER_HEAT_TRANSFER_TOLERANCE >= 0.0,
				OvenTemperatureModel.class,
				"POWER_HEAT_TRANSFER_TOLERANCE >= 0.0");
		ret &= AssertionChecking.checkStaticImplementationInvariant(
				COOLING_TRANSFER_CONSTANT > 0.0,
				OvenTemperatureModel.class,
				"COOLING_TRANSFER_CONSTANT > 0.0");
		ret &= AssertionChecking.checkStaticImplementationInvariant(
				MIN_HEATING_TRANSFER_CONSTANT > 0.0,
				OvenTemperatureModel.class,
				"MIN_HEATING_TRANSFER_CONSTANT > 0.0");
		return ret;
	}

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
		OvenTemperatureModel instance
		)
	{
		assert	instance != null :
				new NeoSim4JavaException("Precondition violation: "
						+ "instance != null");

		boolean ret = true;
		ret &= staticImplementationInvariants();
		ret &= AssertionChecking.checkImplementationInvariant(
				instance.currentState != null,
				OvenTemperatureModel.class,
				instance,
				"currentState != null");
		ret &= AssertionChecking.checkImplementationInvariant(
				instance.currentMode != null,
				OvenTemperatureModel.class,
				instance,
				"currentMode != null");
		ret &= AssertionChecking.checkImplementationInvariant(
				instance.integrationStep.getSimulatedDuration() > 0.0,
				OvenTemperatureModel.class,
				instance,
				"integrationStep.getSimulatedDuration() > 0.0");
		ret &= AssertionChecking.checkImplementationInvariant(
				!instance.isStateInitialised() || instance.start != null,
				OvenTemperatureModel.class,
				instance,
				"!isStateInitialised() || start != null");
		ret &= AssertionChecking.checkImplementationInvariant(
				instance.currentHeatingPower == null ||
					(!instance.currentHeatingPower.isInitialised() ||
								instance.currentHeatingPower.getValue() >= 0.0),
				OvenTemperatureModel.class,
				instance,
				"currentHeatingPower == null || "
				+ "(!currentHeatingPower.isInitialised() || "
				+ "currentHeatingPower.getValue() >= 0.0)");
		ret &= AssertionChecking.checkImplementationInvariant(
				instance.currentTemperature != null,
				OvenTemperatureModel.class,
				instance,
				"currentTemperature != null");
		ret &= AssertionChecking.checkImplementationInvariant(
				instance.targetTemperature != null,
				OvenTemperatureModel.class,
				instance,
				"targetTemperature != null");
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
		ret &= OvenSimulationConfigurationI.staticInvariants();
		ret &= AssertionChecking.checkStaticInvariant(
				URI != null && !URI.isEmpty(),
				OvenTemperatureModel.class,
				"URI != null && !URI.isEmpty()");
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
		OvenTemperatureModel instance
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
	 * create a <code>OvenTemperatureModel</code> instance.
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
	public				OvenTemperatureModel(
		String uri,
		TimeUnit simulatedTimeUnit,
		AtomicSimulatorI simulationEngine
		) throws Exception
	{
		super(uri, simulatedTimeUnit, simulationEngine);

		this.integrationStep = new Duration(STEP, simulatedTimeUnit);
		this.getSimulationEngine().setLogger(new StandardLogger());

		assert	OvenTemperatureModel.implementationInvariants(this) :
				new NeoSim4JavaException(
						"OvenTemperatureModel.implementationInvariants(this)");
		assert	OvenTemperatureModel.invariants(this) :
				new NeoSim4JavaException(
						"OvenTemperatureModel.implementationInvariants(this)");
	}

	// -------------------------------------------------------------------------
	// Methods
	// -------------------------------------------------------------------------

	/**
	 * set the state of the Oven.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code s != null}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param s		the new state.
	 */
	public void			setState(OvenState s)
	{
		this.currentState = s;

		assert	OvenTemperatureModel.implementationInvariants(this) :
				new NeoSim4JavaException(
						"OvenTemperatureModel.implementationInvariants(this)");
		assert	OvenTemperatureModel.invariants(this) :
				new NeoSim4JavaException(
						"OvenTemperatureModel.implementationInvariants(this)");
	}
	
	/**
	 * set the mode of the Oven.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code mode != null}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param mode		the new mode.
	 */
	public void setMode(OvenMode mode, Time t) {
	    assert mode != null;

	    OvenMode old = this.currentMode;
	    this.currentMode = mode;

	    if (mode != OvenMode.CUSTOM)
	        this.targetTemperature.setNewValue(
	        				Oven.MODE_TEMPERATURES.get(mode).getData(), t);
	     
	    if (VERBOSE) {
	        this.logMessage("OvenTemperatureModel: mode changed from "
	                        + old + " to " + mode + ". New target temperature = "
	                        + this.targetTemperature);
	    }
	}
	
	public void setTargetTemperature(Double targetTemperature, Time t) {
		assert targetTemperature != null;
		
		if (this.currentMode == OvenMode.CUSTOM)
			this.targetTemperature.setNewValue(targetTemperature, t);
		
		if (VERBOSE) {
			this.logMessage("OvenTemperatureModel: targetTemperature changed to "
		                        + this.targetTemperature);
		}
	}

	/**
	 * return the state of the Oven.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code ret != null}
	 * </pre>
	 *
	 * @return	the current state.
	 */
	public OvenState	getState()
	{
		return this.currentState;
	}
	
	/**
	 * return the mode of the Oven.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code ret != null}
	 * </pre>
	 *
	 * @return	the current mode.
	 */
	public OvenMode	getMode()
	{
		return this.currentMode;
	}

	/**
	 * compute the current heat transfer constant given the current heating
	 * power of the Oven.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @return	the current heat transfer constant.
	 */
	protected double	currentHeatTransfertConstant()
	{
		// the following formula is just a mathematical trick to get a heat
		// transfer constant that grows as the power gets lower, hence the
		// derivative given by the differential equation will be lower when
		// the power gets lower, what is physically awaited.
		double c = 1.0/(MIN_HEATING_TRANSFER_CONSTANT *
							OvenExternalControlI.MAX_POWER_LEVEL.getData());
		double res =  1.0/(c*this.currentHeatingPower.getValue());
		return res;
	}

	/**
	 * compute the current derivative of the oven internal temperature.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param current	current temperature of the room.
	 * @return			the current derivative.
	 */
	protected double	computeDerivatives(Double current)
	{
		double currentTempDerivative = 0.0;
		if (this.currentState == OvenState.HEATING) {
			if (this.currentHeatingPower.getValue() >
												POWER_HEAT_TRANSFER_TOLERANCE) {
				currentTempDerivative =
						(this.targetTemperature.getValue() - current)/
											this.currentHeatTransfertConstant();
			}
		}
		currentTempDerivative +=
				(AMBIENT_TEMPERATURE - current) / COOLING_TRANSFER_CONSTANT;
		
		return currentTempDerivative;
	}

	/**
	 * compute the current temperature given that a duration of {@code deltaT}
	 * has elapsed since the last update.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code deltaT >= 0.0}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param deltaT	the duration of the step since the last update.
	 * @return			the new temperature in celsius.
	 */
	protected double	computeNewTemperature(double deltaT)
	{
		Time t = this.currentTemperature.getTime();
		double oldTemp = this.currentTemperature.evaluateAt(t);
		double newTemp;

		if (deltaT > TEMPERATURE_UPDATE_TOLERANCE) {
			double derivative = this.currentTemperature.getFirstDerivative();
			newTemp = oldTemp + derivative*deltaT;
		} else {
			newTemp = oldTemp;
		}

		// accumulate the temperature*time to compute the mean temperature
		this.temperatureAcc += ((oldTemp + newTemp)/2.0) * deltaT;
		return newTemp;
	}

	// -------------------------------------------------------------------------
	// DEVS simulation protocol
	// -------------------------------------------------------------------------

	/**
	 * 
	 * @see fr.sorbonne_u.devs_simulation.hioa.models.AtomicHIOA#initialiseState(fr.sorbonne_u.devs_simulation.models.time.Time)
	 */
	@Override
	public void			initialiseState(Time initialTime)
	{
		this.temperatureAcc = 0.0;
		this.start = initialTime;

		if (VERBOSE) {
			this.logMessage("simulation begins.");
		}

		super.initialiseState(initialTime);

//		System.out.println("OvenTemperatureModel::initialiseState "
//				   + this.getURI() + " "
//				   + this.simulationEngine.debugLevel);

		assert	OvenTemperatureModel.implementationInvariants(this) :
				new NeoSim4JavaException(
						"OvenTemperatureModel.implementationInvariants(this)");
		assert	OvenTemperatureModel.invariants(this) :
				new NeoSim4JavaException(
						"OvenTemperatureModel.implementationInvariants(this)");
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.hioa.models.interfaces.VariableInitialisationI#useFixpointInitialiseVariables()
	 */
	@Override
	public boolean		useFixpointInitialiseVariables()
	{
		return true;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.hioa.models.interfaces.VariableInitialisationI#fixpointInitialiseVariables()
	 */
	@Override
	public Pair<Integer, Integer>	fixpointInitialiseVariables()
	{
		int justInitialised = 0;
		int notInitialisedYet = 0;
		
		if (!this.currentTemperature.isInitialised()) {
	        double derivative = this.computeDerivatives(INITIAL_TEMPERATURE);
	        this.currentTemperature.initialise(INITIAL_TEMPERATURE, derivative);
	        justInitialised++;
	    }
		
		if (!this.targetTemperature.isInitialised()) {
	        this.targetTemperature.initialise(0.0);
	        justInitialised++;
	    }

		assert	OvenTemperatureModel.implementationInvariants(this) :
				new NeoSim4JavaException(
						"OvenTemperatureModel.implementationInvariants(this)");
		assert	OvenTemperatureModel.invariants(this) :
				new NeoSim4JavaException(
						"OvenTemperatureModel.implementationInvariants(this)");

		return new Pair<>(justInitialised, notInitialisedYet);
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.AtomicModelI#output()
	 */
	@Override
	public ArrayList<EventI>	output()
	{
		return null;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.ModelI#timeAdvance()
	 */
	@Override
	public Duration		timeAdvance()
	{
		return this.integrationStep;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.AtomicModel#userDefinedInternalTransition(fr.sorbonne_u.devs_simulation.models.time.Duration)
	 */
	@Override
	public void			userDefinedInternalTransition(Duration elapsedTime)
	{
		// First, update the temperature (i.e., the value of the continuous
		// variable) until the current time.
		double newTemp =
				this.computeNewTemperature(elapsedTime.getSimulatedDuration());
		// Next, compute the new derivative
		double newDerivative = this.computeDerivatives(newTemp);
		// Finally, set the new temperature value and derivative
		this.currentTemperature.setNewValue(
						newTemp,
						newDerivative,
						new Time(this.getCurrentStateTime().getSimulatedTime(),
								 this.getSimulatedTimeUnit()));

		// Tracing
		if (VERBOSE) {
			String mark = this.currentState == OvenState.HEATING ? " (h)" : " (-)";
			StringBuffer message = new StringBuffer();
			message.append(this.currentTemperature.getTime().getSimulatedTime());
			message.append(mark);
			message.append(" : ");
			message.append(this.currentTemperature.getValue());
			this.logMessage(message.toString());
		}

		super.userDefinedInternalTransition(elapsedTime);

		assert	OvenTemperatureModel.implementationInvariants(this) :
				new NeoSim4JavaException(
						"OvenTemperatureModel.implementationInvariants(this)");
		assert	OvenTemperatureModel.invariants(this) :
				new NeoSim4JavaException(
						"OvenTemperatureModel.implementationInvariants(this)");
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.AtomicModel#userDefinedExternalTransition(fr.sorbonne_u.devs_simulation.models.time.Duration)
	 */
	@Override
	public void			userDefinedExternalTransition(Duration elapsedTime)
	{
		// get the vector of current external events
		ArrayList<EventI> currentEvents = this.getStoredEventAndReset();
		// when this method is called, there is at least one external event,
		// and for the Oven model, there will be exactly one by
		// construction.
		assert	currentEvents != null && currentEvents.size() == 1;

		Event ce = (Event) currentEvents.get(0);
		assert	ce instanceof OvenEventI;

		if (VERBOSE) {
			StringBuffer sb = new StringBuffer("executing the external event: ");
			sb.append(ce.eventAsString());
			sb.append(".");
			this.logMessage(sb.toString());
		}

		// First, update the temperature (i.e., the value of the continuous
		// variable) until the current time.
		double newTemp =
				this.computeNewTemperature(elapsedTime.getSimulatedDuration());
		// Then, update the current state of the Oven.
		ce.executeOn(this);
		// Next, compute the new derivative
		double newDerivative = this.computeDerivatives(newTemp);
		// Finally, set the new temperature value and derivative
		this.currentTemperature.setNewValue(
					newTemp,
					newDerivative,
					new Time(this.getCurrentStateTime().getSimulatedTime()
										+ elapsedTime.getSimulatedDuration(),
							 this.getSimulatedTimeUnit()));

		super.userDefinedExternalTransition(elapsedTime);

		assert	OvenTemperatureModel.implementationInvariants(this) :
				new NeoSim4JavaException(
						"OvenTemperatureModel.implementationInvariants(this)");
		assert	OvenTemperatureModel.invariants(this) :
				new NeoSim4JavaException(
						"OvenTemperatureModel.implementationInvariants(this)");
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.hioa.models.AtomicHIOA#endSimulation(fr.sorbonne_u.devs_simulation.models.time.Time)
	 */
	@Override
	public void			endSimulation(Time endTime)
	{
		this.meanTemperature =
				this.temperatureAcc/
						endTime.subtract(this.start).getSimulatedDuration();

		if (VERBOSE) {
			this.logMessage("simulation ends.");
		}
		super.endSimulation(endTime);
	}

	// -------------------------------------------------------------------------
	// Optional DEVS simulation protocol: simulation report
	// -------------------------------------------------------------------------

	/**
	 * The class <code>OvenTemperatureReport</code> implements the
	 * simulation report for the <code>OvenTemperatureModel</code>.
	 *
	 * <p><strong>Description</strong></p>
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
	 * <p>Created on : 2023-09-29</p>
	 * 
	 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
	 */
	public static class		OvenTemperatureReport
	implements	SimulationReportI, GlobalReportI
	{
		private static final long serialVersionUID = 1L;
		protected String	modelURI;
		protected double	meanTemperature;

		public			OvenTemperatureReport(
			String modelURI,
			double meanTemperature
			)
		{
			super();
			this.modelURI = modelURI;
			this.meanTemperature = meanTemperature;
		}

		@Override
		public String	getModelURI()
		{
			return this.modelURI;
		}

		@Override
		public String	printout(String indent)
		{
			StringBuffer ret = new StringBuffer(indent);
			ret.append("---\n");
			ret.append(indent);
			ret.append('|');
			ret.append(this.modelURI);
			ret.append(" report\n");
			ret.append(indent);
			ret.append('|');
			ret.append("mean temperature = ");
			ret.append(this.meanTemperature);
			ret.append(".\n");
			ret.append(indent);
			ret.append("---\n");
			return ret.toString();
		}
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.ModelI#getFinalReport()
	 */
	@Override
	public SimulationReportI	getFinalReport()
	{
		return new OvenTemperatureReport(this.getURI(), this.meanTemperature);
	}
}
// -----------------------------------------------------------------------------
