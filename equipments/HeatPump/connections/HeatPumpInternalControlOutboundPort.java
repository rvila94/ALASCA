package equipments.HeatPump.connections;

import equipments.HeatPump.interfaces.HeatPumpInternalControlCI;
import fr.sorbonne_u.alasca.physical_data.Measure;
import fr.sorbonne_u.alasca.physical_data.SignalData;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;

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
public class HeatPumpInternalControlOutboundPort
extends AbstractOutboundPort
implements HeatPumpInternalControlCI {

    public HeatPumpInternalControlOutboundPort(String uri, ComponentI owner) throws Exception {
        super(uri, HeatPumpInternalControlCI.class, owner);
    }

    public HeatPumpInternalControlOutboundPort(ComponentI owner) throws Exception {
        super(HeatPumpInternalControlCI.class, owner);
    }


    @Override
    public boolean heating() throws Exception {
        return ((HeatPumpInternalControlCI)this.getConnector()).heating();
    }

    @Override
    public boolean cooling() throws Exception {
        return ((HeatPumpInternalControlCI)this.getConnector()).cooling();
    }

    @Override
    public void startHeating() throws Exception {
        ((HeatPumpInternalControlCI)this.getConnector()).startHeating();
    }

    @Override
    public void stopHeating() throws Exception {
        ((HeatPumpInternalControlCI)this.getConnector()).stopHeating();
    }

    @Override
    public void startCooling() throws Exception {
        ((HeatPumpInternalControlCI)this.getConnector()).startCooling();
    }

    @Override
    public void stopCooling() throws Exception {
        ((HeatPumpInternalControlCI)this.getConnector()).stopCooling();
    }

    @Override
    public SignalData<Double> getCurrentTemperature() throws Exception {
        return ((HeatPumpInternalControlCI)this.getConnector()).getCurrentTemperature();
    }

    @Override
    public Measure<Double> getTargetTemperature() throws Exception {
        return ((HeatPumpInternalControlCI)this.getConnector()).getTargetTemperature();
    }
}
