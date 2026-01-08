package equipments.fan.simulations.events;

import fr.sorbonne_u.devs_simulation.models.interfaces.AtomicModelI;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import equipments.fan.FanImplementationI.FanMode;
import equipments.fan.FanImplementationI.FanState;
import equipments.fan.simulations.FanSimulationOperationI;
import fr.sorbonne_u.devs_simulation.exceptions.NeoSim4JavaException;

// -----------------------------------------------------------------------------
/**
 * The class <code>SetMediumFan</code> defines the simulation event of the
 * fan being set to Medium temperature mode.
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
 * <p>Created on : 2025-11-11</p>
 * 
 * @author	<a href="mailto:Rodrigo.Vila@etu.sorbonne-universite.fr">Rodrigo Vila</a>
 * @author	<a href="mailto:Damien.Ribeiro@etu.sorbonne-universite.fr">Damien Ribeiro</a>
 */
public class			SetMediumFan
extends		AbstractFanEvent
{
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	private static final long serialVersionUID = 1L;
	
	// -------------------------------------------------------------------------
    // Invariants
    // -------------------------------------------------------------------------

    /**
     *
     * check the implementationInvariants
     *
     * <p><strong>Contract</strong></p>
     *
     * <pre>
     *  pre {@code event != null} // no precondition
     *  post {@code true} // no postcondition
     * </pre>
     * @param event
     */
    protected static boolean implementationInvariants(SetMediumFan event) {
        return priorityInvariant(event);
    }

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * create a <code>SetMediumFan</code> event.
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
	public				SetMediumFan(Time timeOfOccurrence)
	{
		super(timeOfOccurrence, null);
	}

	// -------------------------------------------------------------------------
	// Methods
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.events.Event#executeOn(fr.sorbonne_u.devs_simulation.models.interfaces.AtomicModelI)
	 */
	@Override
	public void				executeOn(AtomicModelI model)
	{
		super.executeOn(model);

		FanSimulationOperationI fan_model = (FanSimulationOperationI) model;
		
		assert fan_model.getState() == FanState.ON :
            new NeoSim4JavaException("fan_model.getState() != Fan.FanState.ON");
        assert fan_model.getMode() != FanMode.MEDIUM :
                new NeoSim4JavaException("fan_model.getMode() != Fan.FanMode.MEDIUM");

        fan_model.setMedium();
	}
	
	/**
     * @see AbstractFanEvent#priorityIndex
     */
    @Override
    protected PriorityIndex priorityIndex() {
        return PriorityIndex.SetMediumEvent;
    }
}
// -----------------------------------------------------------------------------
