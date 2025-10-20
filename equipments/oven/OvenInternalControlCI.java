package equipments.oven;

import fr.sorbonne_u.alasca.physical_data.Measure;
import fr.sorbonne_u.alasca.physical_data.SignalData;
import fr.sorbonne_u.components.interfaces.OfferedCI;
import fr.sorbonne_u.components.interfaces.RequiredCI;

/**
 * The component interface <code>OvenInternalControlCI</code> declares the
 * signatures of services used by the thermostat to control the heating by the
 * oven.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p><strong>Invariants</strong></p>
 * 
 * <pre>
 * invariant	{@code true}	// no more invariant
 * </pre>
 * 
 * <p>Created on : 2025-10-10</p>
 * 
 * @author	<a href="mailto:Rodrigo.Vila@etu.sorbonne-universite.fr">Rodrigo Vila</a>
 * @author	<a href="mailto:Damien.Ribeiro@etu.sorbonne-universite.fr">Damien Ribeiro</a>
 */
public interface OvenInternalControlCI
extends		OfferedCI,
			RequiredCI,
			OvenInternalControlI
{
	/**
	 * @see equipments.oven.OvenInternalControlI#heating()
	 */
	@Override
	public boolean				heating() throws Exception;
	
	/**
	 * @see equipments.oven.OvenInternalControlI#startHeating()
	 */
	@Override
	public void					startHeating() throws Exception;
	
	/**
	 * @see equipments.oven.OvenInternalControlI#stopHeating()
	 */
	@Override
	public void					stopHeating() throws Exception;
	
	/**
	 * @see equipments.oven.OvenTemperatureI#getTargetTemperature()
	 */
	@Override
	public Measure<Double> 		getTargetTemperature() throws Exception;
	
	/**
	 * @see equipments.oven.OvenTemperatureI#getCurrentTemperature()
	 */
	@Override
	public SignalData<Double> getCurrentTemperature() throws Exception;
	
}
