package equipments.fan.connections;

import equipments.fan.FanUserCI;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;

/**
 * The class <code>FanOutboundPort</code> implements an outbound port for
 * the <code>FanUserCI</code> component interface.
 *
 * <p><strong>Description</strong></p>
 * 
 * Allows other components to call the services of the Fan component
 * (on/off, set speed, etc.).
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
 * <p>Created on : 2025-10-04</p>
 * 
 * @author	<a href="mailto:Rodrigo.Vila@etu.sorbonne-universite.fr">Rodrigo Vila</a>
 * @author	<a href="mailto:Damien.Ribeiro@etu.sorbonne-universite.fr">Damien Ribeiro</a>
 */
public class FanOutboundPort
extends AbstractOutboundPort
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
	 * create an outbound port.
	 *
	 ** <pre>
	 * pre	{@code owner != null}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 * 
	 * @param owner			component that owns this port.
	 * @throws Exception 	<i>to do</i>.
	 */
	public FanOutboundPort(ComponentI owner) throws Exception {
		super(FanUserCI.class, owner);
	}

	/**
	 * create an outbound port.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code uri != null && !uri.isEmpty()}
	 * pre	{@code owner != null}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param uri			unique identifier of the port.
	 * @param owner			component that owns this port.
	 * @throws Exception 	<i>to do</i>.
	 */
	public FanOutboundPort(String uri, ComponentI owner) throws Exception {
		super(uri, FanUserCI.class, owner);
	}

	// -------------------------------------------------------------------------
	// Methods
	// -------------------------------------------------------------------------

	@Override
	public FanState getState() throws Exception {
		return ((FanUserCI)this.getConnector()).getState();
	}

	@Override
	public FanMode getMode() throws Exception {
		return ((FanUserCI)this.getConnector()).getMode();
	}

	@Override
	public void turnOn() throws Exception {
		((FanUserCI)this.getConnector()).turnOn();
	}

	@Override
	public void turnOff() throws Exception {
		((FanUserCI)this.getConnector()).turnOff();
	}
	
	@Override
	public void setHigh() throws Exception {
		((FanUserCI)this.getConnector()).setHigh();
	}
	
	@Override
	public void setMedium() throws Exception {
		((FanUserCI)this.getConnector()).setMedium();
	}
	
	@Override
	public void setLow() throws Exception {
		((FanUserCI)this.getConnector()).setLow();
	}
}
