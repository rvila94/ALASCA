package equipments.HeatPump.temperatureSensor;

import fr.sorbonne_u.alasca.physical_data.Measure;
import fr.sorbonne_u.alasca.physical_data.SignalData;
import fr.sorbonne_u.components.connectors.AbstractConnector;

/**
 * The class <code>equipments.HeatPump.bufferTank.TemperatureSensorConnector</code>.
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
public class TemperatureSensorConnector
extends AbstractConnector
implements TemperatureSensorCI {

    @Override
    public boolean on() throws Exception {
        return ((TemperatureSensorCI)this.offering).on();
    }

    @Override
    public void switchOn() throws Exception {
        ((TemperatureSensorCI)this.offering).switchOn();
    }

    @Override
    public void switchOff() throws Exception {
        ((TemperatureSensorCI)this.offering).switchOff();
    }

    @Override
    public SignalData<Double> getCurrentPower() throws Exception {
        return ((TemperatureSensorCI)this.offering).getCurrentPower();
    }

    @Override
    public Measure<Double> getTemperature() throws Exception {
        return ((TemperatureSensorCI)this.offering).getTemperature();
    }

    @Override
    public Measure<Double> getMinimumRequiredPower() throws Exception {
        return ((TemperatureSensorCI)this.offering).getMinimumRequiredPower();
    }

    @Override
    public Measure<Double> getMaximumPower() throws Exception {
        return ((TemperatureSensorCI)this.offering).getMaximumPower();
    }

    @Override
    public void setPower(Measure<Double> power) throws Exception {
        ((TemperatureSensorCI)this.offering).setPower(power);
    }
}
