package equipments.oven.connections;

import equipments.oven.OvenActuatorCI;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;

public class OvenActuatorInboundPort
extends AbstractInboundPort
implements OvenActuatorCI
{
	private static final long serialVersionUID = 1L;

	public OvenActuatorInboundPort(ComponentI owner) throws Exception {
		super(OvenActuatorCI.class, owner);		
	}

	public OvenActuatorInboundPort(String uri, ComponentI owner)
	throws Exception {
		super(uri, OvenActuatorCI.class, owner);
	}

	@Override
	public void startHeating() throws Exception {
		this.getOwner().handleRequest(
			o -> { ((equipments.oven.Oven)o).startHeating(); return null; }
		);
	}

	@Override
	public void stopHeating() throws Exception {
		this.getOwner().handleRequest(
			o -> { ((equipments.oven.Oven)o).stopHeating(); return null; }
		);
	}

	@Override
	public void openDoor() throws Exception {
		this.getOwner().handleRequest(
			o -> { ((equipments.oven.Oven)o).openDoor(); return null; }
		);
	}

	@Override
	public void closeDoor() throws Exception {
		this.getOwner().handleRequest(
			o -> { ((equipments.oven.Oven)o).closeDoor(); return null; }
		);
	}
}
