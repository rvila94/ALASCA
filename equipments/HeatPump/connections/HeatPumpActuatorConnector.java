package equipments.HeatPump.connections;

import equipments.HeatPump.interfaces.HeatPumpActuatorCI;
import fr.sorbonne_u.components.connectors.AbstractConnector;

/**
 * The class <code>equipments.HeatPump.connections.HeatPumpActuatorConnector</code>.
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
public class HeatPumpActuatorConnector
        extends AbstractConnector
implements HeatPumpActuatorCI {


    /**
     * @see equipments.HeatPump.interfaces.HeatPumpActuatorI#startHeating
     */
    @Override
    public void startHeating() throws Exception {
        ((HeatPumpActuatorCI)this.offering).startHeating();
    }

    /**
     * @see equipments.HeatPump.interfaces.HeatPumpActuatorI#stopHeating
     */
    @Override
    public void stopHeating() throws Exception {
        ((HeatPumpActuatorCI)this.offering).stopHeating();
    }

    /**
     * @see equipments.HeatPump.interfaces.HeatPumpActuatorI#startCooling
     */
    @Override
    public void startCooling() throws Exception {
        ((HeatPumpActuatorCI)this.offering).startCooling();
    }

    /**
     * @see equipments.HeatPump.interfaces.HeatPumpActuatorI#stopCooling
     */
    @Override
    public void stopCooling() throws Exception {
        ((HeatPumpActuatorCI)this.offering).stopCooling();
    }
}
