package equipments.dimmerlamp.interfaces;

import equipments.dimmerlamp.interfaces.DimmerLampExternalCI;

/**
 * The class <code>equipments.dimmerlamp.interfaces.DimmerLampExternalJava4CI</code>.
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
public interface DimmerLampExternalJava4CI
extends DimmerLampExternalCI
{

    void setVariationPowerJava4(int variationPower) throws Exception;

    int getCurrentPowerLevelJava4() throws Exception;

}
