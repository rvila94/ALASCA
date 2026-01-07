package equipments.HeatPump.temperatureSensor.Data;

import fr.sorbonne_u.alasca.physical_data.MeasureI;
import fr.sorbonne_u.alasca.physical_data.SignalData;
import fr.sorbonne_u.components.interfaces.DataOfferedCI;
import fr.sorbonne_u.components.interfaces.DataRequiredCI;
import fr.sorbonne_u.utils.aclocks.AcceleratedClock;

import java.time.Instant;

/**
 * The class <code>equipments.HeatPump.temperatureSensor.Data.TemperatureSensorData</code>.
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
public class TemperatureSensorData
        extends SignalData<Double>
        implements DataOfferedCI.DataI, DataRequiredCI.DataI {

    public TemperatureSensorData(MeasureI<Double> measure) {
        super(measure);
    }

    public TemperatureSensorData(MeasureI<Double> measure, Instant timestamp) {
        super(measure, timestamp);
    }

    public TemperatureSensorData(AcceleratedClock ac, MeasureI<Double> measure) {
        super(ac, measure);
    }

    public TemperatureSensorData(AcceleratedClock ac, MeasureI<Double> measure, Instant timestamp) {
        super(ac, measure, timestamp);
    }

}
