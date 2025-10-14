package equipments.oven;

import equipments.oven.connections.*;

import static org.junit.jupiter.api.Assertions.assertTrue;

import equipments.oven.OvenImplementationI.*;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.RequiredInterfaces;
import fr.sorbonne_u.components.exceptions.BCMException;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import fr.sorbonne_u.components.hem2025.tests_utils.TestsStatistics;
import fr.sorbonne_u.components.hem2025e1.CVMIntegrationTest;
import fr.sorbonne_u.utils.aclocks.AcceleratedClock;
import fr.sorbonne_u.utils.aclocks.ClocksServer;
import fr.sorbonne_u.utils.aclocks.ClocksServerCI;
import fr.sorbonne_u.utils.aclocks.ClocksServerConnector;
import fr.sorbonne_u.utils.aclocks.ClocksServerOutboundPort;
import fr.sorbonne_u.exceptions.*;


/**
 * The class <code>OvenTester</code> implements a component performing
 * tests for the class <code>Oven</code> as a BCM4Java component.
 *
 * <p><strong>Description</strong></p>
 * 
 * This tester checks all basic operations of the Oven component:
 * <ul>
 *   <li>Getting state, mode and temperature</li>
 *   <li>Turning ON/OFF</li>
 *   <li>Switching between DEFROST / GRILL / CUSTOM modes</li>
 *   <li>Setting custom temperature</li>
 *   <li>Programming and stopping cooking sessions</li>
 * </ul>
 * <p><strong>Implementation Invariants</strong></p>
 * 
 * <pre>
 * invariant	{@code ovenInboundPortURI != null && !ovenInboundPortURI.isEmpty()}
 * </pre>
 * 
 * <p><strong>Invariants</strong></p>
 * 
 * <pre>
 * invariant	{@code X_RELATIVE_POSITION >= 0}
 * invariant	{@code Y_RELATIVE_POSITION >= 0}
 * </pre>
 * 
 * <p>Created on : 2025-10-08</p>
 * 
 * @author	<a href="mailto:Rodrigo.Vila@etu.sorbonne-universite.fr">Rodrigo Vila</a>
 * @author	<a href="mailto:Damien.Ribeiro@etu.sorbonne-universite.fr">Damien Ribeiro</a>
 */
@RequiredInterfaces(required = {OvenUserCI.class, ClocksServerCI.class})
public class OvenTester extends AbstractComponent
{
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	public static boolean		VERBOSE = false;
	public static int			X_RELATIVE_POSITION = 0;
	public static int			Y_RELATIVE_POSITION = 0;

	protected final boolean		isUnitTest;
	protected OvenOutboundPort	oop;
	protected String			ovenInboundPortURI;
	protected TestsStatistics	statistics;
	
	// -------------------------------------------------------------------------
	// Invariants
	// -------------------------------------------------------------------------
	
	/**
	 * return true if the implementation invariants are observed, false otherwise.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code ot != null}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param ot	instance to be tested.
	 * @return		true if the implementation invariants are observed, false otherwise.
	 */
	protected static boolean implementationInvariants(OvenTester ot) {
		assert	ot != null : new PreconditionException("ot != null");

		boolean ret = true;
		ret &= AssertionChecking.checkImplementationInvariant(
				ot.ovenInboundPortURI != null &&
						!ot.ovenInboundPortURI.isEmpty(),
				OvenTester.class, ot,
				"ot.ovenInboundPortURI != null && !ot.ovenInboundPortURI.isEmpty()");
		return ret;
	}
	
	/**
	 * return true if the invariants are observed, false otherwise.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code ot != null}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param ot	instance to be tested.
	 * @return		true if the invariants are observed, false otherwise.
	 */
	protected static boolean invariants(OvenTester ot) {
		assert	ot != null : new PreconditionException("ot != null");

		boolean ret = true;
		ret &= AssertionChecking.checkInvariant(
				X_RELATIVE_POSITION >= 0,
				OvenTester.class, ot,
				"X_RELATIVE_POSITION >= 0");
		ret &= AssertionChecking.checkInvariant(
				Y_RELATIVE_POSITION >= 0,
				OvenTester.class, ot,
				"Y_RELATIVE_POSITION >= 0");
		return ret;
	}
		
	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------
		
		/**
		 * create a oven tester component.
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
	protected OvenTester(boolean isUnitTest) throws Exception {
		this(isUnitTest, Oven.INBOUND_PORT_URI);
	}
	
	/**
	 * create a oven tester component.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code ovenInboundPortURI != null && !ovenInboundPortURI.isEmpty()}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param isUnitTest				when true, the component performs a unit test.
	 * @param ovenInboundPortURI		URI of the oven inbound port to connect to.
	 * @throws Exception				<i>to do</i>.
	 */
	protected OvenTester(boolean isUnitTest, String ovenInboundPortURI) throws Exception {
		super(1, 0);
		assert ovenInboundPortURI != null && !ovenInboundPortURI.isEmpty() :
			new PreconditionException("ovenInboundPortURI != null && !empty()");
		this.isUnitTest = isUnitTest;
		this.initialise(ovenInboundPortURI);
	}
	
