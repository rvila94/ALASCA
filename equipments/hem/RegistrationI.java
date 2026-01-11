package equipments.hem;

/**
 * The class <code>equipments.hem.RegistrationI</code>.
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
public interface RegistrationI {

    boolean		registered(String uid) throws Exception;

    boolean		register(
            String uid,
            String controlPortURI,
            String xmlControlAdapter
    ) throws Exception;

    void			unregister(String uid) throws Exception;

}
