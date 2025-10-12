package equipments.HeatPump.connections;

import equipments.HeatPump.HeatPump;
import equipments.HeatPump.interfaces.HeatPumpUserCI;
import equipments.HeatPump.interfaces.HeatPumpUserI;
import equipments.dimmerlamp.DimmerLampCI;
import equipments.dimmerlamp.DimmerLampI;
import fr.sorbonne_u.alasca.physical_data.Measure;
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
        return ((HeatPump)this.getOwner()).on();
    }

    @Override
    public void switchOff() throws Exception {
        ((HeatPump)this.getOwner()).switchOff();
    }

    @Override
    public void switchOn() throws Exception {
        ((HeatPump)this.getOwner()).switchOn();
    }

    @Override
    public State getState() throws Exception {
        return ((HeatPump)this.getOwner()).getState();
    }

    @Override
    public void setTargetTemperature(Measure<Double> temperature) throws Exception {
        ((HeatPump)this.getOwner()).setTargetTemperature(temperature);
    }
}
