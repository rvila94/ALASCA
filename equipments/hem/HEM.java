package equipments.hem;

// Copyright Jacques Malenfant, Sorbonne Universite.
// Jacques.Malenfant@lip6.fr
//
// This software is a computer program whose purpose is to implement a mock-up
// of household energy management system.
//
// This software is governed by the CeCILL-C license under French law and
// abiding by the rules of distribution of free software.  You can use,
// modify and/ or redistribute the software under the terms of the
// CeCILL-C license as circulated by CEA, CNRS and INRIA at the following
// URL "http://www.cecill.info".
//
// As a counterpart to the access to the source code and  rights to copy,
// modify and redistribute granted by the license, users are provided only
// with a limited warranty  and the software's author,  the holder of the
// economic rights,  and the successive licensors  have only  limited
// liability. 
//
// In this respect, the user's attention is drawn to the risks associated
// with loading,  using,  modifying and/or developing or reproducing the
// software by the user in light of its specific status of free software,
// that may mean  that it is complicated to manipulate,  and  that  also
// therefore means  that it is reserved for developers  and  experienced
// professionals having in-depth computer knowledge. Users are therefore
// encouraged to load and test the software's suitability as regards their
// requirements in conditions enabling the security of their systems and/or 
// data to be ensured and,  more generally, to use and operate it in the 
// same conditions as regards security. 
//
// The fact that you are presently reading this means that you have had
// knowledge of the CeCILL-C license and that you accept its terms.

import connectorGenerator.ConnectorConfigurationParser;
import equipments.HeatPump.HeatPump;
import equipments.HeatPump.Test.HeatPumpTester;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import fr.sorbonne_u.components.annotations.RequiredInterfaces;
import fr.sorbonne_u.components.exceptions.BCMRuntimeException;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import fr.sorbonne_u.components.hem2025.bases.AdjustableCI;
import fr.sorbonne_u.components.hem2025.bases.RegistrationCI;
import fr.sorbonne_u.components.hem2025.tests_utils.TestsStatistics;
import fr.sorbonne_u.components.hem2025e1.CVMIntegrationTest;
import fr.sorbonne_u.components.hem2025e1.equipments.heater.Heater;
import fr.sorbonne_u.components.hem2025e1.equipments.heater.HeaterUnitTester;
import fr.sorbonne_u.components.hem2025e1.equipments.meter.*;
import fr.sorbonne_u.exceptions.*;
import fr.sorbonne_u.utils.aclocks.*;

import java.time.Instant;
import java.util.Hashtable;
import java.util.concurrent.TimeUnit;

