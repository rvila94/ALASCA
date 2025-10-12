package equipments.HeatPump.connections;

import equipments.HeatPump.interfaces.HeatPumpUserCI;
import fr.sorbonne_u.alasca.physical_data.Measure;
import fr.sorbonne_u.components.connectors.AbstractConnector;

/**
 * The class <code>equipments.HeatPump.connections.HeatPumpUserConnector</code>.
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
public class HeatPumpUserConnector
extends AbstractConnector
implements HeatPumpUserCI {


    @Override
    public boolean on() throws Exception {
        return ((HeatPumpUserCI)this.offering).on();
    }

    @Override
    public void switchOff() throws Exception {
        ((HeatPumpUserCI)this.offering).switchOff();
    }

    @Override
    public void switchOn() throws Exception {
        ((HeatPumpUserCI)this.offering).switchOn();
    }

    @Override
    public State getState() throws Exception {
        return ((HeatPumpUserCI)this.offering).getState();
    }

    @Override
    public void setTargetTemperature(Measure<Double> temperature) throws Exception {
        ((HeatPumpUserCI)this.offering).setTargetTemperature(temperature);
    }
}
