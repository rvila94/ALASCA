package equipments.fan.simulations.events;

import fr.sorbonne_u.devs_simulation.models.interfaces.AtomicModelI;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import equipments.fan.FanImplementationI.FanState;
import equipments.fan.simulations.FanSimulationOperationI;
import fr.sorbonne_u.devs_simulation.exceptions.NeoSim4JavaException;

// -----------------------------------------------------------------------------
/**
 * The class <code>SwitchOffFan</code> defines the simulation event of the
 * fan being switched off.
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
public class			SwitchOffFan
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
    protected static boolean implementationInvariants(SwitchOffFan event) {
        return priorityInvariant(event);
    }

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * create a <code>SwitchOffFan</code> event.
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
	public				SwitchOffFan(Time timeOfOccurrence)
	{
		super(timeOfOccurrence, null);
	}

	// ------------------------------------------------------------------------
	// Methods
	// ------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.events.Event#executeOn(fr.sorbonne_u.devs_simulation.models.interfaces.AtomicModelI)
	 */
	@Override
	public void			executeOn(AtomicModelI model)
	{
		super.executeOn(model);

		FanSimulationOperationI fan_model = (FanSimulationOperationI) model;

        assert fan_model.getState() == FanState.ON :
                new NeoSim4JavaException("fan_model.getState() != Fan.FanState.ON");

        fan_model.turnOff();
	}
	
	/**
     * @see AbstractFanEvent#priorityIndex
     */
    @Override
    protected PriorityIndex priorityIndex() {
        return PriorityIndex.SwitchOffEvent;
    }
}
// -----------------------------------------------------------------------------
