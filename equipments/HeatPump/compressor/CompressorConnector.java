package equipments.HeatPump.compressor;

import fr.sorbonne_u.alasca.physical_data.Measure;
import fr.sorbonne_u.alasca.physical_data.SignalData;
import fr.sorbonne_u.components.connectors.AbstractConnector;

/**
 * The class <code>compressor.HeatPump.equipments.CompressorConnector</code>.
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
public class CompressorConnector
extends AbstractConnector
implements CompressorCI{


    @Override
    public boolean compressing() throws Exception {
        return ((CompressorCI)this.offering).compressing();
    }

    @Override
    public boolean relaxing() throws Exception {
        return ((CompressorCI)this.offering).relaxing();
    }

    @Override
    public void startCompressing() throws Exception {
        ((CompressorCI)this.offering).startCompressing();
    }

    @Override
    public void startRelaxing() throws Exception {
        ((CompressorCI)this.offering).startRelaxing();
    }

    @Override
    public boolean on() throws Exception {
        return ((CompressorCI)this.offering).on();
    }

    @Override
    public void switchOff() throws Exception {

    }

    @Override
    public void setTargetTemperature(Measure<Double> temperature) throws Exception {
        ((CompressorCI)this.offering).setTargetTemperature(temperature);
    }

    @Override
    public SignalData<Double> getCurrentPower() throws Exception {
        return ((CompressorCI)this.offering).getCurrentPower();
    }

    @Override
    public Measure<Double> getMinimumRequiredPower() throws Exception {
        return ((CompressorCI)this.offering).getMinimumRequiredPower();
    }

    @Override
    public Measure<Double> getMaximumPower() throws Exception {
        return ((CompressorCI)this.offering).getMaximumPower();
    }

    @Override
    public Measure<Double> getTargetTemperature() throws Exception {
        return ((CompressorCI)this.offering).getTargetTemperature();
    }

    @Override
    public void setPower(Measure<Double> temperature) throws Exception {
        ((CompressorCI)this.offering).setPower(temperature);
    }

}
