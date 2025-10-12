package equipments.HeatPump.connections;

import equipments.HeatPump.interfaces.HeatPumpUserCI;
import fr.sorbonne_u.alasca.physical_data.Measure;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;

/**
 * The class <code>equipments.HeatPump.connections.HeatPumpUserOutboundPort</code>.
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
public class HeatPumpUserOutboundPort
extends AbstractOutboundPort
implements HeatPumpUserCI {
    public HeatPumpUserOutboundPort(String uri, ComponentI owner) throws Exception {
        super(uri, HeatPumpUserCI.class, owner);
    }

    public HeatPumpUserOutboundPort(ComponentI owner) throws Exception {
        super(HeatPumpUserCI.class, owner);
    }

    @Override
    public boolean on() throws Exception {
        return ((HeatPumpUserCI)this.getConnector()).on();
    }

    @Override
    public void switchOff() throws Exception {
        ((HeatPumpUserCI)this.getConnector()).switchOff();
    }

    @Override
    public void switchOn() throws Exception {
        ((HeatPumpUserCI)this.getConnector()).switchOn();
    }

    @Override
    public State getState() throws Exception {
        return ((HeatPumpUserCI)this.getConnector()).getState();
    }

    @Override
    public void setTargetTemperature(Measure<Double> temperature) throws Exception {
        ((HeatPumpUserCI)this.getConnector()).setTargetTemperature(temperature);
    }
}
