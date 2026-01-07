package equipments.dimmerlamp;

/**
 * The class <code>equipments.dimmerlamp.LampState</code>.
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
public enum LampState {
    OFF,
    ON;

    public boolean isOn() {
        return this == ON;
    }

    public boolean isOff() {
        return this == OFF;
    }

}
