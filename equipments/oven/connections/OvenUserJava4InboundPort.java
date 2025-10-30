package equipments.oven.connections;

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.exceptions.PreconditionException;
import equipments.oven.OvenUserI;
import equipments.oven.Oven;
import equipments.oven.OvenUserCI;
import equipments.oven.OvenUserJava4CI;
import fr.sorbonne_u.alasca.physical_data.Measure;

/**
 * The class <code>OvenUserJava4InboundPort</code> implements an inbound port
 * for the {@code OvenUserJava4CI} component interface.
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
 * invariant	{@code getOwner() instanceof OvenUserI}
 * </pre>
 * 
 * <p>Created on : 2025-10-18</p>
 * 
 * @author	<a href="mailto:Rodrigo.Vila@etu.sorbonne-universite.fr">Rodrigo Vila</a>
 * @author	<a href="mailto:Damien.Ribeiro@etu.sorbonne-universite.fr">Damien Ribeiro</a>
 */
public class			OvenUserJava4InboundPort
extends		OvenUserInboundPort
implements	OvenUserJava4CI
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
	 * pre	{@code owner instanceof OvenUserI}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param owner			component that owns this port.
	 * @throws Exception	<i>to do</i>.
	 */
	public				OvenUserJava4InboundPort(ComponentI owner) throws Exception
	{
		super(OvenUserCI.class, owner);
		assert	owner instanceof OvenUserI :
				new PreconditionException("owner instanceof OvenUserI");
	}

	/**
	 * create an inbound port.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code uri != null && !uri.isEmpty()}
	 * pre	{@code owner != null}
	 * pre	{@code owner instanceof OvenUserI}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param uri			unique identifier of the port.
	 * @param owner			component that owns this port.
	 * @throws Exception	<i>to do</i>.
	 */
	public				OvenUserJava4InboundPort(String uri, ComponentI owner)
	throws Exception
	{
		super(uri, OvenUserCI.class, owner);
		assert	owner instanceof OvenUserI :
				new PreconditionException("owner instanceof OvenUserI");
	}

	// -------------------------------------------------------------------------
	// Methods
	// -------------------------------------------------------------------------

	/**
	 * @see equipments.oven.OvenUserJava4CI#setTargetTemperatureJava4(double)
	 */
	@Override
	public void			setTargetTemperatureJava4(double target)
	throws Exception
	{
		this.setTargetTemperature(
				new Measure<Double>(target, Oven.TEMPERATURE_UNIT));
	}

	/**
	 * @see equipments.oven.OvenUserJava4CI#getMaxPowerLevelJava4()
	 */
	@Override
	public double		getMaxPowerLevelJava4() throws Exception
	{
		return this.getMaxPowerLevel().getData();
	}

	/**
	 * @see equipments.oven.OvenUserJava4CI#setCurrentPowerLevelJava4(double)
	 */
	@Override
	public void			setCurrentPowerLevelJava4(double powerLevel)
	throws Exception
	{
		this.setCurrentPowerLevel(
				new Measure<Double>(powerLevel, Oven.POWER_UNIT));
	}

	/**
	 * @see equipments.oven.OvenUserJava4CI#getCurrentPowerLevelJava4()
	 */
	@Override
	public double		getCurrentPowerLevelJava4() throws Exception
	{
		return this.getCurrentPowerLevel().getMeasure().getData();
	}

	/**
	 * @see equipments.oven.OvenUserJava4CI#getTargetTemperatureJava4()
	 */
	@Override
	public double		getTargetTemperatureJava4() throws Exception
	{	
		return this.getTargetTemperature().getData();
	}

	/**
	 * @see equipments.oven.OvenUserJava4CI#getCurrentTemperatureJava4()
	 */
	@Override
	public double		getCurrentTemperatureJava4() throws Exception
	{
		
		return this.getCurrentTemperature().getMeasure().getData();
	}
}
// -----------------------------------------------------------------------------
