package equipments.HeatPump.mil.events;

import fr.sorbonne_u.devs_simulation.es.events.ES_Event;
import fr.sorbonne_u.devs_simulation.exceptions.NeoSim4JavaException;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.events.EventInformationI;
import fr.sorbonne_u.devs_simulation.models.time.Time;

/**
 * The class <code>equipments.HeatPump.mil.events.AbstractHeatPumpEvent</code>.
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
public abstract class AbstractHeatPumpEvent extends ES_Event {

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * create an event from the given time of occurrence and event description.
     *
     * <p><strong>Contract</strong></p>
     *
     * <pre>
     * pre	{@code timeOfOccurrence != null}
     * post	{@code getTimeOfOccurrence().equals(timeOfOccurrence)}
     * post	{@code content == null || getEventInformation().equals(content)}
     * post	{@code !isCancelled()}
     * </pre>
     *
     * @param timeOfOccurrence time of occurrence of the created event.
     * @param content          description of the created event.
     */
    public AbstractHeatPumpEvent(Time timeOfOccurrence, EventInformationI content) {
        super(timeOfOccurrence, content);
    }

    // -------------------------------------------------------------------------
    // Priority
    // -------------------------------------------------------------------------

    protected enum PriorityIndex {
        SwitchOnEvent(0),
        StopHeatingEvent(1),
        StopCoolingEvent(1),
        StartHeatingEvent(2),
        StartCoolingEvent(2),
        SetPowerEvent(3),
        SwitchOffEvent(4);

        private int code;

        PriorityIndex(int code) {
            this.code = code;
        }

        public int compare(PriorityIndex other) {
            return Integer.compare(this.code, other.code);
        }

    }

    public static boolean priorityInvariant(AbstractHeatPumpEvent event) {
        switch(event.priorityIndex()) {
            default:
                return false;
        }
    }

    protected abstract PriorityIndex priorityIndex();

    private boolean hasPriorityOver(AbstractHeatPumpEvent event) {
        return this.priorityIndex().compare(event.priorityIndex()) <= 0;
    }

    /**
     * <pre>
     *  pre {@code event instanceof AbstractLampEvent}
     *  post {@code true} // no post condition
     * </pre>
     *
     * @see fr.sorbonne_u.devs_simulation.es.events.ES_Event#hasPriorityOver
     */
    @Override
    public boolean hasPriorityOver(EventI event) {
        assert event instanceof AbstractHeatPumpEvent :
                new NeoSim4JavaException("event is not a lamp event");

        AbstractHeatPumpEvent heat_pump_event = (AbstractHeatPumpEvent) event;

        return this.hasPriorityOver(heat_pump_event);
    }

}
