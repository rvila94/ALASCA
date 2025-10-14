package equipments.dimmerlamp.connections;

import equipments.dimmerlamp.interfaces.DimmerLampExternalCI;
import fr.sorbonne_u.alasca.physical_data.Measure;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;

/**
 * The class <code>equipments.dimmerlamp.connections.DimmerLampExternalOutboundPort</code>.
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
public class DimmerLampExternalOutboundPort
extends AbstractOutboundPort
implements DimmerLampExternalCI {
    public DimmerLampExternalOutboundPort(String uri, ComponentI owner) throws Exception {
        super(uri, DimmerLampExternalCI.class, owner);
    }

    public DimmerLampExternalOutboundPort(ComponentI owner) throws Exception {
        super(DimmerLampExternalCI.class, owner);
    }

    @Override
    public void setVariationPower(Measure<Integer> variationPower) throws Exception {
        ((DimmerLampExternalCI)this.getConnector()).setVariationPower(variationPower);
    }

    @Override
    public Measure<Integer> getCurrentPowerLevel() throws Exception {
        return ((DimmerLampExternalCI)this.getConnector()).getCurrentPowerLevel();
    }
}
