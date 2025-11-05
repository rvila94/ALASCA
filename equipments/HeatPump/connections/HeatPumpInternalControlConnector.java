package equipments.HeatPump.connections;

import equipments.HeatPump.interfaces.HeatPumpInternalControlCI;
import fr.sorbonne_u.alasca.physical_data.Measure;
import fr.sorbonne_u.alasca.physical_data.SignalData;
import fr.sorbonne_u.components.connectors.AbstractConnector;

/**
 * The class <code>connections.HeatPump.equipments.HeatPumpInternalControlConnector</code>.
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
public class HeatPumpInternalControlConnector
extends AbstractConnector
implements HeatPumpInternalControlCI {
    @Override
    public boolean heating() throws Exception {
        return ((HeatPumpInternalControlCI)this.offering).heating();
    }

    @Override
    public boolean cooling() throws Exception {
        return ((HeatPumpInternalControlCI)this.offering).cooling();
    }

    @Override
    public void startHeating() throws Exception {
        ((HeatPumpInternalControlCI)this.offering).startHeating();
    }

    @Override
    public void stopHeating() throws Exception {
        ((HeatPumpInternalControlCI)this.offering).stopHeating();
    }

    @Override
    public void startCooling() throws Exception {
        ((HeatPumpInternalControlCI)this.offering).startCooling();
    }

    @Override
    public void stopCooling() throws Exception {
        ((HeatPumpInternalControlCI)this.offering).stopCooling();
    }

    @Override
    public SignalData<Double> getCurrentTemperature() throws Exception {
        return ((HeatPumpInternalControlCI)this.offering).getCurrentTemperature();
    }

    @Override
    public Measure<Double> getTargetTemperature() throws Exception {
        return ((HeatPumpInternalControlCI)this.offering).getTargetTemperature();
    }
}
