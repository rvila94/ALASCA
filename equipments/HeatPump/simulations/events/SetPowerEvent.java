package equipments.HeatPump.simulations.events;

import equipments.HeatPump.interfaces.HeatPumpUserI;
import equipments.HeatPump.simulations.interfaces.CompleteModelI;
import fr.sorbonne_u.devs_simulation.exceptions.NeoSim4JavaException;
import fr.sorbonne_u.devs_simulation.models.events.EventInformationI;
import fr.sorbonne_u.devs_simulation.models.interfaces.AtomicModelI;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import fr.sorbonne_u.exceptions.PreconditionException;

/**
 * The class <code>equipments.HeatPump.simulations.events.mil.SetPowerEvent</code>.
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
public class SetPowerEvent extends AbstractHeatPumpEvent{

    private HeatPumpPowerValue power_value;

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
    public SetPowerEvent(Time timeOfOccurrence, EventInformationI content) {
        super(timeOfOccurrence, content);

        assert content instanceof HeatPumpPowerValue :
                new NeoSim4JavaException("! content instanceof LampPowerValue");
        this.power_value = (HeatPumpPowerValue) content;
    }

    /**
     * @see AbstractHeatPumpEvent#priorityIndex
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
        assert model instanceof CompleteModelI :
                new PreconditionException("model not instance of HeatPumpElectricityModel");

        CompleteModelI pump_model = (CompleteModelI) model;
        assert pump_model.getCurrentState() != HeatPumpUserI.State.Off :
            new NeoSim4JavaException("pump_model.getCurrentState() == HeatPumpUserI.State.Off");

        pump_model.setCurrentPower(this.power_value.power, this.getTimeOfOccurrence());
    }
}
