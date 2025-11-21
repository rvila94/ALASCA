package equipments.dimmerlamp.mil.events;

import equipments.dimmerlamp.DimmerLamp;
import equipments.dimmerlamp.mil.DimmerLampElectricityModel;
import fr.sorbonne_u.devs_simulation.exceptions.NeoSim4JavaException;
import fr.sorbonne_u.devs_simulation.models.interfaces.AtomicModelI;
import fr.sorbonne_u.devs_simulation.models.time.Time;

/**
 * The class <code>equipments.dimmerlamp.mil.events.SwitchOnLampEvent</code>.
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
public class SwitchOnLampEvent extends AbstractLampEvent {

    // -------------------------------------------------------------------------
    // Constants and variables
    // -------------------------------------------------------------------------

    private static final long serialVersionUID = 1L;

    // -------------------------------------------------------------------------
    // Invariants
    // -------------------------------------------------------------------------

    /**
     *
     * check the implementationInvariants
     *
     * <p><strong>Contract</strong></p>
     *
     * <pre>
     *  pre {@code event != null} // no precondition
     *  post {@code true} // no postcondition
     * </pre>
     * @param event
     */
    protected static boolean implementationInvariants(SwitchOnLampEvent event) {
        return AbstractLampEvent.priorityInvariant(event);
    }

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
     */
    public SwitchOnLampEvent(Time timeOfOccurrence) {
        super(timeOfOccurrence, null);
    }

    // -------------------------------------------------------------------------
    // Methods
    // -------------------------------------------------------------------------

    /**
     * @see fr.sorbonne_u.devs_simulation.models.events.Event#executeOn(fr.sorbonne_u.devs_simulation.models.interfaces.AtomicModelI)
     */
    @Override
    public void executeOn(AtomicModelI model)
    {
        super.executeOn(model);

        DimmerLampElectricityModel lamp_model = (DimmerLampElectricityModel)model;

        assert lamp_model.getState() == DimmerLamp.LampState.OFF :
                new NeoSim4JavaException("lamp_mode.getState() != DimmerLamp.LampState.OFF");

        lamp_model.setState(DimmerLamp.LampState.ON);
    }

    /**
     * @see equipments.dimmerlamp.mil.events.AbstractLampEvent#priorityIndex
     */
    @Override
    protected PriorityIndex priorityIndex() {
        return PriorityIndex.SwitchOnEvent;
    }


}
