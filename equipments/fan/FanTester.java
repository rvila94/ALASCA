package equipments.fan;

import equipments.fan.connections.FanConnector;
import equipments.fan.connections.FanOutboundPort;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.RequiredInterfaces;
import fr.sorbonne_u.components.exceptions.BCMException;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import fr.sorbonne_u.components.utils.tests.TestsStatistics;
import fr.sorbonne_u.components.hem2025e1.CVMIntegrationTest;
import fr.sorbonne_u.exceptions.ImplementationInvariantException;
import fr.sorbonne_u.exceptions.AssertionChecking;
import fr.sorbonne_u.exceptions.InvariantException;
import fr.sorbonne_u.exceptions.PreconditionException;
import fr.sorbonne_u.utils.aclocks.AcceleratedClock;
import fr.sorbonne_u.utils.aclocks.ClocksServer;
import fr.sorbonne_u.utils.aclocks.ClocksServerCI;
import fr.sorbonne_u.utils.aclocks.ClocksServerConnector;
import fr.sorbonne_u.utils.aclocks.ClocksServerOutboundPort;

/**
 * The class <code>FanTester</code> implements a component performing
 * tests for the class <code>Fan</code> as a BCM4Java component.
 *
 * <p><strong>Description</strong></p>
 * 
 * * This tester checks the basic operations of the Fan component:
 * <ul>
 *   <li>Getting the state (ON/OFF)</li>
 *   <li>Getting the mode (LOW/MEDIUM/HIGH)</li>
 *   <li>Turning ON/OFF with correct preconditions</li>
 *   <li>Switching between speeds correctly</li>
 * </ul>
 * 
 * <p><strong>Implementation Invariants</strong></p>
 * 
 * <pre>
 * invariant	{@code fanInboundPortURI != null && !fanInboundPortURI.isEmpty()}
 * </pre>
 * 
 * <p><strong>Invariants</strong></p>
 * 
 * <pre>
 * invariant	{@code X_RELATIVE_POSITION >= 0}
 * invariant	{@code Y_RELATIVE_POSITION >= 0}
 * </pre>
 * 
 * <p>Created on : 2025-10-04</p>
 * 
 * @author	<a href="mailto:Rodrigo.Vila@etu.sorbonne-universite.fr">Rodrigo Vila</a>
 * @author	<a href="mailto:Damien.Ribeiro@etu.sorbonne-universite.fr">Damien Ribeiro</a>
 */
@RequiredInterfaces(required = {FanUserCI.class, ClocksServerCI.class})
public class FanTester extends AbstractComponent
{
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	public static boolean				VERBOSE = false;
	public static int					X_RELATIVE_POSITION = 0;
	public static int					Y_RELATIVE_POSITION = 0;

	protected final boolean				isUnitTest;
	protected FanOutboundPort fop;
	protected String					fanInboundPortURI;
	protected TestsStatistics			statistics;

	// -------------------------------------------------------------------------
	// Invariants
	// -------------------------------------------------------------------------
	
	/**
	 * return true if the implementation invariants are observed, false otherwise.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code ft != null}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param ft	instance to be tested.
	 * @return		true if the implementation invariants are observed, false otherwise.
	 */
	protected static boolean implementationInvariants(FanTester ft) {
		assert	ft != null : new PreconditionException("ft != null");

		boolean ret = true;
		ret &= AssertionChecking.checkImplementationInvariant(
				ft.fanInboundPortURI != null &&
						!ft.fanInboundPortURI.isEmpty(),
				FanTester.class, ft,
				"ft.fanInboundPortURI != null && !ft.fanInboundPortURI.isEmpty()");
		return ret;
	}
	
	/**
	 * return true if the invariants are observed, false otherwise.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code ft != null}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param ft	instance to be tested.
	 * @return		true if the invariants are observed, false otherwise.
	 */
	protected static boolean invariants(FanTester ft) {
		assert	ft != null : new PreconditionException("ft != null");

		boolean ret = true;
		ret &= AssertionChecking.checkInvariant(
				X_RELATIVE_POSITION >= 0,
				FanTester.class, ft,
				"X_RELATIVE_POSITION >= 0");
		ret &= AssertionChecking.checkInvariant(
				Y_RELATIVE_POSITION >= 0,
				FanTester.class, ft,
				"Y_RELATIVE_POSITION >= 0");
		return ret;
	}

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------
	
