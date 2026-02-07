package equipments.HeatPump;

/**
 * The class <code>equipments.HeatPump.ControllerParam</code>.
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
public class ControllerParam implements HeatPumpControllerParamI{

    private final double heatingThreshold, coolingThreshold;
    private final double hysteresis;

    public ControllerParam(double heatingThreshold, double coolingThreshold, double hysteresis) {
        this.heatingThreshold = heatingThreshold;
        this.coolingThreshold = coolingThreshold;
        this.hysteresis = hysteresis;
    }

    /**
     * @see equipments.HeatPump.HeatPumpControllerParamI#computeStopHeatingThreshold
     */
    @Override
    public double computeStopHeatingThreshold() {
        return this.heatingThreshold + this.hysteresis;
    }

    /**
     * @see equipments.HeatPump.HeatPumpControllerParamI#computeStopHeatingThreshold
     */
    @Override
    public double computeStartHeatingThreshold() {
        return this.heatingThreshold - this.hysteresis;
    }

    /**
     * @see equipments.HeatPump.HeatPumpControllerParamI#computeStartCoolingThreshold
     */
    @Override
    public double computeStartCoolingThreshold() {
        return this.coolingThreshold + this.hysteresis;
    }

    /**
     * @see equipments.HeatPump.HeatPumpControllerParamI#computeStopCoolingThreshold
     */
    @Override
    public double computeStopCoolingThreshold() {
        return this.coolingThreshold - this.hysteresis;
    }
}
