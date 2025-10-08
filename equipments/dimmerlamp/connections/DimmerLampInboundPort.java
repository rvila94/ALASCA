package equipments.dimmerlamp.connections;

import equipments.dimmerlamp.DimmerLamp;
import equipments.dimmerlamp.DimmerLampI;
import equipments.dimmerlamp.DimmerLampCI;
import fr.sorbonne_u.alasca.physical_data.Measure;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;
import fr.sorbonne_u.exceptions.PreconditionException;

public class DimmerLampInboundPort
extends AbstractInboundPort
implements DimmerLampCI {


    public DimmerLampInboundPort(String uri, ComponentI owner) throws Exception {
        super(uri, DimmerLampCI.class, owner);
        assert owner instanceof DimmerLampI :
                new PreconditionException("owner not instance of DimmerLightI");
    }

    public DimmerLampInboundPort(ComponentI owner) throws Exception {
        super(DimmerLampCI.class, owner);
        assert owner instanceof DimmerLampI:
                new PreconditionException("owner not instance of DimmeLightI");
    }

    @Override
    public void switchOn() throws Exception {
        this.getOwner().handleRequest(
                owner -> {
                    ((DimmerLamp)owner).switchOn();
                    return null;
                }
        );
    }

    @Override
    public void switchOff() throws Exception {
        this.getOwner().handleRequest(
            owner -> {
                ((DimmerLamp)owner).switchOff();
                return null;
            }
        );
    }

    @Override
    public void setVariationPower(Measure<Integer> variationPower) throws Exception {
        this.getOwner().handleRequest(
                owner -> {
                    ((DimmerLamp)owner).setVariationPower(variationPower);
                    return null;
                }
        );
    }

    @Override
    public boolean isOn() throws Exception {
        return this.getOwner().handleRequest(
                owner -> ((DimmerLamp)owner).isOn()
        );
    }

    @Override
    public Measure<Integer> getCurrentPowerLevel() throws Exception {
        return this.getOwner().handleRequest(
                owner -> ((DimmerLamp)owner).getCurrentPowerLevel()
        );
    }
}