	/**
	 * create a fan tester component.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param isUnitTest	when true, the component performs a unit test.
	 * @throws Exception	<i>to do</i>.
	 */
	protected FanTester(boolean isUnitTest) throws Exception {
		this(isUnitTest, Fan.INBOUND_PORT_URI);
	}
	
	/**
	 * create a fan tester component.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code fanInboundPortURI != null && !fanInboundPortURI.isEmpty()}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param isUnitTest				when true, the component performs a unit test.
	 * @param fanInboundPortURI			URI of the fan inbound port to connect to.
	 * @throws Exception				<i>to do</i>.
	 */
	protected FanTester(boolean isUnitTest, String fanInboundPortURI) throws Exception {
		super(1, 0);
		assert fanInboundPortURI != null && !fanInboundPortURI.isEmpty() :
				new PreconditionException("fanInboundPortURI != null && "
										+ "!fanInboundPortURI.isEmpty()");
		this.isUnitTest = isUnitTest;
		this.initialise(fanInboundPortURI);
	}
	
	/**
	 * create a fan tester component.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code fanInboundPortURI != null && !fanInboundPortURI.isEmpty()}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param isUnitTest				when true, the component performs a unit test.
	 * @param fanInboundPortURI			URI of the fan inbound port to connect to.
	 * @param reflectionInboundPortURI	URI of the inbound port offering the <code>ReflectionI</code> interface.
	 * @throws Exception				<i>to do</i>.
	 */
	protected FanTester(
			boolean isUnitTest, 
			String fanInboundPortURI, 
			String reflectionInboundPortURI
			) throws Exception 
	{
		super(reflectionInboundPortURI, 1, 0);
		this.isUnitTest = isUnitTest;
		this.initialise(fanInboundPortURI);
	}
	
	/**
	 * initialise a fan tester component.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code fanInboundPortURI != null && !fanInboundPortURI.isEmpty()}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param fanInboundPortURI			URI of the fan inbound port to connect to.
	 * @throws Exception				<i>to do</i>.
	 */
	protected void initialise(String fanInboundPortURI) throws Exception {
		this.fanInboundPortURI = fanInboundPortURI;
		this.fop = new FanOutboundPort(this);
		this.fop.publishPort();

		if (VERBOSE) {
			this.tracer.get().setTitle("Fan tester component");
			this.tracer.get().setRelativePosition(X_RELATIVE_POSITION, Y_RELATIVE_POSITION);
			this.toggleTracing();
		}

		this.statistics = new TestsStatistics();

		assert implementationInvariants(this) :
				new ImplementationInvariantException("FanTester.implementationInvariants(this)");
		assert invariants(this) :
				new InvariantException("FanTester.invariants(this)");
	}

	// -------------------------------------------------------------------------
	// Component internal methods
	// -------------------------------------------------------------------------
	
	/**
	 * test of the {@code getCurrentState} method when the fan is off.
	 * 
	 * <p><strong>Description</strong></p>
	 * 
	 * <p>Gherkin specification:</p>
	 * <pre>
	 * Feature: Getting the state of the fan
	 * 
	 *   Scenario: getting the state when off
	 *     Given the fan is initialised and never been used yet
	 *     When the fan has not been used yet
	 *     Then the fan is off
	 * </pre>
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 */
	public void testGetState() {
		this.logMessage("Feature: Getting the state of the fan");
		this.logMessage("  Scenario: getting the state when off");
		this.logMessage("    Given the fan is initialised");
		FanImplementationI.FanState result = null;
		try {
			this.logMessage("    When I test the state of the fan");
			result = this.fop.getState();
			this.logMessage("    Then the state of the fan is off");
			if (!FanImplementationI.FanState.OFF.equals(result)) {
				this.statistics.incorrectResult();
				this.logMessage("     but was: " + result);
			}
		} catch (Throwable e) {
			this.statistics.incorrectResult();
			this.logMessage("     but the exception " + e + " has been raised");
		}
		this.statistics.updateStatistics();
	}
	
