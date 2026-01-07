package equipments.dimmerlamp.simulations;

import equipments.dimmerlamp.LampState;
import fr.sorbonne_u.devs_simulation.models.time.Time;

/**
 * The class <code>equipments.dimmerlamp.simulations.DimmerLampSimulationOperationI</code>.
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
public interface DimmerLampSimulationOperationI {

    /**
     *
     * sets the mode of the dimmer lamp in the simulator
     *
     * <p><strong>Contract</strong></p>
     *
     * <pre>
     *  pre {@code state != null}
     *  post {@code this.currentState == state}
     * </pre>
     *
     * @param state the new state
     */
    void setState(LampState state);

    /**
     *
     * gets the current state of the dimmer lamp in the simulator
     *
     * <p><strong>Contract</strong></p>
     *
     * <pre>
     *  pre {@code true}        // no precondition
     *  post {@code true}       // no postcondition
     * </pre>
     * @return LampState    the current state of the dimmer lamp
     */
    LampState getState();

    /**
     *
     * Sets the power of the lamp
     *
     * <p><strong>Contract</strong></p>
     *
     * <pre>
     * pre {@code DimmerLamp.MIN_POWER_VARIATION.getData() <= newPower && newPower <= DimmerLamp.MAX_POWER_VARIATION.getData()}
     * pre {@code time != null}
     * post {@code true} // no postcondition
     * </pre>
     * @param newPower
     * @param time
     */
    void setDimmerLampPower(double newPower, Time time);

}
