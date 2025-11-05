package equipments.oven.connections;

import equipments.oven.OvenExternalControlCI;
import fr.sorbonne_u.alasca.physical_data.Measure;
import fr.sorbonne_u.alasca.physical_data.SignalData;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;

/**
 * The class <code>OvenExternalControlOutboundPort</code> implements an
 * outbound port for the {@code OvenExternalControlCI} component interface.
 *
 * <p><strong>Description</strong></p>
 * 
 * This outbound port is used by an external controller (e.g., the energy
 * manager) to connect to and send requests to the oven component, controlling
 * its power consumption and retrieving temperature or power data.
 * 
 * <p><strong>Implementation Invariants</strong></p>
 * 
 * <pre>
 * invariant	{@code true}
 * </pre>
 * 
 * <p><strong>Invariants</strong></p>
 * 
 * <pre>
 * invariant	{@code true}
 * </pre>
 * 
 * <p>Created on : 2025-10-10</p>
 * 
 * @author	
 * 	<a href="mailto:Rodrigo.Vila@etu.sorbonne-universite.fr">Rodrigo Vila</a>,
 * 	<a href="mailto:Damien.Ribeiro@etu.sorbonne-universite.fr">Damien Ribeiro</a>
 */
public class			OvenExternalControlOutboundPort
extends		AbstractOutboundPort
implements OvenExternalControlCI
{
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	private static final long serialVersionUID = 1L;

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * Create an outbound port.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code owner != null}
	 * post	{@code true}
	 * </pre>
	 *
	 * @param owner			component that owns this port.
	 * @throws Exception 	if an error occurs.
	 */
	public				OvenExternalControlOutboundPort(ComponentI owner)
	throws Exception
	{
		super(OvenExternalControlCI.class, owner);
	}

	/**
	 * Create an outbound port with a given URI.
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
	 * @throws Exception 	if an error occurs.
	 */
	public				OvenExternalControlOutboundPort(
		String uri,
		ComponentI owner
		) throws Exception
	{
		super(uri, OvenExternalControlCI.class, owner);
	}

	// -------------------------------------------------------------------------
	// Methods
	// -------------------------------------------------------------------------

	/**
	 * @see OvenExternalControlCI#getMaxPowerLevel()
	 */
	@Override
	public Measure<Double>	getMaxPowerLevel() throws Exception
	{
		return ((OvenExternalControlCI)this.getConnector()).
													getMaxPowerLevel();
	}

	/**
	 * @see OvenExternalControlCI#setCurrentPowerLevel(fr.sorbonne_u.alasca.physical_data.Measure)
	 */
	@Override
	public void			setCurrentPowerLevel(Measure<Double> powerLevel)
	throws Exception
	{
		((OvenExternalControlCI)this.getConnector()).
											setCurrentPowerLevel(powerLevel);
	}

	/**
	 * @see OvenExternalControlCI#getCurrentPowerLevel()
	 */
	@Override
	public SignalData<Double>	getCurrentPowerLevel() throws Exception
	{
		return ((OvenExternalControlCI)this.getConnector()).
													getCurrentPowerLevel();
	}

	/**
	 * @see OvenExternalControlCI#getTargetTemperature()
	 */
	@Override
	public Measure<Double>	getTargetTemperature() throws Exception
	{
		return ((OvenExternalControlCI)this.getConnector()).
													getTargetTemperature();
	}

	/**
	 * @see OvenExternalControlCI#getCurrentTemperature()
	 */
	@Override
	public SignalData<Double>	getCurrentTemperature() throws Exception
	{
		return ((OvenExternalControlCI)this.getConnector()).
													getCurrentTemperature();
	}
}
