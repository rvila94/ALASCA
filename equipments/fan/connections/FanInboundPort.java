package equipments.fan.connections;

import equipments.fan.FanImplementationI;
import equipments.fan.FanUserCI;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;
import fr.sorbonne_u.exceptions.PreconditionException;

/**
 * The class <code>FanInboundPort</code> implements an inbound port for
 * the <code>FanUserCI</code> component interface.
 *
 * <p><strong>Description</strong></p>
 * 
 * Provides access to the services of the <code>Fan</code> component.
 * 
 * <p><strong>Implementation Invariants</strong></p>
 * 
 * <pre>
 * invariant	{@code getOwner() instanceof HairDryerImplementationI}
 * </pre>
 * 
 * <p><strong>Invariants</strong></p>
 * 
 * <pre>
 * invariant	{@code true}	// no more invariant
 * </pre>
 * 
 * <p>Created on : 2025-10-04</p>
 * 
 * @author	<a href="mailto:Rodrigo.Vila@etu.sorbonne-universite.fr">Rodrigo Vila</a>
 * @author	<a href="mailto:Damien.Ribeiro@etu.sorbonne-universite.fr">Damien Ribeiro</a>
 */
public class FanInboundPort
extends AbstractInboundPort
implements FanUserCI
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
	 ** <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code owner != null}
	 * pre	{@code owner instanceof FanImplementationI}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 * 
	 * @param owner			component owning the port.
	 * @throws Exception	<i>to do</i>.
	 */
	public FanInboundPort(ComponentI owner) throws Exception {
		super(FanUserCI.class, owner);
		assert owner instanceof FanImplementationI :
			new PreconditionException("owner instanceof FanImplementationI");
	}

	/**
	 * create an inbound port instance.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code uri != null && !uri.isEmpty()}
	 * pre	{@code owner != null}
	 * pre	{@code owner instanceof FanImplementationI}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param uri			URI of the port.
	 * @param owner			component owning the port.
	 * @throws Exception	<i>to do</i>.
	 */
	public FanInboundPort(String uri, ComponentI owner) throws Exception {
		super(uri, FanUserCI.class, owner);
		assert owner instanceof FanImplementationI :
			new PreconditionException("owner instanceof FanImplementationI");
	}

	// -------------------------------------------------------------------------
	// Methods
	// -------------------------------------------------------------------------

	@Override
	public FanState getState() throws Exception {
		return this.getOwner().handleRequest(
			o -> ((FanImplementationI)o).getState()
		);
	}

	@Override
	public FanMode getMode() throws Exception {
		return this.getOwner().handleRequest(
			o -> ((FanImplementationI)o).getMode()
		);
	}

	@Override
	public void turnOn() throws Exception {
		this.getOwner().handleRequest(
			o -> { ((FanImplementationI)o).turnOn(); return null; }
		);
	}

	@Override
	public void turnOff() throws Exception {
		this.getOwner().handleRequest(
			o -> { ((FanImplementationI)o).turnOff(); return null; }
		);
	}
	
	@Override
	public void setHigh() throws Exception {
		this.getOwner().handleRequest(
			o -> { ((FanImplementationI)o).setHigh(); return null; }
		);
	}
	
	@Override
	public void setMedium() throws Exception {
		this.getOwner().handleRequest(
			o -> { ((FanImplementationI)o).setMedium(); return null; }
		);
	}

	@Override
	public void setLow() throws Exception {
		this.getOwner().handleRequest(
			o -> { ((FanImplementationI)o).setLow(); return null; }
		);
	}

	@Override
	public void startOscillation() throws Exception {
		this.getOwner().handleRequest(
				o -> { ((FanImplementationI)o).startOscillation(); return null; }
			);
		
	}

	@Override
	public void stopOscillation() throws Exception {
		this.getOwner().handleRequest(
				o -> { ((FanImplementationI)o).stopOscillation(); return null; }
			);
		
	}

	@Override
	public boolean isOscillating() throws Exception {
		return this.getOwner().handleRequest(
				o -> ((FanImplementationI)o).isOscillating()
			);
	}
}
