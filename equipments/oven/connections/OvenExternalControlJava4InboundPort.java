package equipments.oven.connections;

import fr.sorbonne_u.components.ComponentI;
import equipments.oven.Oven;
import equipments.oven.OvenExternalControlJava4CI;
import fr.sorbonne_u.alasca.physical_data.Measure;

/**
 * The class <code>OvenExternalControlJava4InboundPort</code> implements an
 * inbound port for the {@code OvenExternalControlJava4CI} component interface.
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
 * <p>Created on : 2025-10-18</p>
 * 
  * @author	<a href="mailto:Rodrigo.Vila@etu.sorbonne-universite.fr">Rodrigo Vila</a>
 * @author	<a href="mailto:Damien.Ribeiro@etu.sorbonne-universite.fr">Damien Ribeiro</a>
 */
public class			OvenExternalControlJava4InboundPort
extends		OvenExternalControlInboundPort
implements	OvenExternalControlJava4CI
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
	public				OvenExternalControlJava4InboundPort(ComponentI owner)
	throws Exception
	{
		super(OvenExternalControlJava4CI.class, owner);
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
	public				OvenExternalControlJava4InboundPort(
		String uri, ComponentI owner
		) throws Exception
	{
		super(uri, OvenExternalControlJava4CI.class, owner);
	}

	// -------------------------------------------------------------------------
	// Methods
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.components.hem2025e1.equipments.oven.OvenExternalControlJava4CI#getMaxPowerLevelJava4()
	 */
	@Override
	public double		getMaxPowerLevelJava4() throws Exception
	{
		return this.getMaxPowerLevel().getData();
	}

	/**
	 * @see fr.sorbonne_u.components.hem2025e1.equipments.oven.OvenExternalControlJava4CI#setCurrentPowerLevelJava4(double)
	 */
	@Override
	public void			setCurrentPowerLevelJava4(double powerLevel)
	throws Exception
	{
		this.setCurrentPowerLevel(
					new Measure<Double>(powerLevel, Oven.POWER_UNIT));
	}

	/**
	 * @see fr.sorbonne_u.components.hem2025e1.equipments.oven.OvenExternalControlJava4CI#getCurrentPowerLevelJava4()
	 */
	@Override
	public double		getCurrentPowerLevelJava4() throws Exception
	{
		return this.getCurrentPowerLevel().getMeasure().getData();
	}

	/**
	 * @see fr.sorbonne_u.components.hem2025e1.equipments.oven.OvenExternalControlJava4CI#getTargetTemperatureJava4()
	 */
	@Override
	public double		getTargetTemperatureJava4() throws Exception
	{
		return this.getTargetTemperature().getData();
	}

	/**
	 * @see fr.sorbonne_u.components.hem2025e1.equipments.oven.OvenExternalControlJava4CI#getCurrentTemperatureJava4()
	 */
	@Override
	public double		getCurrentTemperatureJava4() throws Exception
	{
		return this.getCurrentTemperature().getMeasure().getData();
	}
}
