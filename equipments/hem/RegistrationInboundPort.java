package equipments.hem;

import equipments.HeatPump.connections.HeatPumpExternalControlInboundPort;
import equipments.HeatPump.interfaces.HeatPumpExternalControlCI;
import equipments.HeatPump.interfaces.HeatPumpExternalControlI;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.hem2025.bases.RegistrationCI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;
import fr.sorbonne_u.exceptions.PreconditionException;

/**
 * The class <code>equipments.hem.RegistrationInboundPort</code>.
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
public class RegistrationInboundPort
extends AbstractInboundPort
implements RegistrationCI {

    public RegistrationInboundPort(String uri, ComponentI owner) throws Exception {
        super(uri, RegistrationCI.class, owner);

    }

    public RegistrationInboundPort(ComponentI owner) throws Exception {
        super(RegistrationCI.class, owner);

    }

    @Override
    public boolean registered(String uid) throws Exception {
        return this.getOwner().handleRequest(
                owner -> ((HEM)owner).registered(uid)
        );
    }

    @Override
    public boolean register(String uid, String controlPortURI, String xmlControlAdapter) throws Exception {
        return this.getOwner().handleRequest(
                owner -> ((HEM)owner).register(uid, controlPortURI, xmlControlAdapter)
        );
    }

    @Override
    public void unregister(String uid) throws Exception {
        this.getOwner().handleRequest(
          owner -> {
              ((HEM)owner).unregister(uid);
              return null;
          }
        );
    }
}
