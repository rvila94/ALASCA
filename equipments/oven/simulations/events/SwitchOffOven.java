package equipments.oven.simulations.events;

import equipments.oven.Oven.OvenState;
import equipments.oven.simulations.OvenElectricityModel;
import equipments.oven.simulations.OvenTemperatureModel;
import equipments.oven.simulations.sil.OvenStateModel;
import fr.sorbonne_u.devs_simulation.es.events.ES_Event;
import fr.sorbonne_u.devs_simulation.exceptions.NeoSim4JavaException;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.interfaces.AtomicModelI;
import fr.sorbonne_u.devs_simulation.models.time.Time;

// -----------------------------------------------------------------------------
/**
 * The class <code>SwitchOffOven</code> defines the simulation event of the
 * Oven being switched off.
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
public class			SwitchOffOven
extends		ES_Event
implements	OvenEventI
{
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	private static final long serialVersionUID = 1L;

	/**
	 * create a <code>SwitchOffOven</code> event.
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
	public				SwitchOffOven(
		Time timeOfOccurrence
		)
	{
		super(timeOfOccurrence, null);
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
		// if many Oven events occur at the same time, the
		// SwitchOffOven one will be executed after all others.
		return false;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.events.Event#executeOn(fr.sorbonne_u.devs_simulation.models.interfaces.AtomicModelI)
	 */
	@Override
	public void			executeOn(AtomicModelI model)
	{
		assert model instanceof OvenElectricityModel
	        || model instanceof OvenTemperatureModel 
	        || model instanceof OvenStateModel :
	        new NeoSim4JavaException(
	            "model must be OvenElectricityModel or "
	            + "OvenTemperatureModel "
	            + "or OvenStateModel");

		if (model instanceof OvenElectricityModel) {
			OvenElectricityModel Oven = (OvenElectricityModel)model;
			assert	Oven.getState() != OvenState.OFF:
					new NeoSim4JavaException(
							"model not in the right state, should not be OFF but is " 
							+ Oven.getState());
			Oven.setState(OvenState.OFF,
							this.getTimeOfOccurrence());
		} else if (model instanceof OvenTemperatureModel){
			OvenTemperatureModel Oven = (OvenTemperatureModel)model;
			// for the temperature model, OvenState.ON is the substitute
			// for OvenState.OFF as it also means not heating
			Oven.setState(OvenState.ON);
		} else {
	    	OvenStateModel Oven = (OvenStateModel) model;
	    	assert	Oven.getState() != OvenState.OFF:
				new NeoSim4JavaException(
						"model not in the right state, should not be OFF but is " 
						+ Oven.getState());
	    	Oven.setState(OvenState.OFF);	
		}
	    	
		
	}
}
// -----------------------------------------------------------------------------
