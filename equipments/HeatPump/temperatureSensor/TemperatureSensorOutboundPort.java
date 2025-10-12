package equipments.HeatPump.temperatureSensor;

import fr.sorbonne_u.alasca.physical_data.Measure;
import fr.sorbonne_u.alasca.physical_data.SignalData;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;

/**
 * The class <code>equipments.HeatPump.bufferTank.TemperatureSensorOutboundPort</code>.
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
public class TemperatureSensorOutboundPort
extends AbstractOutboundPort
implements TemperatureSensorCI {


    public TemperatureSensorOutboundPort(String uri, ComponentI owner) throws Exception {
        super(uri, TemperatureSensorCI.class, owner);
    }

    public TemperatureSensorOutboundPort(ComponentI owner) throws Exception {
        super(TemperatureSensorCI.class, owner);
    }

    @Override
    public boolean on() throws Exception {
        return ((TemperatureSensorCI)this.getConnector()).on();
    }

    @Override
    public void switchOn() throws Exception {
        ((TemperatureSensorCI)this.getConnector()).switchOn();
    }

    @Override
    public void switchOff() throws Exception {
        ((TemperatureSensorCI)this.getConnector()).switchOff();
    }

    @Override
    public SignalData<Double> getCurrentPower() throws Exception {
        return ((TemperatureSensorCI)this.getConnector()).getCurrentPower();
    }

    @Override
    public Measure<Double> getTemperature() throws Exception {
        return ((TemperatureSensorCI)this.getConnector()).getTemperature();
    }

    @Override
    public Measure<Double> getMinimumRequiredPower() throws Exception {
        return ((TemperatureSensorCI)this.getConnector()).getMinimumRequiredPower();
    }

    @Override
    public Measure<Double> getMaximumPower() throws Exception {
        return ((TemperatureSensorCI)this.getConnector()).getMaximumPower();
    }

    @Override
    public void setPower(Measure<Double> power) throws Exception {
        ((TemperatureSensorCI)this.getConnector()).setPower(power);
    }
}
