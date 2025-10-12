package equipments.oven.connections;

import equipments.oven.*;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;
import fr.sorbonne_u.exceptions.PreconditionException;

/**
 * The class <code>OvenInboundPort</code> implements an inbound port for
 * the <code>OvenUserCI</code> component interface.
 *
 * <p><strong>Description</strong></p>
 * 
 * Provides access to the services of the <code>Oven</code> component.
 * 
 * <p><strong>Implementation Invariants</strong></p>
 * 
 * <pre>
 * invariant	{@code getOwner() instanceof OvenImplementationI}
 * </pre>
 * 
 * <p><strong>Invariants</strong></p>
 * 
 * <pre>
 * invariant	{@code true}	// no more invariant
 * </pre>
 * 
 * <p>Created on : 2025-10-08</p>
 * 
 * @author	<a href="mailto:Rodrigo.Vila@etu.sorbonne-universite.fr">Rodrigo Vila</a>
 * @author	<a href="mailto:Damien.Ribeiro@etu.sorbonne-universite.fr">Damien Ribeiro</a>
 */
public class OvenInboundPort
extends AbstractInboundPort
implements OvenUserCI
{
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	private static final long serialVersionUID = 1L;

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * create an inbound port instance.
	 *
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code owner != null}
	 * pre	{@code owner instanceof OvenImplementationI}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 * 
	 * @param owner			component owning the port.
	 * @throws Exception	<i>to do</i>.
	 */
	public OvenInboundPort(ComponentI owner) throws Exception {
		super(OvenUserCI.class, owner);
		assert owner instanceof OvenImplementationI :
			new PreconditionException("owner instanceof OvenImplementationI");
	}

	/**
	 * create an inbound port instance with a specific URI.
	 *
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code uri != null && !uri.isEmpty()}
	 * pre	{@code owner != null}
	 * pre	{@code owner instanceof OvenImplementationI}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param uri			URI of the port.
	 * @param owner			component owning the port.
	 * @throws Exception	<i>to do</i>.
	 */
	public OvenInboundPort(String uri, ComponentI owner) throws Exception {
		super(uri, OvenUserCI.class, owner);
		assert owner instanceof OvenImplementationI :
			new PreconditionException("owner instanceof OvenImplementationI");
	}

	// -------------------------------------------------------------------------
	// Methods
	// -------------------------------------------------------------------------

	@Override
	public OvenState getState() throws Exception {
		return this.getOwner().handleRequest(
			o -> ((OvenImplementationI)o).getState()
		);
	}

	@Override
	public OvenMode getMode() throws Exception {
		return this.getOwner().handleRequest(
			o -> ((OvenImplementationI)o).getMode()
		);
	}

	@Override
	public int getTemperature() throws Exception {
		return this.getOwner().handleRequest(
			o -> ((OvenImplementationI)o).getTemperature()
		);
	}

	@Override
	public boolean isCooking() throws Exception {
		return this.getOwner().handleRequest(
			o -> ((OvenImplementationI)o).isCooking()
		);
	}

	@Override
	public void turnOn() throws Exception {
		this.getOwner().handleRequest(
			o -> { ((OvenImplementationI)o).turnOn(); return null; }
		);
	}

	@Override
	public void turnOff() throws Exception {
		this.getOwner().handleRequest(
			o -> { ((OvenImplementationI)o).turnOff(); return null; }
		);
	}

	@Override
	public void setDefrost() throws Exception {
		this.getOwner().handleRequest(
			o -> { ((OvenImplementationI)o).setDefrost(); return null; }
		);
	}

	@Override
	public void setGrill() throws Exception {
		this.getOwner().handleRequest(
			o -> { ((OvenImplementationI)o).setGrill(); return null; }
		);
	}

	@Override
	public void setTemperature(int temperature) throws Exception {
		this.getOwner().handleRequest(
			o -> { ((OvenImplementationI)o).setTemperature(temperature); return null; }
		);
	}

	@Override
	public void startCooking(int durationInSeconds) throws Exception {
		this.getOwner().handleRequest(
			o -> { ((OvenImplementationI)o).startCooking(durationInSeconds); return null; }
		);
	}

	@Override
	public void programCooking(int delayInSeconds, int durationInSeconds) throws Exception {
		this.getOwner().handleRequest(
			o -> { ((OvenImplementationI)o).programCooking(delayInSeconds, durationInSeconds); return null; }
		);
	}

	@Override
	public void stopProgram() throws Exception {
		this.getOwner().handleRequest(
			o -> { ((OvenImplementationI)o).stopProgram(); return null; }
		);
	}
}
