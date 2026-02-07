package equipments.oven.simulations.events;

import equipments.oven.Oven.OvenState;
import equipments.oven.simulations.OvenTemperatureModel;
import fr.sorbonne_u.devs_simulation.es.events.ES_Event;
import fr.sorbonne_u.devs_simulation.exceptions.NeoSim4JavaException;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.events.EventInformationI;
import fr.sorbonne_u.devs_simulation.models.interfaces.AtomicModelI;
import fr.sorbonne_u.devs_simulation.models.time.Time;

/**
 * The class <code>SetTargetTemperatureOven</code> defines the simulation event of the
 * Oven setting to a given target temperature.
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
public class         SetTargetTemperatureOven
extends               ES_Event
implements            OvenEventI
{
	// -------------------------------------------------------------------------
	// Inner types and classes
	// -------------------------------------------------------------------------
	
	/**
	 * The class <code>TargetTemperatureValue</code> represent a target temperature value to be passed
	 * as an {@code EventInformationI} when creating a {@code SetTargetTemperatureOven}
	 * event.
	 *
	 * <p><strong>Description</strong></p>
	 * 
	 * <p><strong>Implementation Invariants</strong></p>
	 * 
	 * <pre>
	 * invariant	{@code target temperature != null}
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
	public static class TargetTemperatureValue implements EventInformationI {
		private static final long serialVersionUID = 1L;

		protected final Double targetTemperature;
		
		/**
		 * create an instance of {@code TargetTemperatureValue}.
		 * 
		 * <p><strong>Contract</strong></p>
		 * 
		 * <pre>
		 * pre	{@code targetTemperature != null}
		 * post	{@code getTargetTemperature() == targetTemperature}
		 * </pre>
		 *
		 * @param targetTemperature	the targetTemperature to put in this container.
		 */
		public TargetTemperatureValue(Double targetTemperature) {
			super();

			assert targetTemperature != null :
					new NeoSim4JavaException("targetTemperature must not be null");

			this.targetTemperature = targetTemperature;
		}
		
		/**
		 * return the target temperature.
		 * 
		 * <p><strong>Contract</strong></p>
		 * 
		 * <pre>
		 * pre	{@code true}	// no precondition.
		 * post	{@code return == this.targetTemperature}
		 * </pre>
		 *
		 * @return	the target temperature of the oven.
		 */
		public Double getTargetTemperature() {
			return this.targetTemperature;
		}

		@Override
		public String toString() {
			StringBuffer sb = new StringBuffer(this.getClass().getSimpleName());
			sb.append('[');
			sb.append(this.targetTemperature);
			sb.append(']');
			return sb.toString();
		}
	}

	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	private static final long serialVersionUID = 1L;

	protected final TargetTemperatureValue targetTemperatureValue; 

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * create a {@code SetTargetTemperatureOven} event which content is a
	 * {@code TargetTemperatureValue}.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code timeOfOccurrence != null}
	 * pre	{@code content != null && content instanceof TargetTemperatureValue}
	 * post	{@code getTimeOfOccurrence().equals(timeOfOccurrence)}
	 * post	{@code content == null || getEventInformation().equals(content)}
	 * </pre>
	 *
	 * @param timeOfOccurrence	time at which the event must be executed in simulated time.
	 * @param content			the tatrget temperature  value to be set on the 
	 * 							Oven when the event will be executed.
	 */
	public SetTargetTemperatureOven(
			Time timeOfOccurrence,
			EventInformationI content)
	{
		super(timeOfOccurrence, content);

		assert content != null && content instanceof TargetTemperatureValue :
				new NeoSim4JavaException(
						"SetTargetTemperatureOven content must be a TargetTemperatureValue, got: "
						+ content);
		this.targetTemperatureValue = (TargetTemperatureValue) content;
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
	                "SetTargetTemperatureOven executed on wrong model type: "
	                + model.getClass().getSimpleName());

        OvenTemperatureModel oven = (OvenTemperatureModel) model;

        assert oven.getState() != OvenState.OFF :
            new NeoSim4JavaException(
                "Cannot change targetTemperature when oven is OFF.");

        oven.setTargetTemperature(this.targetTemperatureValue.getTargetTemperature(), 
        							this.getTimeOfOccurrence());
    
	}

}
