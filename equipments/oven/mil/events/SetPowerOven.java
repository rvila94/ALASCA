package equipments.oven.mil.events;

import equipments.oven.Oven.OvenState;
import equipments.oven.OvenExternalControlI;
import equipments.oven.mil.OvenElectricityModel;
import fr.sorbonne_u.devs_simulation.es.events.ES_Event;
import fr.sorbonne_u.devs_simulation.exceptions.NeoSim4JavaException;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.events.EventInformationI;
import fr.sorbonne_u.devs_simulation.models.interfaces.AtomicModelI;
import fr.sorbonne_u.devs_simulation.models.time.Time;

// -----------------------------------------------------------------------------
/**
 * The class <code>SetPowerOven</code> defines the simulation event of the
 * Oven power being set to some level (in watts).
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
 * <p>Created on : 2025-11-13</p>
 * 
 * @author	<a href="mailto:Rodrigo.Vila@etu.sorbonne-universite.fr">Rodrigo Vila</a>
 * @author	<a href="mailto:Damien.Ribeiro@etu.sorbonne-universite.fr">Damien Ribeiro</a>
 */
public class			SetPowerOven
extends		ES_Event
implements	OvenEventI
{
	// -------------------------------------------------------------------------
	// Inner types and classes
	// -------------------------------------------------------------------------

	/**
	 * The class <code>PowerValue</code> represent a power value to be passed
	 * as an {@code EventInformationI} when creating a {@code SetPowerOven}
	 * event.
	 *
	 * <p><strong>Description</strong></p>
	 * 
	 * <p><strong>Implementation Invariants</strong></p>
	 * 
	 * <pre>
	 * invariant	{@code power >= 0.0 && power <= OvenElectricityModel.MAX_HEATING_POWER}
	 * </pre>
	 * 
	 * <p><strong>Invariants</strong></p>
	 * 
	 * <pre>
	 * invariant	{@code true}	// no more invariant
	 * </pre>
	 * 
	 * <p>Created on : 2025-11-13</p>
	 * 
	 * @author	<a href="mailto:Rodrigo.Vila@etu.sorbonne-universite.fr">Rodrigo Vila</a>
	 * @author	<a href="mailto:Damien.Ribeiro@etu.sorbonne-universite.fr">Damien Ribeiro</a>
	 */
	public static class	PowerValue
	implements	EventInformationI
	{
		private static final long serialVersionUID = 1L;
		/* a power in watts.												*/
		protected final double	power;

		/**
		 * create an instance of {@code PowerValue}.
		 * 
		 * <p><strong>Contract</strong></p>
		 * 
		 * <pre>
		 * pre	{@code power >= 0.0 && power <= OvenExternalControlI.MAX_POWER_LEVEL.getData()}
		 * post	{@code getPower() == power}
		 * </pre>
		 *
		 * @param power	the power in watts to put in this container.
		 */
		public			PowerValue(double power)
		{
			super();

			assert	power >= 0.0 &&
						power <= OvenExternalControlI.MAX_POWER_LEVEL.getData() :
					new NeoSim4JavaException(
							"power >= 0.0 && power <= OvenExternalControlI."
							+ "MAX_POWER_LEVEL.getData()");

			this.power = power;
		}

		/**
		 * return the power value in watts.
		 * 
		 * <p><strong>Contract</strong></p>
		 * 
		 * <pre>
		 * pre	{@code true}	// no precondition.
		 * post	{@code return >= 0.0 && return <= OvenElectricityModel.MAX_HEATING_POWER}
		 * </pre>
		 *
		 * @return	the power value in watts.
		 */
		public double	getPower()	{ return this.power; }

		/**
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String	toString()
		{
			StringBuffer sb = new StringBuffer(this.getClass().getSimpleName());
			sb.append('[');
			sb.append(this.power);
			sb.append(']');
			return sb.toString();
		}
	}

	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	private static final long	serialVersionUID = 1L;
	/** the power value to be set on the Oven when the event will be
	 *  executed.															*/
	protected final PowerValue	powerValue;

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * create a {@code SetPowerOven} event which content is a
	 * {@code PowerValue}.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code timeOfOccurrence != null}
	 * pre	{@code content != null && content instanceof PowerValue}
	 * post	{@code getTimeOfOccurrence().equals(timeOfOccurrence)}
	 * post	{@code content == null || getEventInformation().equals(content)}
	 * </pre>
	 *
	 * @param timeOfOccurrence	time at which the event must be executed in simulated time.
	 * @param content			the power value to be set on the Oven when the event will be executed.
	 */
	public				SetPowerOven(
		Time timeOfOccurrence,
		EventInformationI content
		)
	{
		super(timeOfOccurrence, content);

		assert	content != null && content instanceof PowerValue :
				new NeoSim4JavaException(
						"Precondition violation: event content is null or"
						+ " not a PowerValue " + content);

		this.powerValue = (PowerValue) content;
	}

	// -------------------------------------------------------------------------
	// Methods
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.devs_simulation.es.events.ES_Event#hasPriorityOver(fr.sorbonne_u.devs_simulation.models.events.EventI)
	 */
	@Override
	public boolean		hasPriorityOver(EventI e)
	{
		// if many Oven events occur at the same time, the SetPowerOven one
		// will be executed first except for SwitchOnOven ones.
		if (e instanceof SwitchOffOven) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.events.Event#executeOn(fr.sorbonne_u.devs_simulation.models.interfaces.AtomicModelI)
	 */
	@Override
	public void			executeOn(AtomicModelI model)
	{
		assert	model instanceof OvenElectricityModel :
				new NeoSim4JavaException(
						"Precondition violation: model instanceof "
						+ "OvenElectricityModel");

		OvenElectricityModel Oven = (OvenElectricityModel)model;
		assert	Oven.getState() == OvenState.HEATING :
				new NeoSim4JavaException(
						"model not in the right state, should be "
						+ "OvenState.HEATING but is " + Oven.getState());
		Oven.setCurrentHeatingPower(this.powerValue.getPower(),
									  this.getTimeOfOccurrence());
	}
}
// -----------------------------------------------------------------------------
