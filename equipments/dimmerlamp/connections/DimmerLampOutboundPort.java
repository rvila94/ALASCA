package equipments.dimmerlamp.connections;

import equipments.dimmerlamp.DimmerLampCI;
import fr.sorbonne_u.alasca.physical_data.Measure;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;

public class DimmerLampOutboundPort
extends AbstractOutboundPort
implements DimmerLampCI {
    public DimmerLampOutboundPort(String uri, ComponentI owner) throws Exception {
        super(uri, DimmerLampCI.class, owner);
    }

    public DimmerLampOutboundPort(ComponentI owner) throws Exception {
        super(DimmerLampCI.class, owner);
    }

    @Override
    public void switchOn() throws Exception {
        ((DimmerLampCI)this.getConnector()).switchOn();
    }

    @Override
    public void switchOff() throws Exception {
        ((DimmerLampCI)this.getConnector()).switchOff();
    }

    @Override
    public void setVariationPower(Measure<Integer> variationPower) throws Exception {
        ((DimmerLampCI)this.getConnector()).setVariationPower(variationPower);
    }

    @Override
    public boolean isOn() throws Exception {
        return ((DimmerLampCI)this.getConnector()).isOn();
    }

    @Override
    public Measure<Integer> getCurrentPowerLevel() throws Exception {
        return ((DimmerLampCI)this.getConnector()).getCurrentPowerLevel();
    }
}
