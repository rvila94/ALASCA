package equipments.oven.mil.events;

import fr.sorbonne_u.devs_simulation.models.events.Event;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.interfaces.AtomicModelI;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import equipments.oven.Oven.OvenState;
import equipments.oven.mil.OvenElectricityModel;
import equipments.oven.mil.OvenTemperatureModel;
import fr.sorbonne_u.devs_simulation.exceptions.NeoSim4JavaException;

// -----------------------------------------------------------------------------
/**
 * The class <code>Heat</code> defines the simulation event of the Oven
 * starting to heat.
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
public class			Heat
extends		Event
implements	OvenEventI
{
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	private static final long serialVersionUID = 1L;

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * create a <code>Heat</code> event.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code timeOfOccurrence != null}
	 * post	{@code getTimeOfOccurrence().equals(timeOfOccurrence)}
	 * post	{@code getEventInformation() == null}
	 * </pre>
	 *
	 * @param timeOfOccurrence	time of occurrence of the event.
	 */
	public				Heat(
		Time timeOfOccurrence
		)
	{
		super(timeOfOccurrence, null);
	}

	// -------------------------------------------------------------------------
	// Methods
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.events.Event#hasPriorityOver(fr.sorbonne_u.devs_simulation.models.events.EventI)
	 */
	@Override
	public boolean		hasPriorityOver(EventI e)
	{
		// if many Oven events occur at the same time, the Heat one will be
		// executed after SwitchOnOven and DoNotHeat ones but before
		// SwitchOffOven.
		if (e instanceof SwitchOnOven || e instanceof DoNotHeat) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.events.Event#executeOn(fr.sorbonne_u.devs_simulation.models.interfaces.AtomicModelI)
	 */
	@Override
	public void			executeOn(AtomicModelI model)
	{
		assert	model instanceof OvenElectricityModel ||
									model instanceof OvenTemperatureModel :
				new NeoSim4JavaException(
						"Precondition violation: model instanceof "
						+ "OvenElectricityModel || "
						+ "model instanceof OvenTemperatureModel");

		if (model instanceof OvenElectricityModel) {
			OvenElectricityModel Oven = (OvenElectricityModel)model;
			assert	Oven.getState() == OvenState.ON:
					new NeoSim4JavaException(
							"model not in the right state, should be "
							+ "OvenElectricityModel.State.ON but is "
							+ Oven.getState());
			Oven.setState(OvenState.HEATING,
							this.getTimeOfOccurrence());
		} else {
			OvenTemperatureModel Oven = (OvenTemperatureModel)model;
			assert	Oven.getState() == OvenState.ON:
					new NeoSim4JavaException(
							"model not in the right state, should be "
							+ "OvenTemperatureModel.State.ON but is "
							+ Oven.getState());
			Oven.setState(OvenState.HEATING);
		}
	}
}
// -----------------------------------------------------------------------------
