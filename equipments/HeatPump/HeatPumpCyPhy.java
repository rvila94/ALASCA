package equipments.HeatPump;

import fr.sorbonne_u.components.hem2025e2.equipments.heater.mil.events.Heat;

/**
 * The class <code>equipments.HeatPump.HeatPumpCyPhy</code>.
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
public class HeatPumpCyPhy extends HeatPump {
    protected HeatPumpCyPhy(
            String compressorURI,
            String bufferTankURI,
            String compressorCcName,
            String bufferCcName,
            String userInboundURI,
            String internalInboundURI,
            String externalInboundURI,
            String registrationHEMURI,
            String registrationHEMCcName) throws Exception {

        super(compressorURI,
                bufferTankURI,
                compressorCcName,
                bufferCcName,
                userInboundURI,
                internalInboundURI,
                externalInboundURI,
                registrationHEMURI,
                registrationHEMCcName);
    }

    protected HeatPumpCyPhy(
            String reflectionInboundPortURI,
            String compressorURI,
            String bufferTankURI,
            String compressorCcName,
            String bufferCcName,
            String userInboundURI,
            String internalInboundURI,
            String externalInboundURI,
            String registrationHEMURI,
            String registrationHEMCcName) throws Exception {
        super(reflectionInboundPortURI,
                compressorURI,
                bufferTankURI,
                compressorCcName,
                bufferCcName,
                userInboundURI,
                internalInboundURI,
                externalInboundURI,
                registrationHEMURI,
                registrationHEMCcName);
    }

    protected HeatPumpCyPhy(
            String compressorURI,
            String bufferTankURI,
            String compressorCcName,
            String bufferCcName,
            String userInboundURI,
            String internalInboundURI,
            String externalInboundURI) throws Exception {
        super(compressorURI,
                bufferTankURI,
                compressorCcName,
                bufferCcName,
                userInboundURI,
                internalInboundURI,
                externalInboundURI);
    }


}
