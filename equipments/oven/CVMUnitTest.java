package equipments.oven;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.cvm.AbstractCVM;
import fr.sorbonne_u.components.exceptions.BCMException;

/**
 * The class <code>CVMUnitTest</code> performs unit tests on the oven component.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p>
 * This class instantiates an {@link Oven} component and an
 * {@link OvenTester} component, then runs the unit tests implemented in
 * <code>OvenTester</code> to validate the correct behaviour of the oven.
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
public class CVMUnitTest
extends AbstractCVM
{
	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	public CVMUnitTest() throws Exception
	{
		OvenTester.VERBOSE = true;
		OvenTester.X_RELATIVE_POSITION = 0;
		OvenTester.Y_RELATIVE_POSITION = 0;

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
	public void deploy() throws Exception
	{
		AbstractComponent.createComponent(
			Oven.class.getCanonicalName(),
			new Object[] {}
		);

		AbstractComponent.createComponent(
			OvenTester.class.getCanonicalName(),
			new Object[] { true }
		);

		super.deploy();
	}

	public static void main(String[] args)
	{
		BCMException.VERBOSE = true;
		try {
			CVMUnitTest cvm = new CVMUnitTest();
			cvm.startStandardLifeCycle(10000L);
			Thread.sleep(10000L);
			System.exit(0);
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
}
