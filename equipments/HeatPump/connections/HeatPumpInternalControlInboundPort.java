package equipments.HeatPump.connections;

import equipments.HeatPump.HeatPump;
import equipments.HeatPump.interfaces.HeatPumpInternalControlCI;
import equipments.HeatPump.interfaces.HeatPumpInternalControlI;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;
import fr.sorbonne_u.exceptions.PreconditionException;

/**
 * The class <code>equipments.HeatPump.connections.HeatPumpInternalControlInboundPort</code>.
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
public class HeatPumpInternalControlInboundPort
extends AbstractInboundPort
implements HeatPumpInternalControlCI {


    public HeatPumpInternalControlInboundPort(String uri, ComponentI owner) throws Exception {
        super(uri, HeatPumpInternalControlCI.class, owner);
        assert owner instanceof HeatPumpInternalControlI :
                new PreconditionException("owner not instance of HeatPumpInternalControlI");
    }

    public HeatPumpInternalControlInboundPort(ComponentI owner) throws Exception {
        super(HeatPumpInternalControlCI.class, owner);
        assert owner instanceof HeatPumpInternalControlI :
                new PreconditionException("owner not instance of HeatPumpInternalControlI");
    }

    @Override
    public boolean heating() throws Exception {
        return this.getOwner().handleRequest(
                o -> ((HeatPump)o).heating()
        );
    }

    @Override
    public boolean cooling() throws Exception {
        return this.getOwner().handleRequest(
                o -> ((HeatPump)o).cooling()
        );
    }

    @Override
    public void startHeating() throws Exception {
        this.getOwner().handleRequest(
                o -> {
                    ((HeatPump)o).startHeating();
                    return null;
                }
        );
    }

    @Override
    public void stopHeating() throws Exception {
        this.getOwner().handleRequest(
                o -> {
                    ((HeatPump)o).stopHeating();
                    return null;
                }
        );
    }

    @Override
    public void startCooling() throws Exception {
        this.getOwner().handleRequest(
                o -> {
                    ((HeatPump)o).startCooling();
                    return null;
                }
        );
    }

    @Override
    public void stopCooling() throws Exception {
        this.getOwner().handleRequest(
                o -> {
                    ((HeatPump)o).stopCooling();
                    return null;
                }
        );
    }
}
