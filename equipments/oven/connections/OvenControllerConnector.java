package equipments.oven.connections;

import equipments.oven.OvenControllerCI;
import fr.sorbonne_u.components.connectors.AbstractConnector;

public class OvenControllerConnector
extends AbstractConnector
implements OvenControllerCI
{
    @Override
    public void startControlling() throws Exception {
        ((OvenControllerCI) this.offering).startControlling();
    }

    @Override
    public void stopControlling() throws Exception {
        ((OvenControllerCI) this.offering).stopControlling();
    }
}
