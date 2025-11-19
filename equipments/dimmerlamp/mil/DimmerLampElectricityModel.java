package equipments.dimmerlamp.mil;

import equipments.dimmerlamp.DimmerLamp;
import equipments.dimmerlamp.DimmerLamp.LampState;
import equipments.dimmerlamp.interfaces.DimmerLampExternalI;
import equipments.dimmerlamp.mil.events.AbstractLampEvent;
import fr.sorbonne_u.components.hem2025e2.equipments.heater.mil.HeaterElectricityModel;
import fr.sorbonne_u.components.hem2025e2.utils.Electricity;
import fr.sorbonne_u.devs_simulation.exceptions.NeoSim4JavaException;
import fr.sorbonne_u.devs_simulation.hioa.annotations.ExportedVariable;
import fr.sorbonne_u.devs_simulation.hioa.models.AtomicHIOA;
import fr.sorbonne_u.devs_simulation.hioa.models.vars.Value;
import fr.sorbonne_u.devs_simulation.models.events.Event;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.time.Duration;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.AtomicSimulatorI;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulationReportI;
import fr.sorbonne_u.devs_simulation.utils.Pair;
import fr.sorbonne_u.devs_simulation.utils.StandardLogger;
import fr.sorbonne_u.exceptions.PostconditionException;
import fr.sorbonne_u.exceptions.PreconditionException;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

