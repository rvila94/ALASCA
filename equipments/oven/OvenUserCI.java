package equipments.oven;

import fr.sorbonne_u.components.interfaces.OfferedCI;
import fr.sorbonne_u.components.interfaces.RequiredCI;

/**
 * The component interface <code>OvenUserCI</code> defines the services a
 * programmable oven component offers and that can be required from it.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p>
 * This interface extends both {@code OfferedCI} and {@code RequiredCI},
 * meaning that it can be used by components offering or requiring the
 * services of an oven. It inherits all the methods from
 * {@code OvenImplementationI} to make them accessible through BCM4Java
 * component ports.
 * </p>
 *
 * <p><strong>Invariants</strong></p>
 * 
 * <pre>
 * invariant	{@code true}	// no more invariant
 * </pre>
 * 
 * <p>Created on : 2025-10-08</p>
 * 
 * @author	<a href="mailto:Rodrigo.Vila@etu.sorbonne-universite.fr">Rodrigo Vila</a>
 * @author	<a href="mailto:Damien.Ribeiro@etu.sorbonne-universite.fr">Damien Ribeiro</a>
 */
public interface OvenUserCI
extends		OfferedCI,
			RequiredCI,
			OvenImplementationI
{
	/**
	 * @see equipments.oven.OvenImplementationI#getState()
	 */
	@Override
	public OvenState getState() throws Exception;

	/**
	 * @see equipments.oven.OvenImplementationI#getMode()
	 */
	@Override
	public OvenMode getMode() throws Exception;

	/**
	 * @see equipments.oven.OvenImplementationI#getTemperature()
	 */
	@Override
	public int getTemperature() throws Exception;

	/**
	 * @see equipments.oven.OvenImplementationI#isCooking()
	 */
	@Override
	public boolean isCooking() throws Exception;

	/**
	 * @see equipments.oven.OvenImplementationI#turnOn()
	 */
	@Override
	public void turnOn() throws Exception;

	/**
	 * @see equipments.oven.OvenImplementationI#turnOff()
	 */
	@Override
	public void turnOff() throws Exception;

	/**
	 * @see equipments.oven.OvenImplementationI#setDefrost()
	 */
	@Override
	public void setDefrost() throws Exception;

	/**
	 * @see equipments.oven.OvenImplementationI#setGrill()
	 */
	@Override
	public void setGrill() throws Exception;

	/**
	 * @see equipments.oven.OvenImplementationI#setTemperature(int)
	 */
	@Override
	public void setTemperature(int temperature) throws Exception;

	/**
	 * @see equipments.oven.OvenImplementationI#startCooking(int)
	 */
	@Override
	public void startCooking(int durationInSeconds) throws Exception;

	/**
	 * @see equipments.oven.OvenImplementationI#programCooking(int, int)
	 */
	@Override
	public void programCooking(int delayInSeconds, int durationInSeconds) throws Exception;

	/**
	 * @see equipments.oven.OvenImplementationI#stopProgram()
	 */
	@Override
	public void stopProgram() throws Exception;
}
