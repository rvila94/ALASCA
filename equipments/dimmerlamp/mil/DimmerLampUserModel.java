package equipments.dimmerlamp.mil;

import equipments.dimmerlamp.DimmerLamp;
import equipments.dimmerlamp.mil.events.*;
import fr.sorbonne_u.devs_simulation.es.events.ES_EventI;
import fr.sorbonne_u.devs_simulation.es.models.AtomicES_Model;
import fr.sorbonne_u.devs_simulation.exceptions.MissingRunParameterException;
import fr.sorbonne_u.devs_simulation.models.annotations.ModelExternalEvents;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.interfaces.ModelI;
import fr.sorbonne_u.devs_simulation.models.time.Duration;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.AtomicSimulatorI;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulationReportI;
import fr.sorbonne_u.devs_simulation.utils.StandardLogger;
import fr.sorbonne_u.exceptions.PostconditionException;
import fr.sorbonne_u.exceptions.PreconditionException;
import org.apache.commons.math3.random.RandomDataGenerator;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * The class <code>equipments.dimmerlamp.mil.DimmerLampUserModel</code>.
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
@ModelExternalEvents(exported = {
        SwitchOnLampEvent.class,
        SwitchOffLampEvent.class,
        SetPowerLampEvent.class
})
public class DimmerLampUserModel
extends AtomicES_Model {

    // -------------------------------------------------------------------------
    // Constants
    // -------------------------------------------------------------------------

    /** time interval between event outputs in hours. */
    protected static double		STEP_MEAN_DURATION = 30.0/60.0; // 30 minutes
    /** time interval between hair dryer usages in hours.					*/
    protected static double		DELAY_MEAN_DURATION = 8.0; // 8 hours
    protected static final String STEP_RUN_PARAMETER = "STEP_MEAN_DURATION";
    protected static final String DELAY_RUN_PARAMETER = "DELAY_MEAN_DURATION";

    protected static final String URI = DimmerLampUserModel.class.getSimpleName();

    /** If true more information will be displayed in the logger */
    public static boolean VERBOSE = false;
    // -------------------------------------------------------------------------
    // Variables
    // -------------------------------------------------------------------------

    protected final RandomDataGenerator rg;

    // -------------------------------------------------------------------------
    // Invariants
    // -------------------------------------------------------------------------

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * create an atomic event scheduling model with the given URI (if null,
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
     * @throws Exception <i>to do</i>.
     */
    public DimmerLampUserModel(String uri, TimeUnit simulatedTimeUnit, AtomicSimulatorI simulationEngine) throws Exception {
        super(uri, simulatedTimeUnit, simulationEngine);

        this.rg = new RandomDataGenerator();
        this.getSimulationEngine().setLogger(new StandardLogger());
    }

    // -------------------------------------------------------------------------
    // Methods
    // -------------------------------------------------------------------------

    protected Time computeTimeOfNextEvent(Time previous_time, double mean) {
        assert previous_time != null :
                new PreconditionException("previous_time == null");

        // "[The exponential distribution] very often occurs in practice as a description
        // of the time elapsing between unpredictable events (such as telephone
        // calls, earthquakes, emissions of radioactive particles, and arrivals
        // of buses, girls and so on)."
        // Geoffrey R. Grimmett and David R. Stirzaker
        // in Probability And Random Processes Fourth Edition
        // In addition, exponential distribution is strictly positive
        // Indeed, if X is an exponential random variable with mean lambda
        // and F is its distribution function we have :
        // F(0) = 1 - exp(-0 * lambda) = 0.0
        double delay = this.rg.nextExponential(mean);

        return previous_time.add(new Duration(delay, this.getSimulatedTimeUnit()));
    }

    protected Time computeTimeOfNextStep(Time previous_time) {
        return this.computeTimeOfNextEvent(previous_time, STEP_MEAN_DURATION);
    }

    protected Time computeTimeOfNextUsage(Time previous_time) {
        return this.computeTimeOfNextEvent(previous_time, DELAY_MEAN_DURATION);
    }

    protected ES_EventI nextLampEvent(EventI previous) {

        ES_EventI result;

        Time previous_time = previous.getTimeOfOccurrence();

        if (previous instanceof SwitchOnLampEvent) {
            Time timeOfOccurrence = this.computeTimeOfNextStep(previous_time);

            final double wattage = rg.nextUniform(
                    DimmerLamp.MIN_POWER_VARIATION.getData(),
                    DimmerLamp.MAX_POWER_VARIATION.getData());
            LampPowerValue power = new LampPowerValue(wattage);

            result = new SetPowerLampEvent(timeOfOccurrence, power);
        } else if (previous instanceof SetPowerLampEvent) {
            Time timeOfOccurrence = this.computeTimeOfNextStep(previous_time);

            result = new SwitchOffLampEvent(timeOfOccurrence);
        } else {
            Time timeOfOccurrence = this.computeTimeOfNextUsage(previous_time);
            result = new SwitchOnLampEvent(timeOfOccurrence);
        }

        return result;
    }

    protected void generateNextEvent() {
        assert ! this.eventList.isEmpty() :
                new PreconditionException("this.eventList.isEmpty()");

        EventI current = this.eventList.peek();

        ES_EventI nextEvent = nextLampEvent(current);

        this.scheduleEvent(nextEvent);

        assert ! this.eventList.isEmpty() :
                new PostconditionException("this.eventList.isEmpty()");
    }

    // -------------------------------------------------------------------------
    // DEVS simulation protocol
    // -------------------------------------------------------------------------

    @Override
    public void initialiseState(Time initialTime){
        super.initialiseState(initialTime);

        this.rg.reSeedSecure();

        Time t = this.computeTimeOfNextStep(this.getCurrentStateTime());

        this.scheduleEvent(new SwitchOnLampEvent(t));

        this.nextTimeAdvance = this.timeAdvance();
        this.timeOfNextEvent =
                this.getCurrentStateTime().add(this.getNextTimeAdvance());

        if (VERBOSE) {
            this.logMessage("simulation begins");
        }
    }

    @Override
    public ArrayList<EventI> output() {

        if (! this.eventList.isEmpty()) {
            this.generateNextEvent();
        }

        return super.output();
    }

    @Override
    public void endSimulation(Time endTime) {
        if (VERBOSE) {
            this.logMessage("simulation ends.");
        }
        super.endSimulation(endTime);
    }

    // -------------------------------------------------------------------------
    // Optional DEVS simulation protocol: simulation run parameters
    // -------------------------------------------------------------------------

    /**
     * @see fr.sorbonne_u.devs_simulation.models.interfaces.ModelI#setSimulationRunParameters
     */
    @Override
    public void setSimulationRunParameters(Map<String, Object> simParams) throws MissingRunParameterException {
        super.setSimulationRunParameters(simParams);

        String stepName =
                ModelI.createRunParameterName(getURI(), STEP_RUN_PARAMETER);
        if (simParams.containsKey(stepName)) {
            STEP_MEAN_DURATION = (double) simParams.get(stepName);
        }

        String usageName =
                ModelI.createRunParameterName(getURI(), DELAY_RUN_PARAMETER);
        if (simParams.containsKey(usageName)) {
            DELAY_MEAN_DURATION = (double) simParams.get(usageName);
        }
    }

    // -------------------------------------------------------------------------
    // Optional DEVS simulation protocol: simulation report
    // -------------------------------------------------------------------------

    /**
     * @see fr.sorbonne_u.devs_simulation.models.interfaces.ModelI#getFinalReport
     */
    @Override
    public SimulationReportI getFinalReport() { return null; }
}
