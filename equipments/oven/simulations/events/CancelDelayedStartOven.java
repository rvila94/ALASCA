package equipments.oven.simulations.events;

import equipments.oven.Oven.OvenState;
import equipments.oven.simulations.sil.OvenStateModel;
import fr.sorbonne_u.devs_simulation.models.events.Event;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.interfaces.AtomicModelI;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import fr.sorbonne_u.devs_simulation.exceptions.NeoSim4JavaException;

// -----------------------------------------------------------------------------
/**
 * The class <code>CancelDelayedStartOven</code> defines the simulation event
 * cancelling a previously scheduled delayed start of the oven.
 *
 * <p><strong>Description</strong></p>
 * <p>
 * This event cancels a delayed start when the oven is in state WAITING
 * and puts it back in state ON.
 * </p>
 *
 * <p>Created on : </p>
 *
 * @author <a href="mailto:Rodrigo.Vila@etu.sorbonne-universite.fr">Rodrigo Vila</a>
 * @author <a href="mailto:Damien.Ribeiro@etu.sorbonne-universite.fr">Damien Ribeiro</a>
 */
public class CancelDelayedStartOven
extends Event
implements OvenEventI
{
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	private static final long serialVersionUID = 1L;

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * create a <code>CancelDelayedStartOven</code> event.
	 *
	 * <p><strong>Contract</strong></p>
	 *
	 * <pre>
	 * pre  {@code timeOfOccurrence != null}
	 * post {@code getTimeOfOccurrence().equals(timeOfOccurrence)}
	 * post {@code getEventInformation() == null}
	 * </pre>
	 *
	 * @param timeOfOccurrence	time of occurrence of the event.
	 */
	public CancelDelayedStartOven(Time timeOfOccurrence)
	{
		super(timeOfOccurrence, null);
	}

	// -------------------------------------------------------------------------
	// Methods
	// -------------------------------------------------------------------------

	@Override
	public boolean hasPriorityOver(EventI e)
	{
		if (e instanceof HeatOven) {
			return true;
		}
		return false;
	}

	@Override
	public void executeOn(AtomicModelI model)
	{
		assert model instanceof OvenStateModel :
			new NeoSim4JavaException(
				"CancelDelayedStartOven can only be executed on OvenStateModel");

		OvenStateModel oven = (OvenStateModel) model;

		assert oven.getState() == OvenState.WAITING :
			new NeoSim4JavaException(
				"CancelDelayedStartOven requires oven to be in WAITING state");

		// Cancel delayed start
		oven.remainingDelay = null;
		oven.setState(OvenState.ON);
	}
}
// -----------------------------------------------------------------------------
