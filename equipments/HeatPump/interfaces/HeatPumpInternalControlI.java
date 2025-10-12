package equipments.HeatPump.interfaces;

/**
 * The class <code>equipments.HeatPump.interfaces.HeatPumpInternalControlI</code>.
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
public interface HeatPumpInternalControlI {

    boolean heating() throws Exception;
    boolean cooling() throws Exception;
    void startHeating() throws Exception;

    void stopHeating() throws Exception;

    void startCooling() throws Exception;

    void stopCooling() throws Exception;

}