// -----------------------------------------------------------------------------
/**
 * The class <code>HEM</code> implements the basis for a household energy
 * management component.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p>
 * As is, this component is only a very limited starting point for the actual
 * component. The given code is there only to ease the understanding of the
 * objectives, but most of it must be replaced to get the correct code.
 * Especially, no registration of the components representing the appliances
 * is given.
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
 * invariant	{@code X_RELATIVE_POSITION >= 0}
 * invariant	{@code Y_RELATIVE_POSITION >= 0}
 * </pre>
 * 
 * <p>Created on : 2021-09-09</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
@RequiredInterfaces(required = {AdjustableCI.class, ElectricMeterCI.class,
								ClocksServerCI.class})
@OfferedInterfaces(offered = {RegistrationCI.class})
public class			HEM
extends		AbstractComponent
{
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	/** when true, methods trace their actions.								*/
	public static boolean					VERBOSE = false;
	/** when tracing, x coordinate of the window relative position.			*/
	public static int						X_RELATIVE_POSITION = 0;
	/** when tracing, y coordinate of the window relative position.			*/
	public static int						Y_RELATIVE_POSITION = 0;

	/** port to connect to the electric meter.								*/
	protected ElectricMeterOutboundPort		meterop;

	/** when true, manage the heater in a customised way, otherwise let
	 *  it register itself as an adjustable appliance.						*/
	protected boolean						isPreFirstStep;
	/** port to connect to the heater when managed in a customised way.		*/
	protected AdjustableOutboundPort heaterop;

	/** when true, this implementation of the HEM performs the tests
	 *  that are planned in the method execute.								*/
	protected boolean						performTest;
	/** accelerated clock used for the tests.								*/
	protected AcceleratedClock				ac;
	/** collector of test statistics.										*/
	protected TestsStatistics				statistics;

	/** Inbound Port used by the components to register into the hem */

	// -------------------------------------------------------------------------
	// Invariants
	// -------------------------------------------------------------------------

	/**
	 * return true if the implementation invariants are observed, false otherwise.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code hem != null}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param hem	instance to be tested.
	 * @return		true if the implementation invariants are observed, false otherwise.
	 */
	protected static boolean	implementationInvariants(HEM hem)
	{
		assert	hem != null : new PreconditionException("hem != null");

		boolean ret = true;
		return ret;
	}

	/**
	 * return true if the invariants are observed, false otherwise.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code hem != null}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param hem	instance to be tested.
	 * @return		true if the invariants are observed, false otherwise.
	 */
	protected static boolean	invariants(HEM hem)
	{
		assert	hem != null : new PreconditionException("hem != null");

		boolean ret = true;
		ret &= AssertionChecking.checkImplementationInvariant(
				X_RELATIVE_POSITION >= 0,
				HEM.class, hem,
				"X_RELATIVE_POSITION >= 0");
		ret &= AssertionChecking.checkImplementationInvariant(
				Y_RELATIVE_POSITION >= 0,
				HEM.class, hem,
				"Y_RELATIVE_POSITION >= 0");
		return ret;
	}

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * create a household energy manager component.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 */
	protected 			HEM() throws Exception
	{
		// by default, perform the tests planned in the method execute.
		this(true);
	}

	/**
	 * create a household energy manager component.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param performTest	if {@code true}, the HEM performs the planned tests, otherwise not.
	 */
	protected 			HEM(boolean performTest) throws Exception {
		// 1 standard thread to execute the method execute and 1 schedulable
		// thread that is used to perform the tests
		super(1, 1);

		this.performTest = performTest;

		// by default, consider this execution as one in the pre-first step
		// and manage the heater in a customised way.
		this.isPreFirstStep = true;
		this.statistics = new TestsStatistics();

		this.registrationTable = new Hashtable<>();
		this.registrationHeatPumpInboundPort = new RegistrationInboundPort(RegistrationHEMURI, this);
		this.registrationHeatPumpInboundPort.publishPort();

		if (VERBOSE) {
			this.tracer.get().setTitle("Home Energy Manager component");
			this.tracer.get().setRelativePosition(X_RELATIVE_POSITION,
												  Y_RELATIVE_POSITION);
			this.toggleTracing();
		}

		assert	HEM.implementationInvariants(this) :
				new ImplementationInvariantException(
						"HEM.implementationInvariants(this)");
		assert	HEM.invariants(this) :
				new InvariantException("HEM.invariants(this)");
	}

	// -------------------------------------------------------------------------
	// Component life-cycle
	// -------------------------------------------------------------------------

	/**
	 * @see AbstractComponent#start()
	 */
	@Override
	public synchronized void	start() throws ComponentStartException
	{
		super.start();

		try {
			this.meterop = new ElectricMeterOutboundPort(this);
			this.meterop.publishPort();
			this.doPortConnection(
					this.meterop.getPortURI(),
					ElectricMeter.ELECTRIC_METER_INBOUND_PORT_URI,
					ElectricMeterConnector.class.getCanonicalName());

			if (this.isPreFirstStep) {
				// in this case, connect using the statically customised
				// heater connector and keep a specific outbound port to
				// call the heater.
				this.heaterop = new AdjustableOutboundPort(this);
				this.heaterop.publishPort();
				this.doPortConnection(
						this.heaterop.getPortURI(),
						Heater.EXTERNAL_CONTROL_INBOUND_PORT_URI,
						HeaterConnector.class.getCanonicalName());
			}
		} catch (Exception e) {
			throw new ComponentStartException(e) ;
		}
	}

	/**
	 * @see AbstractComponent#execute()
	 */
	@Override
	public synchronized void	execute() throws Exception
	{
		// First, get the clock and wait until the start time that it specifies.

		this.ac = null;
		ClocksServerOutboundPort clocksServerOutboundPort =
											new ClocksServerOutboundPort(this);
		clocksServerOutboundPort.publishPort();
		this.doPortConnection(
					clocksServerOutboundPort.getPortURI(),
					ClocksServer.STANDARD_INBOUNDPORT_URI,
					ClocksServerConnector.class.getCanonicalName());
		this.traceMessage("HEM gets the clock.\n");
		this.ac = clocksServerOutboundPort.getClock(CVMIntegrationTest.CLOCK_URI);
		this.doPortDisconnection(clocksServerOutboundPort.getPortURI());
		clocksServerOutboundPort.unpublishPort();
		this.traceMessage("HEM waits until start time.\n");
		this.ac.waitUntilStart();
		this.traceMessage("HEM starts.\n");

		if (this.performTest) {
			this.logMessage("Electric meter tests start.");
			this.testMeter();
			this.logMessage("Electric meter tests end.");
			if (this.isPreFirstStep) {
				this.scheduleTestHeater();
				this.scheduleTestHeatPump();
			}
		}

	}

	/**
	 * @see AbstractComponent#finalise()
	 */
	@Override
	public synchronized void	finalise() throws Exception
	{
		this.doPortDisconnection(this.meterop.getPortURI());
		if (this.isPreFirstStep) {
			this.doPortDisconnection(this.heaterop.getPortURI());
		}

		for (AdjustableOutboundPort port : this.registrationTable.values()) {
			this.doPortDisconnection(port.getPortURI());
		}

		super.finalise();
	}

	/**
	 * @see AbstractComponent#shutdown()
	 */
	@Override
	public synchronized void	shutdown() throws ComponentShutdownException
	{
		try {
			this.meterop.unpublishPort();
			if (this.isPreFirstStep) {
				this.heaterop.unpublishPort();
			}

			for (AdjustableOutboundPort port : this.registrationTable.values()) {
				port.unpublishPort();
			}
			this.registrationTable.clear();

			this.registrationHeatPumpInboundPort.unpublishPort();

		} catch (Exception e) {
			throw new ComponentShutdownException(e) ;
		}
		super.shutdown();
	}

	// -------------------------------------------------------------------------
	// Registration methods
	// -------------------------------------------------------------------------

	protected Hashtable<String, AdjustableOutboundPort> registrationTable;

	public static final String RegistrationHEMURI = "REGISTRATION-HEM-URI";

	protected RegistrationInboundPort registrationHeatPumpInboundPort;

	public boolean registered(String uid) throws Exception {
		return uid != null && !uid.isEmpty() && this.registrationTable.containsKey(uid);
	}

	public boolean register(
			String uid,
			String controlPortURI,
			String xmlControlAdapter
	) throws Exception {

		assert uid != null && ! uid.isEmpty():
				new PreconditionException("uid == null || uid.isEmpty()");
		assert controlPortURI != null && !controlPortURI.isEmpty():
				new PreconditionException("controlPortURI == null || controlPortURI.isEmpty()");
		assert xmlControlAdapter != null && !xmlControlAdapter.isEmpty():
				new PreconditionException("xmlControlAdapter == null || xml.controlPortURI.isEmpty()");
		assert !registered(uid):
				new PreconditionException("registered(uid)");

		boolean res;

		try {

			ConnectorConfigurationParser.ClassFromXml(uid, AdjustableCI.class, xmlControlAdapter);

			AdjustableOutboundPort newOutboundPort = new AdjustableOutboundPort(this);
			newOutboundPort.publishPort();

			this.doPortConnection(
					newOutboundPort.getPortURI(),
					controlPortURI,
					uid
			);

			this.registrationTable.put(uid, newOutboundPort);

			res = true;
		} catch (Exception e) {
			e.printStackTrace();
			res = false;
		}

		assert !res || registered(uid):
				new PostconditionException("res && !registered(uid)");

		return res;
	}

	public void	 unregister(String uid) throws Exception {

		assert uid != null && !uid.isEmpty():
				new PreconditionException("uid == null || uid.isEmpty()");
		assert registered(uid):
				new PreconditionException("!registered(uid)");

		AdjustableOutboundPort outboundPort = this.registrationTable.remove(uid);
		this.doPortDisconnection(outboundPort.getPortURI());
		outboundPort.unpublishPort();

		assert !registered(uid):
				new PostconditionException("registered(uid)");
	}

	// -------------------------------------------------------------------------
	// Internal methods
	// -------------------------------------------------------------------------

	/**
	 * test the {@code ElectricMeter} component.
	 * 
	 * <p><strong>Description</strong></p>
	 * 
	 * <p>
	 * Calls the test methods defined in {@code ElectricMeterUnitTester}.
	 * </p>
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @throws Exception	<i>to do</i>.
	 */
	protected void		testMeter() throws Exception
	{
		ElectricMeterUnitTester.runAllTests(this, this.meterop, this.statistics);
	}

	/**
	 * test the heater.
	 * 
	 * <p><strong>Gherkin specification</strong></p>
	 * 
	 * <pre>
	 * Feature: adjustable appliance mode management
	 *   Scenario: getting the max mode index
	 *     Given the heater has just been turned on
	 *     When I call maxMode()
	 *     Then the result is its max mode index
	 *   Scenario: getting the current mode index
	 *     Given the heater has just been turned on
	 *     When I call currentMode()
	 *     Then the current mode is its max mode
	 *   Scenario: going down one mode index
	 *     Given the heater is turned on
	 *     And the current mode index is the max mode index
	 *     When I call downMode()
	 *     Then the method returns true
	 *     And the current mode is its max mode minus one
	 *   Scenario: going up one mode index
	 *     Given the heater is turned on
	 *     And the current mode index is the max mode index minus one
	 *     When I call upMode()
	 *     Then the method returns true
	 *     And the current mode is its max mode
	 *   Scenario: setting the mode index
	 *     Given the heater is turned on
	 *     And the mode index 1 is legitimate
	 *     When I call setMode(1)
	 *     Then the method returns true
	 *     And the current mode is 1
	 * Feature: Getting the power consumption given a mode
	 *   Scenario: getting the power consumption of the maximum mode
	 *     Given the heater is turned on
	 *     When I get the power consumption of the maximum mode
	 *     Then the result is the maximum power consumption of the heater
	 * Feature: suspending and resuming
	 *   Scenario: checking if suspended when not
	 *     Given the heater is turned on
	 *     And it has not been suspended yet
	 *     When I check if suspended
	 *     Then it is not
	 *   Scenario: suspending
	 *     Given the heater is turned on
	 *     And it is not suspended
	 *     When I call suspend()
	 *     Then the method returns true
	 *     And the heater is suspended
	 *   Scenario: checking the emergency
	 *     Given the heater is turned on
	 *     And it has just been suspended
	 *     When I call emergency()
	 *     Then the emergency is between 0.0 and 1.0
	 *   Scenario: resuming
	 *     Given the heater is turned on
	 *     And it is suspended
	 *     When I call resume()
	 *     Then the method returns true
	 *     And the heater is not suspended
	 * </pre>
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @throws Exception	<i>to do</i>.
	 */
	protected void		testHeater() throws Exception
	{
		this.logMessage("Heater tests start.");
		this.statistics = new TestsStatistics();
		try {
			this.logMessage("Feature: adjustable appliance mode management");
			this.logMessage("  Scenario: getting the max mode index");
			this.logMessage("    Given the heater has just been turned on");
			this.logMessage("    When I call maxMode()");
			this.logMessage("    Then the result is its max mode index");
			final int maxMode = heaterop.maxMode();

			this.statistics.updateStatistics();

			this.logMessage("  Scenario: getting the current mode index");
			this.logMessage("    Given the heater has just been turned on");
			this.logMessage("    When I call currentMode()");
			this.logMessage("    Then the current mode is its max mode");
			int result = heaterop.currentMode();
			if (result != maxMode) {
				this.logMessage("      but was: " + result);
				this.statistics.incorrectResult();
			}

			this.statistics.updateStatistics();

			this.logMessage("  Scenario: going down one mode index");
			this.logMessage("    Given the heater is turned on");
			this.logMessage("    And the current mode index is the max mode index");
			result = heaterop.currentMode();
			if (result != maxMode) {
				this.logMessage("      but was: " + result);
				this.statistics.failedCondition();
			}
			this.logMessage("    When I call downMode()");
			this.logMessage("    Then the method returns true");
			boolean bResult = heaterop.downMode();
			if (!bResult) {
				this.logMessage("      but was: " + bResult);
				this.statistics.incorrectResult();
			}
			this.logMessage("    And the current mode is its max mode minus one");
			result = heaterop.currentMode();
			if (result != maxMode - 1) {
				this.logMessage("      but was: " + result);
				this.statistics.incorrectResult();
			}

			this.statistics.updateStatistics();

			this.logMessage("  Scenario: going up one mode index");
			this.logMessage("    Given the heater is turned on");
			this.logMessage("    And the current mode index is the max mode index minus one");
			result = heaterop.currentMode();
			if (result != maxMode - 1) {
				this.logMessage("      but was: " + result);
				this.statistics.failedCondition();
			}
			this.logMessage("    When I call upMode()");
			this.logMessage("    Then the method returns true");
			bResult = heaterop.upMode();
			if (!bResult) {
				this.logMessage("      but was: " + bResult);
				this.statistics.incorrectResult();
			}
			this.logMessage("    And the current mode is its max mode");
			result = heaterop.currentMode();
			if (result != maxMode) {
				this.logMessage("      but was: " + result);
				this.statistics.incorrectResult();
			}

			this.statistics.updateStatistics();

			this.logMessage("  Scenario: setting the mode index");
			this.logMessage("    Scenario: setting the mode index");
			int index = 1;
			this.logMessage("    And the mode index 1 is legitimate");
			if (index > maxMode) {
				this.logMessage("      but was not!");
				this.statistics.failedCondition();
			}
			this.logMessage("    When I call setMode(1)");
			this.logMessage("    Then the method returns true");
			bResult = heaterop.setMode(1);
			if (!bResult) {
				this.logMessage("      but was: " + bResult);
				this.statistics.incorrectResult();
			}
			this.logMessage("    And the current mode is 1");
			result = heaterop.currentMode();
			if (result != 1) {
				this.logMessage("      but was: " + result);
				this.statistics.incorrectResult();
			}

			this.statistics.updateStatistics();

			this.logMessage("Feature: Getting the power consumption given a mode");
			this.logMessage("  Scenario: getting the power consumption of the maximum mode");
			this.logMessage("    Given the heater is turned on");
			this.logMessage("    When I get the power consumption of the maximum mode");
			double dResult = heaterop.getModeConsumption(maxMode);
			this.logMessage("    Then the result is the maximum power consumption of the heater");

			this.statistics.updateStatistics();

			this.logMessage("Feature: suspending and resuming");
			this.logMessage("  Scenario: checking if suspended when not");
			this.logMessage("    Given the heater is turned on");
			this.logMessage("    And it has not been suspended yet");
			this.logMessage("    When I check if suspended");
			bResult = heaterop.suspended();
			this.logMessage("    Then it is not");
			if (bResult) {
				this.logMessage("      but it was!");
				this.statistics.incorrectResult();
			}

			this.statistics.updateStatistics();

			this.logMessage("  Scenario: suspending");
			this.logMessage("    Given the heater is turned on");
			this.logMessage("    And it is not suspended");
			bResult = heaterop.suspended();
			if (bResult) {
				this.logMessage("      but it was!");
				this.statistics.failedCondition();;
			}
			this.logMessage("    When I call suspend()");
			bResult = heaterop.suspend();
			this.logMessage("    Then the method returns true");
			if (!bResult) {
				this.logMessage("      but was: " + bResult);
				this.statistics.incorrectResult();
			}
			this.logMessage("    And the heater is suspended");
			bResult = heaterop.suspended();
			if (!bResult) {
				this.logMessage("      but it was not!");
				this.statistics.incorrectResult();
			}

			this.statistics.updateStatistics();

			this.logMessage("  Scenario: checking the emergency");
			this.logMessage("    Given the heater is turned on");
			this.logMessage("    And it has just been suspended");
			bResult = heaterop.suspended();
			if (!bResult) {
				this.logMessage("      but it was not!");
				this.statistics.failedCondition();;
			}
			this.logMessage("    When I call emergency()");
			dResult = heaterop.emergency();
			this.logMessage("    Then the emergency is between 0.0 and 1.0");
			if (dResult < 0.0 || dResult > 1.0) {
				this.logMessage("      but was: " + dResult);
				this.statistics.incorrectResult();
			}

			this.statistics.updateStatistics();

			this.logMessage("  Scenario: resuming");
			this.logMessage("    Given the heater is turned on");
			this.logMessage("    And it is suspended");
			bResult = heaterop.suspended();
			if (!bResult) {
				this.logMessage("      but it was not!");
				this.statistics.failedCondition();;
			}
			this.logMessage("    When I call resume()");
			bResult = heaterop.resume();
			this.logMessage("    Then the method returns true");
			if (!bResult) {
				this.logMessage("      but was: " + bResult);
				this.statistics.incorrectResult();
			}
			this.logMessage("    And the heater is not suspended");
			bResult = heaterop.suspended();
			if (bResult) {
				this.logMessage("      but it was!");
				this.statistics.incorrectResult();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		this.statistics.updateStatistics();
		this.statistics.statisticsReport(this);

		this.logMessage("Heater tests end.");
	}

	/**
	 * test the {@code Heater} component, in cooperation with the
	 * {@code HeaterTester} component.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 */
	protected void		scheduleTestHeater()
	{
		// Test for the heater
		Instant heaterTestStart =
				this.ac.getStartInstant().plusSeconds(
							(HeaterUnitTester.SWITCH_ON_DELAY +
											HeaterUnitTester.SWITCH_OFF_DELAY)/2);
		this.traceMessage("HEM schedules the heater test.\n");
		long delay = this.ac.nanoDelayUntilInstant(heaterTestStart);

		// schedule the switch on heater in one second
		this.scheduleTaskOnComponent(
				new AbstractTask() {
					@Override
					public void run() {
						try {
							testHeater();
						} catch (Exception e) {
							throw new BCMRuntimeException(e) ;
						}
					}
				}, delay, TimeUnit.NANOSECONDS);
	}

	/**
	 * test the {@code HeatPump} component, in cooperation with the
	 * {@code HeatPumpTester} component.
	 *
	 * <p><strong>Contract</strong></p>
	 *
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 */
	protected void		scheduleTestHeatPump() {
		// Test for the heater
		Instant heatPumpTestOn1Start =
				this.ac.getStartInstant().plusSeconds(
						(HeatPumpTester.SWITCH_ON_DELAY1 +
								HeatPumpTester.SET_HEATING_DELAY) / 2);
		Instant heatPumpTestHeatingStart =
				this.ac.getStartInstant().plusSeconds(
						(HeatPumpTester.SET_HEATING_DELAY +
								HeatPumpTester.SWITCH_OFF_DELAY1) / 2);
		Instant heatPumpTestOn2 =
				this.ac.getStartInstant().plusSeconds(
						(HeatPumpTester.SWITCH_ON_DELAY2 + HeatPumpTester.SET_COOLING_DELAY) / 2);
		Instant heatPumpTestCoolingStart =
				this.ac.getStartInstant().plusSeconds(
						(HeatPumpTester.SET_COOLING_DELAY + HeatPumpTester.SWITCH_OFF_DELAY2) / 2
				);
		this.traceMessage("HEM schedules the heat pump test.\n");
		long delayOn1 = this.ac.nanoDelayUntilInstant(heatPumpTestOn1Start);
		long delayHeating = this.ac.nanoDelayUntilInstant(heatPumpTestHeatingStart);
		long delayOn2 = this.ac.nanoDelayUntilInstant(heatPumpTestOn2);
		long delayCooling = this.ac.nanoDelayUntilInstant(heatPumpTestCoolingStart);

		// schedule the switch on heater in one second
		this.scheduleTaskOnComponent(
				new AbstractTask() {
					@Override
					public void run() {
						try {
							integrationTestHeatPump();
						} catch (Exception e) {
							throw new BCMRuntimeException(e);
						}
					}
				}, delayOn1, TimeUnit.NANOSECONDS);


		this.scheduleTaskOnComponent(
				new AbstractTask() {
					@Override
					public void run() {
						try {
							integrationTestHeatPump();
						} catch (Exception e) {
							throw new BCMRuntimeException(e);
						}
					}
				}, delayHeating, TimeUnit.NANOSECONDS);

		this.scheduleTaskOnComponent(
				new AbstractTask() {
					@Override
					public void run() {
						try {
							integrationTestHeatPump();
						} catch (Exception e) {
							throw new BCMRuntimeException(e);
						}
					}
				}, delayOn2, TimeUnit.NANOSECONDS);

		this.scheduleTaskOnComponent(
				new AbstractTask() {
					@Override
					public void run() {
						try {
							integrationTestHeatPump();
						} catch (Exception e) {
							throw new BCMRuntimeException(e);
						}
					}
				}, delayCooling, TimeUnit.NANOSECONDS);
	}

	/**
	 * test the heater.
	 *
	 * <p><strong>Gherkin specification</strong></p>
	 *
	 * <pre>
	 *   Feature:
	 * </pre>
	 *
	 * <p><strong>Contract</strong></p>
	 * Feature: adjustable appliance mode management
	 *   Scenario: getting the max mode index
	 *      Given the heat pump has just been turned on
	 *      When I call maxMode()
	 *   Scenario: getting the current mode index
	 *   	Given the heat pump has just been turned on
	 *   	When I call currentMode()
	 *   	Then the result is its max mode index
	 *   Scenario: going down one mode index
	 *       Given the heater is turned on
	 *       And the current mode index is the max mode index
	 *       When I call downMode()
	 *       Then the method returns true
	 *       And the current mode is its max mode minus one
	 *   Scenario: going up one mode index
	 *   	Given the heat pump is turned on
	 *   	And the current mode index is the max mode index minus one
	 *   	When I call upMode()
	 *   	Then the method returns true
	 *   Scenario: setting the mode index
	 *   		Given the heat pump is turned on
	 *   		And the mode index 1 is legitimate
	 *   		When I call setMode(1)
	 *   		Then the method returns true
	 *   		And the current mode is 1
	 * Feature: Getting the power consumption given a mode
	 *   	Scenario: getting the power consumption of the maximum mode
	 *   		Given the heat pump is turned on
	 *   		When I get the power consumption of the maximum mode
	 *   		Then the result is the maximum power consumption of the heater
	 * Feature: suspending and resuming
	 * 		Scenario: checking if suspended when not
	 * 			Given the heat pump is turned on
	 * 			And it has not been suspended yet
	 * 			When I check if suspended
	 * 			Then it is not
	 * 		Scenario: suspending
	 * 			Given the heat pump is turned on
	 * 			And it is not suspended
	 * 			When I call suspend()
	 * 			Then the method returns true
	 * 			And the heat pump is suspended
	 * 		Scenario: checking the emergency
	 * 			Given the heat pump is turned on
	 * 			And it has just been suspended
	 * 		 	When I call emergency()
	 * 		 	Then the emergency is between 0.0 and 1.0
	 * 		 Scenario: resuming
	 * 		 Given the heat pump is turned on
	 * 		 And it is suspended
	 * 		 When I call resume()
	 * 		 Then the method returns true
	 * 		 And the heat pump is not suspended
	 *
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @throws Exception	<i>to do</i>.
	 */
	public void	integrationTestHeatPump() throws Exception {

		AdjustableOutboundPort outbound = this.registrationTable.get(HeatPump.EQUIPMENT_UID);

		this.logMessage("Heat pump tests starts.");
		this.statistics = new TestsStatistics();
		try {
			this.logMessage("Feature: adjustable appliance mode management");
			this.logMessage("	Scenario: getting the max mode index");
			this.logMessage("		Given the heat pump has just been turned on");
			this.logMessage("		When I call maxMode()");
			this.logMessage("   	Then the result is its max mode index");
			final int maxMode = outbound.maxMode();

			this.statistics.updateStatistics();

			this.logMessage("  Scenario: getting the current mode index");
			this.logMessage("    Given the heat pump has just been turned on");
			this.logMessage("    When I call currentMode()");
			this.logMessage("    Then the current mode is its max mode");
			int result = outbound.currentMode();
			if ( result != maxMode ) {
				this.logMessage("		but was: " + result);
				this.statistics.incorrectResult();
			}

			this.statistics.updateStatistics();

			this.logMessage("	Scenario: going down one mode index");
			this.logMessage("    Given the heat pump is turned on");
			this.logMessage("    And the current mode index is the max mode index");
			this.logMessage("	 When I call downMode()");
			this.logMessage("	 Then the method returns true");

			boolean downResult = outbound.downMode();
			if ( !downResult ) {
				this.logMessage("	but was: false");
				this.statistics.incorrectResult();
			}
			this.logMessage("	And the current mode is its max mode minus one");
			result = outbound.currentMode();
			if (result != maxMode - 1) {
				this.logMessage("	but was: " + result);

				this.statistics.incorrectResult();
			}

			this.statistics.updateStatistics();

			this.logMessage("   Scenario: going up one mode index");
			this.logMessage("		Given the heat pump is turned on");
			this.logMessage("    And the current mode index is the max mode index minus one");
			this.logMessage("    When I call upMode()");
			this.logMessage("    Then the method returns true");
			boolean upResult = outbound.upMode();
			if (!upResult) {
				this.logMessage("	but was false");
				this.statistics.incorrectResult();
			}
			this.logMessage("	And the current mode is its max mode");
			result = outbound.currentMode();
			if (result != maxMode) {
				this.logMessage("	but was: " + result);
				this.statistics.incorrectResult();
			}

			this.statistics.updateStatistics();

			this.logMessage("	Scenario: setting the mode index");
			this.logMessage("		Given the heat pump is turned on");
			int index = 1;
			this.logMessage("		And the mode index 1 is legitimate");
			if (index > maxMode) {
				this.logMessage("	but was not");
				this.statistics.failedCondition();
			}
			this.logMessage("	When I call setMode(1)");
			this.logMessage("	Then the method returns true");
			boolean setResult = outbound.setMode(index);
			if (!setResult) {
				this.logMessage("	but was false");
				this.statistics.incorrectResult();
			}
			this.logMessage("	And the current mode is 1");
			result = outbound.currentMode();
			if (result != index) {
				this.logMessage("	but was: " + result);
				this.statistics.incorrectResult();
			}

			this.statistics.updateStatistics();

			this.logMessage("Feature MAXIMIZE_POWER");
			this.logMessage("	Scenario: set the mode index to the maximum");
			this.logMessage("		Given the heat pump is turned on");
			this.logMessage(" 		When I call setMode(maxMode)");
			this.logMessage("		Then the method returns true");
			setResult = outbound.setMode(maxMode);
			if (!setResult) {
				this.logMessage("	but was false");
				this.statistics.incorrectResult();
			}
			this.logMessage("		And the current mode is max mode");
			result = outbound.currentMode();
			if (result == maxMode) {
				this.logMessage("	but was not: " + result);
				this.statistics.incorrectResult();
			}

			this.statistics.updateStatistics();

			this.logMessage("Feature: Getting the power consumption given a mode");
			this.logMessage("  Scenario: getting the power consumption of the maximum mode");
			this.logMessage("    Given the heat pump is turned on");
			this.logMessage("    When I get the power consumption of the maximum mode");
			double modeConsumption = outbound.getModeConsumption(maxMode);
			this.logMessage("    Then the result is the maximum power consumption of the heater");

			this.statistics.updateStatistics();

			this.logMessage("Feature: suspending and resuming");
			this.logMessage("  Scenario: checking if suspended when not");
			this.logMessage("    Given the heat pump is turned on");
			this.logMessage("    And it has not been suspended yet");
			this.logMessage("    When I check if suspended");
			boolean suspendedResult = outbound.suspended();
			this.logMessage("    Then it is not");
			if (suspendedResult) {
				this.logMessage("      but it was!");
				this.statistics.incorrectResult();
			}

			this.statistics.updateStatistics();

			this.logMessage("  Scenario: suspending");
			this.logMessage("    Given the heat pump is turned on");
			this.logMessage("    And it is not suspended");
			this.logMessage("    When I call suspend()");
			suspendedResult = outbound.suspend();
			this.logMessage("    Then the method returns true");
			if (!suspendedResult) {
				this.logMessage("      but was false");
				this.statistics.incorrectResult();
			}
			this.logMessage("    And the heat pump is suspended");
			suspendedResult = outbound.suspended();
			if (!suspendedResult) {
				this.logMessage("      but it was not!");
				this.statistics.incorrectResult();
			}

			this.statistics.updateStatistics();

			this.logMessage("  Scenario: checking the emergency");
			this.logMessage("    Given the heat pump is turned on");
			this.logMessage("    And it has just been suspended");
			this.logMessage("    When I call emergency()");
			double emergencyResult = outbound.emergency();
			this.logMessage("    Then the emergency is between 0.0 and 1.0");
			if (emergencyResult < 0.0 || emergencyResult > 1.0) {
				this.logMessage("      but was: " + emergencyResult);
				this.statistics.incorrectResult();
			}

			this.statistics.updateStatistics();

			this.logMessage("  Scenario: resuming");
			this.logMessage("    Given the heat pump is turned on");
			this.logMessage("    And it is suspended");
			this.logMessage("    When I call resume()");
			boolean resumeResult = outbound.resume();
			this.logMessage("    Then the method returns true");
			if (!resumeResult) {
				this.logMessage("      but was false");
				this.statistics.incorrectResult();
			}
			this.logMessage("    And the heat pump is not suspended");
			suspendedResult = outbound.suspended();
			if (suspendedResult) {
				this.logMessage("      but it was!");
				this.statistics.incorrectResult();
			}

			this.statistics.updateStatistics();



		} catch (Exception e) {
			e.printStackTrace();
		}

		this.statistics.statisticsReport(this);

		this.logMessage("Heat pump tests end");
	}

}




// -----------------------------------------------------------------------------
