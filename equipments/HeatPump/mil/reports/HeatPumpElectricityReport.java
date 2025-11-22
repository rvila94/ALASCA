package equipments.HeatPump.mil.reports;

import fr.sorbonne_u.components.hem2025e2.GlobalReportI;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulationReportI;
import fr.sorbonne_u.exceptions.PreconditionException;

/**
 * The class <code>equipments.HeatPump.mil.reports.HeatPumpElectricityReport</code>.
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
public class HeatPumpElectricityReport extends AbstractHeatPumpReport<Double> {

    private static final String PARAM_REPRESENTATION = "total consumption in kwh = ";

    public HeatPumpElectricityReport(String uri, Double parameter) {
        super(uri, PARAM_REPRESENTATION, parameter);
    }
}
