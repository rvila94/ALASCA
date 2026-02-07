package equipments.oven.simulations.events;

import equipments.oven.Oven.OvenMode;
import equipments.oven.Oven.OvenState;
import equipments.oven.simulations.OvenTemperatureModel;
import fr.sorbonne_u.devs_simulation.es.events.ES_Event;
import fr.sorbonne_u.devs_simulation.exceptions.NeoSim4JavaException;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.events.EventInformationI;
import fr.sorbonne_u.devs_simulation.models.interfaces.AtomicModelI;
import fr.sorbonne_u.devs_simulation.models.time.Time;

/**
 * The class <code>SetModeOven</code> defines the simulation event of the
 * Oven switching to a given mode (CUSTOM, DEFROST, GRILL).
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
 * <p>Created on : 2025-11-19</p>
 * 
 * @author	<a href="mailto:Rodrigo.Vila@etu.sorbonne-universite.fr">Rodrigo Vila</a>
 * @author	<a href="mailto:Damien.Ribeiro@etu.sorbonne-universite.fr">Damien Ribeiro</a>
 */
public class         SetModeOven
extends               ES_Event
implements            OvenEventI
{
	// -------------------------------------------------------------------------
	// Inner types and classes
	// -------------------------------------------------------------------------
	
	/**
	 * The class <code>ModeValue</code> represent a mode value to be passed
	 * as an {@code EventInformationI} when creating a {@code SetModeOven}
	 * event.
	 *
	 * <p><strong>Description</strong></p>
	 * 
	 * <p><strong>Implementation Invariants</strong></p>
	 * 
	 * <pre>
	 * invariant	{@code mode != null}
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
	public static class ModeValue implements EventInformationI {
		private static final long serialVersionUID = 1L;

		protected final OvenMode mode;
		
		/**
		 * create an instance of {@code ModeValue}.
		 * 
		 * <p><strong>Contract</strong></p>
		 * 
		 * <pre>
		 * pre	{@code mode != null}
		 * post	{@code getMode() == mode}
		 * </pre>
		 *
		 * @param mode	the mode to put in this container.
		 */
		public ModeValue(OvenMode mode) {
			super();

			assert mode != null :
					new NeoSim4JavaException("mode must not be null");

			this.mode = mode;
		}
		
		/**
		 * return the mode.
		 * 
		 * <p><strong>Contract</strong></p>
		 * 
		 * <pre>
		 * pre	{@code true}	// no precondition.
		 * post	{@code return == this.mode}
		 * </pre>
		 *
		 * @return	the mode of the oven.
		 */
		public OvenMode getMode() {
			return this.mode;
		}

		@Override
		public String toString() {
			StringBuffer sb = new StringBuffer(this.getClass().getSimpleName());
			sb.append('[');
			sb.append(this.mode);
			sb.append(']');
			return sb.toString();
		}
	}

	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	private static final long serialVersionUID = 1L;

	protected final ModeValue modeValue;   // mode to set

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * create a {@code SetModeOven} event which content is a
	 * {@code ModeValue}.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code timeOfOccurrence != null}
	 * pre	{@code content != null && content instanceof PowerValueOven}
	 * post	{@code getTimeOfOccurrence().equals(timeOfOccurrence)}
	 * post	{@code content == null || getEventInformation().equals(content)}
	 * </pre>
	 *
	 * @param timeOfOccurrence	time at which the event must be executed in simulated time.
	 * @param content			the mode value to be set on the Oven when the event will be executed.
	 */
	public SetModeOven(
			Time timeOfOccurrence,
			EventInformationI content)
	{
		super(timeOfOccurrence, content);

		assert content != null && content instanceof ModeValue :
				new NeoSim4JavaException(
						"SetModeOven content must be a ModeValue, got: "
						+ content);
		this.modeValue = (ModeValue) content;
	}

	// -------------------------------------------------------------------------
	// Methods
	// -------------------------------------------------------------------------

	@Override
	public boolean hasPriorityOver(EventI e) {

		if (e instanceof SwitchOnOven) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public void executeOn(AtomicModelI model)
	{
	    assert model instanceof OvenTemperatureModel :
	            new NeoSim4JavaException(
	                "SetModeOven executed on wrong model type: "
	                + model.getClass().getSimpleName());

        OvenTemperatureModel oven = (OvenTemperatureModel) model;

        assert oven.getState() != OvenState.OFF :
            new NeoSim4JavaException(
                "Cannot change mode when oven is OFF.");

        oven.setMode(this.modeValue.getMode(), this.getTimeOfOccurrence());
    
	}

}
