package equipments.HeatPump.Test;

import equipments.HeatPump.HeatPump;
import equipments.HeatPump.HeatPumpController;
import equipments.HeatPump.HeatPumpCyPhy;
import equipments.HeatPump.compressor.Compressor;
import equipments.HeatPump.compressor.CompressorConnector;
import equipments.HeatPump.connections.HeatPumpActuatorConnector;
import equipments.HeatPump.connections.HeatPumpControllerConnector;
import equipments.HeatPump.connections.HeatPumpExternalControlConnector;
import equipments.HeatPump.temperatureSensor.TemperatureSensor;
import equipments.HeatPump.temperatureSensor.TemperatureSensorConnector;
import equipments.hem.HEM;
import equipments.hem.RegistrationConnector;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.cvm.AbstractCVM;

/**
 * The class <code>Test.HeatPump.equipments.CVMUnitTest</code>.
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
public class CVMUnitSILTest
extends AbstractCVM
{

    public static double ACCELERATION_FACTOR = 360.0;

    public static final String COMPRESSOR_INBOUND_URI = "COMPRESSOR-INBOUND-URI";

    public static final String SENSOR_INBOUND_URI = "TEMPERATURE-INBOUND-URI";

    public static final String HEATPUMP_USER_INBOUND_URI = "HEATPUMP-USER-INBOUND-URI";

    public static final String HEATPUMP_INTERNAL_INBOUND_URI = "HEATPUMP-INTERNAL-INBOUND-URI";

    public static final String HEATPUMP_EXTERNAL_INBOUND_URI = "HEATPUMP-EXTERNAL-INBOUND-URI";

    public static final String HEATPUMP_ACTUATOR_INBOUND_URI = "HEATPUMP-ACTUATOR-INBOUND-URI";

    public static final String HEATPUMP_EXTERNAL_CONTROLLER_INBOUND_URI = "HEATPUMP_EXTERNAL_CONTROLLER_INBOUND_URI";

    public static final String HEATPUMP_CONTROLLER_INBOUND_URI = "HEATPUMP-CONTROLLER-INBOUND-URI";

    public CVMUnitSILTest() throws Exception
    {
        HeatPump.VERBOSE = true;
        HeatPump.X_RELATIVE_POSITION = 0;
        HeatPump.Y_RELATIVE_POSITION = 1;

        HeatPumpTester.VERBOSE = true;
        HeatPumpTester.X_RELATIVE_POSITION = 1;
        HeatPumpTester.Y_RELATIVE_POSITION = 0;

    }

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
                HeatPumpCyPhy.class.getCanonicalName(),
                new Object[]{
                        COMPRESSOR_INBOUND_URI,
                        SENSOR_INBOUND_URI,
                        CompressorConnector.class.getCanonicalName(),
                        TemperatureSensorConnector.class.getCanonicalName(),
                        HEATPUMP_USER_INBOUND_URI,
                        HEATPUMP_INTERNAL_INBOUND_URI,
                        HEATPUMP_EXTERNAL_INBOUND_URI,
                        HEATPUMP_EXTERNAL_CONTROLLER_INBOUND_URI,
                        HEATPUMP_ACTUATOR_INBOUND_URI,
                        HeatPumpController.CONTROLLER_INBOUND_URI,
                        HeatPumpControllerConnector.class.getCanonicalName()
                });

        AbstractComponent.createComponent(
                HeatPumpController.class.getCanonicalName(),
                new Object[]{
                        HEATPUMP_EXTERNAL_CONTROLLER_INBOUND_URI,
                        HEATPUMP_ACTUATOR_INBOUND_URI,
                        HeatPumpExternalControlConnector.class.getCanonicalName(),
                        HeatPumpActuatorConnector.class.getCanonicalName(),
                        HeatPumpController.CONTROLLER_INBOUND_URI,
                        0.5,
                        18.0,
                        22.0,
                        ACCELERATION_FACTOR
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
            CVMUnitSILTest cvm = new CVMUnitSILTest();
            cvm.startStandardLifeCycle(20000L);
            Thread.sleep(100000L);
            System.exit(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
