package equipments.oven.connections;

import equipments.oven.OvenInternalControlCI;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;
import fr.sorbonne_u.alasca.physical_data.Measure;
import fr.sorbonne_u.alasca.physical_data.SignalData;

/**
 * The class <code>OvenInternalControlOutboundPort</code> implements an
 * outbound port for the {@code OvenInternalControlCI} component interface.
 *
 * <p><strong>Description</strong></p>
 * 
 * This outbound port is used by the thermostat to call methods of the oven’s
 * internal control interface, allowing it to manage the oven’s heating state
 * and temperature.
 * 
 * <p><strong>Implementation Invariants</strong></p>
 * 
 * <pre>
 * invariant {@code true}	// no more invariant
 * </pre>
 * 
 * <p><strong>Invariants</strong></p>
 * 
 * <pre>
 * invariant {@code true}	// no more invariant
 * </pre>
 * 
 * <p>Created on : 2025-10-10</p>
 * 
 * @author
 * 	<a href="mailto:Rodrigo.Vila@etu.sorbonne-universite.fr">Rodrigo Vila</a>,
 * 	<a href="mailto:Damien.Ribeiro@etu.sorbonne-universite.fr">Damien Ribeiro</a>
 */
public class			OvenInternalControlOutboundPort
extends		AbstractOutboundPort
implements	OvenInternalControlCI
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
	 * post	{@code true}
	 * </pre>
	 *
	 * @param owner			component that owns this port.
	 * @throws Exception 	<i>to do</i>.
	 */
	public				OvenInternalControlOutboundPort(ComponentI owner)
	throws Exception
	{
		super(OvenInternalControlCI.class, owner);
	}

	/**
	 * create an outbound port.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code uri != null && !uri.isEmpty()}
	 * pre	{@code owner != null}
	 * post	{@code true}
	 * </pre>
	 *
	 * @param uri			unique identifier of the port.
	 * @param owner			component that owns this port.
	 * @throws Exception 	<i>to do</i>.
	 */
	public				OvenInternalControlOutboundPort(
		String uri,
		ComponentI owner
		) throws Exception
	{
		super(uri, OvenInternalControlCI.class, owner);
	}

	// -------------------------------------------------------------------------
	// Methods
	// -------------------------------------------------------------------------

	/**
	 * @see equipments.oven.OvenInternalControlI#heating()
	 */
	@Override
	public boolean		heating() throws Exception
	{
		return ((OvenInternalControlCI)this.getConnector()).heating();
	}

	/**
	 * @see equipments.oven.OvenInternalControlCI#getTargetTemperature()
	 */
	@Override
	public Measure<Double>	getTargetTemperature() throws Exception
	{
		return ((OvenInternalControlCI)this.getConnector()).
													getTargetTemperature();
	}

	/**
	 * @see equipments.oven.OvenInternalControlCI#getCurrentTemperature()
	 */
	@Override
	public SignalData<Double>	getCurrentTemperature() throws Exception
	{
		return ((OvenInternalControlCI)this.getConnector()).
													getCurrentTemperature();
	}

	/**
	 * @see equipments.oven.OvenInternalControlI#startHeating()
	 */
	@Override
	public void			startHeating() throws Exception
	{
		((OvenInternalControlCI)this.getConnector()).startHeating();
	}

	/**
	 * @see equipments.oven.OvenInternalControlI#stopHeating()
	 */
	@Override
	public void			stopHeating() throws Exception
	{
		((OvenInternalControlCI)this.getConnector()).stopHeating();
	}
}
