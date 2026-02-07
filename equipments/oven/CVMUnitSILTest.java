package equipments.oven;

import equipments.oven.connections.OvenActuatorConnector;
import equipments.oven.connections.OvenControllerConnector;
import equipments.oven.connections.OvenExternalControlConnector;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.cvm.AbstractCVM;

/**
 * The class <code>CVMUnitSILTest</code> performs unit tests on the oven component.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p>
 * This class instantiates an {@link OvenCyPhy} component and an
 * {@link OvenUnitTesterCyPhy} component, then runs the unit tests implemented in
 * <code>OvenUnitTester</code> to validate the correct behaviour of the oven.
 * </p>
 *
 * <p><strong>Implementation Invariants</strong></p>
 * 
 * <pre>
 * invariant	{@code true}	// no more invariant
 * </pre>
 * 
 * <p><strong>Invariants</strong></p>
 * 
 * <pre>
 * invariant	{@code true}	// no more invariant
 * </pre>
 * 
 * <p>Created on : 2025-10-08</p>
 * 
 * @author	
 * 	<a href="mailto:Rodrigo.Vila@etu.sorbonne-universite.fr">Rodrigo Vila</a>,
 * 	<a href="mailto:Damien.Ribeiro@etu.sorbonne-universite.fr">Damien Ribeiro</a>
 */
public class CVMUnitSILTest
extends AbstractCVM
{
	// ---------------------------------------------------------------------
    // Constants
    // ---------------------------------------------------------------------

    public static final double ACCELERATION_FACTOR = 360.0;

    public static final String OVEN_USER_INBOUND_URI =
            "OVEN-USER-INBOUND-URI";

    public static final String OVEN_INTERNAL_INBOUND_URI =
            "OVEN-INTERNAL-INBOUND-URI";

    public static final String OVEN_EXTERNAL_INBOUND_URI =
            "OVEN-EXTERNAL-INBOUND-URI";
    
    public static final String OVEN_ACTUATOR_INBOUND_URI = 
    		"OVEN-ACTUATOR-INBOUND-URI";
    
    public static final String OVEN_EXTERNAL_CONTROLLER_INBOUND_URI = 
    		"OVEN_EXTERNAL_CONTROLLER_INBOUND_URI";

    // ---------------------------------------------------------------------
    // Constructor
    // ---------------------------------------------------------------------

    public CVMUnitSILTest() throws Exception
    {
        OvenCyPhy.VERBOSE = true;
        OvenCyPhy.X_RELATIVE_POSITION = 0;
        OvenCyPhy.Y_RELATIVE_POSITION = 1;

        OvenUnitTesterCyPhy.VERBOSE = true;
        OvenUnitTesterCyPhy.X_RELATIVE_POSITION = 1;
        OvenUnitTesterCyPhy.Y_RELATIVE_POSITION = 0;
    }

    // ---------------------------------------------------------------------
    // CVM life cycle
    // ---------------------------------------------------------------------

    @Override
    public void deploy() throws Exception
    {

        // Oven CyPhy component
        AbstractComponent.createComponent(
                OvenCyPhy.class.getCanonicalName(),
                new Object[]{
                        OVEN_USER_INBOUND_URI,
                        OVEN_INTERNAL_INBOUND_URI,
                        OVEN_EXTERNAL_INBOUND_URI,
                        OVEN_ACTUATOR_INBOUND_URI,
                        OVEN_EXTERNAL_CONTROLLER_INBOUND_URI,
                        OvenController.CONTROLLER_INBOUND_URI,
                        OvenControllerConnector.class.getCanonicalName()             
                });
        
        AbstractComponent.createComponent(
        	    OvenController.class.getCanonicalName(),
        	    new Object[]{
        	        OVEN_EXTERNAL_CONTROLLER_INBOUND_URI,
        	        OVEN_ACTUATOR_INBOUND_URI,
        	        OvenExternalControlConnector.class.getCanonicalName(),
        	        OvenActuatorConnector.class.getCanonicalName(),
        	        OvenController.CONTROLLER_INBOUND_URI,
        	        22.0, 
        	        ACCELERATION_FACTOR
        	    });

        // Oven tester
        AbstractComponent.createComponent(
        		OvenUnitTesterCyPhy.class.getCanonicalName(),
                new Object[]{
                        OVEN_USER_INBOUND_URI,
                        OVEN_INTERNAL_INBOUND_URI,
                        OVEN_EXTERNAL_INBOUND_URI
                });

        super.deploy();
    }

    // ---------------------------------------------------------------------
    // Main
    // ---------------------------------------------------------------------

    public static void main(String[] args)
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

