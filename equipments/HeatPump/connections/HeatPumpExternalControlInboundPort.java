package equipments.HeatPump.connections;

import equipments.HeatPump.HeatPump;
import equipments.HeatPump.powerRepartitionPolicy.PowerRepartitionPolicyI;
import equipments.HeatPump.interfaces.HeatPumpExternalControlCI;
import equipments.HeatPump.interfaces.HeatPumpExternalControlI;
import fr.sorbonne_u.alasca.physical_data.Measure;
import fr.sorbonne_u.alasca.physical_data.SignalData;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;
import fr.sorbonne_u.exceptions.PreconditionException;
import org.apache.commons.math3.analysis.function.Sin;

/**
 * The class <code>connections.HeatPump.equipments.HeatPumpExternalControlInboundPort</code>.
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
public class HeatPumpExternalControlInboundPort
extends AbstractInboundPort
implements HeatPumpExternalControlCI {

    public HeatPumpExternalControlInboundPort(String uri, ComponentI owner) throws Exception {
        super(uri, HeatPumpExternalControlCI.class, owner);
        assert owner instanceof HeatPumpExternalControlI :
                new PreconditionException("owner not instance of DimmerLightI");
    }

    public HeatPumpExternalControlInboundPort(ComponentI owner) throws Exception {
        super(HeatPumpExternalControlCI.class, owner);
        assert owner instanceof HeatPumpExternalControlI:
                new PreconditionException("owner not instance of DimmeLightI");
    }

    @Override
    public SignalData<Double> getCurrentPower() throws Exception {
        return this.getOwner().handleRequest(
                o -> ((HeatPump)o).getCurrentPower()

        );
    }

    @Override
    public Measure<Double> getMaximumPower() throws Exception {
        return this.getOwner().handleRequest(
                o -> ((HeatPump)o).getMaximumPower()
        );
    }

    @Override
    public SignalData<Double> getCurrentTemperature() throws Exception {
        System.out.println("INBOUND");
        SignalData<Double> result = this.getOwner().handleRequest(
                o -> ((HeatPumpExternalControlI)o).getCurrentTemperature()
        );
        System.out.println("OUT");
        return result;
    }

    @Override
    public Measure<Double> getTargetTemperature() throws Exception {
        return this.getOwner().handleRequest(
                owner -> ((HeatPump)owner).getTargetTemperature()
        );
    }

    @Override
    public Measure<Double> getMinimumRequiredPower() throws Exception {
        return this.getOwner().handleRequest(
                owner -> ((HeatPump)owner).getMinimumRequiredPower()
        );
    }

    @Override
    public void setCurrentPower(Measure<Double> power) throws Exception {
        this.getOwner().handleRequest(
                owner -> {
                    ((HeatPump)owner).setCurrentPower(power);
                    return null;
                }
        );
    }

    @Override
    public void setCurrentPower(Measure<Double> power, PowerRepartitionPolicyI policy) throws Exception {
        this.getOwner().handleRequest(
                owner -> {
                    ((HeatPump)owner).setCurrentPower(power, policy);
                    return null;
                }
        );
    }

}
