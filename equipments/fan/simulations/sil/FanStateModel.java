package equipments.fan.simulations.sil;

import equipments.fan.Fan;
import equipments.fan.FanImplementationI.FanMode;
import equipments.fan.FanImplementationI.FanState;
import fr.sorbonne_u.components.cyphy.plugins.devs.AtomicSimulatorPlugin;import fr.sorbonne_u.devs_simulation.exceptions.MissingRunParameterException;
import fr.sorbonne_u.devs_simulation.exceptions.NeoSim4JavaException;
import fr.sorbonne_u.devs_simulation.models.AtomicModel;
import fr.sorbonne_u.devs_simulation.models.annotations.ModelExternalEvents;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.time.Duration;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.AtomicSimulatorI;
import fr.sorbonne_u.devs_simulation.utils.StandardLogger;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import equipments.fan.simulations.FanSimulationOperationI;
import equipments.fan.simulations.events.*;

/**
 * The class <code>FanStateSILModel</code> defines a simulation model
 * tracking the state changes on a fan.
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
 * <p>Created on : 2026-01-03</p>
 *
 * @author    <a href="mailto:Rodrigo.Vila@etu.sorbonne-universite.fr">Rodrigo Vila</a>
 * @author    <a href="mailto:Damien.Ribeiro@etu.sorbonne-universite.fr">Damien Ribeiro</a>
 */
