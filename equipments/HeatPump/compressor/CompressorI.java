package equipments.HeatPump.compressor;

import fr.sorbonne_u.alasca.physical_data.Measure;
import fr.sorbonne_u.alasca.physical_data.SignalData;

/**
 * The class <code>equipments.HeatPump.HeatCompressorI</code>.
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
public interface CompressorI {

    enum CompressorState {
        Relaxing,
        Compressing,
        Off
    }

    boolean compressing() throws Exception;

    boolean relaxing() throws Exception;

    void startCompressing() throws Exception;

    void startRelaxing() throws Exception;

    boolean on() throws Exception;

    void switchOff() throws Exception;

    void setTargetTemperature(Measure<Double> temperature) throws Exception;

    SignalData<Double> getCurrentPower() throws Exception;

    Measure<Double> getMinimumRequiredPower() throws Exception;

    Measure<Double> getMaximumPower() throws Exception;

    void setPower(Measure<Double> power) throws Exception;
}
