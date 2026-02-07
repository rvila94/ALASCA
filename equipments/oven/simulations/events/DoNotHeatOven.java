package equipments.oven.simulations.events;

import fr.sorbonne_u.devs_simulation.models.events.Event;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.interfaces.AtomicModelI;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import equipments.oven.Oven.OvenState;
import equipments.oven.simulations.OvenElectricityModel;
import equipments.oven.simulations.OvenTemperatureModel;
import equipments.oven.simulations.sil.OvenStateModel;
import fr.sorbonne_u.devs_simulation.exceptions.NeoSim4JavaException;

// -----------------------------------------------------------------------------
/**
 * The class <code>DoNotHeat</code> defines the simulation event of the
 * oven stopping to heat.
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
public class			DoNotHeatOven
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
	 * create a <code>DoNotHeat</code> event.
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
	public				DoNotHeatOven(
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
		// if many Oven events occur at the same time, the DoNotHeat one
		// will be executed first except for SwitchOnOven ones.
		if (e instanceof SwitchOnOven) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.events.Event#executeOn(fr.sorbonne_u.devs_simulation.models.interfaces.AtomicModelI)
	 */
	@Override
	public void executeOn(AtomicModelI model) {
	    assert model instanceof OvenElectricityModel
	        || model instanceof OvenTemperatureModel 
	        || model instanceof OvenStateModel :
	        new NeoSim4JavaException(
	            "model must be OvenElectricityModel or "
	            + "OvenTemperatureModel "
	            + "or OvenStateModel");

	    if (model instanceof OvenElectricityModel) {
	        OvenElectricityModel oven = (OvenElectricityModel) model;

	        assert oven.getState() == OvenState.HEATING: 
	        	new NeoSim4JavaException( "model not in the right state, "
	        			+ "should be OvenElectricityModel.State.HEATING but is " 
	        			+ oven.getState());

	        oven.setState(OvenState.ON, this.getTimeOfOccurrence());

	    } else if (model instanceof OvenTemperatureModel) {
	        OvenTemperatureModel oven = (OvenTemperatureModel) model;

	        assert oven.getState() == OvenState.HEATING: 
	        	new NeoSim4JavaException( "model not in the right state, "
	        			+ "should be OvenTemperatureModel.State.HEATING but is " 
	        			+ oven.getState());

	        oven.setState(OvenState.ON);
	    } else {
	    	OvenStateModel oven = (OvenStateModel) model;

	        assert oven.getState() == OvenState.HEATING: 
	        	new NeoSim4JavaException( "model not in the right state, "
	        			+ "should be OvenStateModel.State.HEATING but is " 
	        			+ oven.getState());

	        oven.setState(OvenState.ON);
	    }
	    
	    
	}

}
// -----------------------------------------------------------------------------
