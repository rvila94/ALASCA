package equipments.oven.connections;

import fr.sorbonne_u.alasca.physical_data.Measure;
import fr.sorbonne_u.alasca.physical_data.SignalData;
import fr.sorbonne_u.components.connectors.AbstractConnector;
import equipments.oven.OvenExternalControlCI;

/**
 * The class <code>OvenExternalControlConnector</code> implements a
 * connector for the {@code OvenExternalControlCI} component interface.
 *
 * <p><strong>Description</strong></p>
 * 
 * This connector allows an external controller (e.g. the household energy
 * manager) to interact with the oven and monitor or adjust its power
 * consumption and internal temperature.
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
 * invariant	{@code true}	// no additional invariant
 * </pre>
 * 
 * <p>Created on : 2025-10-10</p>
 * 
 * @author	
 * 	<a href="mailto:Rodrigo.Vila@etu.sorbonne-universite.fr">Rodrigo Vila</a>,
 * 	<a href="mailto:Damien.Ribeiro@etu.sorbonne-universite.fr">Damien Ribeiro</a>
 */
public class			OvenExternalControlConnector
extends		AbstractConnector
implements	OvenExternalControlCI
{
	/**
	 * @see equipments.oven.OvenExternalControlCI#getMaxPowerLevel()
	 */
	@Override
	public Measure<Double>	getMaxPowerLevel() throws Exception
	{
		return ((OvenExternalControlCI) this.offering).getMaxPowerLevel();
	}

	/**
	 * @see equipments.oven.OvenExternalControlCI#setCurrentPowerLevel(fr.sorbonne_u.alasca.physical_data.Measure)
	 */
	@Override
	public void	setCurrentPowerLevel(Measure<Double> powerLevel)
	throws Exception
	{
		((OvenExternalControlCI) this.offering).setCurrentPowerLevel(powerLevel);
	}

	/**
	 * @see equipments.oven.OvenExternalControlCI#getCurrentPowerLevel()
	 */
	@Override
	public SignalData<Double>	getCurrentPowerLevel() throws Exception
	{
		return ((OvenExternalControlCI) this.offering).getCurrentPowerLevel();
	}

	/**
	 * @see equipments.oven.OvenExternalControlCI#getTargetTemperature()
	 */
	@Override
	public Measure<Double>	getTargetTemperature() throws Exception
	{
		return ((OvenExternalControlCI) this.offering).getTargetTemperature();
	}

	/**
	 * @see equipments.oven.OvenExternalControlCI#getCurrentTemperature()
	 */
	@Override
	public SignalData<Double>	getCurrentTemperature() throws Exception
	{
		return ((OvenExternalControlCI) this.offering).getCurrentTemperature();
	}
}
