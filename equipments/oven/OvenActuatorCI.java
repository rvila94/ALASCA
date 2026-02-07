package equipments.oven;

import fr.sorbonne_u.components.interfaces.OfferedCI;
import fr.sorbonne_u.components.interfaces.RequiredCI;

/**
 * The component interface <code>OvenActuatorCI</code> declares the actuator
 * methods to physically act on the oven component.
 *
 * <p><strong>Description</strong></p>
 *
 * <p>Created on : </p>
 * 
 * @author	<a href="mailto:Rodrigo.Vila@etu.sorbonne-universite.fr">Rodrigo Vila</a>
 * @author	<a href="mailto:Damien.Ribeiro@etu.sorbonne-universite.fr">Damien Ribeiro</a>
 */
public interface OvenActuatorCI
extends OfferedCI, RequiredCI
{
	/**
	 * Start heating the oven.
	 */
	public void startHeating() throws Exception;

	/**
	 * Stop heating the oven.
	 */
	public void stopHeating() throws Exception;

	/**
	 * Open the oven door.
	 */
	public void openDoor() throws Exception;

	/**
	 * Close the oven door.
	 */
	public void closeDoor() throws Exception;
}
