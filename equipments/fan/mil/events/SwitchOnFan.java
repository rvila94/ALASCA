package equipments.fan.mil.events;

import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.interfaces.AtomicModelI;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import equipments.fan.FanImplementationI.FanState;
import equipments.fan.Fan;
import equipments.fan.mil.FanElectricityModel;
import fr.sorbonne_u.devs_simulation.exceptions.NeoSim4JavaException;

// -----------------------------------------------------------------------------
/**
 * The class <code>SwitchOnFan</code> defines the simulation event of the
 * fan being switched on.
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
* <p>Created on : 2025-11-05</p>
 * 
 * @author	<a href="mailto:Rodrigo.Vila@etu.sorbonne-universite.fr">Rodrigo Vila</a>
 * @author	<a href="mailto:Damien.Ribeiro@etu.sorbonne-universite.fr">Damien Ribeiro</a>
 */
public class			SwitchOnFan
extends		AbstractFanEvent
{
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	private static final long serialVersionUID = 1L;

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * create a <code>SwitchOnFan</code> event.
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
	public				SwitchOnFan(Time timeOfOccurrence)
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
	public boolean			hasPriorityOver(EventI e)
	{
		// if many fan events occur at the same time, the
		// SwitchOnFan one will be executed first.
		return true;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.events.Event#executeOn(fr.sorbonne_u.devs_simulation.models.interfaces.AtomicModelI)
	 */
	@Override
	public void				executeOn(AtomicModelI model)
	{
		assert	model instanceof FanElectricityModel :
				new NeoSim4JavaException(
						"Precondition violation: model instanceof "
						+ "FanElectricityModel");

		// a SwitchOnFan event can be executed when the state of the hair
		// dryer model is in the state OFF
		FanElectricityModel m = (FanElectricityModel)model;
		if (m.getState() == FanState.OFF) {
			// then put it in the state ON and initial mode
			m.setStateMode(FanState.ON, Fan.INITIAL_MODE);
			// trigger an internal transition by toggling the electricity
			// consumption changed boolean to true
			m.toggleConsumptionHasChanged();
		}
	}
}
// -----------------------------------------------------------------------------
