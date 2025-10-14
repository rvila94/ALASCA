package equipments.dimmerlamp.connections;

import equipments.dimmerlamp.interfaces.DimmerLampExternalJava4CI;
import fr.sorbonne_u.alasca.physical_data.Measure;
import fr.sorbonne_u.components.ComponentI;

/**
 * The class <code>equipments.dimmerlamp.connections.DimmerLampExternalJava4InboundPort</code>.
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
public class DimmerLampExternalJava4InboundPort
extends DimmerLampExtenalInboundPort
        implements DimmerLampExternalJava4CI {
    public DimmerLampExternalJava4InboundPort(String uri, ComponentI owner) throws Exception {
        super(uri, DimmerLampExternalJava4CI.class, owner);
    }

    public DimmerLampExternalJava4InboundPort(ComponentI owner) throws Exception {
        super(owner);
    }
    @Override
    public void setVariationPowerJava4(int variationPower) throws Exception {
        this.setVariationPower(new Measure<>(variationPower));
    }

    @Override
    public int getCurrentPowerLevelJava4() throws Exception {
        return this.getCurrentPowerLevel().getData();
    }
}
