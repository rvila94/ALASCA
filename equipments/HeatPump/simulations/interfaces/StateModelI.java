package equipments.HeatPump.simulations.interfaces;

import equipments.HeatPump.interfaces.HeatPumpUserI;

/**
 * The class <code>equipments.HeatPump.simulations.interfaces.StateModelI</code>.
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
public interface StateModelI {

    void setCurrentState(HeatPumpUserI.State state);
    HeatPumpUserI.State getCurrentState();

}
