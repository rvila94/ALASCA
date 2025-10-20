package equipments.oven;

import fr.sorbonne_u.alasca.physical_data.Measure;
import fr.sorbonne_u.alasca.physical_data.SignalData;
import fr.sorbonne_u.components.interfaces.OfferedCI;
import fr.sorbonne_u.components.interfaces.RequiredCI;

/**
 * The component interface <code>OvenExternalControlCI</code> declares the
 * signatures of services used by the household energy manager to adjust
 * the power consumption of the oven.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p><strong>Invariants</strong></p>
 * 
 * <pre>
 * invariant	{@code getCurrentPowerLevel() <= getMaxPowerLevel()}
 * </pre>
 * 
 * <p>Created on : 2025-10-10</p>
 * 
 * @author	<a href="mailto:Rodrigo.Vila@etu.sorbonne-universite.fr">Rodrigo Vila</a>
 * @author	<a href="mailto:Damien.Ribeiro@etu.sorbonne-universite.fr">Damien Ribeiro</a>
 */
public interface OvenExternalControlCI 
extends		RequiredCI,
			OfferedCI,
			OvenExternalControlI
{
	/**
	 * @see equipments.oven.OvenExternalControlI#getMaxPowerLevel()
	 */
	public Measure<Double>	getMaxPowerLevel() throws Exception;
	
	/**
	 * @see equipments.oven.OvenExternalControlI#setCurrentPowerLevel(fr.sorbonne_u.alasca.physical_data.Measure)
	 */
	@Override
	public void			setCurrentPowerLevel(Measure<Double> powerLevel)
	throws Exception;
	
	/**
	 * @see equipments.oven.OvenExternalControlI#getCurrentPowerLevel()
	 */
	@Override
	public SignalData<Double> getCurrentPowerLevel() throws Exception;
	
	/**
	 * @see equipments.oven.OvenTemperatureI#getTargetTemperature()
	 */
	@Override
	public Measure<Double> getTargetTemperature() throws Exception;
	
	/**
	 * @see equipments.oven.OvenTemperatureI#getCurrentTemperature()
	 */
	@Override
	public SignalData<Double> getCurrentTemperature() throws Exception;
}