	/**
	 * create a oven tester component.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code ovenInboundPortURI != null && !ovenInboundPortURI.isEmpty()}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param isUnitTest				when true, the component performs a unit test.
	 * @param ovenInboundPortURI		URI of the oven inbound port to connect to.
	 * @param reflectionInboundPortURI	URI of the inbound port offering the <code>ReflectionI</code> interface.
	 * @throws Exception				<i>to do</i>.
	 */
	protected OvenTester(
			boolean isUnitTest, 
			String ovenInboundPortURI, 
			String reflectionInboundPortURI
			) throws Exception 
	{
		super(reflectionInboundPortURI, 1, 0);
		this.isUnitTest = isUnitTest;
		this.initialise(ovenInboundPortURI);
	}
	
	/**
	 * initialise a oven tester component.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code ovenInboundPortURI != null && !ovenInboundPortURI.isEmpty()}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param ovenInboundPortURI		URI of the oven inbound port to connect to.
	 * @throws Exception				<i>to do</i>.
	 */
	protected void initialise(String ovenInboundPortURI) throws Exception {
		this.ovenInboundPortURI = ovenInboundPortURI;
		this.oop = new OvenOutboundPort(this);
		this.oop.publishPort();

		if (VERBOSE) {
			this.tracer.get().setTitle("Oven tester component");
			this.tracer.get().setRelativePosition(X_RELATIVE_POSITION, Y_RELATIVE_POSITION);
			this.toggleTracing();
		}

		this.statistics = new TestsStatistics();
		
		assert implementationInvariants(this) :
			new ImplementationInvariantException("OvenTester.implementationInvariants(this)");
	assert invariants(this) :
			new InvariantException("OvenTester.invariants(this)");
	}

	// -------------------------------------------------------------------------
	// Component internal methods
	// -------------------------------------------------------------------------
	
