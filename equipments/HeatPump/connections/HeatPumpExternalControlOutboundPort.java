package equipments.HeatPump.connections;

import equipments.HeatPump.interfaces.HeatPumpExternalControlCI;
import equipments.HeatPump.powerRepartitionPolicy.PowerRepartitionPolicyI;
import fr.sorbonne_u.alasca.physical_data.Measure;
import fr.sorbonne_u.alasca.physical_data.SignalData;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;

/**
 * The class <code>equipments.HeatPump.connections.HeatPumpExternalControlOutboundPort</code>.
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
public class HeatPumpExternalControlOutboundPort
extends AbstractOutboundPort
implements HeatPumpExternalControlCI {
    public HeatPumpExternalControlOutboundPort(String uri, ComponentI owner) throws Exception {
        super(uri, HeatPumpExternalControlCI.class, owner);
    }

    public HeatPumpExternalControlOutboundPort(ComponentI owner) throws Exception {
        super(HeatPumpExternalControlCI.class, owner);
    }

    @Override
    public SignalData<Double> getCurrentPower() throws Exception {
        return ((HeatPumpExternalControlCI)this.getConnector()).getCurrentPower();
    }

    @Override
    public Measure<Double> getMaximumPower() throws Exception {
        return ((HeatPumpExternalControlCI)this.getConnector()).getMaximumPower();
    }

    @Override
    public SignalData<Double> getCurrentTemperature() throws Exception {
        return ((HeatPumpExternalControlCI)this.getConnector()).getCurrentTemperature();
    }

    @Override
    public Measure<Double> getMinimumRequiredPower() throws Exception {
        return ((HeatPumpExternalControlCI)this.getConnector()).getMinimumRequiredPower();
    }

    @Override
    public void setCurrentPower(Measure<Double> power) throws Exception {
        ((HeatPumpExternalControlCI)this.getConnector()).setCurrentPower(power);
    }

    @Override
    public void setCurrentPower(Measure<Double> power, PowerRepartitionPolicyI policy) throws Exception {
        ((HeatPumpExternalControlCI)this.getConnector()).setCurrentPower(power, policy);
    }

}