	/**
	 * test of the {@code getMode} method when the fan is off.
	 * 
	 * <p><strong>Description</strong></p>
	 * 
	 * <p>Gherkin specification:</p>
	 * <pre>
	 * Feature: Getting the mode of the fan
	 * 
	 *   Scenario: getting the mode when off
	 *     Given the fan is initialised and never been used yet
	 *     When the method getMode is called
	 *     Then the result is that the fan is in low mode
	 * </pre>
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 */
	public void testGetMode() {
		this.logMessage("Feature: Getting the mode of the fan");
		this.logMessage("  Scenario: getting the mode when off");
		this.logMessage("    Given the fan is initialised");
		FanImplementationI.FanMode result = null;
		try {
			this.logMessage("    When the fan has not been used yet");
			result = this.fop.getMode();
			this.logMessage("    Then the fan is low");
			if (!FanImplementationI.FanMode.LOW.equals(result)) {
				this.statistics.incorrectResult();
				this.logMessage("     but was: " + result);
			}
		} catch (Throwable e) {
			this.statistics.incorrectResult();
			this.logMessage("     but the exception " + e + " has been raised");
		}
		this.statistics.updateStatistics();
	}
	
	/**
	 * test turning on and off the fan.
	 * 
	 * <p><strong>Description</strong></p>
	 * 
	 * <p>Gherkin specification:</p>
	 * <pre>
	 * Feature: turning the fan on and off
	 * 
	 *   Scenario: turning on when off
	 *     Given the fan is off
	 *     When the fan is turned on
	 *     Then the fan is on
	 *     And the fan is low
	 * 
	 *   Scenario: turning on when on
	 *     Given the fan is on
	 *     When the fan is turned on
	 *     Then a precondition exception is thrown
	 * 
	 *   Scenario: turning off when on
	 *     Given the fan is on
	 *     When the fan is turned off
	 *     Then the fan is off
	 * 
	 *   Scenario: turning off when off
	 *     Given the fan is off
	 *     When the fan is turned off
	 *     Then a precondition exception is thrown
	 * </pre>
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 */
	public void testTurnOnOff() {
		
		this.logMessage("Feature: turning the fan on and off");
		this.logMessage("  Scenario: turning on when off");
		FanImplementationI.FanState resultState = null;
		FanImplementationI.FanMode resultMode = null;
		try {
			this.logMessage("    Given the fan is off");
			resultState = this.fop.getState();
			if (!FanImplementationI.FanState.OFF.equals(resultState)) {
				this.logMessage("     but was: " + resultState);
				this.statistics.failedCondition();
			}
			this.logMessage("    When the fan is turned on");
			this.fop.turnOn();
			this.logMessage("    Then the fan is on");
			resultState = this.fop.getState();
			if (!FanImplementationI.FanState.ON.equals(resultState)) {
				this.logMessage("     but was: " + resultState);
				this.statistics.incorrectResult();
			}
			this.logMessage("    And the fan is in mode low");
			resultMode = this.fop.getMode();
			if (!FanImplementationI.FanMode.LOW.equals(resultMode)) {
				this.logMessage("     but was: " + resultState);
				this.statistics.incorrectResult();
			}
		} catch (Throwable e) {
			this.statistics.incorrectResult();
			this.logMessage("     but the exception " + e + " has been raised");
		}

		this.statistics.updateStatistics();

		this.logMessage("  Scenario: turning on when on");
		this.logMessage("    Given the fan is on");
		try {
			resultState = this.fop.getState();
			if (!FanImplementationI.FanState.ON.equals(resultState)) {
				this.logMessage("     but was: " + resultState);
				this.statistics.failedCondition();
			}
		} catch (Throwable e) {
			this.statistics.failedCondition();
			this.logMessage("     but the exception " + e + " has been raised");
		}
		this.logMessage("    When the fan is turned on");
		this.logMessage("    Then a precondition exception is thrown");
		boolean old = BCMException.VERBOSE;
		try {
			BCMException.VERBOSE = false;
			this.fop.turnOn();
			this.logMessage("     but it was not thrown");
			this.statistics.incorrectResult();
		} catch(Throwable e) {
			
		} finally {
			BCMException.VERBOSE = old;
		}

		this.statistics.updateStatistics();

		this.logMessage("  Scenario: turning off when on");
		this.logMessage("    Given the fan is on");
		try {
			resultState = this.fop.getState();
			if (!FanImplementationI.FanState.ON.equals(resultState)) {
				this.logMessage("     but was: " + resultState);
				this.statistics.failedCondition();
			}
		} catch (Throwable e) {
			this.statistics.failedCondition();
			this.logMessage("     but the exception " + e + " has been raised");
		}
		this.logMessage("    When the fan is turned off");
		try {
			this.fop.turnOff();
			this.logMessage("    Then the fan is off");
			resultState = this.fop.getState();
			if (!FanImplementationI.FanState.OFF.equals(resultState)) {
				this.logMessage("     but was: " + resultState);
				this.statistics.incorrectResult();
			}
		} catch (Throwable e) {
			this.statistics.incorrectResult();
			this.logMessage("     but the exception " + e + " has been raised");
		}

		this.statistics.updateStatistics();

		this.logMessage("  Scenario: turning off when off");
		this.logMessage("    Given the fan is off");
		try {
			resultState = this.fop.getState();
			if (!FanImplementationI.FanState.OFF.equals(resultState)) {
				this.logMessage("     but was: " + resultState);
				this.statistics.failedCondition();
			}
		} catch (Throwable e) {
			this.statistics.failedCondition();
			this.logMessage("     but the exception " + e + " has been raised");
		}
		this.logMessage("    When the fan is turned off");
		this.logMessage("    Then a precondition exception is thrown");
		old = BCMException.VERBOSE;
		try {
			BCMException.VERBOSE = false;
			this.fop.turnOff();
			this.logMessage("     but the precondition exception was not thrown");
			this.statistics.incorrectResult();
		} catch (Throwable e) {
			
		} finally {
			BCMException.VERBOSE = old;
		}

		this.statistics.updateStatistics();
	}

