package equipments.HeatPump.mil.events;

import equipments.HeatPump.HeatPump;
import equipments.dimmerlamp.DimmerLamp;
import fr.sorbonne_u.devs_simulation.models.events.EventInformationI;
import fr.sorbonne_u.exceptions.PreconditionException;

/**
 * The class <code>equipments.HeatPump.mil.events.HeatPumpPowerValue</code>.
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
public class HeatPumpPowerValue implements EventInformationI {

    public final double power;

    /**
     *
     * Description
     *
     * <p><strong>Contract</strong></p>
     *
     * <pre>
     * null
     * </pre>
     * @param power
     */
    public HeatPumpPowerValue(double power) {

        assert HeatPump.MIN_REQUIRED_POWER_LEVEL.getData() <= power
                && power <= HeatPump.MAX_POWER_LEVEL.getData() :
                new PreconditionException("HeatPump.MIN_REQUIRED_POWER_LEVEL.getData() > power" +
                        " || power > HeatPump.MAX_POWER_LEVEL.getData()");

        this.power = power;
    }

    /**
     *
     * Returns the power
     *
     * <p><strong>Contract</strong></p>
     *
     * <pre>
     *  pre {@code true} // no precondition
     *  post {@code true} // no postcondition
     * </pre>
     * @return double
     */
    public double getPower() {
        return this.power;
    }

    /**
     * @see java.lang.Object#toString
     */
    @Override
    public String toString() {
        return this.power + "W";
    }

}
