package equipments.oven;

import fr.sorbonne_u.components.interfaces.OfferedCI;
import fr.sorbonne_u.components.interfaces.RequiredCI;
import equipments.oven.Oven.OvenMode;
import equipments.oven.Oven.OvenState;
import fr.sorbonne_u.alasca.physical_data.Measure;
import fr.sorbonne_u.alasca.physical_data.SignalData;

/**
 * The component interface <code>OvenUserCI</code> declares the signatures
 * of the services offered and required by a user of the oven.
 *
 * <p><strong>Description</strong></p>
 * 
 * This interface defines the operations a user component can invoke on an
 * oven component, such as turning it on or off, setting the target temperature,
 * and obtaining temperature-related information.
 * 
 * It extends both {@link OfferedCI} and {@link RequiredCI}, making it usable
 * both on the provider (oven) and the consumer (user or controller) sides.
 *
 * <p><strong>Invariants</strong></p>
 * 
 * <pre>
 * invariant	{@code true}	// no more invariant.
 * </pre>
 * 
 * <p>Created on : 2025-10-10</p>
 * 
 * @author	<a href="mailto:Rodrigo.Vila@etu.sorbonne-universite.fr">Rodrigo Vila</a>
 * @author	<a href="mailto:Damien.Ribeiro@etu.sorbonne-universite.fr">Damien Ribeiro</a>
 */
public interface		OvenUserCI
extends		OfferedCI,
			RequiredCI,
			OvenUserI
{
	/**
	 * @see OvenUserI#on()
	 */
	@Override
	public boolean		on() throws Exception;

	/**
	 * @see OvenUserI#switchOn()
	 */
	@Override
	public void			switchOn() throws Exception;

	/**
	 * @see OvenUserI#switchOff()
	 */
	@Override
	public void			switchOff() throws Exception;
	
	/**
	 * @see OvenUserI#startCooking(double delayInSeconds)
	 */
	public void 		startCooking(double delayInSeconds) throws Exception;
	
	/**
	 * @see OvenUserI#stopCooking
	 */
	public void stopCooking() throws Exception;

	/**
	 * @see OvenExternalControlI#getMaxPowerLevel()
	 */
	@Override
	public Measure<Double>	getMaxPowerLevel() throws Exception;

	/**
	 * @see OvenExternalControlI#setCurrentPowerLevel(fr.sorbonne_u.alasca.physical_data.Measure)
	 */
	@Override
	public void			setCurrentPowerLevel(Measure<Double> powerLevel)
	throws Exception;

	/**
	 * @see OvenExternalControlI#getCurrentPowerLevel()
	 */
	@Override
	public SignalData<Double> getCurrentPowerLevel() throws Exception;
	
	/**
	 * @see OvenUserI#setTargetTemperature(fr.sorbonne_u.alasca.physical_data.Measure)
	 */
	@Override
	public void			setTargetTemperature(Measure<Double> target)
	throws Exception;

	/**
	 * @see OvenTemperatureI#getTargetTemperature()
	 */
	@Override
	public Measure<Double> getTargetTemperature() throws Exception;

	/**
	 * @see OvenTemperatureI#getCurrentTemperature()
	 */
	@Override
	public SignalData<Double> getCurrentTemperature() throws Exception;
	
	/**
	 * @see OvenUserI#getState()
	 */
	@Override
	public OvenState getState() throws Exception;
	
	/**
	 * @see OvenUserI#getMode()
	 */
	@Override
	public OvenMode getMode() throws Exception;
}
