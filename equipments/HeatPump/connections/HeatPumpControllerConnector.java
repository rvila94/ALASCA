package equipments.HeatPump.connections;

import equipments.HeatPump.interfaces.HeatPumpControllerCI;
import fr.sorbonne_u.components.connectors.AbstractConnector;

/**
 * The class <code>equipments.HeatPump.connections.HeatPumpControllerConnector</code>.
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
public class HeatPumpControllerConnector
extends AbstractConnector
implements HeatPumpControllerCI {

    /**
     * @see equipments.HeatPump.interfaces.HeatPumpControllerI#startControlling
     */
    @Override
    public void startControlling() throws Exception {
        ((HeatPumpControllerCI)this.offering).startControlling();
    }

    /**
     * @see equipments.HeatPump.interfaces.HeatPumpControllerI#stopControlling
     */
    @Override
    public void stopControlling() throws Exception {
        ((HeatPumpControllerCI)this.offering).stopControlling();
    }
}
