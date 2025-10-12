package equipments.HeatPump.temperatureSensor;

import fr.sorbonne_u.alasca.physical_data.Measure;
import fr.sorbonne_u.alasca.physical_data.SignalData;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;
import fr.sorbonne_u.exceptions.PreconditionException;

/**
 * The class <code>equipments.HeatPump.bufferTank.TemperatureSensorInboundPort</code>.
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
public class TemperatureSensorInboundPort
extends AbstractInboundPort
implements TemperatureSensorCI {

    public TemperatureSensorInboundPort(String uri, ComponentI owner) throws Exception {
        super(uri, TemperatureSensorCI.class, owner);
        assert owner instanceof TemperatureSensorI :
                new PreconditionException("owner not instance of TemperatureSensorI");
    }

    public TemperatureSensorInboundPort(ComponentI owner) throws Exception {
        super(TemperatureSensorCI.class, owner);
        assert owner instanceof TemperatureSensorI :
                new PreconditionException("owner not instance of TemperatureSensorI");
    }

    @Override
    public boolean on() throws Exception {
        return this.getOwner().handleRequest(
                owner -> ((TemperatureSensor)owner).on()
        );
    }

    @Override
    public void switchOn() throws Exception {
        this.getOwner().handleRequest(
                owner -> {
                    ((TemperatureSensor)owner).switchOn();
                    return null;
                }
        );
    }

    @Override
    public void switchOff() throws Exception {
        this.getOwner().handleRequest(
                owner -> {
                    ((TemperatureSensor)owner).switchOff();
                    return null;
                }
        );
    }

    @Override
    public SignalData<Double> getCurrentPower() throws Exception {
        return this.getOwner().handleRequest(
                owner -> ((TemperatureSensor)owner).getCurrentPower()
        );
    }

    @Override
    public Measure<Double> getTemperature() throws Exception {
        return this.getOwner().handleRequest(
                owner -> ((TemperatureSensor)owner).getTemperature()
        );
    }

    @Override
    public Measure<Double> getMinimumRequiredPower() throws Exception {
        return this.getOwner().handleRequest(
                owner -> ((TemperatureSensor)owner).getMinimumRequiredPower()
        );
    }

    @Override
    public Measure<Double> getMaximumPower() throws Exception {
        return this.getOwner().handleRequest(
                owner -> ((TemperatureSensor)owner).getMaximumPower()
        );
    }

    @Override
    public void setPower(Measure<Double> power) throws Exception {
        this.getOwner().handleRequest(
                owner -> {
                    ((TemperatureSensor)owner).setPower(power);
                    return null;
                }
        );
    }
}
