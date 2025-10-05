package equipments.fan;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.cvm.AbstractCVM;
import fr.sorbonne_u.components.exceptions.BCMException;


/**
 * The class <code>CVMUnitTest</code> performs unit tests on the fan component.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p>This class instantiates a {@link Fan} component and a
 * {@link FanTester} component, then runs the unit tests implemented in
 * <code>FanTester</code> to validate the correct behaviour of the fan.</p>
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
 * <p>Created on : 2025-10-04</p>
 * 
 * @author	<a href="mailto:Rodrigo.Vila@etu.sorbonne-universite.fr">Rodrigo Vila</a>
 * @author	<a href="mailto:Damien.Ribeiro@etu.sorbonne-universite.fr">Damien Ribeiro</a>
 */
public class			CVMUnitTest
extends		AbstractCVM
{
	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	public				CVMUnitTest() throws Exception
	{
		FanTester.VERBOSE = true;
		FanTester.X_RELATIVE_POSITION = 0;
		FanTester.Y_RELATIVE_POSITION = 0;

		Fan.VERBOSE = true;
		Fan.X_RELATIVE_POSITION = 1;
		Fan.Y_RELATIVE_POSITION = 0;
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
					Fan.class.getCanonicalName(),
					new Object[]{});

		AbstractComponent.createComponent(
					FanTester.class.getCanonicalName(),
					new Object[]{true});

		super.deploy();
	}

	public static void		main(String[] args)
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
