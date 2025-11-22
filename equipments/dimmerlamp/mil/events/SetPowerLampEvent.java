package equipments.dimmerlamp.mil.events;

import equipments.dimmerlamp.DimmerLamp;
import equipments.dimmerlamp.mil.DimmerLampElectricityModel;
import fr.sorbonne_u.devs_simulation.exceptions.NeoSim4JavaException;
import fr.sorbonne_u.devs_simulation.models.events.EventInformationI;
import fr.sorbonne_u.devs_simulation.models.interfaces.AtomicModelI;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import fr.sorbonne_u.exceptions.AssertionChecking;
import fr.sorbonne_u.exceptions.PreconditionException;

/**
 * The class <code>equipments.dimmerlamp.mil.events.SetPowerLampEvent</code>.
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
public class SetPowerLampEvent extends AbstractLampEvent {

    // -------------------------------------------------------------------------
    // Variables
    // -------------------------------------------------------------------------

    /** The power value to be set by the electrical model when the event is
     *  executed */
    protected final LampPowerValue powerValue;

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
     *  post {@code true} // no post condition
     * </pre>
     * @param event whose implementations invariants will be checked
     */
    protected static boolean implementationInvariants(SetPowerLampEvent event) {
        assert event != null :
                new PreconditionException("event == null");

        boolean result = true;

        result &= AssertionChecking.checkImplementationInvariant(AbstractLampEvent.priorityInvariant(event),
                SetPowerLampEvent.class,
                event,
                "priority index is poorly defined");
        result &= AssertionChecking.checkImplementationInvariant(event.powerValue != null,
                SetPowerLampEvent.class,
                event,
                "event.powerValue == null");

        return result;
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
     * pre  {@code content instanceof LampPowerValue}
     * post	{@code getTimeOfOccurrence().equals(timeOfOccurrence)}
     * post	{@code getEventInformation().equals(content)}
     * post	{@code !isCancelled()}
     * </pre>
     *
     * @param timeOfOccurrence time of occurrence of the created event.
     */
    public SetPowerLampEvent(Time timeOfOccurrence, EventInformationI content) {
        super(timeOfOccurrence, content);

        assert content instanceof LampPowerValue :
                new PreconditionException("!(content instanceof LampPowerValue)");

        this.powerValue = (LampPowerValue) content;
    }

    // -------------------------------------------------------------------------
    // Methods
    // -------------------------------------------------------------------------

    /**
     * @see equipments.dimmerlamp.mil.events.AbstractLampEvent#priorityIndex
     */
    @Override
    protected PriorityIndex priorityIndex() {
        return PriorityIndex.SetPowerLampEvent;
    }

    /**
     * @see equipments.dimmerlamp.mil.events.AbstractLampEvent#executeOn
     */
    @Override
    public void executeOn(AtomicModelI model) {
        super.executeOn(model);

        DimmerLampElectricityModel lamp_model = (DimmerLampElectricityModel) model;
        assert lamp_model.getState() == DimmerLamp.LampState.ON :
                new NeoSim4JavaException("lamp_model.getCurrentState() != DimmerLamp.LampState.On");

        lamp_model.setDimmerLampPower(this.powerValue.getPower(), this.getTimeOfOccurrence());
    }
}
