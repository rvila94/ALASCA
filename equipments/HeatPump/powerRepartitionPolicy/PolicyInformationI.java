package equipments.HeatPump.powerRepartitionPolicy;

import fr.sorbonne_u.alasca.physical_data.Measure;

/**
 * The class <code>powerRepartitionPolicy.HeatPump.equipments.PolicyInformationI</code>.
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
public interface PolicyInformationI {

    Measure<Double> getPowerAvailable();

    Measure<Double> getMinPumpPowerRequired();

    Measure<Double> getMaxPumpPowerSupported();

    Measure<Double> getMinCompPowerRequired();

    Measure<Double> getMaxCompPowerSupported();

    Measure<Double> getMinSensorPowerRequired();

    Measure<Double> getMaxSensorPowerSupported();
}
