package equipments.HeatPump.connections;

import equipments.HeatPump.interfaces.HeatPumpActuatorCI;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;

/**
 * The class <code>equipments.HeatPump.connections.HeatPumpActuatorOutboundPort</code>.
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
public class HeatPumpActuatorOutboundPort extends AbstractOutboundPort implements HeatPumpActuatorCI{

    public HeatPumpActuatorOutboundPort(String uri, ComponentI owner) throws Exception {
        super(uri, HeatPumpActuatorCI.class, owner);
    }

    public HeatPumpActuatorOutboundPort(ComponentI owner) throws Exception {
        super(HeatPumpActuatorCI.class, owner);
    }

    /**
     * @see equipments.HeatPump.interfaces.HeatPumpActuatorI#startHeating
     */
    @Override
    public void startHeating() throws Exception {
        ((HeatPumpActuatorCI)this.getConnector()).startHeating();
    }

    /**
     * @see equipments.HeatPump.interfaces.HeatPumpActuatorI#stopHeating
     */
    @Override
    public void stopHeating() throws Exception {
        ((HeatPumpActuatorCI)this.getConnector()).stopHeating();
    }

    /**
     * @see equipments.HeatPump.interfaces.HeatPumpActuatorI#startCooling
     */
    @Override
    public void startCooling() throws Exception {
        ((HeatPumpActuatorCI)this.getConnector()).startCooling();
    }

    /**
     * @see equipments.HeatPump.interfaces.HeatPumpActuatorI#stopCooling
     */
    @Override
    public void stopCooling() throws Exception {
        ((HeatPumpActuatorCI)this.getConnector()).stopCooling();
    }
}