	/**
	 * test switching modes of the fan.
	 *
	 * <p><strong>Description</strong></p>
	 * 
	 * <p>Gherkin specification:</p>
	 * <pre>
	 * Feature: switching the fan between modes LOW, MEDIUM and HIGH.
	 * 
	 *   Scenario: set the fan to medium from low
	 *     Given the fan is on
	 *     And the fan is low
	 *     When the fan is set medium
	 *     Then the fan is on
	 *     And  the fan is medium
	 * 
	 *   Scenario: set the fan to high from medium
	 *     Given the fan is on
	 *     And the fan is medium
	 *     When the fan is set high
	 *     Then the fan is on
	 *     And  the fan is high
	 * 
	 *   Scenario: set the fan to medium from high
	 *     Given the fan is on
	 *     And the fan is high
	 *     When the fan is set medium
	 *     Then the fan is on
	 *     And  the fan is medium
	 *     
	 *   Scenario: set the fan medium from medium
	 *     Given the fan is on
	 *     And the fan is medium
	 *     When the fan is set medium
	 *     Then a precondition exception is thrown
	 * 
	 *   Scenario: set the fan to low from medium
	 *     Given the fan is on
	 *     And the fan is medium
	 *     When the fan is set low
	 *     Then the fan is on
	 *     And  the fan is low
	 *     
	 *   Scenario: set the fan low from low
	 *     Given the fan is on
	 *     And the fan is low
	 *     When the fan is set low
	 *     Then a precondition exception is thrown
	 * 
	 *   Scenario: set the fan to high from low
	 *     Given the fan is on
	 *     And the fan is low
	 *     When the fan is set high
	 *     Then the fan is on
	 *     And  the fan is high
	 *     
	 *   Scenario: set the fan high from high
	 *     Given the fan is on
	 *     And the fan is high
	 *     When the fan is set high
	 *     Then a precondition exception is thrown
	 * 
	 *   Scenario: set the fan to low from high
	 *     Given the fan is on
	 *     And the fan is high
	 *     When the fan is set low
	 *     Then the fan is on
	 *     And  the fan is low
	 * 
	 * </pre>
	 */
	public void testSwitchModes()
	{
		this.logMessage("Feature: switching the fan between LOW, MEDIUM and HIGH.");
		this.logMessage("  Scenario: set the fan to medium from low");
		this.logMessage("    Given the fan is on");
		FanImplementationI.FanState resultState = null;
		FanImplementationI.FanMode resultMode = null;

		// Always start with fan ON and LOW
		try {
			this.fop.turnOn();
			resultState = this.fop.getState();
			if (!FanImplementationI.FanState.ON.equals(resultState)) {
				this.logMessage("     but was: " + resultState);
				this.statistics.failedCondition();
			}
		} catch (Throwable e) {
			this.statistics.failedCondition();
			this.logMessage("     but the exception " + e + " has been raised");
		}

		try {
			this.logMessage("    And the fan is low");
			resultMode = this.fop.getMode();
			if (!FanImplementationI.FanMode.LOW.equals(resultMode)) {
				this.logMessage("     but was: " + resultMode);
				this.statistics.failedCondition();
			}
		} catch (Throwable e) {
			this.statistics.failedCondition();
			this.logMessage("     but the exception " + e + " has been raised");
		}
		try {
			this.logMessage("    When the fan is set medium");
			this.logMessage("    Then the fan is on");
			this.fop.setMedium();
			resultState = this.fop.getState();
			if (!FanImplementationI.FanState.ON.equals(resultState)) {
				this.logMessage("     but was: " + resultState);
				this.statistics.incorrectResult();
			}
		} catch (Throwable e) {
			this.statistics.incorrectResult();
			this.logMessage("     but the exception " + e + " has been raised");
		}
		try {
			this.logMessage("    And the fan is medium");
			resultMode = this.fop.getMode();
			if (!FanImplementationI.FanMode.MEDIUM.equals(resultMode)) {
				this.logMessage("     but was: " + resultMode);
				this.statistics.incorrectResult();
			}
		} catch (Throwable e) {
			this.statistics.incorrectResult();
			this.logMessage("     but the exception " + e + " has been raised");
		}
		this.statistics.updateStatistics();
		
		this.logMessage("  Scenario: set the fan to high from medium");
		this.logMessage("    Given the fan is on");
		this.logMessage("    And the fan is medium");
		this.logMessage("    When the fan is set high");
		try {
			this.fop.setHigh();
			this.logMessage("    Then the fan is on");
			resultState = this.fop.getState();
			if (!FanImplementationI.FanState.ON.equals(resultState)) {
				this.logMessage("     but was: " + resultState);
				this.statistics.incorrectResult();
			}
			this.logMessage("    And the fan is high");
			resultMode = this.fop.getMode();
			if (!FanImplementationI.FanMode.HIGH.equals(resultMode)) {
				this.logMessage("     but was: " + resultMode);
				this.statistics.incorrectResult();
			}
		} catch (Throwable e) {
			this.statistics.incorrectResult();
			this.logMessage("     but the exception " + e + " has been raised");
		}
		this.statistics.updateStatistics();

		this.logMessage("  Scenario: set the fan to medium from high");
		this.logMessage("    Given the fan is on");
		this.logMessage("    And the fan is high");
		this.logMessage("    When the fan is set medium");
		try {
			this.fop.setMedium();
			this.logMessage("    Then the fan is on");
			resultState = this.fop.getState();
			if (!FanImplementationI.FanState.ON.equals(resultState)) {
				this.logMessage("     but was: " + resultState);
				this.statistics.incorrectResult();
			}
			this.logMessage("    And the fan is medium");
			resultMode = this.fop.getMode();
			if (!FanImplementationI.FanMode.MEDIUM.equals(resultMode)) {
				this.logMessage("     but was: " + resultMode);
				this.statistics.incorrectResult();
			}
		} catch (Throwable e) {
			this.statistics.incorrectResult();
			this.logMessage("     but the exception " + e + " has been raised");
		}
		this.statistics.updateStatistics();

		this.logMessage("  Scenario: set the fan medium from medium");
		this.logMessage("    Given the fan is on");
		this.logMessage("    And the fan is medium");
		this.logMessage("    When the fan is set medium");
		this.logMessage("    Then a precondition exception is thrown");
		boolean old = BCMException.VERBOSE;
		try {
			BCMException.VERBOSE = false;
			this.fop.setMedium();
			this.logMessage("     but it was not thrown");
			this.statistics.incorrectResult();
		} catch (Throwable e) {
			// expected
		} finally {
			BCMException.VERBOSE = old;
		}
		this.statistics.updateStatistics();

		this.logMessage("  Scenario: set the fan to low from medium");
		this.logMessage("    Given the fan is on");
		this.logMessage("    And the fan is medium");
		this.logMessage("    When the fan is set low");
		try {
			this.fop.setLow();
			this.logMessage("    Then the fan is on");
			resultState = this.fop.getState();
			if (!FanImplementationI.FanState.ON.equals(resultState)) {
				this.logMessage("     but was: " + resultState);
				this.statistics.incorrectResult();
			}
			this.logMessage("    And the fan is low");
			resultMode = this.fop.getMode();
			if (!FanImplementationI.FanMode.LOW.equals(resultMode)) {
				this.logMessage("     but was: " + resultMode);
				this.statistics.incorrectResult();
			}
		} catch (Throwable e) {
			this.statistics.incorrectResult();
			this.logMessage("     but the exception " + e + " has been raised");
		}
		this.statistics.updateStatistics();

		this.logMessage("  Scenario: set the fan low from low");
		this.logMessage("    Given the fan is on");
		this.logMessage("    And the fan is low");
		this.logMessage("    When the fan is set low");
		this.logMessage("    Then a precondition exception is thrown");
		old = BCMException.VERBOSE;
		try {
			BCMException.VERBOSE = false;
			this.fop.setLow();
			this.logMessage("     but it was not thrown");
			this.statistics.incorrectResult();
		} catch (Throwable e) {
			// expected
		} finally {
			BCMException.VERBOSE = old;
		}
		this.statistics.updateStatistics();

		this.logMessage("  Scenario: set the fan to high from low");
		this.logMessage("    Given the fan is on");
		this.logMessage("    And the fan is low");
		this.logMessage("    When the fan is set high");
		try {
			this.fop.setHigh();
			this.logMessage("    Then the fan is on");
			resultState = this.fop.getState();
			if (!FanImplementationI.FanState.ON.equals(resultState)) {
				this.logMessage("     but was: " + resultState);
				this.statistics.incorrectResult();
			}
			this.logMessage("    And the fan is high");
			resultMode = this.fop.getMode();
			if (!FanImplementationI.FanMode.HIGH.equals(resultMode)) {
				this.logMessage("     but was: " + resultMode);
				this.statistics.incorrectResult();
			}
		} catch (Throwable e) {
			this.statistics.incorrectResult();
			this.logMessage("     but the exception " + e + " has been raised");
		}
		this.statistics.updateStatistics();

		this.logMessage("  Scenario: set the fan high from high");
		this.logMessage("    Given the fan is on");
		this.logMessage("    And the fan is high");
		this.logMessage("    When the fan is set high");
		this.logMessage("    Then a precondition exception is thrown");
		old = BCMException.VERBOSE;
		try {
			BCMException.VERBOSE = false;
			this.fop.setHigh();
			this.logMessage("     but it was not thrown");
			this.statistics.incorrectResult();
		} catch (Throwable e) {
			// expected
		} finally {
			BCMException.VERBOSE = old;
		}
		this.statistics.updateStatistics();

		this.logMessage("  Scenario: set the fan to low from high");
		this.logMessage("    Given the fan is on");
		this.logMessage("    And the fan is high");
		this.logMessage("    When the fan is set low");
		try {
			this.fop.setLow();
			this.logMessage("    Then the fan is on");
			resultState = this.fop.getState();
			if (!FanImplementationI.FanState.ON.equals(resultState)) {
				this.logMessage("     but was: " + resultState);
				this.statistics.incorrectResult();
			}
			this.logMessage("    And the fan is low");
			resultMode = this.fop.getMode();
			if (!FanImplementationI.FanMode.LOW.equals(resultMode)) {
				this.logMessage("     but was: " + resultMode);
				this.statistics.incorrectResult();
			}
		} catch (Throwable e) {
			this.statistics.incorrectResult();
			this.logMessage("     but the exception " + e + " has been raised");
		}
		this.statistics.updateStatistics();

		// Turn off at the end of the test
		try {
			this.fop.turnOff();
		} catch (Throwable e) {
			this.logMessage("     but could not switch off the oven: " + e);
			this.statistics.incorrectResult();
		}
	}
	
