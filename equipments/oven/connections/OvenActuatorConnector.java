package equipments.oven.connections;

import equipments.oven.OvenActuatorCI;
import fr.sorbonne_u.components.connectors.AbstractConnector;


public class OvenActuatorConnector
extends AbstractConnector
implements OvenActuatorCI
{
	@Override
	public void startHeating() throws Exception {
		((OvenActuatorCI)this.offering).startHeating();
	}

	@Override
	public void stopHeating() throws Exception {
		((OvenActuatorCI)this.offering).stopHeating();
	}

	@Override
	public void openDoor() throws Exception {
		((OvenActuatorCI)this.offering).openDoor();
	}

	@Override
	public void closeDoor() throws Exception {
		((OvenActuatorCI)this.offering).closeDoor();
	}
}
