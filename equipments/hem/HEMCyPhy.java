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
import equipments.dimmerlamp.DimmerLamp;
import equipments.dimmerlamp.test.DimmerLampTester;
import fr.sorbonne_u.alasca.physical_data.Measure;
import fr.sorbonne_u.alasca.physical_data.MeasureI;
import fr.sorbonne_u.alasca.physical_data.MeasurementUnit;
import fr.sorbonne_u.alasca.physical_data.SignalData;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import fr.sorbonne_u.components.annotations.RequiredInterfaces;
import fr.sorbonne_u.components.cyphy.ExecutionMode;
import fr.sorbonne_u.components.exceptions.BCMException;
import fr.sorbonne_u.components.exceptions.BCMRuntimeException;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import fr.sorbonne_u.components.hem2025.bases.AdjustableCI;
import fr.sorbonne_u.components.hem2025.bases.RegistrationCI;
import fr.sorbonne_u.components.hem2025e1.equipments.batteries.Batteries;
import fr.sorbonne_u.components.hem2025e1.equipments.batteries.BatteriesCI;
import fr.sorbonne_u.components.hem2025e1.equipments.batteries.BatteriesUnitTester;
import fr.sorbonne_u.components.hem2025e1.equipments.batteries.connections.BatteriesConnector;
import fr.sorbonne_u.components.hem2025e1.equipments.batteries.connections.BatteriesOutboundPort;
import fr.sorbonne_u.components.hem2025e1.equipments.generator.Generator;
import fr.sorbonne_u.components.hem2025e1.equipments.generator.GeneratorCI;
import fr.sorbonne_u.components.hem2025e1.equipments.generator.GeneratorImplementationI;
import fr.sorbonne_u.components.hem2025e1.equipments.generator.GeneratorUnitTester;
import fr.sorbonne_u.components.hem2025e1.equipments.generator.connections.GeneratorConnector;
import fr.sorbonne_u.components.hem2025e1.equipments.generator.connections.GeneratorOutboundPort;
import fr.sorbonne_u.components.hem2025e1.equipments.heater.Heater;
import fr.sorbonne_u.components.hem2025e1.equipments.hem.HeaterConnector;
import fr.sorbonne_u.components.hem2025e1.equipments.meter.ElectricMeterCI;
import fr.sorbonne_u.components.hem2025e1.equipments.meter.ElectricMeterUnitTester;
import fr.sorbonne_u.components.hem2025e1.equipments.meter.connections.ElectricMeterConnector;
import fr.sorbonne_u.components.hem2025e1.equipments.meter.connections.ElectricMeterOutboundPort;
import fr.sorbonne_u.components.hem2025e1.equipments.solar_panel.SolarPanel;
import fr.sorbonne_u.components.hem2025e1.equipments.solar_panel.SolarPanelCI;
import fr.sorbonne_u.components.hem2025e1.equipments.solar_panel.SolarPanelUnitTester;
import fr.sorbonne_u.components.hem2025e1.equipments.solar_panel.connections.SolarPanelConnector;
import fr.sorbonne_u.components.hem2025e1.equipments.solar_panel.connections.SolarPanelOutboundPort;
import fr.sorbonne_u.components.utils.tests.TestScenario;
import fr.sorbonne_u.components.utils.tests.TestsStatistics;
import fr.sorbonne_u.exceptions.*;
import fr.sorbonne_u.utils.aclocks.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.function.Predicate;

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
 * @author    <a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
@RequiredInterfaces(required = {
		AdjustableCI.class,
		ElectricMeterCI.class,
		BatteriesCI.class,
		SolarPanelCI.class,
		GeneratorCI.class})
@OfferedInterfaces(offered = {RegistrationCI.class})
public class HEMCyPhy
extends AbstractComponent
		implements RegistrationI

