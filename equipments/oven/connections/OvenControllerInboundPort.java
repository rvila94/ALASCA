package equipments.oven.connections;

import equipments.oven.OvenControllerCI;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;

public class OvenControllerInboundPort
extends AbstractInboundPort
implements OvenControllerCI
{
    private static final long serialVersionUID = 1L;

    public OvenControllerInboundPort(
            String uri,
            ComponentI owner
    ) throws Exception {
        super(uri, OvenControllerCI.class, owner);
        assert owner != null;
    }

    @Override
    public void startControlling() throws Exception {
        this.getOwner().handleRequest(
            owner -> {
                ((OvenControllerCI) owner).startControlling();
                return null;
            }
        );
    }

    @Override
    public void stopControlling() throws Exception {
        this.getOwner().handleRequest(
            owner -> {
                ((OvenControllerCI) owner).stopControlling();
                return null;
            }
        );
    }
}
