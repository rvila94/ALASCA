package equipments.dimmerlamp.connections;

import equipments.dimmerlamp.DimmerLampCyPhy;
import equipments.dimmerlamp.interfaces.DimmerLampUserCI;
import equipments.dimmerlamp.interfaces.DimmerLampUserI;
import equipments.dimmerlamp.DimmerLamp;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;
import fr.sorbonne_u.exceptions.PreconditionException;

public class DimmerLampUserInboundPort
extends AbstractInboundPort
implements DimmerLampUserCI {


    public DimmerLampUserInboundPort(String uri, ComponentI owner) throws Exception {
        super(uri, DimmerLampUserCI.class, owner);
        assert owner instanceof DimmerLampUserI :
                new PreconditionException("owner not instance of DimmerLightI");
    }

    public DimmerLampUserInboundPort(ComponentI owner) throws Exception {
        super(DimmerLampUserCI.class, owner);
        assert owner instanceof DimmerLampUserI :
                new PreconditionException("owner not instance of DimmeLightI");
    }

    @Override
    public void switchOn() throws Exception {
        this.getOwner().handleRequest(
                owner -> {
                    System.out.println("SwitchOn Inbound");
                    ((DimmerLamp)owner).switchOn();
                    return null;
                }
        );
        System.out.println("SwitchOn Inbound Ends");
    }

    @Override
    public void switchOff() throws Exception {
        this.getOwner().handleRequest(
            owner -> {
                System.out.println("HEY");
                ((DimmerLamp)owner).switchOff();
                return null;
            }
        );
    }

    @Override
    public boolean isOn() throws Exception {
        return this.getOwner().handleRequest(

                owner -> {
                    System.out.println("HEY");
                    return ((DimmerLamp)owner).isOn();
                }
        );
    }
}