{
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	/** when true, methods trace their actions.								*/
	public static boolean VERBOSE = false;
	/** when true, methods trace their actions.								*/
	public static boolean DEBUG = false;
	/** when tracing, x coordinate of the window relative position.			*/
	public static int X_RELATIVE_POSITION = 0;
	/** when tracing, y coordinate of the window relative position.			*/
	public static int Y_RELATIVE_POSITION = 0;
	/** standard reflection, inbound port URI for the {@code HEMCyPhy}
	 *  component.															*/
	public static final String REFLECTION_INBOUND_PORT_URI =
			"hem-RIP-URI";

	/** port to connect to the electric meter.								*/
	protected ElectricMeterOutboundPort meterop;

	/** port to connect to the batteries.									*/
	protected BatteriesOutboundPort batteriesop;
	/** port to connect to the solar panel.									*/
	protected SolarPanelOutboundPort solarPanelop;
	/** port to connect to the generator.									*/
	protected GeneratorOutboundPort generatorop;

	/** when true, manage the heater in a customised way, otherwise let
	 *  it register itself as an adjustable appliance.						*/
	protected boolean isPreFirstStep;
	/** port to connect to the heater when managed in a customised way.		*/
	protected AdjustableOutboundPort heaterop;

	// Execution/Simulation

	/** one thread for the method execute.									*/
	protected static int NUMBER_OF_STANDARD_THREADS = 2;
	/** one thread to schedule this component test actions.					*/
	protected static int NUMBER_OF_SCHEDULABLE_THREADS = 3;

	protected ExecutionMode executionMode;
	protected TestScenario testScenario;

	/** accelerated clock used for the tests.								*/
	protected AcceleratedClock ac;
	private boolean performTest;

	// -------------------------------------------------------------------------
	// Invariants
	// -------------------------------------------------------------------------

	/**
	 * return true if the static implementation invariants are observed, false
	 * otherwise.
	 *
	 * <p><strong>Contract</strong></p>
	 *
	 * <pre>
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @return true if the static invariants are observed, false otherwise.
	 */
	public static boolean staticImplementationInvariants() {
		boolean ret = true;
		ret &= AssertionChecking.checkStaticImplementationInvariant(
				NUMBER_OF_STANDARD_THREADS >= 0,
				HEMCyPhy.class,
				"NUMBER_OF_STANDARD_THREADS >= 0");
		ret &= AssertionChecking.checkStaticImplementationInvariant(
				NUMBER_OF_SCHEDULABLE_THREADS >= 0,
				HEMCyPhy.class,
				"NUMBER_OF_SCHEDULABLE_THREADS");
		return ret;
	}

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
	 * @param hem    instance to be tested.
	 * @return true if the implementation invariants are observed, false otherwise.
	 */
	protected static boolean implementationInvariants(HEMCyPhy hem) {
		assert hem != null : new PreconditionException("hem != null");

		boolean ret = true;
		ret &= staticImplementationInvariants();
		return ret;
	}

	/**
	 * return true if the static invariants are observed, false otherwise.
	 *
	 * <p><strong>Contract</strong></p>
	 *
	 * <pre>
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @return true if the static invariants are observed, false otherwise.
	 */
	public static boolean staticInvariants() {
		boolean ret = true;
		ret &= AssertionChecking.checkStaticInvariant(
				X_RELATIVE_POSITION >= 0,
				HEMCyPhy.class,
				"X_RELATIVE_POSITION >= 0");
		ret &= AssertionChecking.checkStaticInvariant(
				Y_RELATIVE_POSITION >= 0,
				HEMCyPhy.class,
				"Y_RELATIVE_POSITION >= 0");
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
	 * @param hem    instance to be tested.
	 * @return true if the invariants are observed, false otherwise.
	 */
	protected static boolean invariants(HEMCyPhy hem) {
		assert hem != null : new PreconditionException("hem != null");

		boolean ret = true;
		ret &= staticInvariants();
		return ret;
	}

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	// Standard execution for manual tests (no test scenario and no simulation)


	protected HEMCyPhy(double controlPeriod, double accelerationFactor) throws Exception {
		this(true, controlPeriod, accelerationFactor);
	}

	/**
	 * create a household energy manager component.
	 *
	 * <p><strong>Contract</strong></p>
	 *
	 * <pre>
	 * pre	{@code !(this instanceof ComponentInterface)}
	 * post	{@code getCurrentExecutionMode().isStandard()}
	 * </pre>
	 *
	 */
	protected HEMCyPhy(boolean performTest, double controlPeriod, double accelerationFactor) throws Exception {
		// 1 standard thread to execute the method execute and 1 schedulable
		// thread that is used to perform the tests
		super(NUMBER_OF_STANDARD_THREADS, NUMBER_OF_SCHEDULABLE_THREADS);

		this.performTest = performTest;

		// by default, consider this execution as one in the pre-first step
		// and manage the heater in a customised way.
		this.isPreFirstStep = true;

		this.executionMode = ExecutionMode.STANDARD;
		this.testScenario = null;

		this.registrationTable = new Hashtable<>();
		this.registrationInboundPort = new RegistrationInboundPort(RegistrationHEMURI, this);
		this.registrationInboundPort.publishPort();

		this.controlPeriod = (long) ((controlPeriod * TimeUnit.SECONDS.toNanos(1)) / accelerationFactor);
		this.time_unit = TimeUnit.NANOSECONDS;

		assert HEMCyPhy.implementationInvariants(this) :
				new ImplementationInvariantException(
						"HEMCyPhy.implementationInvariants(this)");
		assert HEMCyPhy.invariants(this) :
				new InvariantException("HEMCyPhy.invariants(this)");
	}

	// Test execution with test scenario

	/**
	 * create a household energy manager component.
	 *
	 * <p><strong>Contract</strong></p>
	 *
	 * <pre>
	 * pre	{@code !(this instanceof ComponentInterface)}
	 * pre	{@code executionMode != null && (executionMode.isIntegrationTest() || executionMode.isSILIntegrationTest())}
	 * pre	{@code testScenario != null}
	 * post	{@code getCurrentExecutionMode().equals(executionMode)}
	 * </pre>
	 *
	 * @param executionMode    execution mode for the next run.
	 * @param testScenario    test scenario to be executed.
	 * @throws Exception    <i>to do</i>.
	 */
	protected HEMCyPhy(
			ExecutionMode executionMode,
			TestScenario testScenario,
			double controlPeriod,
			double accelerationFactor
	) throws Exception {
		// 1 standard thread to execute the method execute and 1 schedulable
		// thread that is used to perform the tests
		super(REFLECTION_INBOUND_PORT_URI,
				NUMBER_OF_STANDARD_THREADS,
				NUMBER_OF_SCHEDULABLE_THREADS
		);

		assert executionMode != null &&
				(executionMode.isIntegrationTest() ||
						executionMode.isSILIntegrationTest()) :
				new PreconditionException(
						"executionMode != null && (executionMode."
								+ "isIntegrationTest() || "
								+ "executionMode.isSILIntegrationTest())");
		assert testScenario != null :
				new PreconditionException("testScenario != null");

		this.performTest = true;

		this.executionMode = executionMode;
		this.testScenario = testScenario;

		// by default, consider this execution as one in the pre-first step
		// and manage the heater in a customised way.
		this.isPreFirstStep = true;

		this.registrationTable = new Hashtable<>();
		this.registrationInboundPort = new RegistrationInboundPort(RegistrationHEMURI, this);
		this.registrationInboundPort.publishPort();

		this.controlPeriod = (long) ((controlPeriod * TimeUnit.SECONDS.toNanos(1)) / accelerationFactor);
		this.time_unit = TimeUnit.NANOSECONDS;

		if (VERBOSE) {
			this.tracer.get().setTitle("Home Energy Manager component");
			this.tracer.get().setRelativePosition(X_RELATIVE_POSITION,
					Y_RELATIVE_POSITION);
			this.toggleTracing();
		}

		assert HEMCyPhy.implementationInvariants(this) :
				new ImplementationInvariantException(
						"HEMCyPhy.implementationInvariants(this)");
		assert HEMCyPhy.invariants(this) :
				new InvariantException("HEMCyPhy.invariants(this)");
	}

	// -------------------------------------------------------------------------
	// Component life-cycle
	// -------------------------------------------------------------------------

	/**
	 * @see AbstractComponent#start()
	 */
	@Override
	public synchronized void start() throws ComponentStartException {
		super.start();

		try {
			this.meterop = new ElectricMeterOutboundPort(this);
			this.meterop.publishPort();
			this.doPortConnection(
					this.meterop.getPortURI(),
					ElectricMeterCyPhy.ELECTRIC_METER_INBOUND_PORT_URI,
					ElectricMeterConnector.class.getCanonicalName());
			this.batteriesop = new BatteriesOutboundPort(this);
			this.batteriesop.publishPort();
			this.doPortConnection(
					batteriesop.getPortURI(),
					Batteries.STANDARD_INBOUND_PORT_URI,
					BatteriesConnector.class.getCanonicalName());
			this.solarPanelop = new SolarPanelOutboundPort(this);
			this.solarPanelop.publishPort();
			this.doPortConnection(
					this.solarPanelop.getPortURI(),
					SolarPanel.STANDARD_INBOUND_PORT_URI,
					SolarPanelConnector.class.getCanonicalName());
			this.generatorop = new GeneratorOutboundPort(this);
			this.generatorop.publishPort();
			this.doPortConnection(
					this.generatorop.getPortURI(),
					Generator.STANDARD_INBOUND_PORT_URI,
					GeneratorConnector.class.getCanonicalName());

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

				// we add the heater in the table so thaht it us considered in the control loop
				this.registrationTable.put(this.heaterop.getPortURI(), new DeviceControl(this.heaterop));
			}
		} catch (Throwable e) {
			throw new ComponentStartException(e) ;
		}
	}

	/**
	 * @see AbstractComponent#execute()
	 */
	@Override
	public synchronized void execute() throws Exception {
		this.traceMessage("HEM begins execution.\n");

		switch (this.executionMode) {
			case STANDARD:
			case UNIT_TEST:
			case UNIT_TEST_WITH_SIL_SIMULATION:
				throw new BCMException("No unit test for HEM!");
			case INTEGRATION_TEST:
			case INTEGRATION_TEST_WITH_SIL_SIMULATION:
				this.initialiseClock(
						ClocksServer.STANDARD_INBOUNDPORT_URI,
						this.testScenario.getClockURI());
				this.executeTestScenario(this.testScenario);

				this.scheduleTask(
						owner -> {
							try {
								((HEMCyPhy) owner).controlLoop();
							} catch (Exception e) {
								e.printStackTrace();
							}
						},
						this.controlPeriod,
						time_unit
				);

				break;
			case UNIT_TEST_WITH_HIL_SIMULATION:
			case INTEGRATION_TEST_WITH_HIL_SIMULATION:
				throw new BCMException("HIL simulation not implemented yet!");
			default:
		}
		super.execute();

		this.traceMessage("HEM ends execution.\n");

//		if (this.performTest) {
//			this.logMessage("Electric meter tests start.");
//			this.testMeter();
//			this.logMessage("Electric meter tests end.");
//			this.logMessage("Batteries tests start.");
//			this.testBatteries();
//			this.logMessage("Batteries tests end.");
//			this.logMessage("Solar Panel tests start.");
//			this.testSolarPanel();
//			this.logMessage("Solar Panel tests end.");
//			this.logMessage("Generator tests start.");
//			this.testGenerator();
//			this.logMessage("Generator tests end.");
//			if (this.isPreFirstStep) {
//				//this.scheduleTestHeater();
//			}
//		}

	}

	/**
	 * @see AbstractComponent#finalise()
	 */
	@Override
	public synchronized void finalise() throws Exception {
		this.doPortDisconnection(this.meterop.getPortURI());
		this.doPortDisconnection(this.batteriesop.getPortURI());
		this.doPortDisconnection(this.solarPanelop.getPortURI());
		this.doPortDisconnection(this.generatorop.getPortURI());

		for (DeviceControl device : this.registrationTable.values()) {
			this.doPortDisconnection(device.port.getPortURI());
		}

		super.finalise();
	}

	/**
	 * @see AbstractComponent#shutdown()
	 */
	@Override
	public synchronized void shutdown() throws ComponentShutdownException {
		try {
			this.meterop.unpublishPort();
			this.batteriesop.unpublishPort();
			this.solarPanelop.unpublishPort();
			this.generatorop.unpublishPort();

			for (DeviceControl device : this.registrationTable.values()) {
				device.port.unpublishPort();
			}
			this.registrationTable.clear();

			this.registrationInboundPort.unpublishPort();

		} catch (Throwable e) {
			throw new ComponentShutdownException(e) ;
		}
		super.shutdown();
	}

	// -------------------------------------------------------------------------
	// Helper methods
	// -------------------------------------------------------------------------

	protected void tracing(String message) {
		if (VERBOSE) {
			this.logMessage(message + "\n");
		}
	}

	// -------------------------------------------------------------------------
	// Registration methods
	// -------------------------------------------------------------------------

	static class DeviceControl implements AdjustableCI {
		public AdjustableOutboundPort port;
		public int cycle;

		public DeviceControl(AdjustableOutboundPort port) {
			this.port = port;
			this.cycle = 0;
		}

		public void updateCycle() {
			++this.cycle;
		}


		/**
		 * @see AdjustableCI#maxMode
		 */
		@Override
		public int maxMode() throws Exception {
			return port.maxMode();
		}

		/**
		 * @see AdjustableCI#upMode
		 */
		@Override
		public boolean upMode() throws Exception {
			return port.upMode();
		}

		/**
		 * @see AdjustableCI#downMode
		 */
		@Override
		public boolean downMode() throws Exception {
			return port.downMode();
		}

		/**
		 * @see AdjustableCI#setMode
		 */
		@Override
		public boolean setMode(int modeIndex) throws Exception {
			return port.setMode(modeIndex);
		}

		/**
		 * @see AdjustableCI#currentMode
		 */
		@Override
		public int currentMode() throws Exception {
			return port.currentMode();
		}

		@Override
		public double getModeConsumption(int modeIndex) throws Exception {
			return port.getModeConsumption(modeIndex);
		}

		@Override
		public boolean suspended() throws Exception {
			return port.suspended();
		}

		@Override
		public boolean suspend() throws Exception {
			return port.suspend();
		}

		@Override
		public boolean resume() throws Exception {
			return port.resume();
		}

		@Override
		public double emergency() throws Exception {
			return port.emergency();
		}
	}

	protected final Hashtable<String, DeviceControl> registrationTable;

	public static final String RegistrationHEMURI = "REGISTRATION-HEM-URI";

	protected RegistrationInboundPort registrationInboundPort;

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

			this.registrationTable.put(uid, new DeviceControl(newOutboundPort));


			res = true;
		} catch (Exception e) {
			e.printStackTrace();
			res = false;
		}

		assert !res || registered(uid):
				new PostconditionException("res && !registered(uid)");

		return res;
	}

	public void unregister(String uid) throws Exception {

		assert uid != null && !uid.isEmpty():
				new PreconditionException("uid == null || uid.isEmpty()");
		assert registered(uid):
				new PreconditionException("!registered(uid)");

		synchronized ( this.registrationTable ) {
			DeviceControl device = this.registrationTable.remove(uid);
			this.doPortDisconnection(device.port.getPortURI());
			device.port.unpublishPort();
		}

		assert !registered(uid):
				new PostconditionException("registered(uid)");
	}

	// -------------------------------------------------------------------------
	// Energy Management
	// -------------------------------------------------------------------------

	protected static double MAXIMUM_EMERGENCY_THRESHOLD = 0.90;
	protected static double MINIMUM_EMERGENCY_THRESHOLD = 0.50;

	/** A device will only be suspended or resumed after 5 control loop being respectively active and suspended */
	protected static int MINIMUM_RESUME_CYCLE = 5;

	/** After being suspended for more than 25 control loop, we try to resume the device, we want to avoid situation
	 * where a device is never resumed.
	 * */
	protected static int MAXIMUM_RESUME_CYCLE = 25;

	double previous_evolution;
	double resume_threshold = MAXIMUM_EMERGENCY_THRESHOLD;

	protected static Measure<Double> ENERGY_HYSTERESIS = new Measure<>(2.0, MeasurementUnit.VOLTS);

	protected long controlPeriod;

	protected TimeUnit time_unit;

	public static final double STANDARD_CONTROL_PERIOD = 60.0;

	/**
	 *
	 * Computes the emergency level of a device
	 * I an exception is thrown when getting the emergencyLevel from the device
	 * then the device is considered as non urgent
	 *
	 * <p><strong>Contract</strong></p>
	 *
	 * <pre>
	 *  pre {@code true} // no precondition
	 *  post {@code true} // no postcondition
	 * </pre>
	 * @param port
	 * @return
	 */
	protected static double computeDeviceEmergencyLevel(DeviceControl port) {
		double result = 0.0;
		try {
			if ( port.suspended() ) {
				result = port.emergency();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	protected boolean hasDevices(Predicate<DeviceControl> p) {
		return this.registrationTable
				.values()
				.stream()
				.anyMatch(p);
	}

	protected boolean hasUrgentDevices() {
		return this.hasDevices(p -> {
			try {
				return p.suspended();
			} catch (Exception e) {
				return false;
			}
		});
	}

	protected boolean canIncreaseDevices() {
		return this.hasDevices(p -> {
			try {
				return canBeIncreased(p);
			} catch ( Exception e ) {
				e.printStackTrace();
				return false;
			}
		});
	}

	protected boolean canDecreaseDevices() {
		return this.hasDevices(p -> {
			try {
				return canBeDecreased(p);
			} catch ( Exception e ) {
				e.printStackTrace();
				return false;
			}
		});
	}

	/**
	 *
	 * Computes the emergency level of a device
	 * If an exception is thrown when getting the current consumption level from the device
	 * then the device is considered as non urgent
	 *
	 * <p><strong>Contract</strong></p>
	 *
	 * <pre>
	 *  pre {@code true} // no precondition
	 *  post {@code true} // no postcondition
	 * </pre>
	 * @param port
	 * @return
	 */
	protected static double computeDeviceConsumptionLevel(DeviceControl port) {
		double result = 0.0;
		try {
			if ( ! port.suspended() ) {
				final int current_mode = port.currentMode();
				result = port.getModeConsumption(current_mode);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;
	}

	protected static double computeDeviceConsumptionIncrease(DeviceControl port) {
		try {
			final int current_mode = port.currentMode();
			final double current_consumption = port.getModeConsumption(current_mode);
			final double increased_consumption = port.getModeConsumption(current_mode + 1);
			return increased_consumption - current_consumption;
		} catch (Exception e) {
			e.printStackTrace();
			return 0.0;
		}
	}

	protected static boolean decreaseDeviceConsumption(DeviceControl port) {
		try {
			final int current_mode = port.currentMode();
			return port.setMode(current_mode - 1);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	protected static boolean increaseDeviceConsumption(DeviceControl port) {
		try {
			final int current_mode = port.currentMode();
			return port.setMode(current_mode + 1);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	protected static double computeDeviceConsumptionDecrease(DeviceControl port) {
		try {
			final int current_mode = port.currentMode();
			final double current_consumption = port.getModeConsumption(current_mode);
			final double decreased_consumption = port.getModeConsumption(current_mode - 1);
			return current_consumption - decreased_consumption;
		} catch (Exception e) {
			e.printStackTrace();
			return 0.0;
		}
	}


	protected static double computeDeviceConsumptionResume(DeviceControl port) {
		try {
			return port.getModeConsumption(1);
		} catch (Exception e) {
			e.printStackTrace();
			return 0.0;
		}
	}

	protected static boolean canBeIncreased(DeviceControl port) {
		boolean result = false;
		try {
			if ( ! port.suspended() ) {
				int current_mode = port.currentMode();
				result = current_mode < port.maxMode();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	protected static boolean canBeDecreased(DeviceControl port) {
		boolean result = false;
		try {
			if ( ! port.suspended() ) {
				int current_mode = port.currentMode();
				result = current_mode > 1;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	protected DeviceControl[] computeDevicesLevel(Predicate<DeviceControl> p, Comparator<DeviceControl> c) {
		DeviceControl[] ports =
				this.registrationTable
						.values()
						.stream()
						.filter(p)
						.sorted(c)
						.toArray(DeviceControl[]::new);
		return ports;
	}

	/**
	 *
	 * Returns an array containing the outbound ports sorted according to their urgency
	 *
	 * <p><strong>Contract</strong></p>
	 *
	 * <pre>
	 *  pre {@code true} // no precondition
	 *  post {@code true} // no postcondition
	 * </pre>
	 * @return ports of the devices in order of how urgent they need more energy
	 */
	protected DeviceControl[] getUrgentDevices() {
		final Predicate<DeviceControl> suspended = (DeviceControl op) -> {
			try {
				return op.suspended();
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
		};
		final Comparator<DeviceControl> emergency_comparator = (op1, op2) -> {
			final double emergency1 = computeDeviceEmergencyLevel(op1);
			final double emergency2 = computeDeviceEmergencyLevel(op2);
			return Double.compare(emergency1, emergency2);
		};
		return this.computeDevicesLevel(suspended, emergency_comparator);
	}

	/**
	 *
	 * Returns an array containing the outbound ports sorting decreasingly according to their consumption
	 *
	 * <p><strong>Contract</strong></p>
	 *
	 * <pre>
	 *  pre {@code true} // no precondition
	 *  post {@code true} // no postcondition
	 * </pre>
	 * @return ports of the devices in order of how urgent they need more energy
	 * @throws Exception
	 */
	protected DeviceControl[] getConsumer() {
		final Predicate<DeviceControl> not_suspended = (DeviceControl op) -> {
			try {
				return ! op.suspended();
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
		};
		final Comparator<DeviceControl> consumption_comparator = (op1, op2) -> {
			final double consumption1 = computeDeviceConsumptionLevel(op1);
			final double consumption2 = computeDeviceConsumptionLevel(op2);
			return Double.compare(consumption2, consumption1);
		};
		return this.computeDevicesLevel(not_suspended, consumption_comparator);
	}

	protected double computeEnergy(Predicate<DeviceControl> predicate, Function<DeviceControl, Double> mapper) {
		return this.registrationTable
				.values()
				.stream()
				.filter(predicate)
				.map(mapper)
				.reduce(0.0, Double::sum);
	}

	protected static boolean resumeDevice(DeviceControl port)  {
		try {
			if ( port.resume() ) {
				port.cycle = 0;
				return port.setMode(1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	protected static boolean suspendDevice(DeviceControl port)  {
		try {
			port.cycle = 0;
			return port.suspend();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	protected double increaseDevicesConsumption(double available_energy) {

		// sorted in decreasing order according to power consumption
		DeviceControl[] consumers = this.getConsumer();

		for (int i = consumers.length - 1; i >= 0 && available_energy > ENERGY_HYSTERESIS.getData(); --i) {
			final DeviceControl port = consumers[i];

			if ( canBeIncreased(port) ) {
				final double energy_invested = computeDeviceConsumptionIncrease(port);

				boolean success = false;
				if ( available_energy >= energy_invested  ) {
					success = increaseDeviceConsumption(port);
				}

				if ( success ) {
					available_energy -= energy_invested;
				}
			}
		}

		return available_energy;
	}

	protected double decreaseDevicesConsumption(double available_energy) throws Exception {

		// sorted in decreasing order according to power consumption
		DeviceControl[] consumers = this.getConsumer();

		for (int i = 0; i < consumers.length && available_energy < ENERGY_HYSTERESIS.getData(); ++i) {
			final DeviceControl port = consumers[i];

			if ( canBeDecreased(port) ) {
				final double energy_gained = computeDeviceConsumptionDecrease(port);

				boolean success = false;
				if ( available_energy >= energy_gained + ENERGY_HYSTERESIS.getData() ) {
					success = decreaseDeviceConsumption(port);
				}

				if ( success ) {
					available_energy += energy_gained;
				}
			}
		}

		return available_energy;
	}

	protected double resumeUrgentDevices(double available) throws Exception {

		DeviceControl[] urgent_devices = this.getUrgentDevices();

		for (int i = 0; i < urgent_devices.length && available > ENERGY_HYSTERESIS.getData(); ++i) {
			final DeviceControl device = urgent_devices[i];
			final double energy_needed = computeDeviceConsumptionResume(device);
			// we resume the most urgent in priority
			boolean success = false;
			// if we have enough energy we try to resume the device
			if ( available >= energy_needed + ENERGY_HYSTERESIS.getData() ) {
				success = resumeDevice(device);
				// if not but the device needs to be resumed
			} else if ( computeDeviceEmergencyLevel(device) >= this.resume_threshold
					|| device.cycle > MAXIMUM_RESUME_CYCLE) {
				// then we suspend some devices to allow the device to be resumed
				available = freeEnergy(available, energy_needed + ENERGY_HYSTERESIS.getData());

				if ( available >= energy_needed + ENERGY_HYSTERESIS.getData()) {
					success = resumeDevice(device);
				}
			}

			if ( success ) {
				available -= energy_needed;
			}
		}

		return available;
	}

	protected double computeIncreasingDemand() {
		return computeEnergy(HEMCyPhy::canBeIncreased, HEMCyPhy::computeDeviceConsumptionIncrease);
	}

	/**
	 *
	 * Description
	 *
	 * <p><strong>Contract</strong></p>
	 *
	 * <pre>
	 *  pre {@code true} // no precondition
	 *  post {@code true} // no postcondition
	 * </pre>
	 * @param available_energy
	 * @return
	 */
	protected double freeEnergy(double available_energy, double needed_energy) {

		// this array is sorted in decreasing order according to the power consumed
		final DeviceControl[] consumer = this.getConsumer();

		double energy_sum = available_energy;

		ArrayList<DeviceControl> suspended_devices = new ArrayList<>();

		for (int index = 0; index < consumer.length && energy_sum < needed_energy; ++index ) {
			final DeviceControl device = consumer[index];
			if ( device.cycle >= MINIMUM_RESUME_CYCLE ) {
				energy_sum += computeDeviceConsumptionLevel(device);
				suspended_devices.add(device);
			}

		}

		// we only suspend the devices, if we have enough energy
		if ( energy_sum >= needed_energy ) {

			for (DeviceControl device : suspended_devices) {
				suspendDevice(device);
			}
			available_energy = energy_sum;
		}

		return available_energy;
	}

	protected double suspendDevices(double available_energy) {

		// this array is sorted in decreasing order according to the power consumed
		final DeviceControl[] consumer = this.getConsumer();

		for (int index = 0; index < consumer.length && available_energy < ENERGY_HYSTERESIS.getData(); ++index ) {
			final DeviceControl device = consumer[index];
			final double energy_gain = computeDeviceConsumptionLevel(device);
			if ( suspendDevice(device) ) {
				available_energy += energy_gain;
			}

		}

		return available_energy;
	}

	/**
	 * Computes a threshold, used to determine whether or not we should try to resume a device
	 * When the number of suspended devices increases then the threshold also increases
	 *
	 *
	 * <p><strong>Contract</strong></p>
	 *
	 * <pre>
	 *  pre {@code true} // no precondition
	 *  post {@code true} // no postcondition
	 * </pre>
	 * @throws Exception
	 */
	protected void computeResumeThreshold() throws Exception {
		final double number_suspended =
				(double) this.registrationTable
						.values()
						.stream()
						.filter(port -> {
							try {
								return port.suspended();
							} catch (Exception e) {
								e.printStackTrace();
								return false;
							}
						}).count();

		final int number_devices = this.registrationTable.size();

		double threshold = MINIMUM_EMERGENCY_THRESHOLD;
		if ( number_devices > 0 ) {
			threshold +=
					(number_suspended / number_devices) * (MAXIMUM_EMERGENCY_THRESHOLD - MINIMUM_EMERGENCY_THRESHOLD);
		}

		this.resume_threshold = threshold;
	}

	protected void handleOverProduction(double available_energy) throws Exception {

		this.computeResumeThreshold();

		if (this.hasUrgentDevices()) {
			this.resumeUrgentDevices(available_energy);
		} else if (this.generatorop.getState() != GeneratorImplementationI.State.IDLE) {

			final Measure<Double> tension = this.generatorop.nominalOutputTension();

			final double energy_produced = convertIntensityToPower(this.generatorop.currentPowerProduction(), tension);

			if (available_energy - energy_produced >= ENERGY_HYSTERESIS.getData()) {
				this.generatorop.stopGenerator();
			}

		} else if ( this.canIncreaseDevices() ) {
			this.increaseDevicesConsumption(available_energy);
		} else {
			this.batteriesop.startCharging();
		}

	}

	protected void handleOverConsumption(double available_energy) throws Exception {

		if ( this.batteriesop.areCharging() ) {
			this.batteriesop.stopCharging();
		} else if ( this.canDecreaseDevices() ) {
			decreaseDevicesConsumption(available_energy);
		} else if (this.generatorop.getState() == GeneratorImplementationI.State.IDLE ) {
			this.generatorop.startGenerator();
		} else {
			suspendDevices(available_energy);
		}

	}

	protected static double convertIntensityToPower(SignalData<Double> signal, MeasureI<Double> voltage) throws Exception {

		assert signal.getMeasure().getMeasurementUnit() == MeasurementUnit.AMPERES || signal.getMeasure().getMeasurementUnit() == MeasurementUnit.WATTS :
				new PreconditionException("signal.getMeasurementUnit() != MeasurementUnit.AMPERES && signal.getMeasurementUnit() != MeasurementUnit.WATTS");

		final MeasureI<Double> production_signal = signal.getMeasure();

		if ( production_signal.getMeasurementUnit() == MeasurementUnit.WATTS ) {
			return production_signal.getData();
		} else if ( production_signal.getMeasurementUnit() == MeasurementUnit.AMPERES) {
			return production_signal.getData() * voltage.getData();
		} else {
			return 0.0;
		}

	}

	protected void updateDevicesCycle() {
		this.registrationTable.values().forEach(DeviceControl::updateCycle);
	}

	protected void updateProductionState() throws Exception {

		final MeasureI<Double> tension = this.meterop.getTension();

		final double production = convertIntensityToPower(this.meterop.getCurrentProduction(), tension);
		final double consumption = convertIntensityToPower(this.meterop.getCurrentConsumption(), tension);
		double power_evolution = production - consumption;

		if ( power_evolution >= 0. ) {
			handleOverProduction(power_evolution);
		} else {
			handleOverConsumption(power_evolution);
		}

		this.previous_evolution = power_evolution;
	}

	protected void controlLoop() throws Exception {

		if ( ! this.isFinalised() && ! this.isShutdown() ) {

			synchronized (this.registrationTable) {
				updateProductionState();
				updateDevicesCycle();
			}

			this.scheduleTask(
					owner -> {
						try {
							((HEMCyPhy) owner).controlLoop();
						} catch (Exception e) {
							e.printStackTrace();
						}
					},
					this.controlPeriod,
					time_unit
			);
		}
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
	 * @throws Exception    <i>to do</i>.
	 */
	public void testMeter() throws Exception {
		ElectricMeterUnitTester.runAllTests(this, this.meterop,
				new TestsStatistics());
	}

	/**
	 * test the {@code Batteries} component.
	 *
	 * <p><strong>Contract</strong></p>
	 *
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 * @throws Exception    <i>to do</i>.
	 *
	 */
	public void testBatteries() throws Exception {
		BatteriesUnitTester.runAllTests(this, this.batteriesop,
				new TestsStatistics());
	}

	/**
	 * start charging the batteries.
	 *
	 * <p><strong>Contract</strong></p>
	 *
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @throws Exception    <i>to do</i>.
	 */
	public void startChargingBatteries() throws Exception {
		this.batteriesop.startCharging();
	}

	/**
	 * test the state of the batteries.
	 *
	 * <p><strong>Contract</strong></p>
	 *
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @throws Exception    <i>to do</i>.
	 */
	public void testBatteriesState() throws Exception {
		this.logMessage("areCharging = " + this.batteriesop.areCharging());
		this.logMessage("areDischarging = " + this.batteriesop.areDischarging());
		this.logMessage("chargeLevel = " + this.batteriesop.chargeLevel());
		this.logMessage("getCurrentPowerConsumption = " + this.batteriesop.getCurrentPowerConsumption());
	}

	/**
	 * stop charging the batteries.
	 *
	 * <p><strong>Contract</strong></p>
	 *
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @throws Exception    <i>to do</i>.
	 */
	public void stopChargingBatteries() throws Exception {
		this.batteriesop.stopCharging();
	}

	/**
	 * test the {@code SolarPanel} component.
	 *
	 * <p><strong>Contract</strong></p>
	 *
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 * @throws Exception    <i>to do</i>.
	 *
	 */
	public void testSolarPanel() throws Exception {
		SolarPanelUnitTester.runAllTests(this, this.solarPanelop,
				new TestsStatistics());
	}

	/**
	 * test the {@code Generator} component.
	 *
	 * <p><strong>Contract</strong></p>
	 *
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 * @throws Exception    <i>to do</i>.
	 *
	 */
	public void testGenerator() throws Exception {
		GeneratorUnitTester.runAllTests(this, this.generatorop,
				new TestsStatistics());
	}

	/**
	 * return the outbound port connected to the generator component; used in
	 * test scenario.
	 *
	 * <p><strong>Contract</strong></p>
	 *
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @return the outbound port connected to the generator component.
	 */
	public GeneratorOutboundPort getGeneratorPort() {
		return this.generatorop;
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
	 *   Scenario: going down one mode index when suspended
	 *     Given the heater is turned on
	 *     And the heater is suspended
	 *     When I call downMode()
	 *     Then a precondition exception is thrown
	 *   Scenario: going up one mode index when suspended
	 *     Given the heater is turned on
	 *     And the heater is suspended
	 *     When I call upMode()
	 *     Then a precondition exception is thrown
	 *   Scenario: going up one mode index when suspended
	 *     Given the heater is turned on
	 *     And the heater is suspended
	 *     When I call upMode()
	 *     Then a precondition exception is thrown
	 *   Scenario: getting the current mode when suspended
	 *     Given the heater is turned on
	 *     And the heater is suspended
	 *     When I get the current mode
	 *     Then a precondition exception is thrown
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
	 * @throws Exception    <i>to do</i>.
	 */
	public void testHeater() throws Exception {
		this.logMessage("Heater tests start.");
		TestsStatistics statistics = new TestsStatistics();
		try {
			this.logMessage("Feature: adjustable appliance mode management");
			this.logMessage("  Scenario: getting the max mode index");
			this.logMessage("    Given the heater has just been turned on");
			this.logMessage("    When I call maxMode()");
			this.logMessage("    Then the result is its max mode index");
			final int maxMode = heaterop.maxMode();

			statistics.updateStatistics();

			this.logMessage("  Scenario: getting the current mode index");
			this.logMessage("    Given the heater has just been turned on");
			this.logMessage("    When I call currentMode()");
			this.logMessage("    Then the current mode is its max mode");
			int result = heaterop.currentMode();
			if (result != maxMode) {
				this.logMessage("      but was: " + result);
				statistics.incorrectResult();
			}

			statistics.updateStatistics();

			this.logMessage("  Scenario: going down one mode index");
			this.logMessage("    Given the heater is turned on");
			this.logMessage("    And the current mode index is the max mode index");
			result = heaterop.currentMode();
			if (result != maxMode) {
				this.logMessage("      but was: " + result);
				statistics.failedCondition();
			}
			this.logMessage("    When I call downMode()");
			this.logMessage("    Then the method returns true");
			boolean bResult = heaterop.downMode();
			if (!bResult) {
				this.logMessage("      but was: " + bResult);
				statistics.incorrectResult();
			}
			this.logMessage("    And the current mode is its max mode minus one");
			result = heaterop.currentMode();
			if (result != maxMode - 1) {
				this.logMessage("      but was: " + result);
				statistics.incorrectResult();
			}

			statistics.updateStatistics();

			this.logMessage("  Scenario: going up one mode index");
			this.logMessage("    Given the heater is turned on");
			this.logMessage("    And the current mode index is the max mode index minus one");
			result = heaterop.currentMode();
			if (result != maxMode - 1) {
				this.logMessage("      but was: " + result);
				statistics.failedCondition();
			}
			this.logMessage("    When I call upMode()");
			this.logMessage("    Then the method returns true");
			bResult = heaterop.upMode();
			if (!bResult) {
				this.logMessage("      but was: " + bResult);
				statistics.incorrectResult();
			}
			this.logMessage("    And the current mode is its max mode");
			result = heaterop.currentMode();
			if (result != maxMode) {
				this.logMessage("      but was: " + result);
				statistics.incorrectResult();
			}

			statistics.updateStatistics();

			this.logMessage("  Scenario: setting the mode index");
			this.logMessage("    Given the heater is turned on");
			int index = 1;
			this.logMessage("    And the mode index 1 is legitimate");
			if (index > maxMode) {
				this.logMessage("      but was not!");
				statistics.failedCondition();
			}
			this.logMessage("    When I call setMode(1)");
			this.logMessage("    Then the method returns true");
			bResult = heaterop.setMode(1);
			if (!bResult) {
				this.logMessage("      but was: " + bResult);
				statistics.incorrectResult();
			}
			this.logMessage("    And the current mode is 1");
			result = heaterop.currentMode();
			if (result != 1) {
				this.logMessage("      but was: " + result);
				statistics.incorrectResult();
			}

			statistics.updateStatistics();

			this.logMessage("Feature: Getting the power consumption given a mode");
			this.logMessage("  Scenario: getting the power consumption of the maximum mode");
			this.logMessage("    Given the heater is turned on");
			this.logMessage("    When I get the power consumption of the maximum mode");
			double dResult = heaterop.getModeConsumption(maxMode);
			this.logMessage("    Then the result is the maximum power consumption of the heater");

			statistics.updateStatistics();

			this.logMessage("Feature: suspending and resuming");
			this.logMessage("  Scenario: checking if suspended when not");
			this.logMessage("    Given the heater is turned on");
			this.logMessage("    And it has not been suspended yet");
			this.logMessage("    When I check if suspended");
			bResult = heaterop.suspended();
			this.logMessage("    Then it is not");
			if (bResult) {
				this.logMessage("      but it was!");
				statistics.incorrectResult();
			}

			statistics.updateStatistics();

			this.logMessage("  Scenario: suspending");
			this.logMessage("    Given the heater is turned on");
			this.logMessage("    And it is not suspended");
			bResult = heaterop.suspended();
			if (bResult) {
				this.logMessage("      but it was!");
				statistics.failedCondition();;
			}
			this.logMessage("    When I call suspend()");
			bResult = heaterop.suspend();
			this.logMessage("    Then the method returns true");
			if (!bResult) {
				this.logMessage("      but was: " + bResult);
				statistics.incorrectResult();
			}
			this.logMessage("    And the heater is suspended");
			bResult = heaterop.suspended();
			if (!bResult) {
				this.logMessage("      but it was not!");
				statistics.incorrectResult();
			}

			statistics.updateStatistics();

			this.logMessage("  Scenario: going down one mode index when suspended");
			this.logMessage("    Given the heater is turned on");
			this.logMessage("    And the heater is suspended");
			bResult = heaterop.suspended();
			if (!bResult) {
				this.logMessage("      but it was not!");
				statistics.failedCondition();;
			}
			this.logMessage("    When I call downMode()");
			this.logMessage("    Then a precondition exception is thrown");
			boolean old = BCMException.VERBOSE;
			try {
				BCMException.VERBOSE = false;
				heaterop.downMode();
				this.logMessage("      but it was not!");
				statistics.incorrectResult();
			} catch (Throwable e) {
			} finally {
				BCMException.VERBOSE = old;
			}

			statistics.updateStatistics();

			this.logMessage("  Scenario: going up one mode index when suspended");
			this.logMessage("    Given the heater is turned on");
			this.logMessage("    And the heater is suspended");
			bResult = heaterop.suspended();
			if (!bResult) {
				this.logMessage("      but it was not!");
				statistics.failedCondition();;
			}
			this.logMessage("    When I call upMode()");
			this.logMessage("    Then a precondition exception is thrown");
			old = BCMException.VERBOSE;
			try {
				BCMException.VERBOSE = false;
				heaterop.upMode();
				this.logMessage("      but it was not!");
				statistics.incorrectResult();
			} catch (Throwable e) {
			} finally {
				BCMException.VERBOSE = old;
			}

			statistics.updateStatistics();

			this.logMessage("  Scenario: setting the mode when suspended");
			this.logMessage("    Given the heater is turned on");
			this.logMessage("    And the heater is suspended");
			bResult = heaterop.suspended();
			if (!bResult) {
				this.logMessage("      but it was not!");
				statistics.failedCondition();;
			}
			this.logMessage("    And the mode index 1 is legitimate");
			if (index > maxMode) {
				this.logMessage("      but was not!");
				statistics.failedCondition();
			}
			this.logMessage("    When I call setMode(1)");
			this.logMessage("    Then a precondition exception is thrown");
			old = BCMException.VERBOSE;
			try {
				BCMException.VERBOSE = false;
				heaterop.upMode();
				this.logMessage("      but it was not!");
				statistics.incorrectResult();
			} catch (Throwable e) {
			} finally {
				BCMException.VERBOSE = old;
			}

			statistics.updateStatistics();

			this.logMessage("  Scenario: getting the current mode when suspended");
			this.logMessage("    Given the heater is turned on");
			this.logMessage("    And the heater is suspended");
			bResult = heaterop.suspended();
			if (!bResult) {
				this.logMessage("      but it was not!");
				statistics.failedCondition();;
			}
			this.logMessage("    When I get the current mode");
			this.logMessage("    Then a precondition exception is thrown");
			old = BCMException.VERBOSE;
			try {
				BCMException.VERBOSE = false;
				heaterop.currentMode();
				this.logMessage("      but it was not!");
				statistics.incorrectResult();
			} catch (Throwable e) {
			} finally {
				BCMException.VERBOSE = old;
			}

			statistics.updateStatistics();

			this.logMessage("  Scenario: checking the emergency");
			this.logMessage("    Given the heater is turned on");
			this.logMessage("    And it has just been suspended");
			bResult = heaterop.suspended();
			if (!bResult) {
				this.logMessage("      but it was not!");
				statistics.failedCondition();;
			}
			this.logMessage("    When I call emergency()");
			dResult = heaterop.emergency();
			this.logMessage("    Then the emergency is between 0.0 and 1.0");
			if (dResult < 0.0 || dResult > 1.0) {
				this.logMessage("      but was: " + dResult);
				statistics.incorrectResult();
			}

			statistics.updateStatistics();

			this.logMessage("  Scenario: resuming");
			this.logMessage("    Given the heater is turned on");
			this.logMessage("    And it is suspended");
			bResult = heaterop.suspended();
			if (!bResult) {
				this.logMessage("      but it was not!");
				statistics.failedCondition();;
			}
			this.logMessage("    When I call resume()");
			bResult = heaterop.resume();
			this.logMessage("    Then the method returns true");
			if (!bResult) {
				this.logMessage("      but was: " + bResult);
				statistics.incorrectResult();
			}
			this.logMessage("    And the heater is not suspended");
			bResult = heaterop.suspended();
			if (bResult) {
				this.logMessage("      but it was!");
				statistics.incorrectResult();
			}
		} catch (Throwable e) {
			e.printStackTrace();
		}

		statistics.updateStatistics();
		statistics.statisticsReport(this);

		this.logMessage("Heater tests end.");
	}

	/**
	 * test the dimmer lamp.
	 *
	 * <p><strong>Gherkin specification</strong></p>
	 *
	 * <pre>
	 * Feature: adjustable appliance mode management
	 *   Scenario: getting the max mode index
	 *      	Given the heat pump has just been turned on
	 *      	When I call maxMode()
	 *   Scenario: getting the current mode index
	 *   		Given the dimmer lamp has just been turned on
	 *   		When I call currentMode()
	 *   		Then the result is its max mode index
	 *   Scenario: going down one mode index
	 *       	Given the heater is turned on
	 *       	And the current mode index is the max mode index
	 *       	When I call downMode()
	 *       	Then the method returns true
	 *       	And the current mode is its max mode minus one
	 *   Scenario: going up one mode index
	 *   		Given the dimmer lamp is turned on
	 *   		And the current mode index is the max mode index minus one
	 *   		When I call upMode()
	 *   		Then the method returns true
	 *   Scenario: setting the mode index
	 *   		Given the dimmer lamp is turned on
	 *   		And the mode index 1 is legitimate
	 *   		When I call setMode(1)
	 *   		Then the method returns true
	 *   		And the current mode is 1
	 * Feature: Getting the power consumption given a mode
	 *   	Scenario: getting the power consumption of the maximum mode
	 *   		Given the dimmer lamp is turned on
	 *   		When I get the power consumption of the maximum mode
	 *   		Then the result is the maximum power consumption of the heater
	 * Feature: suspending and resuming
	 * 		Scenario: checking if suspended when not
	 * 			Given the dimmer lamp is turned on
	 * 			And it has not been suspended yet
	 * 			When I check if suspended
	 * 			Then it is not
	 * 		Scenario: suspending
	 * 			Given the dimmer lamp is turned on
	 * 			And it is not suspended
	 * 			When I call suspend()
	 * 			Then the method returns true
	 * 			And the dimmer lamp is suspended
	 * 		Scenario: checking the emergency
	 * 			Given the dimmer lamp is turned on
	 * 			And it has just been suspended
	 * 		 	When I call emergency()
	 * 		 	Then the emergency is between 0.0 and 1.0
	 * 		 Scenario: resuming
	 * 		 	Given the dimmer lamp is turned on
	 * 		 	And it is suspended
	 * 		 	When I call resume()
	 * 		 	Then the method returns true
	 * 		 	And the dimmer lamp is not suspended
	 * </pre>
	 *
	 * <p><strong>Contract</strong></p>
	 *
	 * <pre>
	 *  pre {@code this.registrationTable.has(DimmerLamp.EQUIPMENT_UID)}
	 *  post {@code true} // no postcondition
	 * </pre>
	 * @throws Exception
	 */
	public void integrationTestDimmerLamp() throws Exception {

		DeviceControl outbound = this.registrationTable.get(DimmerLamp.EQUIPMENT_UID);

		this.logMessage("Dimmer lamp tests starts.");
		TestsStatistics statistics = new TestsStatistics();
		try {
			this.logMessage("Feature: adjustable appliance mode management");
			this.logMessage("	Scenario: getting the max mode index");
			this.logMessage("		Given the dimmer lamp has just been turned on");
			this.logMessage("		When I call maxMode()");
			this.logMessage("   	Then the result is its max mode index");
			final int maxMode = outbound.maxMode();

			statistics.updateStatistics();

			this.logMessage("  Scenario: getting the current mode index");
			this.logMessage("    Given the dimmer lamp has just been turned on");
			this.logMessage("    When I call currentMode()");
			this.logMessage("    Then the current mode is its max mode");
			int result = outbound.currentMode();
			if ( result != maxMode ) {
				this.logMessage("		but was: " + result);
				statistics.incorrectResult();
			}

			statistics.updateStatistics();

			this.logMessage("	Scenario: going down one mode index");
			this.logMessage("    Given the dimmer lamp is turned on");
			this.logMessage("    And the current mode index is the max mode index");
			this.logMessage("	 When I call downMode()");
			this.logMessage("	 Then the method returns true");

			boolean downResult = outbound.downMode();
			if ( !downResult ) {
				this.logMessage("	but was: false");
				statistics.incorrectResult();
			}
			this.logMessage("	And the current mode is its max mode minus one");
			result = outbound.currentMode();
			if (result != maxMode - 1) {
				this.logMessage("	but was: " + result);

				statistics.incorrectResult();
			}

			statistics.updateStatistics();

			this.logMessage("   Scenario: going up one mode index");
			this.logMessage("		Given the dimmer lamp is turned on");
			this.logMessage("    And the current mode index is the max mode index minus one");
			this.logMessage("    When I call upMode()");
			this.logMessage("    Then the method returns true");
			boolean upResult = outbound.upMode();
			if (!upResult) {
				this.logMessage("	but was false");
				statistics.incorrectResult();
			}
			this.logMessage("	And the current mode is its max mode");
			result = outbound.currentMode();
			if (result != maxMode) {
				this.logMessage("	but was: " + result);
				statistics.incorrectResult();
			}

			statistics.updateStatistics();

			this.logMessage("	Scenario: setting the mode index");
			this.logMessage("		Given the dimmer lamp is turned on");
			int index = 1;
			this.logMessage("		And the mode index 1 is legitimate");
			if (index > maxMode) {
				this.logMessage("	but was not");
				statistics.failedCondition();
			}
			this.logMessage("	When I call setMode(1)");
			this.logMessage("	Then the method returns true");
			boolean setResult = outbound.setMode(index);
			if (!setResult) {
				this.logMessage("	but was false");
				statistics.incorrectResult();
			}
			this.logMessage("	And the current mode is 1");
			result = outbound.currentMode();
			if (result != index) {
				this.logMessage("	but was: " + result);
				statistics.incorrectResult();
			}

			statistics.updateStatistics();

			this.logMessage("Feature MAXIMIZE_POWER");
			this.logMessage("	Scenario: set the mode index to the maximum");
			this.logMessage("		Given the dimmer lamp is turned on");
			this.logMessage(" 		When I call setMode(maxMode)");
			this.logMessage("		Then the method returns true");
			setResult = outbound.setMode(maxMode);
			if (!setResult) {
				this.logMessage("	but was false");
				statistics.incorrectResult();
			}
			this.logMessage("		And the current mode is max mode");
			result = outbound.currentMode();
			if (result != maxMode) {
				this.logMessage("	but was not " + maxMode + " instead "+ result);
				statistics.incorrectResult();
			}

			statistics.updateStatistics();

			this.logMessage("Feature: Getting the power consumption given a mode");
			this.logMessage("  Scenario: getting the power consumption of the maximum mode");
			this.logMessage("    Given the dimmer lamp is turned on");
			this.logMessage("    When I get the power consumption of the maximum mode");
			double modeConsumption = outbound.getModeConsumption(maxMode);
			this.logMessage("    Then the result is the maximum power consumption of the dimmer lamp");

			statistics.updateStatistics();

			this.logMessage("Feature: suspending and resuming");
			this.logMessage("  Scenario: checking if suspended when not");
			this.logMessage("    Given the dimmer lamp is turned on");
			this.logMessage("    And it has not been suspended yet");
			this.logMessage("    When I check if suspended");
			boolean suspendedResult = outbound.suspended();
			this.logMessage("    Then it is not");
			if (suspendedResult) {
				this.logMessage("      but it was!");
				statistics.incorrectResult();
			}

			statistics.updateStatistics();

			this.logMessage("  Scenario: suspending");
			this.logMessage("    Given the dimmer lamp is turned on");
			this.logMessage("    And it is not suspended");
			this.logMessage("    When I call suspend()");
			suspendedResult = outbound.suspend();
			this.logMessage("    Then the method returns true");
			if (!suspendedResult) {
				this.logMessage("      but was false");
				statistics.incorrectResult();
			}
			this.logMessage("    And the dimmer lamp is suspended");
			suspendedResult = outbound.suspended();
			if (!suspendedResult) {
				this.logMessage("      but it was not!");
				statistics.incorrectResult();
			}

			statistics.updateStatistics();

			this.logMessage("  Scenario: checking the emergency");
			this.logMessage("    Given the dimmer lamp is turned on");
			this.logMessage("    And it has just been suspended");
			this.logMessage("    When I call emergency()");
			double emergencyResult = outbound.emergency();
			this.logMessage("    Then the emergency is between 0.0 and 1.0");
			if (emergencyResult < 0.0 || emergencyResult > 1.0) {
				this.logMessage("      but was: " + emergencyResult);
				statistics.incorrectResult();
			}

			statistics.updateStatistics();

			this.logMessage("  Scenario: resuming");
			this.logMessage("    Given the dimmer lamp is turned on");
			this.logMessage("    And it is suspended");
			this.logMessage("    When I call resume()");
			boolean resumeResult = outbound.resume();
			this.logMessage("    Then the method returns true");
			if (!resumeResult) {
				this.logMessage("      but was false");
				statistics.incorrectResult();
			}
			this.logMessage("    And the dimmer lamp is not suspended");
			suspendedResult = outbound.suspended();
			if (suspendedResult) {
				this.logMessage("      but it was!");
				statistics.incorrectResult();
			}

			statistics.updateStatistics();


		} catch (Exception e) {
			e.printStackTrace();
		}

		statistics.statisticsReport(this);

		this.logMessage("Heat pump tests end");
	}


	protected void scheduleTestDimmerLamp() {
		// Test for the dimmer lamp
		Instant dimmerLampTestOn1=
				this.ac.getStartInstant().plusSeconds(
						(DimmerLampTester.SWITCH_ON_DELAY1 +
								DimmerLampTester.SWITCH_OFF_DELAY1) / 2);
		Instant dimmerLampTestOn2 =
				this.ac.getStartInstant().plusSeconds(
						(DimmerLampTester.SWITCH_ON_DELAY2 +
								DimmerLampTester.SWITCH_OFF_DELAY2) / 2);
		this.traceMessage("HEM schedules the heat pump test.\n");
		long delayOn1 = this.ac.nanoDelayUntilInstant(dimmerLampTestOn1);
		long delayOn2 = this.ac.nanoDelayUntilInstant(dimmerLampTestOn2);

		// schedule the switch on dimmer lamp in one second
		this.scheduleTaskOnComponent(
				new AbstractTask() {
					@Override
					public void run() {
						try {
							integrationTestDimmerLamp();
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
							integrationTestDimmerLamp();
						} catch (Exception e) {
							throw new BCMRuntimeException(e);
						}
					}
				}, delayOn2, TimeUnit.NANOSECONDS);

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
//	protected void		scheduleTestHeater()
//	{
//		// Test for the heater
//		Instant heaterTestStart =
//				this.ac.getStartInstant().plusSeconds(
//							(HeaterUnitTester.SWITCH_ON_DELAY +
//											HeaterUnitTester.SWITCH_OFF_DELAY)/2);
//		this.traceMessage("HEM schedules the heater test.\n");
//		long delay = this.ac.nanoDelayUntilInstant(heaterTestStart);
//
//		// schedule the switch on heater in one second
//		this.scheduleTaskOnComponent(
//				new AbstractComponent.AbstractTask() {
//					@Override
//					public void run() {
//						try {
//							testHeater();
//						} catch (Throwable e) {
//							throw new BCMRuntimeException(e) ;
//						}
//					}
//				}, delay, TimeUnit.NANOSECONDS);
//	}
}
// -----------------------------------------------------------------------------
