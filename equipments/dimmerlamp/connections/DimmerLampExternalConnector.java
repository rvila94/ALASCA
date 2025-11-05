package equipments.dimmerlamp.connections;

import equipments.dimmerlamp.interfaces.DimmerLampExternalCI;
import fr.sorbonne_u.alasca.physical_data.Measure;
import fr.sorbonne_u.components.connectors.AbstractConnector;

/**
 * The class <code>connections.dimmerlamp.equipments.DimmerLampExternalConnector</code>.
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
public class DimmerLampExternalConnector
extends AbstractConnector
implements DimmerLampExternalCI {
    @Override
    public void setVariationPower(Measure<Double> variationPower) throws Exception {
        ((DimmerLampExternalCI)this.offering).setVariationPower(variationPower);
    }

    @Override
    public Measure<Double> getCurrentPowerLevel() throws Exception {
        return ((DimmerLampExternalCI)this.offering).getCurrentPowerLevel();
    }

    @Override
    public Measure<Double> getMaxPowerLevel() throws Exception {
        return ((DimmerLampExternalCI)this.offering).getMaxPowerLevel();
    }
}
