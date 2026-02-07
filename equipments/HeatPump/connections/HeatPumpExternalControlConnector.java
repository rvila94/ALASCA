package equipments.HeatPump.connections;

import equipments.HeatPump.powerRepartitionPolicy.PowerRepartitionPolicyI;
import equipments.HeatPump.interfaces.HeatPumpExternalControlCI;
import fr.sorbonne_u.alasca.physical_data.Measure;
import fr.sorbonne_u.alasca.physical_data.SignalData;
import fr.sorbonne_u.components.connectors.AbstractConnector;

/**
 * The class <code>connections.HeatPump.equipments.HeatPumpExternalControlConnector</code>.
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
public class HeatPumpExternalControlConnector
extends AbstractConnector
implements HeatPumpExternalControlCI {

    @Override
    public SignalData<Double> getCurrentPower() throws Exception {
        return ((HeatPumpExternalControlCI)this.offering).getCurrentPower();
    }

    @Override
    public Measure<Double> getMaximumPower() throws Exception {
        return ((HeatPumpExternalControlCI)this.offering).getMaximumPower();
    }

    @Override
    public SignalData<Double> getCurrentTemperature() throws Exception {
        SignalData result = ((HeatPumpExternalControlCI)this.offering).getCurrentTemperature();
        return result;
    }

    @Override
    public Measure<Double> getTargetTemperature() throws Exception {
        return ((HeatPumpExternalControlCI)this.offering).getTargetTemperature();
    }

    @Override
    public Measure<Double> getMinimumRequiredPower() throws Exception {
        return ((HeatPumpExternalControlCI)this.offering).getMinimumRequiredPower();
    }

    @Override
    public void setCurrentPower(Measure<Double> power) throws Exception {
        ((HeatPumpExternalControlCI)this.offering).setCurrentPower(power);
    }

    @Override
    public void setCurrentPower(Measure<Double> power, PowerRepartitionPolicyI policy) throws Exception {
        ((HeatPumpExternalControlCI)this.offering).setCurrentPower(power, policy);
    }

}
