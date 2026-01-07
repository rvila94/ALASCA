package equipments.dimmerlamp.interfaces;

import fr.sorbonne_u.alasca.physical_data.Measure;
import fr.sorbonne_u.alasca.physical_data.MeasurementUnit;

/**
 * The class <code>interfaces.dimmerlamp.equipments.DimmerLampExternalI</code>.
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
public interface DimmerLampExternalI {

    MeasurementUnit TENSION_UNIT = MeasurementUnit.VOLTS;
    Measure<Double> TENSION = new Measure<>(220., TENSION_UNIT);

    void setPower(Measure<Double> variationPower) throws Exception;

    Measure<Double> getCurrentPowerLevel() throws Exception;

    Measure<Double> getMaxPowerLevel() throws Exception;
}