	/**
	 * test starting and stopping oscillation of the fan.
	 *
	 * <p><strong>Description</strong></p>
	 * 
	 * <p>Gherkin specification:</p>
	 * <pre>
	 * Feature: controlling the oscillation of the fan.
	 * 
	 *   Scenario: start oscillation when the fan is off
	 *     Given the fan is off
	 *     When the fan starts oscillation
	 *     Then a precondition exception is thrown
	 *     
	 *   Scenario: stop oscillation when the fan is off
	 *     Given the fan is off
	 *     When the fan stops oscillation
	 *     Then a precondition exception is thrown
	 * 
	 *   Scenario: start oscillation when the fan is on and not oscillating
	 *     Given the fan is on
	 *     And the fan is not oscillating
	 *     When the fan starts oscillation
	 *     Then the fan is on
	 *     And  the fan is oscillating
	 * 
	 *   Scenario: start oscillation when the fan is already oscillating
	 *     Given the fan is on
	 *     And the fan is oscillating
	 *     When the fan starts oscillation
	 *     Then a precondition exception is thrown 
	 * 
	 *   Scenario: stop oscillation when the fan is oscillating
	 *     Given the fan is on
	 *     And the fan is oscillating
	 *     When the fan stops oscillation
	 *     Then the fan is on
	 *     And  the fan is not oscillating
	 * 
	 *   Scenario: stop oscillation when the fan is not oscillating
	 *     Given the fan is on
	 *     And the fan is not oscillating
	 *     When the fan stops oscillation
	 *     Then a precondition exception is thrown
	 * 
	 *   Scenario: turn off the fan while oscillating
	 *     Given the fan is on
	 *     And the fan is oscillating
	 *     When the fan is turned off
	 *     Then the fan is off
	 *     And  the fan is not oscillating
	 * </pre>
	 */
	public void testOscillation()
	{
		this.logMessage("Feature: controlling the oscillation of the fan.");

		FanImplementationI.FanState resultState = null;
		boolean oscillating = false;
		
		this.logMessage("  Scenario: start oscillation when the fan is off");
		this.logMessage("    Given the fan is off");
		this.logMessage("    When the fan starts oscillation");
		this.logMessage("    Then a precondition exception is thrown");

		boolean old = BCMException.VERBOSE;
		try {
			BCMException.VERBOSE = false;
			this.fop.startOscillation();
			this.logMessage("     but it was not thrown");
			this.statistics.incorrectResult();
		} catch (Throwable e) {
			// expected
		} finally {
			BCMException.VERBOSE = old;
		}
		this.statistics.updateStatistics();
		
		this.logMessage("  Scenario: stop oscillation when the fan is off");
		this.logMessage("    Given the fan is off");
		this.logMessage("    When the fan stops oscillation");
		this.logMessage("    Then a precondition exception is thrown");

		old = BCMException.VERBOSE;
		try {
			BCMException.VERBOSE = false;
			this.fop.stopOscillation();
			this.logMessage("     but it was not thrown");
			this.statistics.incorrectResult();
		} catch (Throwable e) {
			// expected
		} finally {
			BCMException.VERBOSE = old;
		}
		this.statistics.updateStatistics();

		this.logMessage("  Scenario: start oscillation when the fan is on and not oscillating");
		try {
			this.logMessage("    Given the fan is on");
			this.fop.turnOn();

			this.logMessage("    And the fan is not oscillating");
			oscillating = this.fop.isOscillating();
			if (oscillating) {
				this.logMessage("     but it was oscillating");
				this.statistics.failedCondition();
			}

			this.logMessage("    When the fan starts oscillation");
			this.fop.startOscillation();

			this.logMessage("    Then the fan is on");
			resultState = this.fop.getState();
			if (!FanImplementationI.FanState.ON.equals(resultState)) {
				this.logMessage("     but was: " + resultState);
				this.statistics.incorrectResult();
			}

			this.logMessage("    And the fan is oscillating");
			if (!this.fop.isOscillating()) {
				this.logMessage("     but it was not oscillating");
				this.statistics.incorrectResult();
			}
		} catch (Throwable e) {
			this.statistics.incorrectResult();
			this.logMessage("     but the exception " + e + " has been raised");
		}
		this.statistics.updateStatistics();

		this.logMessage("  Scenario: start oscillation when the fan is already oscillating");
		this.logMessage("    Given the fan is on");
		this.logMessage("    And the fan is oscillating");
		this.logMessage("    When the fan starts oscillation");
		this.logMessage("    Then a precondition exception is thrown");

		old = BCMException.VERBOSE;
		try {
			BCMException.VERBOSE = false;
			this.fop.startOscillation();
			this.logMessage("     but it was not thrown");
			this.statistics.incorrectResult();
		} catch (Throwable e) {
			// expected
		} finally {
			BCMException.VERBOSE = old;
		}
		this.statistics.updateStatistics();

		this.logMessage("  Scenario: stop oscillation when the fan is oscillating");
		this.logMessage("    Given the fan is on");
		this.logMessage("    And the fan is oscillating");
		try {
			if (!this.fop.isOscillating()) {
				this.logMessage("     but it was not oscillating");
				this.statistics.failedCondition();
			}

			this.logMessage("    When the fan stops oscillation");
			this.fop.stopOscillation();

			this.logMessage("    Then the fan is on");
			resultState = this.fop.getState();
			if (!FanImplementationI.FanState.ON.equals(resultState)) {
				this.logMessage("     but was: " + resultState);
				this.statistics.incorrectResult();
			}

			this.logMessage("    And the fan is not oscillating");
			if (this.fop.isOscillating()) {
				this.logMessage("     but it was oscillating");
				this.statistics.incorrectResult();
			}
		} catch (Throwable e) {
			this.statistics.incorrectResult();
			this.logMessage("     but the exception " + e + " has been raised");
		}
		this.statistics.updateStatistics();

		this.logMessage("  Scenario: stop oscillation when the fan is not oscillating");
		this.logMessage("    Given the fan is on");
		this.logMessage("    And the fan is not oscillating");
		this.logMessage("    When the fan stops oscillation");
		this.logMessage("    Then a precondition exception is thrown");

		old = BCMException.VERBOSE;
		try {
			BCMException.VERBOSE = false;
			this.fop.stopOscillation();
			this.logMessage("     but it was not thrown");
			this.statistics.incorrectResult();
		} catch (Throwable e) {
			// expected
		} finally {
			BCMException.VERBOSE = old;
		}
		this.statistics.updateStatistics();

		this.logMessage("  Scenario: turn off the fan while oscillating");
		try {
			this.logMessage("    Given the fan is on");
			this.logMessage("    And the fan is oscillating");
			this.fop.startOscillation();

			if (!this.fop.isOscillating()) {
				this.statistics.failedCondition();
			}

			this.logMessage("    When the fan is turned off");
			this.fop.turnOff();

			this.logMessage("    Then the fan is off");
			resultState = this.fop.getState();
			if (!FanImplementationI.FanState.OFF.equals(resultState)) {
				this.logMessage("     but was: " + resultState);
				this.statistics.incorrectResult();
			}

			this.logMessage("    And the fan is not oscillating");
			if (this.fop.isOscillating()) {
				this.logMessage("     but it was oscillating");
				this.statistics.incorrectResult();
			}
		} catch (Throwable e) {
			this.statistics.incorrectResult();
			this.logMessage("     but the exception " + e + " has been raised");
		}
		this.statistics.updateStatistics();
	}