	/**
	 * test of the {@code getState} method when the oven is off.
	 * 
	 * <p><strong>Description</strong></p>
	 * 
	 * <p>Gherkin specification:</p>
	 * <pre>
	 * Feature: Getting the state of the oven
	 * 
	 *   Scenario: getting the state when off
	 *     Given the oven is initialised and never been used yet
	 *     When the oven has not been used yet
	 *     Then the oven is off
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
		this.logMessage("Feature: Getting the state of the oven");
		this.logMessage("  Scenario: getting the state when off");
		this.logMessage("    Given the oven is initialised and never been used yet");
		OvenState result = null;
		try {
			this.logMessage("    When I test the state of the oven");
			result = this.oop.getState();
			this.logMessage("    Then the state of the oven is off");

			if (!OvenState.OFF.equals(result)) {
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
	 * test of the {@code getMode} method when the oven is off.
	 * 
	 * <p><strong>Description</strong></p>
	 * 
	 * <p>Gherkin specification:</p>
	 * <pre>
	 * Feature: Getting the mode of the oven
	 * 
	 *   Scenario: getting the mode when off
	 *     Given the oven is initialised and never been used yet
	 *     When the method getMode is called
	 *     Then the result is that the oven is in CUSTOM mode
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
		this.logMessage("Feature: Getting the mode of the oven");
		this.logMessage("  Scenario: getting the mode when off");
		this.logMessage("    Given the oven is initialised and never been used yet");
		OvenMode result = null;
		try {
			this.logMessage("    When the oven has not been used yet");
			result = this.oop.getMode();
			this.logMessage("    Then the oven is in CUSTOM mode");

			if (!OvenMode.CUSTOM.equals(result)) {
				this.logMessage("     but was: " + result);
				this.statistics.incorrectResult();
			}
		} catch (Throwable e) {
			this.statistics.incorrectResult();
			this.logMessage("     but the exception " + e + " has been raised");
		}

		this.statistics.updateStatistics();
	}
	
	/**
	 * test turning on and off the oven.
	 * 
	 * <p><strong>Description</strong></p>
	 * 
	 * <p>Gherkin specification:</p>
	 * <pre>
	 * Feature: turning the oven on and off
	 * 
	 *   Scenario: turning on when off
	 *     Given the oven is off
	 *     When the oven is turned on
	 *     Then the oven is on
	 *     And the oven is in CUSTOM mode
	 * 
	 *   Scenario: turning on when on
	 *     Given the oven is on
	 *     When the oven is turned on
	 *     Then a precondition exception is thrown
	 * 
	 *   Scenario: turning off when on
	 *     Given the oven is on
	 *     When the oven is turned off
	 *     Then the oven is off
	 * 
	 *   Scenario: turning off when off
	 *     Given the oven is off
	 *     When the oven is turned off
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

		this.logMessage("Feature: turning the oven on and off");
		this.logMessage("  Scenario: turning on when off");
		OvenState resultState = null;
		OvenMode resultMode = null;
		try {
			this.logMessage("    Given the oven is off");
			resultState = this.oop.getState();
			if (!OvenState.OFF.equals(resultState)) {
				this.logMessage("     but was: " + resultState);
				this.statistics.failedCondition();
			}
			this.logMessage("    When the oven is turned on");
			this.oop.turnOn();
			this.logMessage("    Then the oven is on");
			resultState = this.oop.getState();
			if (!OvenState.ON.equals(resultState)) {
				this.logMessage("     but was: " + resultState);
				this.statistics.incorrectResult();
			}
			this.logMessage("    And the oven is in CUSTOM mode");
			resultMode = this.oop.getMode();
			if (!OvenMode.CUSTOM.equals(resultMode)) {
				this.logMessage("     but was: " + resultMode);
				this.statistics.incorrectResult();
			}
		} catch (Throwable e) {
			this.statistics.incorrectResult();
			this.logMessage("     but the exception " + e + " has been raised");
		}
		this.statistics.updateStatistics();

		this.logMessage("  Scenario: turning on when on");
		this.logMessage("    Given the oven is on");
		try {
			resultState = this.oop.getState();
			if (!OvenState.ON.equals(resultState)) {
				this.logMessage("     but was: " + resultState);
				this.statistics.failedCondition();
			}
		} catch (Throwable e) {
			this.statistics.failedCondition();
			this.logMessage("     but the exception " + e + " has been raised");
		}
		this.logMessage("    When the oven is turned on");
		this.logMessage("    Then a precondition exception is thrown");
		boolean old = BCMException.VERBOSE;
		try {
			BCMException.VERBOSE = false;
			this.oop.turnOn();
			this.logMessage("     but it was not thrown");
			this.statistics.incorrectResult();
		} catch(Throwable e) {
			
		} finally {
			BCMException.VERBOSE = old;
		}
		this.statistics.updateStatistics();

		this.logMessage("  Scenario: turning off when on");
		this.logMessage("    Given the oven is on");
		try {
			resultState = this.oop.getState();
			if (!OvenState.ON.equals(resultState)) {
				this.logMessage("     but was: " + resultState);
				this.statistics.failedCondition();
			}
		} catch (Throwable e) {
			this.statistics.failedCondition();
			this.logMessage("     but the exception " + e + " has been raised");
		}
		this.logMessage("    When the oven is turned off");
		try {
			this.oop.turnOff();
			this.logMessage("    Then the oven is off");
			resultState = this.oop.getState();
			if (!OvenState.OFF.equals(resultState)) {
				this.logMessage("     but was: " + resultState);
				this.statistics.incorrectResult();
			}
		} catch (Throwable e) {
			this.statistics.incorrectResult();
			this.logMessage("     but the exception " + e + " has been raised");
		}
		this.statistics.updateStatistics();

		this.logMessage("  Scenario: turning off when off");
		this.logMessage("    Given the oven is off");
		try {
			resultState = this.oop.getState();
			if (!OvenState.OFF.equals(resultState)) {
				this.logMessage("     but was: " + resultState);
				this.statistics.failedCondition();
			}
		} catch (Throwable e) {
			this.statistics.failedCondition();
			this.logMessage("     but the exception " + e + " has been raised");
		}
		this.logMessage("    When the oven is turned off");
		this.logMessage("    Then a precondition exception is thrown");
		old = BCMException.VERBOSE;
		try {
			BCMException.VERBOSE = false;
			this.oop.turnOff();
			this.logMessage("     but the precondition exception was not thrown");
			this.statistics.incorrectResult();
		} catch (Throwable e) {
			
		} finally {
			BCMException.VERBOSE = old;
		}
		this.statistics.updateStatistics();
	}
	
	/**
	 * test switching modes of the oven.
	 *
	 * <p><strong>Description</strong></p>
	 * 
	 * <p>Gherkin specification:</p>
	 * <pre>
	 * Feature: Switching the oven between CUSTOM, DEFROST and GRILL modes.
	 * 
	 *   Scenario: setting a mode when oven is OFF
	 *     Given the oven is OFF
	 *     When trying to set CUSTOM mode (temperature 150°C)
	 *     Then a precondition exception is thrown
	 *     When trying to set DEFROST mode
	 *     Then a precondition exception is thrown
	 *     When trying to set GRILL mode
	 *     Then a precondition exception is thrown
	 * 
	 *   Scenario: setting CUSTOM -> CUSTOM (different temperature)
	 *     Given the oven is ON and in CUSTOM mode
	 *     When setting temperature to 200°C
	 *     Then the oven remains in CUSTOM mode
	 *     And the temperature is 200°C
	 * 
	 *   Scenario: setting CUSTOM -> CUSTOM (same temperature)
	 *     Given the oven is ON and in CUSTOM mode at 200°C
	 *     When setting the same temperature again
	 *     Then a precondition exception is thrown
	 * 
	 *   Scenario: setting CUSTOM -> CUSTOM (invalid temperature)
	 *     Given the oven is ON
	 *     When setting a temperature below 50°C
	 *     Then a precondition exception is thrown
	 * 
	 *   Scenario: setting CUSTOM -> DEFROST
	 *     Given the oven is ON and in CUSTOM mode
	 *     When setting DEFROST mode
	 *     Then the oven is in DEFROST mode
	 *     And the temperature is 80°C
	 * 
	 *   Scenario: setting DEFROST -> DEFROST
	 *     Given the oven is in DEFROST mode
	 *     When setting DEFROST mode again
	 *     Then a precondition exception is thrown
	 * 
	 *   Scenario: setting DEFROST -> GRILL
	 *     Given the oven is in DEFROST mode
	 *     When setting GRILL mode
	 *     Then the oven is in GRILL mode
	 *     And the temperature is 220°C
	 * 
	 *   Scenario: setting GRILL -> GRILL
	 *     Given the oven is in GRILL mode
	 *     When setting GRILL mode again
	 *     Then a precondition exception is thrown
	 * 
	 *   Scenario: setting GRILL -> CUSTOM
	 *     Given the oven is in GRILL mode
	 *     When setting CUSTOM mode to 150°C
	 *     Then the oven is in CUSTOM mode
	 *     And the temperature is 150°C
	 * 
	 *   Scenario: setting CUSTOM -> GRILL
	 *     Given the oven is in CUSTOM mode
	 *     When setting GRILL mode
	 *     Then the oven is in GRILL mode
	 *     And the temperature is 220°C
	 * 
	 *   Scenario: setting GRILL -> DEFROST
	 *     Given the oven is in GRILL mode
	 *     When setting DEFROST mode
	 *     Then the oven is in DEFROST mode
	 *     And the temperature is 80°C
	 * </pre>
	 * 
	 * <p><strong>Contract</strong></p>
	 * <pre>
	 * pre  {@code true} // no precondition.
	 * post {@code true} // no postcondition.
	 * </pre>
	 */
	public void testSwitchModes() {
		this.logMessage("Feature: Switching the oven between CUSTOM, DEFROST and GRILL modes.");
		OvenState resultState = null;
		OvenMode resultMode = null;
		int resultTemp = 0;

		// Scenario: setting a mode when oven is OFF
		this.logMessage("  Scenario: setting a mode when oven is OFF");
		this.logMessage("    Given the oven is OFF");
		this.logMessage("    When trying to set CUSTOM mode (150°C)");
		this.logMessage("    Then a precondition exception is thrown");
		boolean old = BCMException.VERBOSE;
		try {
			resultState = this.oop.getState();
			if (!OvenState.OFF.equals(resultState)) {
				this.logMessage("     but was: " + resultState);
				this.statistics.failedCondition();
			}
			BCMException.VERBOSE = false;
			this.oop.setTemperature(150);
			this.logMessage("     but the precondition exception was not thrown");
			this.statistics.incorrectResult();
		} catch (Throwable e) {
			// expected
		} finally {
			BCMException.VERBOSE = old;
		}
		this.logMessage("    When trying to set DEFROST mode");
		this.logMessage("    Then a precondition exception is thrown");
		old = BCMException.VERBOSE;
		try {
			BCMException.VERBOSE = false;
			this.oop.setDefrost();
			this.logMessage("     but the precondition exception was not thrown");
			this.statistics.incorrectResult();
		} catch (Throwable e) {
			// expected
		} finally {
			BCMException.VERBOSE = old;
		}
		this.logMessage("    When trying to set GRILL mode");
		this.logMessage("    Then a precondition exception is thrown");
		old = BCMException.VERBOSE;
		try {
			BCMException.VERBOSE = false;
			this.oop.setGrill();
			this.logMessage("     but the precondition exception was not thrown");
			this.statistics.incorrectResult();
		} catch (Throwable e) {
			// expected
		} finally {
			BCMException.VERBOSE = old;
		}
		
		this.statistics.updateStatistics();

		// Turn oven ON
		try {
			this.oop.turnOn();
		} catch (Throwable e) {
			this.logMessage("     failed to turn on oven for subsequent tests");
		}

		// Scenario: CUSTOM -> CUSTOM (different temperature)
		this.logMessage("  Scenario: setting CUSTOM -> CUSTOM (different temperature)");
		this.logMessage("    Given the oven is on");
		this.logMessage("    And the oven is in CUSTOM mode with 0°C");
		this.logMessage("    When the oven temperature is set to 200°C");
		this.logMessage("    Then the oven is in CUSTOM mode and its temperature is 200°C");
		try {
			this.oop.setTemperature(200);
			resultMode = this.oop.getMode();
			resultTemp = this.oop.getTemperature();
			if (!OvenMode.CUSTOM.equals(resultMode) || resultTemp != 200) {
				this.logMessage("     but mode=" + resultMode + " temp=" + resultTemp);
				this.statistics.incorrectResult();
			}
		} catch (Throwable e) {
			this.statistics.incorrectResult();
			this.logMessage("     but the exception " + e + " has been raised");
		}
		this.statistics.updateStatistics();

		// Scenario: CUSTOM -> CUSTOM (same temperature)
		this.logMessage("  Scenario: setting CUSTOM -> CUSTOM (same temperature)");
		this.logMessage("    Given the oven is on");
		this.logMessage("    And the oven is in CUSTOM mode with 200°C");
		this.logMessage("    When the oven temperature is set to 200°C");
		this.logMessage("    Then a precondition exception is thrown");
		old = BCMException.VERBOSE;
		try {
			BCMException.VERBOSE = false;
			this.oop.setTemperature(200);
			this.logMessage("     but precondition exception was not thrown");
			this.statistics.incorrectResult();
		} catch (Throwable e) {
			// expected
		} finally {
			BCMException.VERBOSE = old;
		}
		this.statistics.updateStatistics();

		// Scenario: CUSTOM -> CUSTOM (invalid temperature)
		this.logMessage("  Scenario: setting CUSTOM -> CUSTOM (invalid temperature)");
		this.logMessage("    Given the oven is on");
		this.logMessage("    And the oven is in CUSTOM mode with 200°C");
		this.logMessage("    When the oven temperature is set to 0°C");
		this.logMessage("    Then a precondition exception is thrown");
		old = BCMException.VERBOSE;
		try {
			BCMException.VERBOSE = false;
			this.oop.setTemperature(0);
			this.logMessage("     but precondition exception was not thrown");
			this.statistics.incorrectResult();
		} catch (Throwable e) {
			// expected
		} finally {
			BCMException.VERBOSE = old;
		}
		this.statistics.updateStatistics();

		// Scenario: CUSTOM -> DEFROST
		this.logMessage("  Scenario: setting CUSTOM -> DEFROST");
		this.logMessage("    Given the oven is on");
		this.logMessage("    And the oven is in CUSTOM mode with 200°C");
		this.logMessage("    When the oven is set to DEFROST mode");
		this.logMessage("    Then the oven is in DEFROST mode and its temperature is 80°C");
		try {
			this.oop.setDefrost();
			resultMode = this.oop.getMode();
			resultTemp = this.oop.getTemperature();
			if (!OvenMode.DEFROST.equals(resultMode) || resultTemp != 80) {
				this.logMessage("     but mode=" + resultMode + " temp=" + resultTemp);
				this.statistics.incorrectResult();
			}
		} catch (Throwable e) {
			this.statistics.incorrectResult();
		}
		this.statistics.updateStatistics();

		// Scenario: DEFROST -> DEFROST
		this.logMessage("  Scenario: setting DEFROST -> DEFROST");
		this.logMessage("    Given the oven is on");
		this.logMessage("    And the oven is in DEFROST mode");
		this.logMessage("    When the oven is set to DEFROST mode");
		this.logMessage("    Then a precondition exception is thrown");
		old = BCMException.VERBOSE;
		try {
			BCMException.VERBOSE = false;
			this.oop.setDefrost();
			this.logMessage("     but precondition exception was not thrown");
			this.statistics.incorrectResult();
		} catch (Throwable e) {
			// expected
		} finally {
			BCMException.VERBOSE = old;
		}
		this.statistics.updateStatistics();

		// Scenario: DEFROST -> GRILL
		this.logMessage("  Scenario: setting DEFROST -> GRILL");
		this.logMessage("    Given the oven is on");
		this.logMessage("    And the oven is in DEFROST mode");
		this.logMessage("    When the oven is set to GRILL mode");
		this.logMessage("    Then the oven is in GRILL mode and its temperature is 220°C");
		try {
			this.oop.setGrill();
			resultMode = this.oop.getMode();
			resultTemp = this.oop.getTemperature();
			if (!OvenMode.GRILL.equals(resultMode) || resultTemp != 220) {
				this.logMessage("     but mode=" + resultMode + " temp=" + resultTemp);
				this.statistics.incorrectResult();
			}
		} catch (Throwable e) {
			this.statistics.incorrectResult();
		}
		this.statistics.updateStatistics();

		// Scenario: GRILL -> GRILL
		this.logMessage("  Scenario: setting GRILL -> GRILL");
		this.logMessage("    Given the oven is on");
		this.logMessage("    And the oven is in GRILL mode");
		this.logMessage("    When the oven is set to GRILL mode");
		this.logMessage("    Then a precondition exception is thrown");
		old = BCMException.VERBOSE;
		try {
			BCMException.VERBOSE = false;
			this.oop.setGrill();
			this.logMessage("     but precondition exception was not thrown");
			this.statistics.incorrectResult();
		} catch (Throwable e) {
			// expected
		} finally {
			BCMException.VERBOSE = old;
		}
		this.statistics.updateStatistics();

		// Scenario: GRILL -> CUSTOM
		this.logMessage("  Scenario: setting GRILL -> CUSTOM");
		this.logMessage("    Given the oven is on");
		this.logMessage("    And the oven is in GRILL mode");
		this.logMessage("    When the oven is set to 150°C");
		this.logMessage("    Then the oven is in CUSTOM mode and its temperature is 150°C");
		try {
			this.oop.setTemperature(150);
			resultMode = this.oop.getMode();
			resultTemp = this.oop.getTemperature();
			if (!OvenMode.CUSTOM.equals(resultMode) || resultTemp != 150) {
				this.logMessage("     but mode=" + resultMode + " temp=" + resultTemp);
				this.statistics.incorrectResult();
			}
		} catch (Throwable e) {
			this.statistics.incorrectResult();
		}
		this.statistics.updateStatistics();

		// Scenario: CUSTOM -> GRILL
		this.logMessage("  Scenario: setting CUSTOM -> GRILL");
		this.logMessage("    Given the oven is on");
		this.logMessage("    And the oven is in CUSTOM mode with 150°C");
		this.logMessage("    When the oven is set to GRILL mode");
		this.logMessage("    Then the oven is in GRILL mode and its temperature is 220°C");
		try {
			this.oop.setGrill();
			resultMode = this.oop.getMode();
			resultTemp = this.oop.getTemperature();
			if (!OvenMode.GRILL.equals(resultMode) || resultTemp != 220) {
				this.logMessage("     but mode=" + resultMode + " temp=" + resultTemp);
				this.statistics.incorrectResult();
			}
		} catch (Throwable e) {
			this.statistics.incorrectResult();
		}
		this.statistics.updateStatistics();

		// Scenario: GRILL -> DEFROST
		this.logMessage("  Scenario: setting GRILL -> DEFROST");
		this.logMessage("    Given the oven is on");
		this.logMessage("    And the oven is in GRILL mode");
		this.logMessage("    When the oven is set to DEFROST mode");
		this.logMessage("    Then the oven is in DEFROST mode and its temperature is 80°C");
		try {
			this.oop.setDefrost();
			resultMode = this.oop.getMode();
			resultTemp = this.oop.getTemperature();
			if (!OvenMode.DEFROST.equals(resultMode) || resultTemp != 80) {
				this.logMessage("     but mode=" + resultMode + " temp=" + resultTemp);
				this.statistics.incorrectResult();
			}
		} catch (Throwable e) {
			this.statistics.incorrectResult();
		}
		this.statistics.updateStatistics();

		// Turn off oven at the end
		try {
			this.oop.turnOff();
		} catch (Throwable e) {
			assertTrue(false);
		}
	}

