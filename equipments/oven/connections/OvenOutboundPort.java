package equipments.oven.connections;

import equipments.oven.*;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;

/**
 * The class <code>OvenOutboundPort</code> implements an outbound port for
 * the <code>OvenUserCI</code> component interface.
 *
 * <p><strong>Description</strong></p>
 * 
 * Allows other components to call the services of the <code>Oven</code> component
 * (on/off, cooking modes, programming, etc.).
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
 * <p>Created on : 2025-10-08</p>
 * 
 * @author	<a href="mailto:Rodrigo.Vila@etu.sorbonne-universite.fr">Rodrigo Vila</a>
 * @author	<a href="mailto:Damien.Ribeiro@etu.sorbonne-universite.fr">Damien Ribeiro</a>
 */
public class OvenOutboundPort
extends AbstractOutboundPort
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
	 * create an outbound port.
	 *
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code owner != null}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 * 
	 * @param owner			component that owns this port.
	 * @throws Exception 	<i>to do</i>.
	 */
	public OvenOutboundPort(ComponentI owner) throws Exception {
		super(OvenUserCI.class, owner);
	}

	/**
	 * create an outbound port with a specific URI.
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
	public OvenOutboundPort(String uri, ComponentI owner) throws Exception {
		super(uri, OvenUserCI.class, owner);
	}

	// -------------------------------------------------------------------------
	// Methods
	// -------------------------------------------------------------------------

	@Override
	public OvenState getState() throws Exception {
		return ((OvenUserCI)this.getConnector()).getState();
	}

	@Override
	public OvenMode getMode() throws Exception {
		return ((OvenUserCI)this.getConnector()).getMode();
	}

	@Override
	public int getTemperature() throws Exception {
		return ((OvenUserCI)this.getConnector()).getTemperature();
	}

	@Override
	public boolean isCooking() throws Exception {
		return ((OvenUserCI)this.getConnector()).isCooking();
	}

	@Override
	public void turnOn() throws Exception {
		((OvenUserCI)this.getConnector()).turnOn();
	}

	@Override
	public void turnOff() throws Exception {
		((OvenUserCI)this.getConnector()).turnOff();
	}

	@Override
	public void setDefrost() throws Exception {
		((OvenUserCI)this.getConnector()).setDefrost();
	}

	@Override
	public void setGrill() throws Exception {
		((OvenUserCI)this.getConnector()).setGrill();
	}

	@Override
	public void setTemperature(int temperature) throws Exception {
		((OvenUserCI)this.getConnector()).setTemperature(temperature);
	}

	@Override
	public void startCooking(int durationInSeconds) throws Exception {
		((OvenUserCI)this.getConnector()).startCooking(durationInSeconds);
	}

	@Override
	public void programCooking(int delayInSeconds, int durationInSeconds) throws Exception {
		((OvenUserCI)this.getConnector()).programCooking(delayInSeconds, durationInSeconds);
	}

	@Override
	public void stopProgram() throws Exception {
		((OvenUserCI)this.getConnector()).stopProgram();
	}
}
