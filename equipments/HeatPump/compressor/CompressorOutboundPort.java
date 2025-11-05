package equipments.HeatPump.compressor;

import fr.sorbonne_u.alasca.physical_data.Measure;
import fr.sorbonne_u.alasca.physical_data.SignalData;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;

/**
 * The class <code>compressor.HeatPump.equipments.CompressorOutboundPort</code>.
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
public class CompressorOutboundPort
extends AbstractOutboundPort
implements CompressorCI{
    public CompressorOutboundPort(String uri, ComponentI owner) throws Exception {
        super(uri, CompressorCI.class, owner);
    }

    public CompressorOutboundPort(ComponentI owner) throws Exception {
        super(CompressorCI.class, owner);
    }

    @Override
    public boolean compressing() throws Exception {
        return ((CompressorCI)this.getConnector()).compressing();
    }

    @Override
    public boolean relaxing() throws Exception {
        return ((CompressorCI)this.getConnector()).relaxing();
    }

    @Override
    public void startCompressing() throws Exception {
        ((CompressorCI)this.getConnector()).startCompressing();
    }

    @Override
    public void startRelaxing() throws Exception {
        ((CompressorCI)this.getConnector()).startRelaxing();
    }

    @Override
    public boolean on() throws Exception {
        return ((CompressorCI)this.getConnector()).on();
    }

    @Override
    public void switchOff() throws Exception {
        ((CompressorCI)this.getConnector()).switchOff();
    }

    @Override
    public void setTargetTemperature(Measure<Double> temperature) throws Exception {
        ((CompressorCI)this.getConnector()).setTargetTemperature(temperature);
    }

    @Override
    public SignalData<Double> getCurrentPower() throws Exception {
        return ((CompressorCI)this.getConnector()).getCurrentPower();
    }

    @Override
    public Measure<Double> getMinimumRequiredPower() throws Exception {
        return ((CompressorCI)this.getConnector()).getMinimumRequiredPower();
    }

    @Override
    public Measure<Double> getMaximumPower() throws Exception {
        return ((CompressorCI)this.getConnector()).getMaximumPower();
    }

    @Override
    public Measure<Double> getTargetTemperature() throws Exception {
        return ((CompressorCI)this.getConnector()).getTargetTemperature();
    }

    @Override
    public void setPower(Measure<Double> temperature) throws Exception {
        ((CompressorCI)this.getConnector()).setPower(temperature);
    }
}
