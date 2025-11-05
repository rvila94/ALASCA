package equipments.HeatPump.interfaces;

import equipments.HeatPump.powerRepartitionPolicy.PowerRepartitionPolicyI;

/**
 * The class <code>interfaces.HeatPump.equipments.HeatPumpExternalJava4CI</code>.
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
public interface HeatPumpExternalJava4CI
extends HeatPumpExternalControlCI
{

    double getMaxPowerJava4() throws Exception;
    double getMinimumRequiredPowerJava4() throws Exception;
    double getCurrentPowerJava4() throws Exception;
    void setCurrentPowerLevelJava4(double powerLevel) throws Exception;

    void setCurrentPowerLevelJava4(double powerLevel, PowerRepartitionPolicyI policy) throws Exception;

    double getCurrentTemperatureJava4() throws Exception;

    double getTargetTemperatureJava4() throws Exception;
}
