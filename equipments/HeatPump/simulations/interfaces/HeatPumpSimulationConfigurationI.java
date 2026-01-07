package equipments.HeatPump.simulations.interfaces;

import java.util.concurrent.TimeUnit;

/**
 * The class <code>equipments.HeatPump.mil.HeatPumpSiulationConfigurationI</code>.
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
public interface HeatPumpSimulationConfigurationI {

    // -------------------------------------------------------------------------
    // Constants
    // -------------------------------------------------------------------------

    /** time unit used in the heat pump simulation.								*/
    TimeUnit TIME_UNIT = TimeUnit.HOURS;

}
