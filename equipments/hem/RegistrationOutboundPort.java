package equipments.hem;

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.hem2025.bases.RegistrationCI;
import fr.sorbonne_u.components.interfaces.RequiredCI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;

/**
 * The class <code>equipments.hem.RegistrationOutboundPort</code>.
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
public class RegistrationOutboundPort
extends AbstractOutboundPort
implements RegistrationCI {
    public RegistrationOutboundPort(String uri, ComponentI owner) throws Exception {
        super(uri, RegistrationCI.class, owner);
    }

    public RegistrationOutboundPort(ComponentI owner) throws Exception {
        super(RegistrationCI.class, owner);
    }

    @Override
    public boolean registered(String uid) throws Exception {
        return ((RegistrationCI)this.getConnector()).registered(uid);
    }

    @Override
    public boolean register(String uid, String controlPortURI, String xmlControlAdapter) throws Exception {
        return ((RegistrationCI)this.getConnector()).register(uid, controlPortURI, xmlControlAdapter);
    }

    @Override
    public void unregister(String uid) throws Exception {
        ((RegistrationCI)this.getConnector()).unregister(uid);
    }
}
