package equipments.HeatPump.powerRepartitionPolicy;

import fr.sorbonne_u.alasca.physical_data.Measure;

/**
 * The class <code>powerRepartitionPolicy.HeatPump.equipments.PowerRepartition</code>.
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
public class PowerRepartition
implements PowerRepartitionI {

    public final Measure<Double> heatPumpPower;
    public final Measure<Double> compressorPower;

    public final Measure<Double> sensorPower;

    public PowerRepartition(
            Measure<Double> heatPumpPower,
            Measure<Double> compressorPower,
            Measure<Double> sensorPower) {
        this.heatPumpPower = heatPumpPower;
        this.compressorPower = compressorPower;
        this.sensorPower = sensorPower;
    }

    public Measure<Double> getHeatPumpPower() {
        return this.heatPumpPower;
    }

    @Override
    public Measure<Double> getSensorPower() {
        return null;
    }

    @Override
    public Measure<Double> getCompressorPower() {
        return null;
    }


}
