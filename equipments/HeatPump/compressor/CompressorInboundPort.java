package equipments.HeatPump.compressor;

import fr.sorbonne_u.alasca.physical_data.Measure;
import fr.sorbonne_u.alasca.physical_data.SignalData;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;
import fr.sorbonne_u.exceptions.PreconditionException;

/**
 * The class <code>equipments.HeatPump.compressor.CompressorInboundPort</code>.
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
public class CompressorInboundPort
extends AbstractInboundPort
implements CompressorCI {

    public CompressorInboundPort(String uri, ComponentI owner) throws Exception {
        super(uri, CompressorCI.class, owner);
        assert owner instanceof CompressorI :
                new PreconditionException("owner not instance of DimmerLightI");
    }

    public CompressorInboundPort(ComponentI owner) throws Exception {
        super(CompressorCI.class, owner);
        assert owner instanceof CompressorI :
                new PreconditionException("owner not instance of DimmeLightI");
    }

    @Override
    public boolean compressing() throws Exception {
        return this.getOwner().handleRequest(
                owner -> ((Compressor)owner).compressing()
        );
    }

    @Override
    public boolean relaxing() throws Exception {
        return this.getOwner().handleRequest(
                owner -> ((Compressor)owner).relaxing()
        );
    }

    @Override
    public void startCompressing() throws Exception {
        this.getOwner().handleRequest(
                owner -> {
                    ((Compressor)owner).startCompressing();
                    return null;
                }
        );
    }

    @Override
    public void startRelaxing() throws Exception {
        this.getOwner().handleRequest(
                owner -> {
                    ((Compressor)owner).startRelaxing();
                    return null;
                }
        );
    }

    @Override
    public boolean on() throws Exception {
        return this.getOwner().handleRequest(
                owner -> ((Compressor)owner).on()
        );
    }

    @Override
    public void switchOff() throws Exception {
        this.getOwner().handleRequest(
                owner -> {
                    ((Compressor)owner).switchOff();
                    return null;
                }
        );
    }

    @Override
    public void setTargetTemperature(Measure<Double> temperature) throws Exception {
        this.getOwner().handleRequest(
                o -> {
                    ((Compressor)o).setTargetTemperature(temperature);
                    return null;
                }
        );
    }

    @Override
    public SignalData<Double> getCurrentPower() throws Exception {
        return         this.getOwner().handleRequest(
                o -> ((Compressor)o).getCurrentPower()
        );
    }

    @Override
    public Measure<Double> getMinimumRequiredPower() throws Exception {
        return this.getOwner().handleRequest(
                owner -> ((Compressor)owner).getMinimumRequiredPower()
        );
    }

    @Override
    public Measure<Double> getMaximumPower() throws Exception {
        return         this.getOwner().handleRequest(
                o -> ((Compressor)o).getMaximumPower()

        );
    }

    @Override
    public Measure<Double> getTargetTemperature() throws Exception {
        return this.getOwner().handleRequest(
                owner -> ((Compressor)owner).getTargetTemperature()
        );
    }

    @Override
    public void setPower(Measure<Double> power) throws Exception {
        this.getOwner().handleRequest(
                o -> {
                    ((Compressor)o).setPower(power);
                    return null;
                }
        );
    }
}
