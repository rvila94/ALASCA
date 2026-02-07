package equipments.HeatPump.simulations.sil;

import equipments.HeatPump.interfaces.HeatPumpUserI;
import equipments.HeatPump.simulations.interfaces.CompleteModelI;
import equipments.HeatPump.simulations.interfaces.ElectricityModelI;
import equipments.HeatPump.simulations.events.*;
import equipments.HeatPump.simulations.interfaces.StateModelI;
import fr.sorbonne_u.components.cyphy.plugins.devs.AtomicSimulatorPlugin;
import fr.sorbonne_u.devs_simulation.exceptions.MissingRunParameterException;
import fr.sorbonne_u.devs_simulation.exceptions.NeoSim4JavaException;
import fr.sorbonne_u.devs_simulation.models.AtomicModel;
import fr.sorbonne_u.devs_simulation.models.annotations.ModelExternalEvents;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.time.Duration;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.AtomicSimulatorI;
import fr.sorbonne_u.devs_simulation.utils.StandardLogger;
import fr.sorbonne_u.exceptions.PostconditionException;
import fr.sorbonne_u.exceptions.PreconditionException;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * The class <code>equipments.HeatPump.simulations.sil.HeatPumpStateModel</code>.
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
@ModelExternalEvents(
        imported = {SwitchOnEvent.class, SwitchOffEvent.class, SetPowerEvent.class,
                StartHeatingEvent.class, StopHeatingEvent.class, StartCoolingEvent.class,
                StopCoolingEvent.class},
        exported = {SwitchOnEvent.class, SwitchOffEvent.class, SetPowerEvent.class,
                StartHeatingEvent.class, StopHeatingEvent.class, StartCoolingEvent.class,
                StopCoolingEvent.class}
)
public class HeatPumpStateModel
extends AtomicModel
implements CompleteModelI {

    public static boolean VERBOSE = true;

    public static String URI = "HEAT-PUMP-STATE-MODEL-URI";

    protected HeatPumpUserI.State currentState;

    /** previous event */
    protected EventI previousEvent;

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
    public HeatPumpStateModel(String uri, TimeUnit simulatedTimeUnit, AtomicSimulatorI simulationEngine) {
        super(uri, simulatedTimeUnit, simulationEngine);

        if ( VERBOSE ) {
            this.getSimulationEngine().setLogger(new StandardLogger());
        }
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
     * @see StateModelI#setCurrentState
     */
    @Override
    public void setCurrentState(HeatPumpUserI.State state) {

        assert state != null :
                new PreconditionException("state == null");

        this.currentState = state;

        assert this.currentState == state :
                new PostconditionException("this.currentState != state");

    }

    /**
     * @see StateModelI#getCurrentState
     */
    @Override
    public HeatPumpUserI.State getCurrentState() {
        return this.currentState;
    }

    /**
     * @see ElectricityModelI#setCurrentPower
     */
    @Override
    public void setCurrentPower(double newPower, Time time) {
        assert time != null:
                new PreconditionException("time == null");
    }

    @Override
    public void initialiseState(Time initialTime)
    {
        super.initialiseState(initialTime);

        this.currentState = HeatPumpUserI.State.Off;
        this.previousEvent = null;

        this.logging("simulation begins.");
    }

    /**
     * @see fr.sorbonne_u.devs_simulation.models.AtomicModel#userDefinedExternalTransition
     */
    @Override
    public void userDefinedExternalTransition(Duration elapsedTime)
    {
        super.userDefinedExternalTransition(elapsedTime);

        // get the vector of current external events
        ArrayList<EventI> currentEvents = this.getStoredEventAndReset();
        // when this method is called, there is at least one external event,
        // and for the hair dryer model, there will be exactly one by
        // construction.
        assert currentEvents != null && currentEvents.size() == 1 :
                new NeoSim4JavaException(
                        "currentEvents != null && currentEvents.size() == 1");

        EventI event = currentEvents.get(0);
        assert event instanceof AbstractHeatPumpEvent :
                new NeoSim4JavaException("event is not an abstract lamp event");

        event.executeOn(this);
        this.previousEvent = event;

        this.logging("this model executes : " + event);
    }

    /**
     * @see fr.sorbonne_u.devs_simulation.models.interfaces.ModelI#setSimulationRunParameters
     */
    @Override
    public void setSimulationRunParameters(Map<String, Object> simParams) throws MissingRunParameterException {

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

    /**
     * @see fr.sorbonne_u.devs_simulation.models.interfaces.ModelI#timeAdvance
     */
    @Override
    public Duration timeAdvance() {
        final Duration zero = Duration.zero(this.getSimulatedTimeUnit());

        return this.previousEvent != null ? zero : Duration.INFINITY;
    }

    /**
     * @see fr.sorbonne_u.devs_simulation.models.interfaces.ModelI#endSimulation
     */
    @Override
    public void endSimulation(Time endTime) {
        this.logging("Simulation ends.");

        super.endSimulation(endTime);
    }

    /**
     * @see fr.sorbonne_u.devs_simulation.models.interfaces.AtomicModelI#output
     */
    @Override
    public ArrayList<EventI> output() {
        ArrayList<EventI> result = null;

        if (this.previousEvent != null) {
            this.logging("output sends : " + this.previousEvent);

            result = new ArrayList<>();
            result.add(this.previousEvent);
            this.previousEvent = null;
        }

        return result;
    }

}
