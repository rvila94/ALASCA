package equipments.HeatPump.interfaces;

import equipments.HeatPump.powerRepartitionPolicy.PowerRepartitionPolicyI;
import fr.sorbonne_u.alasca.physical_data.Measure;
import fr.sorbonne_u.alasca.physical_data.SignalData;

/**
 * The class <code>equipments.HeatPump.interfaces.HeatPumpExternalControlI</code>.
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
public interface HeatPumpExternalControlI {

    SignalData<Double> getCurrentPower() throws Exception;

    Measure<Double> getMaximumPower() throws Exception;

    SignalData<Double> getCurrentTemperature() throws Exception;

    Measure<Double> getTargetTemperature() throws Exception;

    Measure<Double> getMinimumRequiredPower() throws Exception;

    void setCurrentPower(Measure<Double> power) throws Exception;

    void setCurrentPower(Measure<Double> power, PowerRepartitionPolicyI policy) throws Exception;
}
