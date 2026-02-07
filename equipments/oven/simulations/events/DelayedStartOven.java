package equipments.oven.simulations.events;

import equipments.oven.Oven.OvenState;
import equipments.oven.simulations.sil.OvenStateModel;
import fr.sorbonne_u.devs_simulation.es.events.ES_Event;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.events.EventInformationI;
import fr.sorbonne_u.devs_simulation.models.interfaces.AtomicModelI;
import fr.sorbonne_u.devs_simulation.models.time.Duration;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import fr.sorbonne_u.devs_simulation.exceptions.NeoSim4JavaException;

public class DelayedStartOven
extends ES_Event
implements OvenEventI
{
	// -------------------------------------------------------------------------
	// Inner classes
	// -------------------------------------------------------------------------

	/**
	 * The class <code>DelayValue</code> encapsulates the delay duration.
	 */
	public static class DelayValue implements EventInformationI{
		private static final long serialVersionUID = 1L;

		protected final Duration delay;

		public DelayValue(Duration delay)
		{
			assert delay != null && delay.getSimulatedDuration() > 0.0 :
				new NeoSim4JavaException("delay must be > 0");

			this.delay = delay;
		}

		public Duration getDelay()
		{
			return this.delay;
		}

		@Override
		public String toString()
		{
			return this.getClass().getSimpleName() + "[" + delay + "]";
		}
	}

	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	private static final long serialVersionUID = 1L;

	protected final DelayValue delayValue;

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	public DelayedStartOven(
		Time timeOfOccurrence,
		EventInformationI content)
	{
		super(timeOfOccurrence, content);

		assert content != null && content instanceof DelayValue :
			new NeoSim4JavaException(
				"DelayedStartOven content must be a DelayValue");

		this.delayValue = (DelayValue) content;
	}

	// -------------------------------------------------------------------------
	// Methods
	// -------------------------------------------------------------------------

	@Override
	public boolean hasPriorityOver(EventI e)
	{
		if (e instanceof SwitchOnOven) {
			return false;
		}
		return true;
	}

	@Override
	public void executeOn(AtomicModelI model)
	{
		assert model instanceof OvenStateModel :
			new NeoSim4JavaException(
				"DelayedStartOven can only be executed on OvenStateModel");

		OvenStateModel oven = (OvenStateModel) model;

		assert oven.getState() == OvenState.ON :
			new NeoSim4JavaException(
				"DelayedStartOven requires oven to be ON");

		oven.setState(OvenState.WAITING);
		oven.remainingDelay = this.delayValue.getDelay();
	}
}
