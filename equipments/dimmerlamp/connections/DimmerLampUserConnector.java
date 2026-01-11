package equipments.dimmerlamp.connections;

import equipments.dimmerlamp.interfaces.DimmerLampUserCI;
import fr.sorbonne_u.components.connectors.AbstractConnector;

public class DimmerLampUserConnector
extends AbstractConnector
implements DimmerLampUserCI {
    @Override
    public void switchOn() throws Exception {
        ((DimmerLampUserCI)this.offering).switchOn();
    }

    @Override
    public void switchOff() throws Exception {
        ((DimmerLampUserCI)this.offering).switchOff();
    }

    @Override
    public boolean isOn() throws Exception {
        return ((DimmerLampUserCI)this.offering).isOn();
    }

}
