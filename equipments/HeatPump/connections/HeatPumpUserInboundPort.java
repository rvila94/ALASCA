package equipments.HeatPump.connections;

import equipments.HeatPump.HeatPump;
import equipments.HeatPump.interfaces.HeatPumpUserCI;
import equipments.HeatPump.interfaces.HeatPumpUserI;
import fr.sorbonne_u.alasca.physical_data.Measure;
import fr.sorbonne_u.alasca.physical_data.SignalData;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;
import fr.sorbonne_u.exceptions.PreconditionException;

/**
 * The class <code>equipments.HeatPump.connections.HeatPumpUserInboundPort</code>.
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
public class HeatPumpUserInboundPort
extends AbstractInboundPort
implements HeatPumpUserCI {

    public HeatPumpUserInboundPort(String uri, ComponentI owner) throws Exception {
        super(uri, HeatPumpUserCI.class, owner);
        assert owner instanceof HeatPumpUserI :
                new PreconditionException("owner not instance of HeatPumpUserI");
    }

    public HeatPumpUserInboundPort(ComponentI owner) throws Exception {
        super(HeatPumpUserCI.class, owner);
        assert owner instanceof HeatPumpUserI:
                new PreconditionException("owner not instance of HeatPumpUserI");
    }

    @Override
    public boolean on() throws Exception {
        return this.getOwner().handleRequest(
                owner -> ((HeatPump)owner).on()
        );
    }

    @Override
    public void switchOff() throws Exception {
        this.getOwner().handleRequest(
                owner -> {
                    ((HeatPump)owner).switchOff();
                    return null;
                }
        );
    }

    @Override
    public void switchOn() throws Exception {
        this.getOwner().handleRequest(
                owner -> {
                    ((HeatPump)owner).switchOn();
                    return null;
                }
        );
    }

    @Override
    public State getState() throws Exception {
        return ((HeatPump)this.getOwner()).getState();
    }

    @Override
    public void setTargetTemperature(Measure<Double> temperature) throws Exception {
        this.getOwner().handleRequest(
                owner -> {
                    ((HeatPump)owner).setTargetTemperature(temperature);
                    return null;
                }
        );
    }

    @Override
    public SignalData<Double> getCurrentTemperature() throws Exception {
        return this.getOwner().handleRequest(
                owner -> ((HeatPump)owner).getCurrentTemperature()
        );
    }

    @Override
    public Measure<Double> getTargetTemperature() throws Exception {
        return this.getOwner().handleRequest(
                owner -> ((HeatPump)owner).getTargetTemperature()
        );
    }
}
