package equipments.dimmerlamp.connections;

import equipments.dimmerlamp.interfaces.DimmerLampUserCI;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;

public class DimmerLampUserOutboundPort
extends AbstractOutboundPort
implements DimmerLampUserCI {
    public DimmerLampUserOutboundPort(String uri, ComponentI owner) throws Exception {
        super(uri, DimmerLampUserCI.class, owner);
    }

    public DimmerLampUserOutboundPort(ComponentI owner) throws Exception {
        super(DimmerLampUserCI.class, owner);
    }

    @Override
    public void switchOn() throws Exception {
        ((DimmerLampUserCI)this.getConnector()).switchOn();
    }

    @Override
    public void switchOff() throws Exception {
        ((DimmerLampUserCI)this.getConnector()).switchOff();
    }

    @Override
    public boolean isOn() throws Exception {
        return ((DimmerLampUserCI)this.getConnector()).isOn();
    }

}
