package equipments.oven.connections;

import equipments.oven.OvenControllerCI;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;

public class OvenControllerOutboundPort
extends AbstractOutboundPort
implements OvenControllerCI
{
    private static final long serialVersionUID = 1L;

    public OvenControllerOutboundPort(ComponentI owner) throws Exception {
        super(OvenControllerCI.class, owner);
        assert owner != null;
    }

    @Override
    public void startControlling() throws Exception {
        ((OvenControllerCI) this.getConnector()).startControlling();
    }

    @Override
    public void stopControlling() throws Exception {
        ((OvenControllerCI) this.getConnector()).stopControlling();
    }
}
