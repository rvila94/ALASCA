package equipments.HeatPump.Test;

import equipments.HeatPump.HeatPump;
import equipments.HeatPump.compressor.Compressor;
import equipments.HeatPump.compressor.CompressorConnector;
import equipments.HeatPump.temperatureSensor.TemperatureSensor;
import equipments.HeatPump.temperatureSensor.TemperatureSensorConnector;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.cvm.AbstractCVM;
import fr.sorbonne_u.components.hem2025e1.equipments.heater.Heater;
import fr.sorbonne_u.components.hem2025e1.equipments.heater.HeaterUnitTester;

/**
 * The class <code>equipments.HeatPump.Test.CVMUnitTest</code>.
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
public class CVMUnitTest
extends AbstractCVM
{

    public static final String COMPRESSOR_INBOUND_URI = "COMPRESSOR-INBOUND-URI";

    public static final String SENSOR_INBOUND_URI = "TEMPERATURE-INBOUND-URI";

    public static final String HEATPUMP_USER_INBOUND_URI = "HEATPUMP-USER-INBOUND-URI";

    public static final String HEATPUMP_INTERNAL_INBOUND_URI = "HEATPUMP-INTERNAL-INBOUND-URI";

    public static final String HEATPUMP_EXTERNAL_INBOUND_URI = "HEATPUMP-EXTERNAL-INBOUND-URI";

    public				CVMUnitTest() throws Exception
    {}

    @Override
    public void			deploy() throws Exception
    {

        AbstractComponent.createComponent(
                Compressor.class.getCanonicalName(),
                new Object[]{COMPRESSOR_INBOUND_URI});

        AbstractComponent.createComponent(
                TemperatureSensor.class.getCanonicalName(),
                new Object[]{SENSOR_INBOUND_URI});

        AbstractComponent.createComponent(
                HeatPump.class.getCanonicalName(),
                new Object[]{
                        COMPRESSOR_INBOUND_URI,
                        SENSOR_INBOUND_URI,
                        CompressorConnector.class.getCanonicalName(),
                        TemperatureSensorConnector.class.getCanonicalName(),
                        HEATPUMP_USER_INBOUND_URI,
                        HEATPUMP_INTERNAL_INBOUND_URI,
                        HEATPUMP_EXTERNAL_INBOUND_URI
                });

        AbstractComponent.createComponent(
                HeatPumpTester.class.getCanonicalName(),
                new Object[]{
                        true,
                        HEATPUMP_USER_INBOUND_URI,
                        HEATPUMP_INTERNAL_INBOUND_URI,
                        HEATPUMP_EXTERNAL_INBOUND_URI
                });	// is unit test

        super.deploy();
    }

    public static void	main(String[] args)
    {
        try {
            CVMUnitTest cvm = new CVMUnitTest();
            cvm.startStandardLifeCycle(1000L);
            Thread.sleep(100000L);
            System.exit(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
