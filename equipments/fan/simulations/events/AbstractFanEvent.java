package equipments.fan.simulations.events;

import equipments.fan.simulations.FanSimulationOperationI;
import fr.sorbonne_u.devs_simulation.es.events.ES_Event;
import fr.sorbonne_u.devs_simulation.exceptions.NeoSim4JavaException;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.events.EventInformationI;
import fr.sorbonne_u.devs_simulation.models.interfaces.AtomicModelI;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import fr.sorbonne_u.exceptions.PreconditionException;

/**
 * The abstract class <code>AbstractFanEvent</code> enforces a common
 * type for all fan simulation events.
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
 * <p>Created on : 2025-11-11</p>
 * 
 * @author	<a href="mailto:Rodrigo.Vila@etu.sorbonne-universite.fr">Rodrigo Vila</a>
 * @author	<a href="mailto:Damien.Ribeiro@etu.sorbonne-universite.fr">Damien Ribeiro</a>
 */
public abstract class			AbstractFanEvent
extends		ES_Event
{
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	private static final long serialVersionUID = 1L;

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * used to create an event used by the fan simulation model.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code timeOfOccurrence != null}
	 * post	{@code getTimeOfOccurrence().equals(timeOfOccurrence)}
	 * post	{@code content == null || getEventInformation().equals(content)}
	 * </pre>
	 *
	 * @param timeOfOccurrence	time of occurrence of the event.
	 * @param content			content (data) associated with the event.
	 */
	public				AbstractFanEvent(
		Time timeOfOccurrence,
		EventInformationI content
		)
	{
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
        assert model != null :
                new PreconditionException("model == null");
        assert model instanceof FanSimulationOperationI :
                new PreconditionException(
                        "Precondition violation: model instanceof "
                                + "FanSimulationOperationI");
    }

    protected enum PriorityIndex {
        SwitchOnEvent,
        SetHighEvent,
        SetMediumEvent,
        SetLowEvent,
        SwitchOffEvent
    }

    public static boolean priorityInvariant(AbstractFanEvent event) {
        switch(event.priorityIndex()) {
            case SwitchOnEvent:
                return event instanceof SwitchOnFan;
            case SwitchOffEvent:
                return event instanceof SwitchOffFan;
            case SetHighEvent:
                return event instanceof SetHighFan;
            case SetMediumEvent:
                return event instanceof SetMediumFan;
            case SetLowEvent:
                return event instanceof SetLowFan;
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

    private boolean hasPriorityOver(AbstractFanEvent event){
        return this.priorityIndex().compareTo(event.priorityIndex()) <= 0;
    }

    /**
     * <pre>
     *  pre {@code event instanceof AbstractFanEvent}
     *  post {@code true} // no post condition
     * </pre>
     *
     * @see fr.sorbonne_u.devs_simulation.es.events.ES_Event#hasPriorityOver
     */
    @Override
    public boolean hasPriorityOver(EventI event) {
        assert event instanceof AbstractFanEvent :
                new NeoSim4JavaException("event is not a fan event");

        AbstractFanEvent fan_event = (AbstractFanEvent) event;

        return this.hasPriorityOver(fan_event);
    }
}
// -----------------------------------------------------------------------------
