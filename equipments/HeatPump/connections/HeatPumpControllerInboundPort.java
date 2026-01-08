package equipments.HeatPump.connections;

import equipments.HeatPump.HeatPumpController;
import equipments.HeatPump.interfaces.HeatPumpControllerCI;
import equipments.HeatPump.interfaces.HeatPumpControllerI;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;
import fr.sorbonne_u.exceptions.PreconditionException;

/**
 * The class <code>equipments.HeatPump.connections.HeatPumpControllerInboundPort</code>.
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
public class HeatPumpControllerInboundPort extends AbstractInboundPort implements HeatPumpControllerCI {

    public HeatPumpControllerInboundPort(String uri, ComponentI owner) throws Exception {
        super(uri, HeatPumpControllerCI.class, owner);
        assert owner instanceof HeatPumpControllerI :
                new PreconditionException("owner not instance of HeatPumpControllerI");
    }

    public HeatPumpControllerInboundPort(ComponentI owner) throws Exception {
        super(HeatPumpControllerCI.class, owner);
        assert owner instanceof HeatPumpControllerI :
                new PreconditionException("owner not instance of HeatPumpControllerI");
    }

    /**
     * @see equipments.HeatPump.interfaces.HeatPumpControllerI#startControlling
     */
    @Override
    public void startControlling() throws Exception {
        this.getOwner().handleRequest(
                owner -> {
                    ((HeatPumpController)owner).startControlling();
                    return null;
                }
        );
    }

    /**
     * @see equipments.HeatPump.interfaces.HeatPumpControllerI#stopControlling
     */
    @Override
    public void stopControlling() throws Exception {
        this.getOwner().handleRequest(
                owner -> {
                    ((HeatPumpController)owner).stopControlling();
                    return null;
                }
        );
    }
}
