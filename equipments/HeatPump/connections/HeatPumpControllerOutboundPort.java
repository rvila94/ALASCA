package equipments.HeatPump.connections;

import equipments.HeatPump.interfaces.HeatPumpActuatorCI;
import equipments.HeatPump.interfaces.HeatPumpControllerCI;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;

/**
 * The class <code>equipments.HeatPump.connections.HeatPumpControllerOutboundPort</code>.
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
public class HeatPumpControllerOutboundPort
extends AbstractOutboundPort
implements HeatPumpControllerCI {

    public HeatPumpControllerOutboundPort(String uri, ComponentI owner) throws Exception {
        super(uri, HeatPumpControllerCI.class, owner);
    }

    public HeatPumpControllerOutboundPort(ComponentI owner) throws Exception {
        super(HeatPumpControllerCI.class, owner);
    }

    /**
     * @see equipments.HeatPump.interfaces.HeatPumpControllerI#startControlling
     */
    @Override
    public void startControlling() throws Exception {
        ((HeatPumpControllerCI)this.getConnector()).startControlling();
    }

    /**
     * @see equipments.HeatPump.interfaces.HeatPumpControllerI#stopControlling
     */
    @Override
    public void stopControlling() throws Exception {
        ((HeatPumpControllerCI)this.getConnector()).stopControlling();
    }
}
