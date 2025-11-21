package equipments.dimmerlamp.mil.events;

import equipments.dimmerlamp.mil.DimmerLampElectricityModel;
import fr.sorbonne_u.devs_simulation.es.events.ES_Event;
import fr.sorbonne_u.devs_simulation.exceptions.NeoSim4JavaException;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.events.EventInformationI;
import fr.sorbonne_u.devs_simulation.models.interfaces.AtomicModelI;
import fr.sorbonne_u.devs_simulation.models.time.Time;

/**
 * The class <code>equipments.dimmerlamp.mil.events.AbstractLampEvent</code>.
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
public abstract class AbstractLampEvent extends ES_Event {

    // -------------------------------------------------------------------------
    // Constants and variables
    // -------------------------------------------------------------------------

    private static final long serialVersionUID = 1L;

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
    public AbstractLampEvent(Time timeOfOccurrence, EventInformationI content) {
        super(timeOfOccurrence, content);
    }

    // -------------------------------------------------------------------------
    // methods
    // -------------------------------------------------------------------------

    /**
     * @see fr.sorbonne_u.devs_simulation.models.events.Event#executeOn(fr.sorbonne_u.devs_simulation.models.interfaces.AtomicModelI)
     */
    @Override
    public void executeOn(AtomicModelI model)
    {
        assert model instanceof DimmerLampElectricityModel :
                new NeoSim4JavaException(
                        "Precondition violation: model instanceof "
                                + "DimmerLampElectricityModel");
    }

    public enum PriorityIndex {
        SwitchOnEvent,
        SetPowerLampEvent,
        SwitchOffEvent
    }

    public static boolean priorityInvariant(AbstractLampEvent event) {
        switch(event.priorityIndex()) {
            case SwitchOnEvent:
                return event instanceof SwitchOnLampEvent;
            case SwitchOffEvent:
                return event instanceof SwitchOffLampEvent;
            case SetPowerLampEvent:
                return event instanceof SetPowerLampEvent;
            default:
                return false;
        }

    }

    /**
     * returns an enum indicating the priority of the event over the others
     *
     * <p><strong>Contract</strong></p>
     *
     * <pre>
     *  pre {@code true} // no pre condition
     *  post {@code true} // no post condition
     * </pre>
     * @return the PriorityIndex associated with the class of default
     */
    protected abstract PriorityIndex priorityIndex();

    private boolean hasPriorityOver(AbstractLampEvent event){
        return this.priorityIndex().compareTo(event.priorityIndex()) <= 0;
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
        assert event instanceof AbstractLampEvent :
                new NeoSim4JavaException("event is not a lamp event");

        AbstractLampEvent lamp_event = (AbstractLampEvent) event;

        return this.hasPriorityOver(lamp_event);
    }

}
