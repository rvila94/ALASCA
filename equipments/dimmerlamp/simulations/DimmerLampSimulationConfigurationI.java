package equipments.dimmerlamp.simulations;

import java.util.concurrent.TimeUnit;

/**
 * The class <code>equipments.dimmerlamp.mil.events.HeaterSimulationConfigurationI</code>.
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
public interface DimmerLampSimulationConfigurationI {
    // -------------------------------------------------------------------------
    // Constants
    // -------------------------------------------------------------------------

    /** time unit used in the dimmer lamp simulation.								*/
    TimeUnit TIME_UNIT = TimeUnit.HOURS;

    // -------------------------------------------------------------------------
    // Invariants
    // -------------------------------------------------------------------------

    /**
     * return true if the static invariants are observed, false otherwise.
     *
     * <p><strong>Contract</strong></p>
     *
     * <pre>
     * pre	{@code instance != null}
     * post	{@code true}	// no postcondition.
     * </pre>
     *
     * @return	true if the invariants are observed, false otherwise.
     */
    public static boolean	staticInvariants() {
        // TODO
        return true;
    }

}