	/**
	 * test the {@code startCooking}, {@code programCooking} and {@code stopProgram} methods.
	 * 
	 * <p><strong>Description</strong></p>
	 * 
	 * <p>Gherkin specification:</p>
	 * <pre>
	 * Feature: Managing oven cooking cycles (start, program, stop)
	 * 
	 *   Scenario: starting cooking when oven is OFF
	 *     Given the oven is OFF
	 *     When startCooking is called
	 *     Then a precondition exception is thrown
	 * 
	 *   Scenario: programming cooking when oven is OFF
	 *     Given the oven is OFF
	 *     When programCooking is called
	 *     Then a precondition exception is thrown
	 * 
	 *   Scenario: stopping program when oven is OFF
	 *     Given the oven is OFF
	 *     When stopProgram is called
	 *     Then a precondition exception is thrown
	 * 
	 *   Scenario: stopping program when oven is ON
	 *     Given the oven is ON
	 *     When stopProgram is called
	 *     Then a precondition exception is thrown
	 * 
	 *   Scenario: programming cooking with invalid delay (0 seconds)
	 *     Given the oven is ON
	 *     When programCooking(0, 10) is called
	 *     Then a precondition exception is thrown
	 * 
	 *   Scenario: programming cooking with invalid duration (0 seconds)
	 *     Given the oven is ON
	 *     When programCooking(5, 0) is called
	 *     Then a precondition exception is thrown
	 * 
	 *   Scenario: starting immediate cooking
	 *     Given the oven is ON
	 *     When startCooking(10) is called
	 *     Then the oven state becomes COOKING
	 *     
	 *   Scenario: stopping cooking while COOKING
	 *     Given the oven is COOKING
	 *     When stopProgram is called
	 *     Then the oven state becomes ON
	 * 
	 *   Scenario: programming a delayed cooking
	 *     Given the oven is ON
	 *     When programCooking(5, 10) is called
	 *     Then the oven state becomes PROGRAMMED
	 *     And when the delay is over, startCooking(10) is called
	 *     Then the oven state becomes COOKING
	 * 
	 *   Scenario: programming cooking while already COOKING
	 *     Given the oven is COOKING
	 *     When programCooking(5, 10) is called
	 *     Then a precondition exception is thrown
	 *  
	 *   Scenario: cancelling a programmed cooking
	 *     Given the oven is PROGRAMMED
	 *     When stopProgram is called
	 *     Then the oven state becomes ON
	 * </pre>
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre  {@code true}	// no precondition.
	 * post {@code true}	// no postcondition.
	 * </pre>
	 */
	public void testCookingCycle() {
		this.logMessage("Feature: Managing oven cooking cycles (start, program, stop)");
		OvenState resultState = null;

		String[] offMethods = {"startCooking", "programCooking", "stopProgram"};
		for (String method : offMethods) {
			this.logMessage("  Scenario: calling " + method + " when oven is OFF");
			this.logMessage("    Given the oven is OFF");
			try {
				resultState = this.oop.getState();
				if (!OvenState.OFF.equals(resultState)) {
					this.logMessage("     but was: " + resultState);
					this.statistics.failedCondition();
				}
			} catch (Throwable e) { this.statistics.failedCondition(); }
			
			this.logMessage("    When " + method + " is called");
			this.logMessage("    Then a precondition exception is thrown");
			boolean old = BCMException.VERBOSE;
			try {
				BCMException.VERBOSE = false;
				if (method.equals("startCooking"))
					this.oop.startCooking(10);
				else if (method.equals("programCooking"))
					this.oop.programCooking(5, 10);
				else
					this.oop.stopProgram();

				this.logMessage("     but precondition exception was not thrown");
				this.statistics.incorrectResult();
			} catch (Throwable e) {
				// expected
			} finally {
				BCMException.VERBOSE = old;
			}
			this.statistics.updateStatistics();
		}
		// Turn ON oven
		try {
			this.oop.turnOn();
		} catch (Throwable e) {
			this.logMessage("     failed to turn on oven for subsequent tests");
		}

		this.logMessage("  Scenario: stopping program when oven is ON");
		this.logMessage("    Given the oven is ON");
		this.logMessage("    When stopProgram is called");
		this.logMessage("    Then a precondition exception is thrown");
		boolean old = BCMException.VERBOSE;
		try {
			BCMException.VERBOSE = false;	
			resultState = this.oop.getState();
			if (!OvenState.ON.equals(resultState)) {
				this.logMessage("     but was: " + resultState);
				this.statistics.failedCondition();
			}
			this.oop.stopProgram();
			this.logMessage("     but precondition exception was not thrown");
			this.statistics.incorrectResult();
		} catch (Throwable e) {
			// expected
		} finally {
			BCMException.VERBOSE = old;
		}
		this.statistics.updateStatistics();

		this.logMessage("  Scenario: programming cooking with invalid delay (0 seconds)");
		this.logMessage("    Given the oven is ON");
		this.logMessage("    When programCooking is called with 0 delay");
		this.logMessage("    Then a precondition exception is thrown");
		old = BCMException.VERBOSE;
		try {
			BCMException.VERBOSE = false;	
			this.oop.programCooking(0, 10);
			this.logMessage("     but precondition exception was not thrown");
			this.statistics.incorrectResult();
		} catch (Throwable e) {
			// expected
		} finally {
			BCMException.VERBOSE = old;
		}
		this.statistics.updateStatistics();

		this.logMessage("  Scenario: programming cooking with invalid duration (0 seconds)");
		this.logMessage("    Given the oven is ON");
		this.logMessage("    When programCooking is called with 0 duration");
		this.logMessage("    Then a precondition exception is thrown");
		old = BCMException.VERBOSE;
		try {
			BCMException.VERBOSE = false;
			this.oop.programCooking(5, 0);
			this.logMessage("     but precondition exception was not thrown");
			this.statistics.incorrectResult();
		} catch (Throwable e) {
			// expected
		} finally {
			BCMException.VERBOSE = old;
		}
		this.statistics.updateStatistics();

		this.logMessage("  Scenario: starting immediate cooking");
		this.logMessage("    Given the oven is ON");
		this.logMessage("    When startCooking is called");
		this.logMessage("    Then the oven state becomes COOKING");
		try {
			this.oop.startCooking(10);
			resultState = this.oop.getState();
			if (!OvenState.COOKING.equals(resultState)) {
				this.logMessage("     but was: " + resultState);
				this.statistics.incorrectResult();
			}
		} catch (Throwable e) {
			this.statistics.incorrectResult();
		}
		this.statistics.updateStatistics();
		
		this.logMessage("  Scenario: stopping cooking while COOKING");
		this.logMessage("    Given the oven is COOKING");
		this.logMessage("    When stopCooking is called");
		this.logMessage("    Then the oven state becomes ON");
		try {
			this.oop.stopProgram();
			resultState = this.oop.getState();
			if (!OvenState.ON.equals(resultState)) {
				this.logMessage("     but was: " + resultState);
				this.statistics.incorrectResult();
			}
		} catch (Throwable e) {
			this.statistics.incorrectResult();
		}
		this.statistics.updateStatistics();

		this.logMessage("  Scenario: programming a delayed cooking");
		this.logMessage("    Given the oven is ON");
		this.logMessage("    When programmedCooking is called");
		this.logMessage("    Then the oven state becomes PROGRAMMED");
		try {
			this.oop.programCooking(5, 10);
			resultState = this.oop.getState();
			if (!OvenState.PROGRAMMED.equals(resultState)) {
				this.logMessage("     but was: " + resultState);
				this.statistics.incorrectResult();
			}
			this.logMessage("    Simulating delay... then calling startCooking");
			this.oop.stopProgram(); // Le delai est fini, on appel startCooking
			this.oop.startCooking(10);
			this.logMessage("    Then the oven state becomes COOKING");
			resultState = this.oop.getState();
			if (!OvenState.COOKING.equals(resultState)) {
				this.logMessage("     but was: " + resultState);
				this.statistics.incorrectResult();
			}
		} catch (Throwable e) {
			this.statistics.incorrectResult();
		}
		this.statistics.updateStatistics();

		this.logMessage("  Scenario: programming cooking while already COOKING");
		this.logMessage("    Given the oven is COOKING");
		this.logMessage("    When programmedCooking is called");
		this.logMessage("    Then a precondition exception is thrown");
		old = BCMException.VERBOSE;
		try {
			BCMException.VERBOSE = false;
			this.oop.programCooking(5, 10);
			this.logMessage("     but precondition exception was not thrown");
			this.statistics.incorrectResult();
		} catch (Throwable e) {
			// expected
		} finally {
			BCMException.VERBOSE = old;
		}
		this.statistics.updateStatistics();

		this.logMessage("  Scenario: cancelling a programmed cooking");
		try {
			// Stop last cooking
			this.oop.stopProgram();
			// Start new programmed cooking
			this.oop.programCooking(5, 10);
			this.logMessage("    Given the oven is PROGRAMMED");
			this.logMessage("    When stopProgram is called");
			this.logMessage("    Then the oven state becomes ON");
			this.oop.stopProgram();
			resultState = this.oop.getState();
			if (!OvenState.ON.equals(resultState)) {
				this.logMessage("     but was: " + resultState);
				this.statistics.incorrectResult();
			}
		} catch (Throwable e) {
			this.statistics.incorrectResult();
		}
		this.statistics.updateStatistics();

		// turn off at the end of the tests
		try {
			this.oop.turnOff();
		} catch (Throwable e) {
			assertTrue(false);
		}
	}
	