/**
 * The class <code>equipments.dimmerlamp.mil.DimmerLampElectricityModel</code>.
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
public class DimmerLampElectricityModel extends AtomicHIOA {

    // -------------------------------------------------------------------------
    // Constants
    // -------------------------------------------------------------------------

    private static final long serialVersionUID = 1L;
    /** when true, leaves a trace of the execution of the model.			*/
    public static boolean VERBOSE = true;
    /** when true, leaves a debugging trace of the execution of the model.	*/
    public static boolean DEBUG = false;

    /** URI for an instance model; works as long as only one instance is
     *  created.															*/
    public static final String URI = DimmerLampElectricityModel.class.
            getSimpleName();

    // -------------------------------------------------------------------------
    // Variables
    // -------------------------------------------------------------------------

    /** current state of the dimmer lamp (ON, OFF) */
    protected LampState currentState;

    /** true when the electricity consumption of the dimmer lamp has changed
     *  after executing an external event; the external event changes the
     *  value of <code>currentState</code> and then an internal transition
     *  will be triggered by putting through in this variable which will
     *  update the variable <code>currentIntensity</code>.					*/
    protected boolean consumptionHasChanged;

    /** total consumption of the heater during the simulation in kwh.		*/
    protected double totalConsumption;

    // -------------------------------------------------------------------------
    // HIOA model variables
    // -------------------------------------------------------------------------

    /** current power of the dimmer lamp between
     * {@code DimmerLamp.MIN_POWER_VARIATION} and {@code DimmerLamp.MAX_POWER_VARIATION}
     * in the power unit used by the dimmer lamp*/
    @ExportedVariable(type = Double.class)
    protected Value<Double> currentPower = new Value(this);

    /** current intensity in the power unit defined by the electric meter.	*/
    @ExportedVariable(type = Double.class)
    protected final Value<Double> currentIntensity = new Value<Double>(this);

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
     * @return true if the static implementation invariants are observed, false otherwise.
     */
    protected static boolean staticImplementationInvariants()
    {
        // TODO
        return true;
    }

    /**
     * return true if the implementation invariants are observed, false otherwise.
     *
     * <p><strong>Contract</strong></p>
     *
     * <pre>
     * pre	{@code instance != null}
     * post	{@code true}	// no postcondition.
     * </pre>
     *
     * @param instance    instance to be tested.
     * @return true if the implementation invariants are observed, false otherwise.
     */
    protected static boolean implementationInvariants(
            HeaterElectricityModel instance
    )
    {
        // TODO
        return true;
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
     * @return true if the invariants are observed, false otherwise.
     */
    public static boolean staticInvariants()
    {
        // TODO
        return true;
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
     * @param instance    instance to be tested.
     * @return true if the invariants are observed, false otherwise.
     */
    protected static boolean invariants(
            HeaterElectricityModel instance
    )
    {
        // TODO
        return true;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * create an atomic hybrid input/output model with the given URI (if null,
     * one will be generated) and to be run by the given simulator using the
     * given time unit for its clock.
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
    public DimmerLampElectricityModel(String uri, TimeUnit simulatedTimeUnit, AtomicSimulatorI simulationEngine) {
        super(uri, simulatedTimeUnit, simulationEngine);
        this.getSimulationEngine().setLogger(new StandardLogger());

        assert DimmerLampElectricityModel.implementationInvariants(this) :
                new NeoSim4JavaException(
                        "HeaterElectricityModel.implementationInvariants(this)");
        assert DimmerLampElectricityModel.invariants(this) :
                new NeoSim4JavaException(
                        "HeaterElectricityModel.invariants(this)");
    }

    // -------------------------------------------------------------------------
    // Private functions
    // -------------------------------------------------------------------------

    /**
     *
     * toogles lamp state
     *
     * <p><strong>Contract</strong></p>
     *
     * <pre>
     * pre {@code this.currentState != null}
     * </pre>
     */
    private void toogleLampState() {

        assert this.currentState != null :
                new PreconditionException("this.currentState == null");

        switch(this.currentState) {
            case ON:
                this.currentState = LampState.OFF;
                break;
            default:
                this.currentState = LampState.ON;
        }
    }

    /**
     *
     * negates the value of {@code this.consuptionHasChanged}
     *
     * <p><strong>Contract</strong></p>
     *
     * <pre>
     * null
     * </pre>
     */
    private void toggleConsumptionHasChanged() {
       this.consumptionHasChanged = !this.consumptionHasChanged;
    }

    /**
     *
     * Computes current power used
     *
     * <p><strong>Contract</strong></p>
     *
     * <pre>
     * null
     * </pre>
     * @return
     */
    private double computePower() {
        // TODO add DimmerLampExternalI.TENSION != null in invariants
        return DimmerLampExternalI.TENSION.getData() * this.currentIntensity.getValue();
    }

    /**
     *
     * Compute current intensity
     *
     * <p><strong>Contract</strong></p>
     *
     * <pre>
     * null
     * </pre>
     * @return
     */
    private double computeIntensity() {
        // TODO add DimmerLampExternalI.TENSION != null in invariants
        // TODO add DimmerLampExternalI.TENSION.getData() != 0
        return this.currentPower.getValue() / DimmerLampExternalI.TENSION.getData();
    }

    /**
     *
     * Compute the total energy consumption
     *
     * <p><strong>Contract</strong></p>
     *
     * <pre>
     *  pre {@code duration != null}
     *  post {@code true}       // no postcodition
     * </pre>
     * @param duration time since the last call to computeTotalConsumption
     */
    private void computeTotalConsumption(Duration duration) {
        final double power = this.computePower();
        this.totalConsumption += Electricity.computeConsumption(duration, power);
    }

    // -------------------------------------------------------------------------
    // Simulation methods
    // -------------------------------------------------------------------------

    /**
     *
     * sets the mode of the dimmer lamp in the simulator
     *
     * <p><strong>Contract</strong></p>
     *
     * <pre>
     *  pre {@code state != null}
     *  post {@code this.currentState == state}
     * </pre>
     *
     * @param state the new state
     */
    public void setState(LampState state) {

        assert state != null :
                new PreconditionException("state == null");

        if (this.currentState != state) {
            this.currentState = state;
            // TODO maybe change that to toggleConsumptionHasChanged
            this.consumptionHasChanged = true;
        }

        assert this.currentState == state :
                new PostconditionException("this.currentState != state");

        assert DimmerLampElectricityModel.implementationInvariants(this):
                new NeoSim4JavaException(
                        "DimmerLampElectricityModel.implementationInvariants(this)");
        assert HeaterElectricityModel.invariants(this) :
                new NeoSim4JavaException(
                        "DimmerLampElectricityModel.invariants(this)");
    }

    /**
     *
     * gets the current state of the dimmer lamp in the simulator
     *
     * <p><strong>Contract</strong></p>
     *
     * <pre>
     *  pre {@code true}        // no precondition
     *  post {@code true}       // no postcondition
     * </pre>
     * @return LampState    the current state of the dimmer lamp
     */
    public LampState getState() {
        return this.currentState;
    }

    /**
     *
     * Sets the power of the lamp
     *
     * <p><strong>Contract</strong></p>
     *
     * <pre>
     * pre {@code DimmerLamp.MIN_POWER_VARIATION.getData() <= newPower && newPower <= DimmerLamp.MAX_POWER_VARIATION.getData()}
     * pre {@code time != null}
     * post {@code this.currentPower.getValue() == newPower}
     * </pre>
     * @param newPower
     * @param time
     */
    public void setDimmerLampPower(double newPower, Time time) {
        assert DimmerLamp.MIN_POWER_VARIATION.getData() <= newPower &&
            newPower <= DimmerLamp.MAX_POWER_VARIATION.getData() :
            new PreconditionException("DimmerLamp.MIN_POWER_VARIATION.getData() > newPower ||" +
                    "newPower < DimmerLamp.MAX_POWER_VARIATION.getData()");
        assert time != null:
                new PreconditionException("time == null");

        double oldPower = this.currentPower.getValue();
        this.currentPower.setNewValue(newPower, time);
        if (newPower != oldPower) {
            // TODO see if can use toggleConsumptionPower
            this.consumptionHasChanged = true;
        }

        assert this.currentPower.getValue() == newPower :
                new PostconditionException("this.currentPower.getValue != newPower");

        assert DimmerLampElectricityModel.implementationInvariants(this):
                new NeoSim4JavaException(
                        "DimmerLampElectricityModel.implementationInvariants(this)");
        assert HeaterElectricityModel.invariants(this) :
                new NeoSim4JavaException(
                        "DimmerLampElectricityModel.invariants(this)");
    }

    // -------------------------------------------------------------------------
    // DEVS simulation protocol
    // -------------------------------------------------------------------------

    /**
     * @see fr.sorbonne_u.devs_simulation.hioa.models.AtomicHIOA#initialiseState(fr.sorbonne_u.devs_simulation.models.time.Time)
     */
    @Override
    public void initialiseState(Time initialTime) {
        super.initialiseState(initialTime);

        this.currentState = LampState.OFF;
        this.consumptionHasChanged = false;
        this.totalConsumption = DimmerLamp.MIN_POWER_VARIATION.getData();

        if (VERBOSE) {
            this.logMessage("Simulation begins.");
        }

        assert DimmerLampElectricityModel.implementationInvariants(this) :
                new NeoSim4JavaException(
                        "HeaterElectricityModel.implementationInvariants(this)");
        assert DimmerLampElectricityModel.invariants(this) :
                new NeoSim4JavaException(
                        "HeaterElectricityModel.invariants(this)");

    }

    /**
     * @see fr.sorbonne_u.devs_simulation.models.interfaces.ModelI#timeAdvance()
     */
    @Override
    public Duration timeAdvance() {
        Duration ret;

        if (this.consumptionHasChanged) {
            // transition to a new state after duration 0
            this.toggleConsumptionHasChanged();
            ret = Duration.zero(this.getSimulatedTimeUnit());
        } else {
            ret = Duration.INFINITY;
        }

        assert HeaterElectricityModel.implementationInvariants(this) :
                new NeoSim4JavaException(
                        "HeaterElectricityModel.implementationInvariants(this)");
        assert HeaterElectricityModel.invariants(this) :
                new NeoSim4JavaException(
                        "HeaterElectricityModel.invariants(this)");

        return ret;
    }

    /**
     * return the simulation report of the last simulation run.
     *
     * <p><strong>Contract</strong></p>
     *
     * <pre>
     * pre	{@code true}	// no precondition.
     * post	{@code true}	// no postcondition.
     * </pre>
     *
     * @return the simulation report of the last simulation run.
     */
    @Override
    public SimulationReportI getFinalReport() {
        return new DimmerLampElectricityReport(this.getURI(), this.totalConsumption);
    }

    /**
     * maps the current internal state to the output set; this method is
     * user-model-dependent hence must be implemented by the user for atomic
     * models.
     *
     * <p>Description</p>
     *
     * <p>
     * Beware that when this method is called, though the simulation time
     * has conceptually reached the time if the next internal event, the
     * value returned by <code>getCurrentStateTime</code> has not yet been
     * updated to that time. Hence the actual current simulation time is
     * given by
     * <code>this.getCurrentStateTime().add(this.getNextTimeAdvance())</code>.
     * </p>
     *
     * <p><strong>Contract</strong></p>
     *
     * <pre>
     * pre	{@code isInitialised()}
     * pre	{@code getNextTimeAdvance().lessThan(Duration.INFINITY)}
     * pre	{@code getNextTimeAdvance().equals(getTimeOfNextEvent().subtract(getCurrentStateTime()))}
     * post	{@code true}	// no postcondition.
     * </pre>
     *
     * @return the corresponding external events or null if none.
     */
    @Override
    public ArrayList<EventI> output() {
        // the model does not export events.
        return null;
    }

    /**
     * return true if the model uses the fixpoint algorithm to initialise its
     * model variables, false otherwise; when true, the method
     * {@code fixpointInitialiseVariables} must be implemented by the model, and
     * when false, it is the method {@code initialiseVariables} that must be.
     *
     * <p><strong>Contract</strong></p>
     *
     * <pre>
     * pre	{@code true}	// no precondition.
     * post	{@code true}	// no postcondition.
     * </pre>
     *
     * @return true if the model uses the fixpoint algorithm to initialise its model variables, false otherwise.
     */
    @Override
    public boolean useFixpointInitialiseVariables() {
        return true;
    }

    /**
     * @see fr.sorbonne_u.devs_simulation.hioa.models.AtomicHIOA#fixpointInitialiseVariables()
     */
    @Override
    public Pair<Integer, Integer> fixpointInitialiseVariables() {
        Pair<Integer, Integer> res;

        if (!this.currentIntensity.isInitialised() || !this.currentPower.isInitialised()) {
            // TODO gotta do it better
            this.currentIntensity.initialise(0.);
            this.currentPower.initialise(DimmerLamp.MIN_POWER_VARIATION.getData());

            if (VERBOSE) {
                this.logMessage("Initialisation of HIOA model variables");
            }

            res = new Pair<>(2, 0);
        } else {
            res = new Pair<>(0, 0);
        }

        return res;
    }

    /**
     * @see fr.sorbonne_u.devs_simulation.models.AtomicModel#userDefinedInternalTransition(fr.sorbonne_u.devs_simulation.models.time.Duration)
     */
    @Override
    public void userDefinedInternalTransition(Duration elapsedTime) {
        super.userDefinedInternalTransition(elapsedTime);

        Time time = this.getCurrentStateTime();
        switch(this.currentState) {
            case ON:
                final double intensity = this.computeIntensity();
                this.currentIntensity.setNewValue(
                        intensity,
                        time);
                break;
            default:
                this.currentIntensity.setNewValue(0.0, time);
        }

        assert DimmerLampElectricityModel.implementationInvariants(this) :
                new NeoSim4JavaException(
                        "DimmerLampElectricityModel.implementationInvariants(this)");
        assert DimmerLampElectricityModel.invariants(this) :
                new NeoSim4JavaException(
                        "DimmerLampElectricityModel.invariants(this)");
    }

    /**
     * @see fr.sorbonne_u.devs_simulation.models.AtomicModel#userDefinedExternalTransition(fr.sorbonne_u.devs_simulation.models.time.Duration)
     */
    @Override
    public void userDefinedExternalTransition(Duration elapsedTime) {
        super.userDefinedExternalTransition(elapsedTime);

        ArrayList<EventI> currentEvents = this.getStoredEventAndReset();
        assert currentEvents != null && currentEvents.size() == 1;

        Event event = (Event) currentEvents.get(0);
        assert event instanceof AbstractLampEvent;

        this.computeTotalConsumption(elapsedTime);

        if (VERBOSE) {
            // TODO
        }

        event.executeOn(this);

        assert	DimmerLampElectricityModel.implementationInvariants(this) :
                new NeoSim4JavaException(
                        "DimmerLampElectricity.implementationInvariants(this)");
        assert	DimmerLampElectricityModel.invariants(this) :
                new NeoSim4JavaException(
                        "DimmerLampElectricity.invariants(this)");
    }

    /**
     * @see fr.sorbonne_u.devs_simulation.hioa.models.AtomicHIOA#endSimulation(fr.sorbonne_u.devs_simulation.models.time.Time)
     */
    @Override
    public void endSimulation(Time endTime)
    {
        Duration d = endTime.subtract(this.getCurrentStateTime());
        this.computeTotalConsumption(d);

        if (VERBOSE) {
            this.logMessage("simulation ends.");
        }

        super.endSimulation(endTime);
    }
}
