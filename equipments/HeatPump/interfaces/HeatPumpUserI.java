package equipments.HeatPump.interfaces;

import fr.sorbonne_u.alasca.physical_data.Measure;

/**
 * The class <code>equipments.HeatPump.HeatPumpI</code>.
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
public interface HeatPumpUserI {

    enum State {
        Off,
        On,
        Heating,
        Cooling
    }

    boolean on() throws Exception;
    void switchOff() throws Exception;

    void switchOn() throws Exception;

    State getState() throws Exception;

    void setTargetTemperature(Measure<Double> temperature) throws Exception;

}
