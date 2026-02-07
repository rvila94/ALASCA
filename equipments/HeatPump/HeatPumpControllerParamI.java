package equipments.HeatPump;

/**
 * The class <code>equipments.HeatPump.HeatPumpControllerParamI</code>.
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
public interface HeatPumpControllerParamI {

    double computeStopHeatingThreshold();
    double computeStartHeatingThreshold();

    double computeStartCoolingThreshold();
    double computeStopCoolingThreshold();

}
