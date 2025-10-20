package equipments.oven;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.cvm.AbstractCVM;

/**
 * The class <code>CVMUnitTest</code> performs unit tests on the oven component.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p>
 * This class instantiates an {@link Oven} component and an
 * {@link OvenUnitTester} component, then runs the unit tests implemented in
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
public class			CVMUnitTest
extends		AbstractCVM
{
	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	public				CVMUnitTest() throws Exception
	{
		OvenUnitTester.VERBOSE = true;
		OvenUnitTester.X_RELATIVE_POSITION = 0;
		OvenUnitTester.Y_RELATIVE_POSITION = 0;
		Oven.VERBOSE = true;
		Oven.X_RELATIVE_POSITION = 1;
		Oven.Y_RELATIVE_POSITION = 0;
	}

	// -------------------------------------------------------------------------
	// CVM life-cycle
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.components.cvm.AbstractCVM#deploy()
	 */
	@Override
	public void			deploy() throws Exception
	{
		AbstractComponent.createComponent(
				Oven.class.getCanonicalName(),
				new Object[]{});

		AbstractComponent.createComponent(
				OvenUnitTester.class.getCanonicalName(),
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
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
}