package equipments.dimmerlamp.connections;

import equipments.HeatPump.interfaces.HeatPumpExternalControlCI;
import equipments.dimmerlamp.DimmerLamp;
import equipments.dimmerlamp.interfaces.DimmerLampExternalCI;
import equipments.dimmerlamp.interfaces.DimmerLampExternalI;
import fr.sorbonne_u.alasca.physical_data.Measure;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.interfaces.OfferedCI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;
import fr.sorbonne_u.exceptions.PreconditionException;

/**
 * The class <code>equipments.dimmerlamp.connections.DimmerLampExternalInboundPort</code>.
 *
 * <p><strong>Description</strong></p>
 *
 * <p>
 *
 * </p>
 *
 * <p><strong>Invariants</strong></p>
 *
 * <pre>
 * </pre>
 *
 * <p>Created on : 2025-10-04</p>
 *
 * @author    <a href="mailto:Rodrigo.Vila@etu.sorbonne-universite.fr">Rodrigo Vila</a>
 * @author    <a href="mailto:Damien.Ribeiro@etu.sorbonne-universite.fr">Damien Ribeiro</a>
 */
public class DimmerLampExtenalInboundPort
extends AbstractInboundPort
implements DimmerLampExternalCI {

    public DimmerLampExtenalInboundPort(String uri, ComponentI owner) throws Exception {
        super(uri, HeatPumpExternalControlCI.class, owner);
        assert owner instanceof DimmerLampExternalI :
                new PreconditionException("owner not instance of DimmerLightI");
    }

    public DimmerLampExtenalInboundPort(ComponentI owner) throws Exception {
        super(HeatPumpExternalControlCI.class, owner);
        assert owner instanceof DimmerLampExternalI:
                new PreconditionException("owner not instance of DimmeLightI");
    }

    public DimmerLampExtenalInboundPort(String uri, Class<? extends OfferedCI> implementedInterface, ComponentI owner) throws Exception {
        super(uri, implementedInterface, owner);
    }

    public DimmerLampExtenalInboundPort(Class<? extends OfferedCI> implementedInterface, ComponentI owner) throws Exception {
        super(implementedInterface, owner);
    }

    @Override
    public void setPower(Measure<Double> variationPower) throws Exception {
        this.getOwner().handleRequest(
          owner -> {
              ((DimmerLamp)owner).setPower(variationPower);
              return null;
          }
        );
    }

    @Override
    public Measure<Double> getCurrentPowerLevel() throws Exception {
        return this.getOwner().handleRequest(
                owner -> ((DimmerLamp)owner).getCurrentPowerLevel()
        );
    }

    @Override
    public Measure<Double> getMaxPowerLevel() throws Exception {
        return this.getOwner().handleRequest(
                owner -> ((DimmerLamp)owner).getMaxPowerLevel()
        );
    }
}
