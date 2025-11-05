package equipments.oven.connections;

import equipments.oven.OvenExternalControlCI;
import equipments.oven.OvenExternalControlI;
import equipments.oven.OvenTemperatureI;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.interfaces.OfferedCI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;
import fr.sorbonne_u.exceptions.PreconditionException;
import fr.sorbonne_u.alasca.physical_data.Measure;
import fr.sorbonne_u.alasca.physical_data.SignalData;

/**
 * The class <code>OvenExternalControlInboundPort</code> implements the
 * inbound port for the {@code OvenExternalControlCI} component interface.
 *
 * <p><strong>Description</strong></p>
 * 
 * This inbound port allows the energy manager (or another external controller)
 * to query and control the oven’s power and temperature levels through the
 * {@link OvenExternalControlI} interface.
 * 
 * <p><strong>Implementation Invariants</strong></p>
 * 
 * <pre>
 * invariant	{@code true}	// no additional invariant
 * </pre>
 * 
 * <p><strong>Invariants</strong></p>
 * 
 * <pre>
 * invariant	{@code getOwner() instanceof OvenExternalControlI}
 * </pre>
 * 
 * <p>Created on : 2025-10-10</p>
 * 
 * @author	
 * 	<a href="mailto:Rodrigo.Vila@etu.sorbonne-universite.fr">Rodrigo Vila</a>,
 * 	<a href="mailto:Damien.Ribeiro@etu.sorbonne-universite.fr">Damien Ribeiro</a>
 */
public class			OvenExternalControlInboundPort
extends		AbstractInboundPort
implements	OvenExternalControlCI
{
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	private static final long serialVersionUID = 1L;

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * create an inbound port.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code owner != null}
	 * pre	{@code owner instanceof OvenExternalControlI}
	 * post	{@code true}
	 * </pre>
	 *
	 * @param owner			component that owns this port.
	 * @throws Exception	if an error occurs.
	 */
	public				OvenExternalControlInboundPort(ComponentI owner)
	throws Exception
	{
		this(OvenExternalControlCI.class, owner);
	}

	/**
	 * create an inbound port with a given URI.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code uri != null && !uri.isEmpty()}
	 * pre	{@code owner != null}
	 * pre	{@code owner instanceof OvenExternalControlI}
	 * post	{@code true}
	 * </pre>
	 *
	 * @param uri			unique identifier of the port.
	 * @param owner			component that owns this port.
	 * @throws Exception	if an error occurs.
	 */
	public				OvenExternalControlInboundPort(
		String uri,
		ComponentI owner
		) throws Exception
	{
		this(uri, OvenExternalControlCI.class, owner);
	}

	/**
	 * create an inbound port with a specific implemented interface.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code implementedInterface != null && OvenExternalControlCI.class.isAssignableFrom(implementedInterface)}
	 * pre	{@code owner != null}
	 * pre	{@code owner instanceof OvenExternalControlI}
	 * post	{@code true}
	 * </pre>
	 *
	 * @param implementedInterface	interface implemented by this port.
	 * @param owner					component that owns this port.
	 * @throws Exception			if an error occurs.
	 */
	public				OvenExternalControlInboundPort(
		Class<? extends OfferedCI> implementedInterface,
		ComponentI owner
		) throws Exception
	{
		super(implementedInterface, owner);

		assert	implementedInterface != null &&
				OvenExternalControlCI.class.isAssignableFrom(implementedInterface)
				: new PreconditionException(
					"implementedInterface != null && "
					+ "OvenExternalControlCI.class.isAssignableFrom(implementedInterface)");
		assert	owner instanceof OvenExternalControlI :
				new PreconditionException(
					"owner instanceof OvenExternalControlI");
	}

	/**
	 * create an inbound port with URI and specific implemented interface.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code uri != null && !uri.isEmpty()}
	 * pre	{@code implementedInterface != null && OvenExternalControlCI.class.isAssignableFrom(implementedInterface)}
	 * pre	{@code owner != null}
	 * pre	{@code owner instanceof OvenExternalControlI}
	 * post	{@code true}
	 * </pre>
	 *
	 * @param uri			unique identifier of the port.
	 * @param implementedInterface	interface implemented by this port.
	 * @param owner					component that owns this port.
	 * @throws Exception			if an error occurs.
	 */
	public				OvenExternalControlInboundPort(
		String uri,
		Class<? extends OfferedCI> implementedInterface,
		ComponentI owner
		) throws Exception
	{
		super(uri, implementedInterface, owner);

		assert	implementedInterface != null &&
				OvenExternalControlCI.class.isAssignableFrom(implementedInterface)
				: new PreconditionException(
					"implementedInterface != null && "
					+ "OvenExternalControlCI.class.isAssignableFrom(implementedInterface)");
		assert	owner instanceof OvenExternalControlI :
				new PreconditionException(
					"owner instanceof OvenExternalControlI");
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
		return this.getOwner().handleRequest(
				o -> ((OvenExternalControlI)o).getMaxPowerLevel());
	}

	/**
	 * @see OvenExternalControlCI#setCurrentPowerLevel(fr.sorbonne_u.alasca.physical_data.Measure)
	 */
	@Override
	public void	setCurrentPowerLevel(Measure<Double> powerLevel)
	throws Exception
	{
		this.getOwner().handleRequest(
			o -> { ((OvenExternalControlI)o).setCurrentPowerLevel(powerLevel);
				   return null; });
	}

	/**
	 * @see OvenExternalControlCI#getCurrentPowerLevel()
	 */
	@Override
	public SignalData<Double>	getCurrentPowerLevel() throws Exception
	{
		return this.getOwner().handleRequest(
				o -> ((OvenExternalControlI)o).getCurrentPowerLevel());
	}

	/**
	 * @see OvenExternalControlCI#getTargetTemperature()
	 */
	@Override
	public Measure<Double>	getTargetTemperature() throws Exception
	{
		return this.getOwner().handleRequest(
				o -> ((OvenTemperatureI)o).getTargetTemperature());
	}

	/**
	 * @see OvenExternalControlCI#getCurrentTemperature()
	 */
	@Override
	public SignalData<Double>	getCurrentTemperature() throws Exception
	{
		return this.getOwner().handleRequest(
				o -> ((OvenTemperatureI)o).getCurrentTemperature());
	}
}
