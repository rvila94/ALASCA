package equipments.HeatPump.simulations.reports;

/**
 * The class <code>equipments.HeatPump.simulations.reports.mil.HeatPumpTemperatureReport</code>.
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
public class HeatPumpTemperatureReport extends AbstractHeatPumpReport<Double> {

    private static final String PARAM_REPRESENTATION = "mean temperature = ";

    public HeatPumpTemperatureReport(String uri, Double parameter) {
        super(uri, PARAM_REPRESENTATION, parameter);
    }
}