	protected void runAllUnitTests() {
		this.testGetState();
		this.testGetMode();
		this.testTurnOnOff();
		this.testSwitchModes();
		this.testOscillation();
		this.statistics.statisticsReport(this);
	}

	// -------------------------------------------------------------------------
	// Component lifecycle
	// -------------------------------------------------------------------------

	@Override
	public synchronized void start() throws ComponentStartException {
		super.start();
		try {
			this.doPortConnection(
					this.fop.getPortURI(),
					fanInboundPortURI,
					FanConnector.class.getCanonicalName());
		} catch (Throwable e) {
			throw new ComponentStartException(e);
		}
	}

	@Override
	public synchronized void execute() throws Exception {
		if (!this.isUnitTest) {
			ClocksServerOutboundPort clocksServerOutboundPort = 
												new ClocksServerOutboundPort(this);
			clocksServerOutboundPort.publishPort();
			this.doPortConnection(
					clocksServerOutboundPort.getPortURI(),
					ClocksServer.STANDARD_INBOUNDPORT_URI,
					ClocksServerConnector.class.getCanonicalName());
			AcceleratedClock ac = 
					clocksServerOutboundPort.getClock(CVMIntegrationTest.CLOCK_URI);
			this.doPortDisconnection(clocksServerOutboundPort.getPortURI());
			clocksServerOutboundPort.unpublishPort();
			clocksServerOutboundPort = null;
			
			this.traceMessage("Fan Tester waits until start.\n");
			ac.waitUntilStart();
		}
		this.traceMessage("Fan Tester starts the tests.\n");
		this.runAllUnitTests();
		this.traceMessage("Fan Tester ends.\n");
	}

	@Override
	public synchronized void finalise() throws Exception {
		this.doPortDisconnection(this.fop.getPortURI());
		super.finalise();
	}

	@Override
	public synchronized void shutdown() throws ComponentShutdownException {
		try {
			this.fop.unpublishPort();
		} catch (Throwable e) {
			throw new ComponentShutdownException(e);
		}
		super.shutdown();
	}
}