@ModelExternalEvents(
        imported = {SwitchOnFan.class, SwitchOffFan.class,
                SetLowFan.class, SetMediumFan.class, SetHighFan.class},
        exported = {SwitchOnFan.class, SwitchOffFan.class,
        		SetLowFan.class, SetMediumFan.class, SetHighFan.class}
)
public class FanStateModel
extends AtomicModel
implements FanSimulationOperationI {

    // -------------------------------------------------------------------------
    // Constants and variables
    // -------------------------------------------------------------------------

	private static final long serialVersionUID = 1L;

	public static final String URI = "FAN-STATE-MODEL-URI";

    public static boolean VERBOSE = false;

    protected FanState currentState = FanState.OFF;
    
    protected FanMode currentMode = FanMode.LOW;

    protected EventI previousEvent;

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * create an atomic simulation model with the given URI (if null, one will
     * be generated) and to be run by the given simulator using the given time
     * unit for its clock.
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
     * @param uri               unique identifier of the model.
     * @param simulatedTimeUnit time unit used for the simulation clock.
     * @param simulationEngine  simulation engine enacting the model.
     */
    public FanStateModel(String uri, TimeUnit simulatedTimeUnit, AtomicSimulatorI simulationEngine) {
        super(uri, simulatedTimeUnit, simulationEngine);

        this.getSimulationEngine().setLogger(new StandardLogger());
    }

    // -------------------------------------------------------------------------
    // Helper Methods
    // -------------------------------------------------------------------------

    protected void logging(String message) {
        if ( VERBOSE ) {
            this.logMessage(message);
        }
    }

    // -------------------------------------------------------------------------
    // Methods : State transition
    // -------------------------------------------------------------------------
    
    /**
	 * @see equipments.fan.simulations.FanSimulationOperationI#turnOn()
	 */
	@Override
	public void			turnOn()
	{
		if (this.currentState == FanState.OFF) {
			this.currentState = FanState.ON;
			this.currentMode = Fan.INITIAL_MODE;
		}
	}

	/**
	 * @see equipments.fan.simulations.FanSimulationOperationI#turnOff()
	 */
	@Override
	public void			turnOff()
	{
		if (this.currentState == FanState.ON) {
			this.currentState = FanState.OFF;
			this.currentMode = Fan.INITIAL_MODE;
		}
	}

	/**
	 * @see equipments.fan.simulations.FanSimulationOperationI#setHigh()
	 */
	@Override
	public void			setHigh()
	{
		assert	this.currentState == FanState.ON :
				new NeoSim4JavaException("currentState == FanState.ON");

		if (this.currentMode != FanMode.HIGH) {
			this.currentMode = FanMode.HIGH;
		}
	}
	
	/**
	 * @see equipments.fan.simulations.FanSimulationOperationI#setMedium()
	 */
	@Override
	public void			setMedium()
	{
		assert	this.currentState == FanState.ON :
				new NeoSim4JavaException("currentState == FanState.ON");

		if (this.currentMode != FanMode.MEDIUM) {
			this.currentMode = FanMode.MEDIUM;
		}
	}

	/**
	 * @see equipments.fan.simulations.FanSimulationOperationI#setLow()
	 */
	@Override
	public void			setLow()
	{
		assert	this.currentState == FanState.ON :
			new NeoSim4JavaException("currentState == FanState.ON");

		if (this.currentMode != FanMode.LOW) {
			this.currentMode = FanMode.LOW;
		}
	}
	
	@Override
	public FanState getState() {
		return this.currentState;
	}

	@Override
	public FanMode getMode() {
		return this.currentMode;
	}

    // -------------------------------------------------------------------------
    // Methods : Devs
    // -------------------------------------------------------------------------

    /**
     * @see fr.sorbonne_u.devs_simulation.models.AtomicModel#initialiseState(fr.sorbonne_u.devs_simulation.models.time.Time)
     */
    @Override
    public void initialiseState(Time initialTime)
    {
        super.initialiseState(initialTime);

        this.currentState = FanState.OFF;
        this.previousEvent = null;

        this.logging("simulation begins.");
    }

    @Override
    public ArrayList<EventI> output()
    {
        ArrayList<EventI> result = null;

        if (this.previousEvent != null) {
            this.logging("output sends : " + this.previousEvent);

            result = new ArrayList<>();
            result.add(this.previousEvent);
            this.previousEvent = null;
        }

        return result;
    }

    /**
     * @see fr.sorbonne_u.devs_simulation.models.interfaces.ModelI#timeAdvance
     */
    @Override
    public Duration timeAdvance() {
        final Duration zero = Duration.zero(this.getSimulatedTimeUnit());

        if (this.previousEvent != null) {
            return zero;
        } else {
            return Duration.INFINITY;
        }
    }

    /**
     * @see fr.sorbonne_u.devs_simulation.models.AtomicModel#endSimulation(fr.sorbonne_u.devs_simulation.models.time.Time)
     */
    @Override
    public void endSimulation(Time endTime)
    {
        this.logging("simulation ends.");

        super.endSimulation(endTime);
    }

    /**
     * @see fr.sorbonne_u.devs_simulation.models.AtomicModel#userDefinedExternalTransition(fr.sorbonne_u.devs_simulation.models.time.Duration)
     */
    @Override
    public void userDefinedExternalTransition(Duration elapsedTime)
    {
        super.userDefinedExternalTransition(elapsedTime);

        // get the vector of current external events
        ArrayList<EventI> currentEvents = this.getStoredEventAndReset();
        // when this method is called, there is at least one external event,
        // and for the fan model, there will be exactly one by
        // construction.
        assert currentEvents != null && currentEvents.size() == 1 :
                new NeoSim4JavaException(
                        "currentEvents != null && currentEvents.size() == 1");

        EventI event = currentEvents.get(0);
        assert event instanceof AbstractFanEvent :
                new NeoSim4JavaException("event is not an abstract fan event");

        event.executeOn(this);
        this.previousEvent = event;

        this.logging("this model executes : " + event);
    }

    // -------------------------------------------------------------------------
    // Optional DEVS simulation protocol: simulation run parameters
    // -------------------------------------------------------------------------

    @Override
    public void			setSimulationRunParameters(
            Map<String, Object> simParams
    ) throws MissingRunParameterException
    {
        super.setSimulationRunParameters(simParams);

        // this gets the reference on the owner component which is required
        // to have simulation models able to make the component perform some
        // operations or tasks or to get the value of variables held by the
        // component when necessary.
        if (simParams.containsKey(
                AtomicSimulatorPlugin.OWNER_RUNTIME_PARAMETER_NAME)) {
            // by the following, all of the logging will appear in the owner
            // component logger
            this.getSimulationEngine().setLogger(
                    AtomicSimulatorPlugin.createComponentLogger(simParams));
        }
    }
}
