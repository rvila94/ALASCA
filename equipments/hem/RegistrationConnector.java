package equipments.hem;

import fr.sorbonne_u.components.connectors.AbstractConnector;
import fr.sorbonne_u.components.hem2025.bases.RegistrationCI;

/**
 * The class <code>equipments.hem.RegistrationConnector</code>.
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
public class RegistrationConnector
extends AbstractConnector
implements RegistrationCI {
    @Override
    public boolean registered(String uid) throws Exception {
        return ((RegistrationCI)this.offering).registered(uid);
    }

    @Override
    public boolean register(String uid, String controlPortURI, String xmlControlAdapter) throws Exception {
        return ((RegistrationCI)this.offering).register(uid, controlPortURI, xmlControlAdapter);
    }

    @Override
    public void unregister(String uid) throws Exception {
        ((RegistrationCI)this.offering).unregister(uid);
    }
}
