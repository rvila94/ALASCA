package equipments.HeatPump.powerRepartitionPolicy;

import fr.sorbonne_u.alasca.physical_data.Measure;

/**
 * The class <code>equipments.HeatPump.powerRepartitionPolicy.PolicyInformation</code>.
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
public class PolicyInformation
implements PolicyInformationI{

    public final Measure<Double> powerAvailable;
    public final Measure<Double> minPumpPowerRequired;

    public final Measure<Double> maxPumpPowerSupported;

    public final Measure<Double> minCompPowerRequired;

    public final Measure<Double> maxCompPowerSupported;

    public final Measure<Double> minSensorPowerRequired;

    public final Measure<Double> maxSensorPowerSupported;

    public PolicyInformation(
            Measure<Double> powerAvailable,
            Measure<Double> minPumpPowerRequired,
            Measure<Double> maxPumpPowerSupported,
            Measure<Double> minCompPowerRequired,
            Measure<Double> maxCompPowerSupported,
            Measure<Double> minSensorPowerRequired,
            Measure<Double> maxSensorPowerSupported)
    {
        this.powerAvailable = powerAvailable;

        this.minPumpPowerRequired = minPumpPowerRequired;
        this.maxPumpPowerSupported = maxPumpPowerSupported;

        this.minCompPowerRequired = minCompPowerRequired;
        this.maxCompPowerSupported = maxCompPowerSupported;

        this.minSensorPowerRequired = minSensorPowerRequired;
        this.maxSensorPowerSupported = maxSensorPowerSupported;
    }

    @Override
    public Measure<Double> getPowerAvailable() {
        return this.powerAvailable;
    }

    @Override
    public Measure<Double> getMinPumpPowerRequired() {
        return this.minPumpPowerRequired;
    }

    @Override
    public Measure<Double> getMaxPumpPowerSupported() {
        return this.maxPumpPowerSupported;
    }

    @Override
    public Measure<Double> getMinCompPowerRequired() {
        return this.minCompPowerRequired;
    }

    @Override
    public Measure<Double> getMaxCompPowerSupported() {
        return this.maxCompPowerSupported;
    }

    @Override
    public Measure<Double> getMinSensorPowerRequired() {
        return this.minSensorPowerRequired;
    }

    @Override
    public Measure<Double> getMaxSensorPowerSupported() {
        return this.maxSensorPowerSupported;
    }
}
