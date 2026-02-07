package equipments.oven.connections;

import equipments.oven.OvenActuatorCI;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;

public class OvenActuatorOutboundPort
extends AbstractOutboundPort
implements OvenActuatorCI
{
	private static final long serialVersionUID = 1L;

	public OvenActuatorOutboundPort(ComponentI owner) throws Exception {
		super(OvenActuatorCI.class, owner);
	}

	public OvenActuatorOutboundPort(String uri, ComponentI owner)
	throws Exception {
		super(uri, OvenActuatorCI.class, owner);
	}

	@Override
	public void startHeating() throws Exception {
		((OvenActuatorCI)this.getConnector()).startHeating();
	}

	@Override
	public void stopHeating() throws Exception {
		((OvenActuatorCI)this.getConnector()).stopHeating();
	}

	@Override
	public void openDoor() throws Exception {
		((OvenActuatorCI)this.getConnector()).openDoor();
	}

	@Override
	public void closeDoor() throws Exception {
		((OvenActuatorCI)this.getConnector()).closeDoor();
	}
}
