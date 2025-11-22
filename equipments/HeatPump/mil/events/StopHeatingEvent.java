package equipments.HeatPump.mil.events;

import equipments.HeatPump.interfaces.HeatPumpUserI;
import equipments.HeatPump.mil.StateModelI;
import fr.sorbonne_u.devs_simulation.exceptions.NeoSim4JavaException;
import fr.sorbonne_u.devs_simulation.models.events.EventInformationI;
import fr.sorbonne_u.devs_simulation.models.interfaces.AtomicModelI;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import fr.sorbonne_u.exceptions.PreconditionException;

/**
 * The class <code>equipments.HeatPump.mil.events.StopHeatingEvent</code>.
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
public class StopHeatingEvent extends AbstractHeatPumpEvent {

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
    public StopHeatingEvent(Time timeOfOccurrence) {
        super(timeOfOccurrence, null);
    }

    /**
     * @see equipments.HeatPump.mil.events.AbstractHeatPumpEvent#priorityIndex
     */
    @Override
    protected PriorityIndex priorityIndex() {
        return PriorityIndex.SetPowerEvent;
    }

    /**
     * @see fr.sorbonne_u.devs_simulation.models.events.Event#executeOn(fr.sorbonne_u.devs_simulation.models.interfaces.AtomicModelI)
     */
    @Override
    public void executeOn(AtomicModelI model)
    {
        assert model != null :
                new PreconditionException("model == null");
        assert model instanceof StateModelI :
                new PreconditionException("model not instanceof StateModelI");

        StateModelI state_model = (StateModelI) model;
        assert state_model.getCurrentState() == HeatPumpUserI.State.Heating :
                new NeoSim4JavaException("pump_model.getCurrentState() != HeatPumpUserI.State.Heating");

        state_model.setCurrentState(HeatPumpUserI.State.On);
    }
}
