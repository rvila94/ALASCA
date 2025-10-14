package equipments.HeatPump.connections;

import equipments.HeatPump.interfaces.HeatPumpExternalJava4CI;;
import equipments.HeatPump.powerRepartitionPolicy.PowerRepartitionPolicyI;
import fr.sorbonne_u.alasca.physical_data.Measure;
import fr.sorbonne_u.alasca.physical_data.MeasurementUnit;
import fr.sorbonne_u.components.ComponentI;

/**
 * The class <code>equipments.HeatPump.connections.HeatPumpExternalJava4InboundPort</code>.
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
public class HeatPumpExternalJava4InboundPort
extends HeatPumpExternalControlInboundPort
implements HeatPumpExternalJava4CI {
    public HeatPumpExternalJava4InboundPort(String uri, ComponentI owner) throws Exception {
        super(uri, owner);
    }

    public HeatPumpExternalJava4InboundPort(ComponentI owner) throws Exception {
        super(owner);
    }

    @Override
    public double getMaxPowerJava4() throws Exception {
        return this.getMaximumPower().getData();
    }

    @Override
    public double getMinimumRequiredPowerJava4() throws Exception {
        return this.getMinimumRequiredPower().getData();
    }

    @Override
    public double getCurrentPowerJava4() throws Exception {
        return this.getCurrentPower().getMeasure().getData();
    }

    @Override
    public void setCurrentPowerLevelJava4(double powerLevel) throws Exception {
        this.setCurrentPower(new Measure<>(powerLevel, MeasurementUnit.WATTS));
    }

    @Override
    public void setCurrentPowerLevelJava4(double powerLevel, PowerRepartitionPolicyI policy) throws Exception {
        this.setCurrentPower(new Measure<>(powerLevel, MeasurementUnit.WATTS), policy);
    }

    @Override
    public double getCurrentTemperatureJava4() throws Exception {
        return this.getCurrentTemperature().getMeasure().getData();
    }

    @Override
    public double getTargetTemperatureJava4() throws Exception {
        return this.getTargetTemperature().getData();
    }
}
