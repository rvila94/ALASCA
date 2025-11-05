package equipments.oven.connections;

import equipments.oven.OvenInternalControlCI;
import equipments.oven.OvenInternalControlI;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;
import fr.sorbonne_u.alasca.physical_data.Measure;
import fr.sorbonne_u.alasca.physical_data.SignalData;
import fr.sorbonne_u.exceptions.PreconditionException;

/**
 * The class <code>OvenInternalControlInboundPort</code> implements an
 * inbound port for the {@code OvenInternalControlCI} component interface.
 *
 * <p><strong>Description</strong></p>
 * 
 * This inbound port exposes the internal control services of the oven
 * (temperature management and heating control) to its thermostat controller.
 * The thermostat can query the current and target temperatures and start or
 * stop heating as needed.
 * 
 * <p><strong>Implementation Invariants</strong></p>
 * 
 * <pre>
 * invariant {@code true}
 * </pre>
 * 
 * <p><strong>Invariants</strong></p>
 * 
 * <pre>
 * invariant {@code getOwner() instanceof OvenInternalControlI}
 * </pre>
 * 
 * <p>Created on : 2025-10-10</p>
 * 
 * @author
 * 	<a href="mailto:Rodrigo.Vila@etu.sorbonne-universite.fr">Rodrigo Vila</a>,
 * 	<a href="mailto:Damien.Ribeiro@etu.sorbonne-universite.fr">Damien Ribeiro</a>
 */
public class			OvenInternalControlInboundPort
extends		AbstractInboundPort
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
	 * create an inbound port.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code owner != null}
	 * pre	{@code owner instanceof OvenInternalControlI}
	 * post	{@code true}
	 * </pre>
	 *
	 * @param owner			component that owns this port.
	 * @throws Exception	<i>to do</i>.
	 */
	public				OvenInternalControlInboundPort(ComponentI owner)
	throws Exception
	{
		super(OvenInternalControlCI.class, owner);
		assert	owner instanceof OvenInternalControlI :
				new PreconditionException(
						"owner instanceof OvenInternalControlI");
	}

	/**
	 * create an inbound port.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code uri != null && !uri.isEmpty()}
	 * pre	{@code owner != null}
	 * pre	{@code owner instanceof OvenInternalControlI}
	 * post	{@code true}
	 * </pre>
	 *
	 * @param uri			unique identifier of the port.
	 * @param owner			component that owns this port.
	 * @throws Exception	<i>to do</i>.
	 */
	public				OvenInternalControlInboundPort(
		String uri,
		ComponentI owner
		) throws Exception
	{
		super(uri, OvenInternalControlCI.class, owner);
		assert	owner instanceof OvenInternalControlI :
				new PreconditionException(
						"owner instanceof OvenInternalControlI");
	}

	// -------------------------------------------------------------------------
	// Methods
	// -------------------------------------------------------------------------

	/**
	 * @see OvenInternalControlI#heating()
	 */
	@Override
	public boolean		heating() throws Exception
	{
		return this.getOwner().handleRequest(
				o -> ((OvenInternalControlI)o).heating());
	}

	/**
	 * @see OvenInternalControlCI#getTargetTemperature()
	 */
	@Override
	public Measure<Double>	getTargetTemperature() throws Exception
	{
		return this.getOwner().handleRequest(
				o -> ((OvenInternalControlI)o).getTargetTemperature());
	}

	/**
	 * @see OvenInternalControlCI#getCurrentTemperature()
	 */
	@Override
	public SignalData<Double>	getCurrentTemperature() throws Exception
	{
		return this.getOwner().handleRequest(
				o -> ((OvenInternalControlI)o).getCurrentTemperature());
	}

	/**
	 * @see OvenInternalControlI#startHeating()
	 */
	@Override
	public void			startHeating() throws Exception
	{
		this.getOwner().handleRequest(
				o -> { ((OvenInternalControlI)o).startHeating();
						return null;
				});
	}

	/**
	 * @see OvenInternalControlI#stopHeating()
	 */
	@Override
	public void			stopHeating() throws Exception
	{
		this.getOwner().handleRequest(
				o -> { ((OvenInternalControlI)o).stopHeating();
						return null;
				});
	}
}
