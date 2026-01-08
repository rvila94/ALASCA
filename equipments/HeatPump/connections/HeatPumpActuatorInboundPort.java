package equipments.HeatPump.connections;

import equipments.HeatPump.HeatPump;
import equipments.HeatPump.interfaces.HeatPumpActuatorCI;
import equipments.HeatPump.interfaces.HeatPumpActuatorI;
import equipments.HeatPump.interfaces.HeatPumpInternalControlCI;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;
import fr.sorbonne_u.exceptions.PreconditionException;

/**
 * The class <code>equipments.HeatPump.connections.HeatPumpActuatorInboundPort</code>.
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
public class HeatPumpActuatorInboundPort
        extends AbstractInboundPort
    implements HeatPumpActuatorCI {

    public HeatPumpActuatorInboundPort(String uri, ComponentI owner) throws Exception {
        super(uri, HeatPumpInternalControlCI.class, owner);
        assert owner instanceof HeatPumpActuatorI :
                new PreconditionException("owner not instance of HeatPumpActuatorI");
    }

    public HeatPumpActuatorInboundPort(ComponentI owner) throws Exception {
        super(HeatPumpInternalControlCI.class, owner);
        assert owner instanceof HeatPumpActuatorI :
                new PreconditionException("owner not instance of HeatPumpActuatorI");
    }

    /**
     * @see equipments.HeatPump.interfaces.HeatPumpActuatorI#startHeating
     */
    @Override
    public void startHeating() throws Exception {
        this.getOwner().handleRequest(
                owner -> {
                    ((HeatPump)owner).startHeating();
                    return null;
                }
        );
    }

    /**
     * @see equipments.HeatPump.interfaces.HeatPumpActuatorI#stopHeating
     */
    @Override
    public void stopHeating() throws Exception {
        this.getOwner().handleRequest(
                owner -> {
                    ((HeatPump)owner).stopHeating();
                    return null;
                }
        );
    }

    /**
     * @see equipments.HeatPump.interfaces.HeatPumpActuatorI#startCooling
     */
    @Override
    public void startCooling() throws Exception {
        this.getOwner().handleRequest(
                owner -> {
                    ((HeatPump)owner).startCooling();
                    return null;
                }
        );
    }

    /**
     * @see equipments.HeatPump.interfaces.HeatPumpActuatorI#stopCooling
     */
    @Override
    public void stopCooling() throws Exception {
        this.getOwner().handleRequest(
                owner -> {
                    ((HeatPump)owner).stopCooling();
                    return null;
                }
        );
    }

}