	/**
	 * run all unit tests.
	 * 
	 * <p><strong>Description</strong></p>
	 * 
	 * <p>The tests are run in the following order:</p>
	 * <ol>
	 * <li>{@code testGetState}</li>
	 * <li>{@code testGetMode}</li>
	 * <li>{@code testTurnOnOff(}</li>
	 * <li>{@code testSwitchModes}</li>
	 * <li>{@code testCookingCycle}</li>
	 * </ol>
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 */
	protected void runAllUnitTests() {
		
		this.testGetState();
		this.testGetMode();
		this.testTurnOnOff();
		this.testSwitchModes();
		this.testCookingCycle();
		
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
				this.oop.getPortURI(),
				this.ovenInboundPortURI,
				OvenConnector.class.getCanonicalName()
			);
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
			
			this.traceMessage("Oven Tester waits until start.\n");
			ac.waitUntilStart();
		}

		this.traceMessage("Oven Tester starts tests.\n");
		this.runAllUnitTests();
		this.traceMessage("Oven Tester ends.\n");
	}

	@Override
	public synchronized void finalise() throws Exception {
		this.doPortDisconnection(this.oop.getPortURI());
		super.finalise();
	}

	@Override
	public synchronized void shutdown() throws ComponentShutdownException {
		try {
			this.oop.unpublishPort();
		} catch (Throwable e) {
			throw new ComponentShutdownException(e);
		}
		super.shutdown();
	}
}
