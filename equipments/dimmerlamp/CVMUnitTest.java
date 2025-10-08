package equipments.dimmerlamp;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.cvm.AbstractCVM;

/**
 * The class <code>equipments.dimmerlamp.CVMUnitTest</code>.
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
extends AbstractCVM {

    public				CVMUnitTest() throws Exception
    {
        DimmerLampTester.VERBOSE = true;
        DimmerLamp.VERBOSE = true;
    }


    @Override
    public void			deploy() throws Exception
    {
        AbstractComponent.createComponent(
                DimmerLamp.class.getCanonicalName(),
                new Object[]{});

        AbstractComponent.createComponent(
                DimmerLamp.class.getCanonicalName(),
                new Object[]{true});	// is unit test

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
