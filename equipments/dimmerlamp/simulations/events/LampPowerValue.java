package equipments.dimmerlamp.simulations.events;

import equipments.dimmerlamp.DimmerLamp;
import fr.sorbonne_u.devs_simulation.models.events.EventInformationI;
import fr.sorbonne_u.exceptions.PreconditionException;

/**
 * The class <code>equipments.dimmerlamp.simulations.events.mil.LampPowerValue</code>.
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
public class LampPowerValue implements EventInformationI {

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
    public LampPowerValue(double power) {

        assert DimmerLamp.MIN_POWER_VARIATION.getData() <= power
                && power <= DimmerLamp.MAX_POWER_VARIATION.getData() :
                new PreconditionException("DimmerLamp.MIN_POWER_VARIATION.getData() > power" +
                        " || power > DimmerLamp.MAX_POWER_VARIATION.getData()");

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
