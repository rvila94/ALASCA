package equipments.dimmerlamp.connections;

import equipments.dimmerlamp.DimmerLampCI;
import fr.sorbonne_u.alasca.physical_data.Measure;
import fr.sorbonne_u.components.connectors.AbstractConnector;

public class DimmerLampConnector
extends AbstractConnector
implements DimmerLampCI {
    @Override
    public void switchOn() throws Exception {
        ((DimmerLampCI)this.offering).switchOn();
    }

    @Override
    public void switchOff() throws Exception {
        ((DimmerLampCI)this.offering).switchOff();
    }

    @Override
    public void setVariationPower(Measure<Integer> variationPower) throws Exception {
        ((DimmerLampCI)this.offering).setVariationPower(variationPower);
    }

    @Override
    public boolean isOn() throws Exception {
        return ((DimmerLampCI)this.offering).isOn();
    }

    @Override
    public Measure<Integer> getCurrentPowerLevel() throws Exception {
        return ((DimmerLampCI)this.offering).getCurrentPowerLevel();
    }
}
