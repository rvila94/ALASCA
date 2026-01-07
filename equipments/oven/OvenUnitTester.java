package equipments.oven;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.RequiredInterfaces;
import fr.sorbonne_u.components.exceptions.BCMException;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import fr.sorbonne_u.components.utils.tests.TestsStatistics;
import fr.sorbonne_u.devs_simulation.models.time.Duration;
import fr.sorbonne_u.components.hem2025e1.CVMIntegrationTest;
import equipments.oven.connections.OvenExternalControlConnector;
import equipments.oven.connections.OvenExternalControlOutboundPort;
import equipments.oven.connections.OvenInternalControlConnector;
import equipments.oven.connections.OvenInternalControlOutboundPort;
import equipments.oven.connections.OvenUserConnector;
import equipments.oven.connections.OvenUserOutboundPort;
import fr.sorbonne_u.exceptions.ImplementationInvariantException;
import fr.sorbonne_u.exceptions.AssertionChecking;
import fr.sorbonne_u.exceptions.InvariantException;
import fr.sorbonne_u.exceptions.PreconditionException;
import fr.sorbonne_u.utils.aclocks.AcceleratedClock;
import fr.sorbonne_u.utils.aclocks.ClocksServer;
import fr.sorbonne_u.utils.aclocks.ClocksServerCI;
import fr.sorbonne_u.utils.aclocks.ClocksServerConnector;
import fr.sorbonne_u.utils.aclocks.ClocksServerOutboundPort;

import java.time.Instant;
import java.util.concurrent.TimeUnit;

import fr.sorbonne_u.alasca.physical_data.Measure;
import fr.sorbonne_u.alasca.physical_data.SignalData;

/**
 * The class <code>OvenUnitTester</code> implements a component performing 
 * unit tests for the class <code>Oven</code> as a BCM component.
 *
 * <p><strong>Description</strong></p>
 * 
 * This component reuses the same test scenarios as the heater tester but
 * targets the Oven component (state, switching, temperature, power levels).
 *
 * <p><strong>Implementation Invariants</strong></p>
 * 
 * <pre>
 * invariant {@code ovenUserInboundPortURI != null && !ovenUserInboundPortURI.isEmpty()}
 * invariant {@code ovenInternalControlInboundPortURI != null && !ovenInternalControlInboundPortURI.isEmpty()}
 * invariant {@code ovenExternalControlInboundPortURI != null && !ovenExternalControlInboundPortURI.isEmpty()}
 * </pre>
 * 
 * <p><strong>Invariants</strong></p>
 * 
 * <pre>
 * invariant {@code X_RELATIVE_POSITION >= 0}
 * invariant {@code Y_RELATIVE_POSITION >= 0}
 * </pre>
 * 
 * <p>Created on : 2025-10-10</p>
 * 
 * @author	<a href="mailto:Rodrigo.Vila@etu.sorbonne-universite.fr">Rodrigo Vila</a>
 * @author	<a href="mailto:Damien.Ribeiro@etu.sorbonne-universite.fr">Damien Ribeiro</a>
 */
@RequiredInterfaces(required={OvenUserCI.class,
                              OvenInternalControlCI.class,
                              OvenExternalControlCI.class,
                              ClocksServerCI.class})
public class OvenUnitTester 
extends AbstractComponent
{
    // -------------------------------------------------------------------------
    // Constants and variables
    // -------------------------------------------------------------------------
    /** in clock-driven scenario, delay from start instant at which oven is switched on. */
    public static final int SWITCH_ON_DELAY = 2;
    /** in clock-driven scenario, delay from start instant at which oven is switched off. */
    public static final int SWITCH_OFF_DELAY = 9;

    /** when true, methods trace their actions. */
    public static boolean VERBOSE = false;
    /** tracing relative position X */
    public static int X_RELATIVE_POSITION = 0;
    /** tracing relative position Y */
    public static int Y_RELATIVE_POSITION = 0;

    /** true => run unit tests; false => run integration/clock scenario. */
    protected final boolean isUnitTest;

    /** inbound URIs to connect to the oven under test. */
    protected String ovenUserInboundPortURI;
    protected String ovenInternalControlInboundPortURI;
    protected String ovenExternalControlInboundPortURI;

    /** outbound ports used by the tester to call the oven. */
    protected OvenUserOutboundPort oop;
    protected OvenInternalControlOutboundPort oicop;
    protected OvenExternalControlOutboundPort oecop;

    /** test statistics collector */
    protected TestsStatistics statistics;

    // -------------------------------------------------------------------------
    // Invariants
    // -------------------------------------------------------------------------
    
    /**
	 * return true if the implementation invariants are observed, false
	 * otherwise.
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
    protected static boolean implementationInvariants(OvenUnitTester ot)
    {
        assert ot != null : new PreconditionException("ot != null");
        boolean ret = true;
        ret &= AssertionChecking.checkImplementationInvariant(
                ot.ovenUserInboundPortURI != null && !ot.ovenUserInboundPortURI.isEmpty(),
                OvenUnitTester.class, ot,
                "ot.ovenUserInboundPortURI != null && !ot.ovenUserInboundPortURI.isEmpty()");
        ret &= AssertionChecking.checkImplementationInvariant(
                ot.ovenInternalControlInboundPortURI != null && !ot.ovenInternalControlInboundPortURI.isEmpty(),
                OvenUnitTester.class, ot,
                "ot.ovenInternalControlInboundPortURI != null && !ot.ovenInternalControlInboundPortURI.isEmpty()");
        ret &= AssertionChecking.checkImplementationInvariant(
                ot.ovenExternalControlInboundPortURI != null && !ot.ovenExternalControlInboundPortURI.isEmpty(),
                OvenUnitTester.class, ot,
                "ot.ovenExternalControlInboundPortURI != null && !ot.ovenExternalControlInboundPortURI.isEmpty()");
        return ret;
    }
    
    /**
	 * return true if the invariants is observed, false otherwise.
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
    protected static boolean invariants(OvenUnitTester ot)
    {
        assert ot != null : new PreconditionException("ot != null");
        boolean ret = true;
        ret &= AssertionChecking.checkInvariant(X_RELATIVE_POSITION >= 0, OvenUnitTester.class, ot, "X_RELATIVE_POSITION >= 0");
        ret &= AssertionChecking.checkInvariant(Y_RELATIVE_POSITION >= 0, OvenUnitTester.class, ot, "Y_RELATIVE_POSITION >= 0");
        return ret;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------
    
    /**
	 * create a oven test component.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param isUnitTest	true if the component must perform unit tests, otherwise it executes integration tests actions.
	 * @throws Exception	<i>to do</i>.
	 */
    protected OvenUnitTester(boolean isUnitTest) throws Exception {
        this(isUnitTest,
             Oven.USER_INBOUND_PORT_URI,
             Oven.INTERNAL_CONTROL_INBOUND_PORT_URI,
             Oven.EXTERNAL_CONTROL_INBOUND_PORT_URI);
    }
    
    /**
	 * create a oven test component.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code ovenUserInboundPortURI != null && !ovenUserInboundPortURI.isEmpty()}
	 * pre	{@code ovenInternalControlInboundPortURI != null && !ovenInternalControlInboundPortURI.isEmpty()}
	 * pre	{@code ovenExternalControlInboundPortURI != null && !ovenExternalControlInboundPortURI.isEmpty()}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param isUnitTest							true if the component must perform unit tests, otherwise it executes integration tests actions.
	 * @param ovenUserInboundPortURI				URI of the user component interface inbound port.
	 * @param ovenInternalControlInboundPortURI	URI of the internal control component interface inbound port.
	 * @param ovenExternalControlInboundPortURI	URI of the external control component interface inbound port.
	 * @throws Exception							<i>to do</i>.
	 */
    protected OvenUnitTester(
            boolean isUnitTest,
            String ovenUserInboundPortURI,
            String ovenInternalControlInboundPortURI,
            String ovenExternalControlInboundPortURI
    ) throws Exception {
        super(1, 1);
        this.isUnitTest = isUnitTest;
        this.initialise(ovenUserInboundPortURI, 
        				ovenInternalControlInboundPortURI, 
        				ovenExternalControlInboundPortURI);
    }
    
    /**
	 * create a oven test component.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code ovenUserInboundPortURI != null && !ovenUserInboundPortURI.isEmpty()}
	 * pre	{@code ovenInternalControlInboundPortURI != null && !ovenInternalControlInboundPortURI.isEmpty()}
	 * pre	{@code ovenExternalControlInboundPortURI != null && !ovenExternalControlInboundPortURI.isEmpty()}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param isUnitTest							true if the component must perform unit tests, otherwise it executes integration tests actions.
	 * @param reflectionInboundPortURI				URI of the reflection inbound port of the component.
	 * @param ovenUserInboundPortURI				URI of the user component interface inbound port.
	 * @param ovenInternalControlInboundPortURI	URI of the internal control component interface inbound port.
	 * @param ovenExternalControlInboundPortURI	URI of the external control component interface inbound port.
	 * @throws Exception							<i>to do</i>.
	 */
    protected OvenUnitTester(
            boolean isUnitTest,
            String reflectionInboundPortURI,
            String ovenUserInboundPortURI,
            String ovenInternalControlInboundPortURI,
            String ovenExternalControlInboundPortURI
    ) throws Exception {
        super(reflectionInboundPortURI, 1, 1);
        this.isUnitTest = isUnitTest;
        this.initialise(ovenUserInboundPortURI, 
        				ovenInternalControlInboundPortURI, 
        				ovenExternalControlInboundPortURI);
    }
    
    /**
	 * initialise a oven test component.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code ovenUserInboundPortURI != null && !ovenUserInboundPortURI.isEmpty()}
	 * pre	{@code ovenInternalControlInboundPortURI != null && !ovenInternalControlInboundPortURI.isEmpty()}
	 * pre	{@code ovenExternalControlInboundPortURI != null && !ovenExternalControlInboundPortURI.isEmpty()}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param ovenUserInboundPortURI				URI of the user component interface inbound port.
	 * @param ovenInternalControlInboundPortURI	URI of the internal control component interface inbound port.
	 * @param ovenExternalControlInboundPortURI	URI of the external control component interface inbound port.
	 * @throws Exception							<i>to do</i>.
	 */
    protected void initialise(
            String ovenUserInboundPortURI,
            String ovenInternalControlInboundPortURI,
            String ovenExternalControlInboundPortURI
    ) throws Exception {
        this.ovenUserInboundPortURI = ovenUserInboundPortURI;
        this.oop = new OvenUserOutboundPort(this);
        this.oop.publishPort();

        this.ovenInternalControlInboundPortURI = ovenInternalControlInboundPortURI;
        this.oicop = new OvenInternalControlOutboundPort(this);
        this.oicop.publishPort();

        this.ovenExternalControlInboundPortURI = ovenExternalControlInboundPortURI;
        this.oecop = new OvenExternalControlOutboundPort(this);
        this.oecop.publishPort();

        if (VERBOSE) {
            this.tracer.get().setTitle("Oven tester component");
            this.tracer.get().setRelativePosition(X_RELATIVE_POSITION, Y_RELATIVE_POSITION);
            this.toggleTracing();
        }

        this.statistics = new TestsStatistics();

        assert OvenUnitTester.implementationInvariants(this) :
                new ImplementationInvariantException("OvenUnitTester.implementationInvariants(this)");
        assert OvenUnitTester.invariants(this) :
                new InvariantException("OvenUnitTester.invariants(this)");
    }

    // -------------------------------------------------------------------------
    // Component services implementation
    // -------------------------------------------------------------------------
    
    /**
     * Test getting the state and mode of the oven when initialised and off.
     * 
     * <p><strong>Description</strong></p>
     * 
     * <p>Gherkin specification</p>
     * <pre>
     * Feature: getting the state and mode of the oven
     * 
     *   Scenario: getting the state of the oven when off
     *     Given the oven is initialised
     *     And the oven has not been used yet
     *     When I test the state of the oven
     *     Then the state of the oven is OFF
     * 
     *   Scenario: getting the mode of the oven when off
     *     Given the oven is initialised
     *     And the oven has not been used yet
     *     When I test the mode of the oven
     *     Then the mode of the oven is CUSTOM
     * </pre>
     * 
     * <p><strong>Contract</strong></p>
     * 
     * <pre>
     * pre  {@code true}   // no precondition
     * post {@code true}   // no postcondition
     * </pre>
     */
    protected void testInitialStateAndMode() {
    	
        this.logMessage("Feature: getting the state and mode of the oven");
        this.logMessage("  Scenario: getting the state of the oven when off");
        this.logMessage("    Given the oven is initialised");
        this.logMessage("    And the oven has not been used yet");
        try {
            this.logMessage("    When I test the state of the oven");
            boolean result = !this.oop.on();
            if (result) {
                this.logMessage("    Then the state of the oven is OFF");
            } else {
                this.logMessage("     but was: ON");
                this.statistics.incorrectResult();
            }
        } catch (Throwable e) {
            this.statistics.incorrectResult();
            this.logMessage("     but the exception " + e + " has been raised");
        }

        this.statistics.updateStatistics();

        this.logMessage("  Scenario: getting the mode of the oven when off");
        this.logMessage("    Given the oven is initialised");
        this.logMessage("    And the oven has not been used yet");
        try {
            this.logMessage("    When I test the mode of the oven");
            Oven.OvenMode mode = this.oop.getMode();
            if (mode == Oven.OvenMode.CUSTOM) {
                this.logMessage("    Then the mode of the oven is CUSTOM");
            } else {
                this.logMessage("     but was: " + mode);
                this.statistics.incorrectResult();
            }
        } catch (Throwable e) {
            this.statistics.incorrectResult();
            this.logMessage("     but the exception " + e + " has been raised");
        }

        this.statistics.updateStatistics();
    }

    /**
     * Test switching on and off the oven.
     * 
     * <p><strong>Description</strong></p>
     * 
     * <p>Gherkin specification</p>
     * <pre>
     * Feature: switching on and off the oven
     * 
     *   Scenario: switching on the oven when off
     *     Given the oven is initialised
     *     And the oven has not been used yet
     *     When I switch on the oven
     *     Then the state of the oven is ON
     *     And the mode of the oven is CUSTOM
     * 
     *   Scenario: switching mode while oven is on
     *     Given the oven is on
     *     When I change the mode to GRILL
     *     Then the oven mode is GRILL
     * 
     *   Scenario: switching off the oven when on
     *     Given the oven is on
     *     And the oven mode is GRILL
     *     When I switch off the oven
     *     Then the state of the oven is OFF
     *     And the oven mode returns to CUSTOM
     * </pre>
     * 
     * <p><strong>Contract</strong></p>
     * 
     * <pre>
     * pre  {@code true}  // no precondition.
     * post {@code true}  // no postcondition.
     * </pre>
     */
    protected void testSwitchOnSwitchOff() {
    	this.logMessage("Feature: switching on and off the oven");

    	boolean result;
    	try {
    		this.logMessage("  Scenario: switching on the oven when off");
    		this.logMessage("    Given the oven is initialised");
    		this.logMessage("    And the oven has not been used yet");

    		this.logMessage("    When I switch on the oven");
    		this.oop.switchOn();

    		result = this.oop.on();
    		Oven.OvenState state = this.oop.getState();
    		Oven.OvenMode mode = this.oop.getMode();

    		if (result && state == Oven.OvenState.ON && mode == Oven.OvenMode.CUSTOM) {
    			this.logMessage("    Then the oven state is ON and mode is CUSTOM");
    		} else {
    			this.statistics.incorrectResult();
    			this.logMessage("     but was: state=" + state + ", mode=" + mode);
    		}

    		this.statistics.updateStatistics();

    		this.logMessage("  Scenario: switching mode while oven is on");
    		this.logMessage("    Given the oven is on");
    		this.logMessage("    When I change the mode to GRILL");

    		this.oop.setMode(Oven.OvenMode.GRILL);
    		mode = this.oop.getMode();

    		if (mode == Oven.OvenMode.GRILL) {
    			this.logMessage("    Then the oven mode is GRILL");
    		} else {
    			this.statistics.incorrectResult();
    			this.logMessage("     but was: " + mode);
    		}

    		this.statistics.updateStatistics();

    		this.logMessage("  Scenario: switching off the oven when on");
    		this.logMessage("    Given the oven is on");
    		this.logMessage("    And the oven mode is GRILL");

    		this.logMessage("    When I switch off the oven");
    		this.oop.switchOff();

    		result = !this.oop.on();
    		state = this.oop.getState();
    		mode = this.oop.getMode();

    		if (result && state == Oven.OvenState.OFF && mode == Oven.OvenMode.CUSTOM) {
    			this.logMessage("    Then the oven state is OFF and mode is reset to CUSTOM");
    		} else {
    			this.statistics.incorrectResult();
    			this.logMessage("     but was: state=" + state + ", mode=" + mode);
    		}

    		this.statistics.updateStatistics();

    	} catch (Throwable e) {
    		this.statistics.incorrectResult();
    		this.logMessage("     but the exception " + e + " has been raised");
    	}

    	this.statistics.updateStatistics();
    }
    
    /**
     * test changing the mode of the oven.
     * 
     * <p><strong>Description</strong></p>
     * 
     * <p>Gherkin specification</p>
     * <p></p>
     * <pre>
     * Feature: changing the mode of the oven
     * 
     *   Scenario: changing the mode when the oven is off
     *     Given the oven is initialised
     *     And the oven is currently off
     *     When I try to change the mode to GRILL
     *     Then a PreconditionException is raised
     * 
     *   Scenario: changing the mode to the same mode when the oven is on
     *     Given the oven is switched on
     *     And the current mode is CUSTOM
     *     When I try to change the mode to CUSTOM again
     *     Then a PreconditionException is raised
     * 
     *   Scenario: changing the mode to DEFROST when the oven is on
     *     Given the oven is switched on
     *     And the current mode is CUSTOM
     *     When I change the mode to DEFROST
     *     Then the oven mode becomes DEFROST
     *     And the target temperature is automatically set to 50°C
     * 
     *   Scenario: changing the mode to GRILL when the oven is on
     *     Given the oven is switched on
     *     And the current mode is DEFROST
     *     When I change the mode to GRILL
     *     Then the oven mode becomes GRILL
     *     And the target temperature is automatically set to 220°C
     * 
     *   Scenario: changing the mode back to CUSTOM when the oven is on
     *     Given the oven is switched on
     *     And the current mode is GRILL
     *     When I change the mode to CUSTOM
     *     Then the oven mode becomes CUSTOM
     *     And the target temperature can now be set manually
     * </pre>
     * 
     * <p><strong>Contract</strong></p>
     * 
     * <pre>
     * pre	{@code true}	// no precondition.
     * post	{@code true}	// no postcondition.
     * </pre>
     */
    protected void testModeChanges() {
    	this.logMessage("Feature: changing the mode of the oven");
    	this.logMessage("  Scenario: changing the mode when the oven is off");
    	this.logMessage("    Given the oven is initialised");
    	this.logMessage("    And the oven is currently off");
    	this.logMessage("    When I try to change the mode to GRILL");
    	this.logMessage("    Then a precondition exception is raised");
		boolean old = BCMException.VERBOSE;
		try {
    		this.oop.setMode(Oven.OvenMode.GRILL);
    		this.logMessage("     but it was not raised");
    		this.statistics.incorrectResult();
    	} catch (Throwable e) {

    	} finally {
			BCMException.VERBOSE = old;
		}
    	this.statistics.updateStatistics();

    	this.logMessage("  Scenario: changing the mode to the same mode when the oven is on");
    	this.logMessage("    Given the oven is switched on");
    	this.logMessage("    And the current mode is CUSTOM");
    	try {
			this.oop.switchOn();
    		Oven.OvenState state = this.oop.getState();
    		Oven.OvenMode mode = this.oop.getMode();

    		if ( !(this.oop.on() && state == Oven.OvenState.ON && mode == Oven.OvenMode.CUSTOM)) {
    			this.statistics.incorrectResult();
    			this.logMessage("     but was: state=" + state + ", mode=" + mode);
    		}
    	} catch (Throwable e) {
    		this.statistics.incorrectResult();
    		this.logMessage("     but the exception " + e + " has been raised");
    	}
    	
		this.logMessage("    When I try to change the mode to CUSTOM again");   		
    	this.logMessage("    Then a precondition exception is raised");
		old = BCMException.VERBOSE;
    	try {
    		this.oop.setMode(Oven.OvenMode.CUSTOM);
    		this.logMessage("     but no exception was raised");
    		this.statistics.incorrectResult();
    	} catch (Throwable e) {
    		
    	} finally {
			BCMException.VERBOSE = old;
		}
    	this.statistics.updateStatistics();

    	this.logMessage("  Scenario: changing the mode to DEFROST when the oven is on");
    	this.logMessage("    Given the oven is switched on");
    	this.logMessage("    And the current mode is CUSTOM");
		this.logMessage("    When I change the mode to DEFROST");
    	try {
    		this.oop.setMode(Oven.OvenMode.DEFROST);
    		Oven.OvenMode mode = this.oop.getMode();
    		Measure<Double> temp = this.oop.getTargetTemperature();
    		if (mode == Oven.OvenMode.DEFROST &&
    			temp.getData() == 50.0) {
    			this.logMessage("    Then the oven mode becomes DEFROST");
    			this.logMessage("    And the target temperature is automatically set to 50°C");
    		} else {
    			this.logMessage("     but mode or temperature were incorrect: " + mode + ", " + temp);
    			this.statistics.incorrectResult();
    		}
    	} catch (Throwable e) {
    		this.logMessage("     but an exception was raised: " + e);
    		this.statistics.incorrectResult();
    	}
    	this.statistics.updateStatistics();

    	this.logMessage("  Scenario: changing the mode to GRILL when the oven is on");
    	this.logMessage("    Given the oven is switched on");
    	this.logMessage("    And the current mode is DEFROST");
    	this.logMessage("    When I change the mode to GRILL");
    	try {
    		this.oop.setMode(Oven.OvenMode.GRILL);
    		Oven.OvenMode mode = this.oop.getMode();
    		Measure<Double> temp = this.oop.getTargetTemperature();
    		if (mode == Oven.OvenMode.GRILL &&
    			temp.getData() == 220.0) {
    			this.logMessage("    Then the oven mode becomes GRILL");
    			this.logMessage("    And the target temperature is automatically set to 220°C");
    		} else {
    			this.logMessage("     but mode or temperature were incorrect: " + mode + ", " + temp);
    			this.statistics.incorrectResult();
    		}
    	} catch (Throwable e) {
    		this.logMessage("     but an exception was raised: " + e);
    		this.statistics.incorrectResult();
    	}
    	this.statistics.updateStatistics();

    	this.logMessage("  Scenario: changing the mode back to CUSTOM when the oven is on");
    	this.logMessage("    Given the oven is switched on");
    	this.logMessage("    And the current mode is GRILL");
    	this.logMessage("    When I change the mode to CUSTOM");
    	try {
    		this.oop.setMode(Oven.OvenMode.CUSTOM);
    		Oven.OvenMode mode = this.oop.getMode();
    		if (mode == Oven.OvenMode.CUSTOM) {
    			this.logMessage("    Then the oven mode becomes CUSTOM");
    			this.logMessage("    And the target temperature can now be set manually");
    		} else {
    			this.logMessage("     but mode was incorrect: " + mode);
    			this.statistics.incorrectResult();
    		}
    	} catch (Throwable e) {
    		this.logMessage("     but an exception was raised: " + e);
    		this.statistics.incorrectResult();
    	}
    	this.statistics.updateStatistics();
    	
    	// Turn off at the end of the test
        try {
            this.oop.switchOff();
        } catch (Throwable e) {
            this.logMessage("     but could not switch off the oven: " + e);
            this.statistics.incorrectResult();
        }
    }


    /**
     * test getting and setting the target temperature of the oven.
     * 
     * <p><strong>Description</strong></p>
     * 
     * <p>Gherkin specification</p>
     * <pre>
     * Feature: getting and setting the target temperature of the oven
     * 
     *   Scenario: getting the target temperature through the user interface when just initialised
     *     Given the oven is initialised
     *     And the oven has not been used yet
     *     And the oven is on
     *     When I get the target temperature through the user interface
     *     Then the target temperature of the oven is 0°C
     * 
     *   Scenario: getting the target temperature through the internal control interface when just initialised
     *     Given the oven is initialised
     *     And the oven has not been used yet
     *     And the oven is on
     *     When I get the target temperature through the internal control interface
     *     Then the target temperature of the oven is 0°C
     * 
     *   Scenario: setting the target temperature of the oven in CUSTOM mode
     *     Given the oven is initialised
     *     And the oven is on
     *     And the oven mode is CUSTOM
     *     When I set the target temperature to 180°C
     *     Then the target temperature of the oven becomes 180°C
     *     
     *   Scenario: setting an invalid target temperature in CUSTOM mode
	 *     Given the oven is initialised
	 *     And the oven is on
	 *     And the oven mode is CUSTOM
	 *     When I try to set a temperature outside the valid range (below minimum or above maximum)
	 *     Then a PreconditionException is raised
     * 
     *   Scenario: setting the target temperature of the oven in DEFROST (or GRILL) mode
     *     Given the oven is initialised
     *     And the oven is on
     *     And the oven mode is DEFROST (or GRILL)
     *     When I try to set the target temperature manually
     *     Then a PreconditionException is raised
     * </pre>
     * 
     * <p><strong>Contract</strong></p>
     * 
     * <pre>
     * pre  {@code true}  // no precondition.
     * post {@code true}  // no postcondition.
     * </pre>
     */
    protected void testTargetTemperature() {
        this.logMessage("Feature: getting and setting the target temperature of the oven");
        this.logMessage("  Scenario: getting the target temperature through the user interface when just initialised");
        this.logMessage("    Given the oven is initialised");
        this.logMessage("    And the oven has not been used yet");
        this.logMessage("    And the oven is on");
        try {
            this.oop.switchOn();
            boolean result = this.oop.on();
            if (!result) {
                this.statistics.failedCondition();
                this.logMessage("     but was: off");
            }
            this.logMessage("    When I get the target temperature through the user interface");
            Measure<Double> temperature = this.oop.getTargetTemperature();
            if (temperature.getData() == 0.0) {
                this.logMessage("    Then the target temperature of the oven is 0°C");
            } else {
                this.logMessage("     but was: " + temperature.getData());
                this.statistics.incorrectResult();
            }
        } catch (Throwable e) {
            this.statistics.incorrectResult();
            this.logMessage("     but the exception " + e + " has been raised");
        }
        this.statistics.updateStatistics();

        this.logMessage("  Scenario: getting the target temperature through the internal control interface when just initialised");
        this.logMessage("    Given the oven is initialised");
        this.logMessage("    And the oven has not been used yet");
        this.logMessage("    And the oven is on");
        try {
            this.logMessage("    When I get the target temperature through the internal control interface");
            Measure<Double> temperature = this.oicop.getTargetTemperature();
            if (temperature.getData() == 0.0) {
                this.logMessage("    Then the target temperature of the oven is 0°C");
            } else {
                this.logMessage("     but was: " + temperature.getData());
                this.statistics.incorrectResult();
            }
        } catch (Throwable e) {
            this.statistics.incorrectResult();
            this.logMessage("     but the exception " + e + " has been raised");
        }
        this.statistics.updateStatistics();

        this.logMessage("  Scenario: setting the target temperature of the oven in CUSTOM mode");
        this.logMessage("    Given the oven is initialised");
        this.logMessage("    And the oven is on");
        this.logMessage("    And the oven mode is CUSTOM");
        try {
            Measure<Double> newTemp = new Measure<>(180.0, Oven.TEMPERATURE_UNIT);
            this.logMessage("    When I set the target temperature to 180°C");
            this.oop.setTargetTemperature(newTemp);
            Measure<Double> currentTemp = this.oop.getTargetTemperature();
            if (currentTemp.getData() == 180.0) {
                this.logMessage("    Then the target temperature of the oven becomes 180°C");
            } else {
                this.logMessage("     but was: " + currentTemp.getData());
                this.statistics.incorrectResult();
            }
        } catch (Throwable e) {
            this.statistics.incorrectResult();
            this.logMessage("     but the exception " + e + " has been raised");
        }
        this.statistics.updateStatistics();
        
        this.logMessage("  Scenario: setting an invalid target temperature in CUSTOM mode");
        this.logMessage("    When I try to set a temperature outside the valid range (below minimum or above maximum)");
        this.logMessage("    Then a PreconditionException is raised");
        boolean old = BCMException.VERBOSE;
        try {
            BCMException.VERBOSE = false; 
            Measure<Double> invalidTemp = new Measure<>(Oven.MAX_TARGET_TEMPERATURE.getData() + 100.0, Oven.TEMPERATURE_UNIT);
            this.oop.setTargetTemperature(invalidTemp);
            this.logMessage("     but no exception was raised");
            this.statistics.incorrectResult();
        } catch (Throwable e) {
            
        } finally {
            BCMException.VERBOSE = old;
        }
        this.statistics.updateStatistics();
        
        this.logMessage("  Scenario: setting the target temperature of the oven in DEFROST");
        this.logMessage("    Given the oven is initialised");
        this.logMessage("    And the oven is on");
        this.logMessage("    And the oven mode is DEFROST");
        this.logMessage("    When I try to set the target temperature manually");
        this.logMessage("    Then a PreconditionException is raised");

        old = BCMException.VERBOSE;
        try {
            BCMException.VERBOSE = false;
            this.oop.setMode(Oven.OvenMode.DEFROST);
            this.oop.setTargetTemperature(new Measure<>(100.0, Oven.TEMPERATURE_UNIT));
            this.logMessage("     but no exception was raised");
            this.statistics.incorrectResult();
        } catch (Throwable e) {

        } finally {
            BCMException.VERBOSE = old;
        }
        this.statistics.updateStatistics();

        // Turn off at the end of the test
        try {
            this.oop.switchOff();
        } catch (Throwable e) {
            this.logMessage("     but could not switch off the oven: " + e);
            this.statistics.incorrectResult();
        }
    }


    /**
     * test getting the current temperature of the oven.
     * 
     * <p><strong>Description</strong></p>
     * 
     * <p>Gherkin specification</p>
     * <pre>
     * Feature: getting the current temperature of the oven
     * 
     *   Scenario: getting the current temperature through the user interface when on
     *     Given the oven is initialised
     *     And the oven has not been used yet
     *     And the oven is switched on
     *     When I get the current temperature through the user interface
     *     Then the current temperature is the oven fake current temperature
     * 
     *   Scenario: getting the current temperature through the internal control interface when on
     *     Given the oven is initialised
     *     And the oven has not been used yet
     *     And the oven is switched on
     *     When I get the current temperature through the internal control interface
     *     Then the current temperature is the oven fake current temperature
     * 
     *   Scenario: getting the current temperature when the oven is off
     *     Given the oven is initialised
     *     And the oven is currently off
     *     When I try to get the current temperature
     *     Then a PreconditionException is raised
     * </pre>
     * 
     * <p><strong>Contract</strong></p>
     * 
     * <pre>
     * pre  {@code true}  // no precondition.
     * post {@code true}  // no postcondition.
     * </pre>
     */
    protected void testCurrentTemperature() {
        boolean result;
        SignalData<Double> temperature = null;
        
        this.logMessage("Feature: getting the current temperature of the oven");
        this.logMessage("  Scenario: getting the current temperature through the user interface when on");
        this.logMessage("    Given the oven is initialised");
        this.logMessage("    And the oven has not been used yet");
        this.logMessage("    And the oven is switched on");
        try {
            this.oop.switchOn();
            result = this.oop.on();
            if (!result) {
                this.logMessage("     but was: off");
                this.statistics.failedCondition();
            }
            this.logMessage("    When I get the current temperature through the user interface");
            temperature = this.oop.getCurrentTemperature();
            if (temperature.getMeasure().getData() ==
                    Oven.FAKE_CURRENT_TEMPERATURE.getMeasure().getData() &&
                temperature.getMeasure().getMeasurementUnit().equals(
                    Oven.FAKE_CURRENT_TEMPERATURE.getMeasure().getMeasurementUnit())) {
                this.logMessage("    Then the current temperature is the oven fake current temperature");
            } else {
                this.logMessage("     but was: " + temperature.getMeasure().getData());
                this.statistics.incorrectResult();
            }
        } catch (Throwable e) {
            this.statistics.incorrectResult();
            this.logMessage("     but the exception " + e + " has been raised");
        }
        this.statistics.updateStatistics();

        this.logMessage("  Scenario: getting the current temperature through the internal control interface when on");
        this.logMessage("    Given the oven is initialised");
        this.logMessage("    And the oven has not been used yet");
        this.logMessage("    And the oven is switched on");
        try {
            this.logMessage("    When I get the current temperature through the internal control interface");
            temperature = this.oicop.getCurrentTemperature();
            if (temperature.getMeasure().getData() ==
                    Oven.FAKE_CURRENT_TEMPERATURE.getMeasure().getData() &&
                temperature.getMeasure().getMeasurementUnit().equals(
                    Oven.FAKE_CURRENT_TEMPERATURE.getMeasure().getMeasurementUnit())) {
                this.logMessage("    Then the current temperature is the oven fake current temperature");
            } else {
                this.statistics.incorrectResult();
                this.logMessage("     but was: " + temperature.getMeasure().getData());
            }
        } catch (Throwable e) {
            this.statistics.incorrectResult();
            this.logMessage("     but the exception " + e + " has been raised");
        }
        this.statistics.updateStatistics();

        this.logMessage("  Scenario: getting the current temperature when the oven is off");
        this.logMessage("    Given the oven is initialised");
        this.logMessage("    And the oven is currently off");
        this.logMessage("    When I try to get the current temperature");
        this.logMessage("    Then a PreconditionException is raised");
        boolean old = BCMException.VERBOSE;
        try {
            this.oop.switchOff();
            BCMException.VERBOSE = false;
            this.oop.getCurrentTemperature();
            this.logMessage("     but no exception was raised");
            this.statistics.incorrectResult();
        } catch (Throwable e) {
            
        } finally {
            BCMException.VERBOSE = old;
        }
        this.statistics.updateStatistics();
    }


    /**
     * test getting and setting the power level of the oven.
     * 
     * <p><strong>Description</strong></p>
     * 
     * <p>Gherkin specification</p>
     * <pre>
     * Feature: getting and setting the power level of the oven
     *   Scenario: getting the maximum power level through the user interface
     *     Given the oven is initialised
     *     When I get the maximum power level through the user interface
     *     Then the result is the oven maximum power level
     * 
     *   Scenario: getting the maximum power level through the external control interface
     *     Given the oven is initialised
     *     When I get the maximum power level through the external control interface
     *     Then the result is the oven maximum power level
     * 
     *   Scenario: getting the current power level through the user interface when just initialised
     *     Given the oven is initialised
     *     And the oven has not been used yet
     *     And the oven is on
     *     When I get the current power level through the user interface
     *     Then the result is the oven initial power level (0.0 W)
     * 
     *   Scenario: getting the current power level through the external control interface when just initialised
     *     Given the oven is initialised
     *     And the oven has not been used yet
     *     And the oven is on
     *     When I get the current power level through the external control interface
     *     Then the result is the oven initial power level (0.0 W)
     * 
     *   Scenario: setting the power level to a given level between 0 and the maximum power level through the user interface
     *     Given the oven is initialised
     *     And the oven is on
     *     When I set the current power level through the user interface to a given level between 0 and the maximum power level
     *     Then the current power level is the given power level
     * 
     *   Scenario: setting the power level to a given level over the maximum power level through the user interface
     *     Given the oven is initialised
     *     And the oven is on
     *     When I set the current power level through the user interface to a given level over the maximum power level
     *     Then the current power level is the oven maximum power level
     * 
     *   Scenario: setting the power level to a given level between 0 and the maximum power level through the external control interface
     *     Given the oven is initialised
     *     And the oven is on
     *     When I set the current power level through the external control interface to a given level between 0 and the maximum power level
     *     Then the current power level is the given power level
     * 
     *   Scenario: setting the power level to a given level over the maximum power level through the external control interface
	 *     Given the heater is initialised
	 *     And the heater is on
	 *     When I set the current power level through the external control interface to a given level over the maximum power level
	 *     Then the current power level is the maximum power level
     * 
     *   Scenario: setting the power level when the oven is off
     *     Given the oven is initialised
     *     And the oven is off
     *     When I try to set the power level
     *     Then a PreconditionException is raised
     * </pre>
     * 
     * <p><strong>Contract</strong></p>
     * 
     * <pre>
     * pre  {@code true}  // no precondition.
     * post {@code true}  // no postcondition.
     * </pre>
     */
    protected void testPowerLevel() {        
        Measure<Double> powerLevel = null;
        SignalData<Double> powerLevelSignal = null;
        boolean result;
        
        this.logMessage("Feature: getting and setting the power level of the oven");
        this.logMessage("  Scenario: getting the maximum power level through the user interface");
        this.logMessage("    Given the oven is initialised");
        try {
            this.logMessage("    When I get the maximum power level through the user interface");
            powerLevel = this.oop.getMaxPowerLevel();
            if (powerLevel.getData() == Oven.MAX_POWER_LEVEL.getData() &&
                powerLevel.getMeasurementUnit().equals(Oven.MAX_POWER_LEVEL.getMeasurementUnit())) {
                this.logMessage("    Then the result is the oven maximum power level");
            } else {
                this.logMessage("     but was: " + powerLevel.getData());
                this.statistics.incorrectResult();
            }
        } catch (Throwable e) {
            this.logMessage("     but the exception " + e + " has been raised");
            this.statistics.incorrectResult();
        }
        this.statistics.updateStatistics();

        this.logMessage("  Scenario: getting the maximum power level through the external control interface");
        this.logMessage("    Given the oven is initialised");
        try {
            this.logMessage("    When I get the maximum power level through the external control interface");
            powerLevel = this.oecop.getMaxPowerLevel();
            if (powerLevel.getData() == Oven.MAX_POWER_LEVEL.getData() &&
                powerLevel.getMeasurementUnit().equals(Oven.MAX_POWER_LEVEL.getMeasurementUnit())) {
                this.logMessage("    Then the result is the oven maximum power level");
            } else {
                this.logMessage("     but was: " + powerLevel.getData());
                this.statistics.incorrectResult();
            }
        } catch (Throwable e) {
            this.logMessage("     but the exception " + e + " has been raised");
            this.statistics.incorrectResult();
        }
        this.statistics.updateStatistics();

        this.logMessage("  Scenario: getting the current power level through the user interface when just initialised");
        this.logMessage("    Given the oven is initialised");
        this.logMessage("    And the oven has not been used yet");
        this.logMessage("    And the oven is on");
        try {
            this.oop.switchOn();
            result = this.oop.on();
            if (!result) {
                this.logMessage("     but was: off");
                this.statistics.failedCondition();
            }
            this.logMessage("    When I get the current power level through the user interface");
            powerLevelSignal = this.oop.getCurrentPowerLevel();
            if (powerLevelSignal.getMeasure().getData() == 0.0 &&
                powerLevelSignal.getMeasure().getMeasurementUnit().equals(Oven.POWER_UNIT)) {
                this.logMessage("    Then the result is the oven initial power level (0.0 W)");
            } else {
                this.logMessage("     but was: " + powerLevelSignal.getMeasure().getData());
                this.statistics.incorrectResult();
            }
        } catch (Throwable e) {
            this.logMessage("     but the exception " + e + " has been raised");
            this.statistics.incorrectResult();
        }
        this.statistics.updateStatistics();

        this.logMessage("  Scenario: getting the current power level through the external control interface when just initialised");
        this.logMessage("    Given the oven is initialised");
        this.logMessage("    And the oven has not been used yet");
        this.logMessage("    And the oven is on");
        try {
            this.logMessage("    When I get the current power level through the external control interface");
            powerLevelSignal = this.oecop.getCurrentPowerLevel();
            if (powerLevelSignal.getMeasure().getData() == 0.0 &&
                powerLevelSignal.getMeasure().getMeasurementUnit().equals(Oven.POWER_UNIT)) {
                this.logMessage("    Then the result is the oven initial power level (0.0 W)");
            } else {
                this.logMessage("     but was: " + powerLevelSignal.getMeasure().getData());
                this.statistics.incorrectResult();
            }
        } catch (Throwable e) {
            this.logMessage("     but the exception " + e + " has been raised");
            this.statistics.incorrectResult();
        }
        this.statistics.updateStatistics();

        this.logMessage("  Scenario: setting the power level through the user interface to a valid level");
        this.logMessage("    Given the oven is initialised");
        this.logMessage("    And the oven is on");
        try {
            this.oop.setCurrentPowerLevel(new Measure<>(Oven.MAX_POWER_LEVEL.getData() / 2.0, Oven.POWER_UNIT));
            powerLevelSignal = this.oop.getCurrentPowerLevel();
            if (powerLevelSignal.getMeasure().getData() == Oven.MAX_POWER_LEVEL.getData() / 2.0) {
                this.logMessage("    Then the current power level is the given level");
            } else {
                this.logMessage("     but was: " + powerLevelSignal.getMeasure().getData());
                this.statistics.incorrectResult();
            }
        } catch (Throwable e) {
            this.logMessage("     but the exception " + e + " has been raised");
            this.statistics.incorrectResult();
        }
        this.statistics.updateStatistics();

        this.logMessage("  Scenario: setting the power level above the maximum through the user interface");
        this.logMessage("    Given the oven is initialised");
        this.logMessage("    And the oven is on");
        try {
            this.oop.setCurrentPowerLevel(new Measure<>(Oven.MAX_POWER_LEVEL.getData() + 500.0, Oven.POWER_UNIT));
            powerLevelSignal = this.oop.getCurrentPowerLevel();
            if (powerLevelSignal.getMeasure().getData() == Oven.MAX_POWER_LEVEL.getData()) {
                this.logMessage("    Then the current power level is the oven maximum power level");
            } else {
                this.logMessage("     but was: " + powerLevelSignal.getMeasure().getData());
                this.statistics.incorrectResult();
            }
        } catch (Throwable e) {
            this.logMessage("     but the exception " + e + " has been raised");
            this.statistics.incorrectResult();
        }
        this.statistics.updateStatistics();
        
        
        this.logMessage("  Scenario: setting the power level through the external control interface to a valid level");
        this.logMessage("    Given the oven is initialised");
        this.logMessage("    And the oven is on");
        try {
            this.oecop.setCurrentPowerLevel(new Measure<>(Oven.MAX_POWER_LEVEL.getData() / 2.0, Oven.POWER_UNIT));
            powerLevelSignal = this.oecop.getCurrentPowerLevel();
            if (powerLevelSignal.getMeasure().getData() == Oven.MAX_POWER_LEVEL.getData() / 2.0) {
                this.logMessage("    Then the current power level is the given level");
            } else {
                this.logMessage("     but was: " + powerLevelSignal.getMeasure().getData());
                this.statistics.incorrectResult();
            }
        } catch (Throwable e) {
            this.logMessage("     but the exception " + e + " has been raised");
            this.statistics.incorrectResult();
        }
        this.statistics.updateStatistics();

        this.logMessage("  Scenario: setting the power level above the maximum through the external control interface");
        this.logMessage("    Given the oven is initialised");
        this.logMessage("    And the oven is on");
        try {
            this.oecop.setCurrentPowerLevel(new Measure<>(Oven.MAX_POWER_LEVEL.getData() + 500.0, Oven.POWER_UNIT));
            powerLevelSignal = this.oecop.getCurrentPowerLevel();
            if (powerLevelSignal.getMeasure().getData() == Oven.MAX_POWER_LEVEL.getData()) {
                this.logMessage("    Then the current power level is the oven maximum power level");
            } else {
                this.logMessage("     but was: " + powerLevelSignal.getMeasure().getData());
                this.statistics.incorrectResult();
            }
        } catch (Throwable e) {
            this.logMessage("     but the exception " + e + " has been raised");
            this.statistics.incorrectResult();
        }
        this.statistics.updateStatistics();

        this.logMessage("  Scenario: setting the power level when the oven is off");
        this.logMessage("    Given the oven is initialised");
        this.logMessage("    And the oven is off");
        this.logMessage("    When I try to set the power level");
        this.logMessage("    Then a PreconditionException is raised");
        boolean old = BCMException.VERBOSE;
        try {
            this.oop.switchOff();
            BCMException.VERBOSE = false;
            this.oop.setCurrentPowerLevel(new Measure<>(500.0, Oven.POWER_UNIT));
            this.logMessage("     but no exception was raised");
            this.statistics.incorrectResult();
        } catch (Throwable e) {
        
        } finally {
            BCMException.VERBOSE = old;
        }
        this.statistics.updateStatistics();
    }
    
    /**
     * test the oven cooking cycle: immediate and delayed starts, stopping programmed and ongoing cooking.
     * 
     * <p><strong>Description</strong></p>
     * 
     * <p>Gherkin specification</p>
     * <pre>
     * Feature: managing the oven cooking cycle
     * 
     *   Scenario: starting cooking when the oven is off
     *     Given the oven is initialised
     *     And the oven is currently off
     *     When I try to start cooking with no delay
     *     Then a PreconditionException is raised
     * 
     *   Scenario: starting cooking with a delay when the oven is off
     *     Given the oven is initialised
     *     And the oven is currently off
     *     When I try to start cooking with delay of 10 seconds
     *     Then a PreconditionException is raised
     * 
     *   Scenario: starting cooking immediately when the oven is on
     *     Given the oven is switched on
     *     When I start cooking
     *     Then the oven state becomes HEATING
     * 
     *   Scenario: starting cooking with a delay when already heating
     *     Given the oven is switched on
     *     And the oven is currently heating
     *     When I try to start cooking with delay of 5 seconds
     *     Then a PreconditionException is raised
     * 
     *   Scenario: stopping the cooking
     *     Given the oven is currently heating
     *     When I stop the cooking
     *     Then the oven stops heating and its state becomes on
     * 
     *   Scenario: starting cooking with an invalid delay
     *     Given the oven is on
     *     When I start cooking with delay of 0 seconds
     *     Then a PreconditionException is raised 
     *
     *   Scenario: starting cooking with a delay
     *     Given the oven is switched on
     *     And the oven is not heating
     *     When I start cooking with delay of 5 minutes
     *     Then the oven state becomes WAITING
     * 
     *   Scenario: stopping a programmed cooking
     *     Given the oven is waiting
     *     When I cancel the delayed start
     *     Then the oven state becomes ON
     * 
     *   Scenario: stopping a programmed cooking when not waiting
     *     Given the oven is on
     *     When I try to cancel a delayed start
     *     Then a PreconditionException is raised
     * 
     *   Scenario: switch off when a cooking is programmed
     *     Given the oven is waiting
     *     When I switch off the oven
     *     Then the oven state becomes OFF
     * 
     *   Scenario: switch off when cooking
     *     Given the oven is switched heating
     *     When I switch off the oven
     *     Then the oven stops heating and its state becomes OFF
     * </pre>
     * 
     * <p><strong>Contract</strong></p>
     * <pre>
     * pre  {@code true}  // no precondition.
     * post {@code true}  // no postcondition.
     * </pre>
     */
    protected void testCookingCycle() {
        this.logMessage("Feature: managing the oven heating cycle");
        this.logMessage("  Scenario: starting cooking when the oven is off");
        this.logMessage("    Given the oven is initialised and off");
        this.logMessage("    When I try to start cooking with no delay");
        this.logMessage("    Then a precondition exception is raised");
        boolean old = BCMException.VERBOSE;
        try {
        	BCMException.VERBOSE = false;
            this.oop.startCooking();
            this.statistics.incorrectResult();
            this.logMessage("     but no exception was raised");
        } catch (Throwable e) {

        } finally {
        	BCMException.VERBOSE = old;
        }
        this.statistics.updateStatistics();

        this.logMessage("  Scenario: starting cooking with a delay when the oven is off");
        this.logMessage("    Given the oven is initialised and off");
        this.logMessage("    When I try to start cooking with delay");
        this.logMessage("    Then a precondition exception is raised");
        old = BCMException.VERBOSE;
        try {
        	BCMException.VERBOSE = false;
            this.oop.startDelayedCooking(new Duration(10, TimeUnit.SECONDS));
            this.statistics.incorrectResult();
            this.logMessage("     but no exception was raised");
        } catch (Throwable e) {

        } finally {
        	BCMException.VERBOSE = old;
        }
        this.statistics.updateStatistics();

        this.logMessage("  Scenario: starting cooking when the oven is on");
        this.logMessage("    Given the oven is initialised and on");
        this.logMessage("    When I try to start cooking");
        this.logMessage("    Then the oven state becomes HEATING");
        try {
            this.oop.switchOn();
            this.oop.startCooking();
            Oven.OvenState s = this.oop.getState();
            if (s != Oven.OvenState.HEATING) {
                this.logMessage("     but was: " + s);
                this.statistics.incorrectResult();
            }
        } catch (Throwable e) {
            this.statistics.incorrectResult();
            this.logMessage("     but the exception " + e + " has been raised");
        }
        this.statistics.updateStatistics();

        this.logMessage("  Scenario: starting cooking when already heating");
        this.logMessage("    Given the oven is initialised and heating");
        this.logMessage("    When I try to start cooking");
        this.logMessage("    Then a precondition exception is raised");
        old = BCMException.VERBOSE;
        BCMException.VERBOSE = false;
        try {
            this.oop.startCooking();
            this.logMessage("     but no exception was raised");
            this.statistics.incorrectResult();
        } catch (Throwable e) {
            // Expected
        } finally {
        	BCMException.VERBOSE = old;
        }
        this.statistics.updateStatistics();

        this.logMessage("  Scenario: stopping the cooking");
        this.logMessage("    Given the oven is initialised and heating");
        this.logMessage("    When I try to stop the cooking");
        this.logMessage("    Then the oven stops heating and its state becomes ON");
        try {
            this.oop.stopCooking();
            Oven.OvenState s = this.oop.getState();
            if (s != Oven.OvenState.ON && this.oicop.heating()) {
            	this.logMessage("     but was: " + s);
                this.statistics.incorrectResult();
            }
        } catch (Throwable e) {
            this.statistics.incorrectResult();
            this.logMessage("     but the exception " + e + " has been raised");
        }
        this.statistics.updateStatistics();

        this.logMessage("  Scenario: starting cooking with an invalid delay");
        this.logMessage("    Given the oven is initialised and on");
        this.logMessage("    When I try to start the cooking with 0sec delay");
        this.logMessage("    Then a precondition exception is raised");
        old = BCMException.VERBOSE;
        BCMException.VERBOSE = false;
        try {
            this.oop.startDelayedCooking(new Duration(0, TimeUnit.SECONDS));
            this.statistics.incorrectResult();
            this.logMessage("     but no exception was raised");
        } catch (Throwable e) {
            
        } finally {
            BCMException.VERBOSE = old;
        }
        this.statistics.updateStatistics();

        this.logMessage("  Scenario: starting cooking with a delay");
        this.logMessage("    Given the oven is initialised and on");
        this.logMessage("    When I try to start the cooking with 5min delay");
        this.logMessage("    Then the oven state becomes WAITING");
        try {
            this.oop.startDelayedCooking(new Duration(5, TimeUnit.MINUTES));
            Oven.OvenState s = this.oop.getState();
            if (s != Oven.OvenState.WAITING) {
            	this.logMessage("     but was: " + s);
                this.statistics.incorrectResult();
            }
        } catch (Throwable e) {
            this.statistics.incorrectResult();
            this.logMessage("     but the exception " + e + " has been raised");
        }
        this.statistics.updateStatistics();

        this.logMessage("  Scenario: stopping a delayed start");
        this.logMessage("    Given the oven is initialised and waiting");
        this.logMessage("    When I try to stop the programmed cooking");
        this.logMessage("    Then the oven state becomes ON");
        try {
            this.oop.stopCooking();
            Oven.OvenState s = this.oop.getState();
            if (s != Oven.OvenState.ON) {
            	this.logMessage("     but was: " + s);
                this.statistics.incorrectResult();
            }
        } catch (Throwable e) {
            this.statistics.incorrectResult();
            this.logMessage("     but the exception " + e + " has been raised");
        }
        this.statistics.updateStatistics();

        this.logMessage("  Scenario: stopping a cooking when not cooking nor programmed");
        this.logMessage("    Given the oven is initialised and on");
        this.logMessage("    When I try to stop the cooking");
        this.logMessage("    Then a precondition exception is raised");
        old = BCMException.VERBOSE;
        BCMException.VERBOSE = false;
        try {
            this.oop.stopCooking();
            this.statistics.incorrectResult();
            this.logMessage("     but no exception was raised");
        } catch (Throwable e) {
            
        } finally {
    		BCMException.VERBOSE = old;
        }
        this.statistics.updateStatistics();

        this.logMessage("  Scenario: switch off when a cooking is programmed");
        try {
            this.oop.startDelayedCooking(new Duration(5, TimeUnit.SECONDS));
            this.logMessage("    Given the oven is initialised and waiting");
            this.logMessage("    When I try to turn off the oven");
            this.logMessage("    Then the oven state is OFF");
            this.oop.switchOff();
            Oven.OvenState s = this.oop.getState();
            if (s != Oven.OvenState.OFF) {
            	this.logMessage("     but was: " + s);
                this.statistics.incorrectResult();
            }
        } catch (Throwable e) {
            this.statistics.incorrectResult();
            this.logMessage("     but the exception " + e + " has been raised");
        }
        this.statistics.updateStatistics();

        this.logMessage("  Scenario: switch off when a cooking is ongoing");
        try {
            this.oop.switchOn();
            this.oop.startCooking();
            this.logMessage("    Given the oven is initialised and waiting");
            this.logMessage("    When I try to turn off the oven");
            this.logMessage("    Then the oven stops heating and its state becomes OFF");
            this.oop.switchOff();
            Oven.OvenState s = this.oop.getState();
            if (s != Oven.OvenState.OFF && this.oicop.heating()) {
            	this.logMessage("     but was: " + s);
                this.statistics.incorrectResult();
            }
        } catch (Throwable e) {
            this.statistics.incorrectResult();
            this.logMessage("     but the exception " + e + " has been raised");
        }
        this.statistics.updateStatistics();
    }
    
    /**
     * Test opening and closing the oven door.
     * 
     * <p><strong>Description</strong></p>
     * 
     * <p>Gherkin specification</p>
     * <pre>
     * Feature: opening and closing the oven door
     * 
     *   Scenario: opening the oven door when closed
     *     Given the oven is initialised
     *     And the oven door is closed
     *     When I open the oven door
     *     Then the oven door is open
     * 
     *   Scenario: closing the oven door when open
     *     Given the oven door is open
     *     When I close the oven door
     *     Then the oven door is closed
     * 
     *   Scenario: closing an already closed oven door
     *     Given the oven door is closed
     *     When I try to close the oven door
     *     Then an exception is raised
     * 
     *   Scenario: opening an already open oven door
     *     Given the oven door is open
     *     When I try to open the oven door
     *     Then an exception is raised
     * </pre>
     * 
     * <p><strong>Contract</strong></p>
     * 
     * <pre>
     * pre  {@code true}  // no precondition.
     * post {@code true}  // no postcondition.
     * </pre>
     */
    protected void testOpenCloseDoor() {
    	this.logMessage("Feature: opening and closing the oven door");

    	try {
    		this.logMessage("  Scenario: opening the oven door when closed");
    		this.logMessage("    Given the oven is initialised");
    		this.logMessage("    And the oven door is closed");

    		boolean doorOpen = this.oop.isDoorOpen();
    		if (doorOpen) {
    			this.statistics.incorrectResult();
    			this.logMessage("     but the door was already open");
    		}

    		this.logMessage("    When I open the oven door");
    		this.oop.openDoor();

    		doorOpen = this.oop.isDoorOpen();
    		if (doorOpen) {
    			this.logMessage("    Then the oven door is open");
    		} else {
    			this.statistics.incorrectResult();
    			this.logMessage("     but the door is still closed");
    		}

    		this.statistics.updateStatistics();

    		this.logMessage("  Scenario: closing the oven door when open");
    		this.logMessage("    Given the oven door is open");

    		this.logMessage("    When I close the oven door");
    		this.oop.closeDoor();

    		doorOpen = this.oop.isDoorOpen();
    		if (!doorOpen) {
    			this.logMessage("    Then the oven door is closed");
    		} else {
    			this.statistics.incorrectResult();
    			this.logMessage("     but the door is still open");
    		}

    		this.statistics.updateStatistics();
    		
    		this.logMessage("  Scenario: closing an already closed oven door");
    		this.logMessage("    Given the oven door is closed");

    		this.logMessage("    When I try to close the oven door");
    		try {
    			this.oop.closeDoor();
    			this.statistics.incorrectResult();
    			this.logMessage("     but no exception was raised");
    		} catch (Exception e) {
    			this.logMessage("    Then an exception is raised as expected");
    		}

    		this.statistics.updateStatistics();
    		
    		this.logMessage("  Scenario: opening an already open oven door");
    		this.logMessage("    Given the oven door is open");

    		this.oop.openDoor();

    		this.logMessage("    When I try to open the oven door");
    		try {
    			this.oop.openDoor();
    			this.statistics.incorrectResult();
    			this.logMessage("     but no exception was raised");
    		} catch (Exception e) {
    			this.logMessage("    Then an exception is raised as expected");
    		}

    		this.statistics.updateStatistics();

    	} catch (Throwable e) {
    		this.statistics.incorrectResult();
    		this.logMessage("     but the exception " + e + " has been raised");
    	}

    	this.statistics.updateStatistics();
    }


    protected void runAllUnitTests()
    {
        this.testInitialStateAndMode();     
        this.testTargetTemperature();
        this.testSwitchOnSwitchOff();
        this.testModeChanges();
        this.testCurrentTemperature();
        this.testPowerLevel();
        this.testCookingCycle();
        this.testOpenCloseDoor();

        this.statistics.statisticsReport(this);
    }

    // -------------------------------------------------------------------------
    // Component life-cycle
    // -------------------------------------------------------------------------
    @Override
    public synchronized void start() throws ComponentStartException
    {
        super.start();
        try {
            this.doPortConnection(this.oop.getPortURI(),
                                  this.ovenUserInboundPortURI,
                                  OvenUserConnector.class.getCanonicalName());
            this.doPortConnection(this.oicop.getPortURI(),
                                  this.ovenInternalControlInboundPortURI,
                                  OvenInternalControlConnector.class.getCanonicalName());
            this.doPortConnection(this.oecop.getPortURI(),
                                  this.ovenExternalControlInboundPortURI,
                                  OvenExternalControlConnector.class.getCanonicalName());
        } catch (Throwable e) {
            throw new ComponentStartException(e);
        }
    }

    @Override
    public synchronized void execute() throws Exception
    {
        if (this.isUnitTest) {
            this.runAllUnitTests();
        } else {
            ClocksServerOutboundPort clocksServerOutboundPort = new ClocksServerOutboundPort(this);
            clocksServerOutboundPort.publishPort();
            this.doPortConnection(clocksServerOutboundPort.getPortURI(),
                                  ClocksServer.STANDARD_INBOUNDPORT_URI,
                                  ClocksServerConnector.class.getCanonicalName());
            this.traceMessage("Oven tester gets the clock.\n");
            AcceleratedClock ac = clocksServerOutboundPort.getClock(CVMIntegrationTest.CLOCK_URI);
            this.doPortDisconnection(clocksServerOutboundPort.getPortURI());
            clocksServerOutboundPort.unpublishPort();
            clocksServerOutboundPort = null;

            Instant startInstant = ac.getStartInstant();
            Instant ovenSwitchOn = startInstant.plusSeconds(SWITCH_ON_DELAY);
            Instant ovenSwitchOff = startInstant.plusSeconds(SWITCH_OFF_DELAY);
            this.traceMessage("Oven tester waits until start.\n");
            ac.waitUntilStart();
            this.traceMessage("Oven tester schedules switch on and off.\n");
            long delayToSwitchOn = ac.nanoDelayUntilInstant(ovenSwitchOn);
            long delayToSwitchOff = ac.nanoDelayUntilInstant(ovenSwitchOff);

            AbstractComponent o = this;

            this.scheduleTaskOnComponent(
                    new AbstractComponent.AbstractTask() {
                        @Override
                        public void run() {
                            try {
                                o.traceMessage("Oven switches on.\n");
                                oop.switchOn();
                            } catch (Throwable e) {
                                e.printStackTrace();
                            }
                        }
                    }, delayToSwitchOn, TimeUnit.NANOSECONDS);

            this.scheduleTaskOnComponent(
                    new AbstractComponent.AbstractTask() {
                        @Override
                        public void run() {
                            try {
                                o.traceMessage("Oven switches off.\n");
                                oop.switchOff();
                            } catch (Throwable e) {
                                e.printStackTrace();
                            }
                        }
                    }, delayToSwitchOff, TimeUnit.NANOSECONDS);
        }
    }

    @Override
    public synchronized void finalise() throws Exception
    {
        this.doPortDisconnection(this.oop.getPortURI());
        this.doPortDisconnection(this.oicop.getPortURI());
        this.doPortDisconnection(this.oecop.getPortURI());
        super.finalise();
    }

    @Override
    public synchronized void shutdown() throws ComponentShutdownException
    {
        try {
            this.oop.unpublishPort();
            this.oicop.unpublishPort();
            this.oecop.unpublishPort();
        } catch (Throwable e) {
            throw new ComponentShutdownException(e);
        }
        super.shutdown();
    }
}
