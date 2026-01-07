package equipments.dimmerlamp.test;

import equipments.dimmerlamp.DimmerLamp;
import equipments.dimmerlamp.connections.DimmerLampExternalOutboundPort;
import equipments.dimmerlamp.connections.DimmerLampUserOutboundPort;
import equipments.dimmerlamp.interfaces.DimmerLampExternalCI;
import equipments.dimmerlamp.interfaces.DimmerLampUserCI;
import fr.sorbonne_u.alasca.physical_data.Measure;
import fr.sorbonne_u.alasca.physical_data.MeasurementUnit;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.RequiredInterfaces;
import fr.sorbonne_u.components.connectors.AbstractConnector;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import fr.sorbonne_u.components.hem2025e1.CVMIntegrationTest;
import fr.sorbonne_u.components.utils.tests.TestsStatistics;
import fr.sorbonne_u.exceptions.PreconditionException;
import fr.sorbonne_u.utils.aclocks.*;

import java.time.Instant;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * The class <code>test.dimmerlamp.equipments.DimmerLampTester</code>.
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

@RequiredInterfaces(required = {DimmerLampUserCI.class, DimmerLampExternalCI.class, ClocksServerCI.class})
public class DimmerLampTester
extends AbstractComponent {

    public static boolean VERBOSE = false;

    /** when tracing, x coordinate of the window relative position.			*/
    public static int				X_RELATIVE_POSITION = 0;
    /** when tracing, y coordinate of the window relative position.			*/
    public static int				Y_RELATIVE_POSITION = 0;


    /**	in clock-driven scenario, the delay from the start instant at which
     *  the heatPump is switched on for the first time.											*/
    public static final int SWITCH_ON_DELAY1 = 3;
    /**	in clock-driven scenario, the delay from the start instant at which
     *  the heat pump is put in heating mode.                                  */
    public static final int SWITCH_OFF_DELAY1 = 8;
    /**	in clock-driven scenario, the delay from the start instant at which
     *  the heatPump is switched on for the second time.											*/
    public static final int SWITCH_ON_DELAY2 = 11;
    /**	in clock-driven scenario, the delay from the start instant at which
     *  the heat pump is put in heating mode.                                  */
    public static final int SWITCH_OFF_DELAY2 = 14;
    /**	in clock-driven scenario, the delay from the start instant at which
     *  the heatPump is switched on for the second time. */

    protected static final int NUMBER_THREADS = 1;
    protected static final int NUMBER_SCHEDULABLE_THREADS = 1;

    protected static final String BASE_REFLECTION_INBOUND_PORT = "DIMMER-LAMP-TESTER-INBOUND-PORT";

    protected DimmerLampUserOutboundPort userOutboundPort;

    protected DimmerLampExternalOutboundPort externalOutboundPort;

    protected String userInboundPortURI;

    protected String externalInboundPortURI;

    protected TestsStatistics statistics;

    protected final boolean isUnitTest;

    protected final String connectorUserClassName;

    protected final String connectorExternalClassName;

    protected DimmerLampTester(
            boolean isUnitTest,
            String userInboundPortURI,
            String externalInboundPortURI,
            Class<? extends AbstractConnector> connectorUserClass,
            Class<? extends AbstractConnector> connectorExternalClass) throws Exception {
        this(isUnitTest,
                BASE_REFLECTION_INBOUND_PORT,
                userInboundPortURI,
                externalInboundPortURI,
                connectorUserClass,
                connectorExternalClass);
    }

    protected DimmerLampTester(
            boolean isUnitTest,
            String reflectionInboundPortURI,
            String userInboundPortURI,
            String externalInboundPortURI,
            Class<? extends AbstractConnector> connectorUserClass,
            Class<? extends AbstractConnector> connectorExternalClass) throws Exception {
        super(reflectionInboundPortURI, NUMBER_THREADS, NUMBER_SCHEDULABLE_THREADS);

        assert userInboundPortURI != null && ! userInboundPortURI.isEmpty() : new PreconditionException("lampURI == null || lamp.isEmpty()");

        this.isUnitTest = isUnitTest;
        this.statistics = new TestsStatistics();

        this.userOutboundPort = new DimmerLampUserOutboundPort(this);
        this.userOutboundPort.publishPort();
        this.userInboundPortURI = userInboundPortURI;
        this.connectorUserClassName = connectorUserClass.getCanonicalName();

        this.externalOutboundPort = new DimmerLampExternalOutboundPort(this);
        this.externalOutboundPort.publishPort();
        this.externalInboundPortURI = externalInboundPortURI;
        this.connectorExternalClassName = connectorExternalClass.getCanonicalName();

        if (DimmerLampTester.VERBOSE) {
            this.tracer.get().setTitle("Dimmer Lamp test component");
            this.tracer.get().setRelativePosition(X_RELATIVE_POSITION,
                    Y_RELATIVE_POSITION);
            this.toggleTracing();
        }
    }

    /**
     * test of the {@code isOn} method when the fan is off.
     *
     * <p><strong>Description</strong></p>
     *
     * <p>Gherkin specification:</p>
     * <pre>
     * Feature: Getting the state of the dimmer lamp
     *
     *   Scenario: getting the state when off
     *     Given the dimmer lamp is initialised and never been used yet
     *     When the dimmer lamp has not been used yet
     *     Then the dimmer lamp is off
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
    public void testInitialState() {

        this.logMessage("Feature: Getting the state of the dimmer lamp");
        this.logMessage("   Scenario: getting the state when off");
        this.logMessage("   Given the dimmer lamp is initialised and never been used yet");
        this.logMessage("   When the dimmer lamp has not been used yet");

        try {
            boolean result = ! this.userOutboundPort.isOn();
            if (result) {
                this.logMessage("   Then the dimmer lamp is off");
            } else {
                this.logMessage("   but was on");
                this.statistics.incorrectResult();
            }

        } catch (Exception e) {
            this.statistics.incorrectResult();
            this.logMessage("The exception " + e + "has been raised");
        }

        this.statistics.updateStatistics();
    }

    /**
     * test of the {@code SwitchOn} method when the fan is off.
     *
     * <p><strong>Description</strong></p>
     *
     * <p>Gherkin specification:</p>
     * <pre>
     * Feature: Switching on the dimmer lamp
     *
     *   Scenario: Switching on the dimmer lamp when off
     *     Given the dimmer lamp is initialised
     *     And the dimmer lamp is off
     *     When the user switches on the lamp
     *     Then the dimmer lamp is on
     *
     *   Scenario: Switching on the dimmer lamp when on
     *     Given the dimmer lamp is on
     *     When the user switches on the lamp
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
    public void testSwitchOn() {
        this.logMessage("Feature: Switching on the dimmer lamp");
        this.logMessage("   Scenario: Switching off the dimmer lamp when off");
        this.logMessage("   Given the dimmer lamp is initialised");
        this.logMessage("   And the dimmer lamp is off");

        try {
            this.logMessage("   When the user switches on the dimmer lamp");
            this.userOutboundPort.switchOn();
            boolean result = this.userOutboundPort.isOn();
            if (result) {
                this.logMessage("   Then the dimmer lamp is on");
            } else {
                this.logMessage("   but was off");
                this.statistics.incorrectResult();
            }
        } catch (Exception e) {
            this.logMessage("The exception " + e + "has been raised");
            this.statistics.incorrectResult();
        }

        this.statistics.updateStatistics();

        this.logMessage("Scenario: Switching on the dimmer lamp when on");
        this.logMessage("   Given the dimmer lamp is on");

        try {
            this.logMessage("   When the user switches on the lamp");
            this.userOutboundPort.switchOn();
            this.logMessage("But was not");
            this.statistics.incorrectResult();
        } catch (Exception e) {
            this.logMessage("   Then a precondition exception is thrown");
        }

        this.statistics.updateStatistics();
    }

    /**
     * test of the {@code switchOff} method when the fan is off.
     *
     * <p><strong>Description</strong></p>
     *
     * <p>Gherkin specification:</p>
     * <pre>
     * Feature: Switching off the dimmer lamp
     *
     *   Scenario: Switching off the dimmer lamp when on
     *     Given the dimmer lamp is initialised
     *     And the dimmer lamp is on
     *     When the user switches off the dimmer lamp
     *     Then the dimmer lamp is off
     *
     *   Scenario: Setting the power when the lamp is off
     *     Given the dimmer lamp is off
     *     When the power of the lamp is set to a given wattage
     *     Then a precondition exception is thrown
     *
     *   Scenario: Getting the power of the lamp when the lamp is off
     *      Given the dimmer lamp is off
     *      When getting the power of the lamp through the external interface
     *      Then a precondition exception is thrown
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
    public void testSwitchOff() {

        this.logMessage("Feature: Switching off the dimmer lamp");
        this.logMessage("   Scenario: Switching off the dimmer lamp when on");
        this.logMessage("   Given the dimmer lamp is initialised");
        this.logMessage("   And the dimmer lamp is on");

        try {
            this.logMessage("   When the user switches off the dimmer lamp");
            this.userOutboundPort.switchOff();
            boolean result = ! this.userOutboundPort.isOn();
            if (result) {
                this.logMessage("   Then the dimmer lamp is off");
            } else {
                this.logMessage("   but was on");
                this.statistics.incorrectResult();
            }
        } catch (Exception e) {
            this.logMessage("The exception " + e + "has been raised");
            this.statistics.incorrectResult();
        }

        this.statistics.updateStatistics();

        this.logMessage("   Scenario: Setting the power when the lamp is off");
        this.logMessage("   Given the dimmer lamp is off");

        try {
            this.logMessage("   When the power of the lamp is set to a given wattage");
            this.externalOutboundPort.setPower(DimmerLamp.BASE_POWER_VARIATION);
            this.logMessage("But was not");
            this.statistics.incorrectResult();
        } catch (Exception e) {
            this.logMessage("   Then a precondition exception is thrown");
        }

        this.statistics.updateStatistics();

        this.logMessage("   Scenario: Getting the power of the lamp when the lamp is off");
        this.logMessage("   Given the dimmer lamp is off");

        try {
            this.logMessage("   When getting the power of the lamp through the external interface");
            this.externalOutboundPort.getCurrentPowerLevel();
            this.logMessage("But was not");
            this.statistics.incorrectResult();
        } catch (Exception e) {
            this.logMessage("   Then a precondition exception is thrown");
        }

        this.statistics.updateStatistics();
    }

    /**
     * test of the {@code setPower} and {@code getCurrentPowerLevel} method when the fan is off.
     *
     * <p><strong>Description</strong></p>
     *
     * <p>Gherkin specification:</p>
     * <pre>
     * Feature: getting and setting the power level of the dimmer lamp
     *
     *   Scenario: Getting the power level of the lamp when just initialised
     *     Given the dimmer lamp is initialised
     *     And the dimmer lamp has not been used yet
     *     When the user gets the current power level
     *     Then the current power level is equal to base power level
     *   Scenario: Setting the power level of the dimmer lamp to a power level between the minimum power level and the maximum power level
     *     Given the dimmer lamp is initialised
     *     And the dimmer lamp is on
     *     When the user sets the power to a level between the minimum power level and the maximum power level
     *     Then the current power level is equal to the power level set by the user
     *
     *   Scenario: Setting the power to null
     *      Given the dimmer lamp is initialised
     *      And the dimmer lamp is on
     *      When the power is set to null
     *      Then a precondition exception is thrown
     *
     *   Scenario: Setting the power to a negative wattage
     *      Given the dimmer lamp is initialised
     *      And the dimmer lamp is on
     *      When the power is set to a negative wattage
     *      Then a precondition exception is thrown
     *
     *   Scenario: Setting the power to an invalid wattage
     *      Given the dimmer lamp is intialised
     *      And the dimmer lamp is on
     *      When the power is set to the maximum wattage + 1
     *      Then a precondition exception is thrown
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
    public void testManipulatePower() {

        this.logMessage("Feature: getting and setting the power level of the dimmer lamp");
        this.logMessage("   Scenario: Getting the power level of the lamp when just initialised");
        this.logMessage("   Given the dimmer lamp is initialised");
        this.logMessage("   And the dimmer lamp has not been used yet");
        this.logMessage("   When the user gets the current power level");

        try {
            Measure<Double> result = this.externalOutboundPort.getCurrentPowerLevel();

            if (result.getData() == DimmerLamp.BASE_POWER_VARIATION.getData()) {
                this.logMessage("   Then the current power level is equal to the base power level");
            } else {
                this.logMessage("   But was different from the power the base power level");
                this.statistics.incorrectResult();
            }
        } catch (Exception e) {
            this.logMessage("The exception" + e + "has been raised");
            this.statistics.incorrectResult();
        }
        this.statistics.updateStatistics();

        this.logMessage("   Scenario: Setting the power level of the dimmer lamp to a power level between "
                + "the minimum power level and the maximum power level");
        this.logMessage("  Given the dimmer lamp is initialised");
        this.logMessage("  And the dimmer lamp is on");
        this.logMessage("When the user sets the power to a level between the minimum power level and the maximum power level");

        try {
            Random rd = new Random();
            Measure<Double> random_power_level = new Measure<>(rd.nextDouble() * 100., MeasurementUnit.WATTS);
            this.externalOutboundPort.setPower(random_power_level);
            Measure<Double> result = this.externalOutboundPort.getCurrentPowerLevel();

            if (result.getData() == random_power_level.getData()) {
                this.logMessage("   Then the current power level is equal to the power level set by the user");
            } else {
                this.logMessage("   But the current power level is diffrent from the power level set by the user");
                this.statistics.incorrectResult();
            }

        } catch (Exception e) {
            this.logMessage("The exception" + e + "has been raised");
            this.statistics.incorrectResult();
        }

        this.statistics.updateStatistics();

        this.logMessage("   Scenario: Setting the power to null");
        this.logMessage("   Given the dimmer lamp is initialised");
        this.logMessage("   And the dimmer lamp is on");
        try {
            this.logMessage("   When the power is set to null");
            this.externalOutboundPort.setPower(null);
            this.logMessage("But was not");
            this.statistics.incorrectResult();
        } catch (Exception e) {
            this.logMessage("   Then a precondition exception is thrown");
        }

        this.logMessage("   Scenario: Setting the power to a negative wattage");
            this.logMessage("   Given the dimmer lamp is initialised");
        this.logMessage("   And the dimmer lamp is on");
        try {
            this.logMessage("   When the power is set to a negative wattage");
            this.externalOutboundPort.setPower(new Measure<>(-1., MeasurementUnit.WATTS));
            this.logMessage("But was not");
            this.statistics.incorrectResult();
        } catch (Exception e) {
            this.logMessage("   Then a precondition exception is thrown");
        }

        this.statistics.updateStatistics();

        this.logMessage("   Scenario: Setting the power to an invalid wattage");
        this.logMessage("   Given the dimmer lamp is initialised");
        this.logMessage("   And the dimmer lamp is on");
        try {
            this.logMessage("   When the power is set to the maximum wattage + 1");
            double max_power = DimmerLamp.MAX_POWER_VARIATION.getData() + 1.;
            this.externalOutboundPort.setPower(new Measure<>(max_power, MeasurementUnit.WATTS));
            this.logMessage("But was not");
            this.statistics.incorrectResult();
        } catch (Exception e) {
            this.logMessage("   Then a precondition exception is thrown");
        }

        this.statistics.updateStatistics();
    }

    public void runAllUnitTests() {
        this.testInitialState();
        this.testSwitchOn();
        this.testManipulatePower();
        this.testSwitchOff();

        this.statistics.statisticsReport(this);
    }

    @Override
    public synchronized void start() throws ComponentStartException {

        super.start();

        try {
            this.doPortConnection(
                    this.userOutboundPort.getPortURI(),
                    this.userInboundPortURI,
                    this.connectorUserClassName
            );
            this.doPortConnection(
                    this.externalOutboundPort.getPortURI(),
                    this.externalInboundPortURI,
                    this.connectorExternalClassName
            );
        } catch (Exception e) {
            throw new ComponentStartException(e);
        }

    }


    @Override
    public synchronized void	finalise() throws Exception
    {
        this.doPortDisconnection(this.userOutboundPort.getPortURI());
        this.doPortDisconnection(this.externalOutboundPort.getPortURI());
        super.finalise();
    }

    @Override
    public synchronized void	shutdown() throws ComponentShutdownException
    {
        try {
            this.userOutboundPort.unpublishPort();
            this.externalOutboundPort.unpublishPort();
        } catch (Exception e) {
            throw new ComponentShutdownException(e) ;
        }
        super.shutdown();
    }

    @Override
    public synchronized void execute() throws Exception {

        if ( this.isUnitTest ) {
            this.runAllUnitTests();
        } else {
            ClocksServerOutboundPort clocksServerOutboundPort =
                    new ClocksServerOutboundPort(this);
            clocksServerOutboundPort.publishPort();
            this.doPortConnection(
                    clocksServerOutboundPort.getPortURI(),
                    ClocksServer.STANDARD_INBOUNDPORT_URI,
                    ClocksServerConnector.class.getCanonicalName());
            this.traceMessage("Heater tester gets the clock.\n");
            AcceleratedClock ac =
                    clocksServerOutboundPort.getClock(
                            CVMIntegrationTest.CLOCK_URI);
            this.doPortDisconnection(
                    clocksServerOutboundPort.getPortURI());
            clocksServerOutboundPort.unpublishPort();

            Instant startInstant = ac.getStartInstant();
            Instant heatPumpSwitchOn1 = startInstant.plusSeconds(SWITCH_ON_DELAY1);
            Instant heatPumpSwitchOff1 = startInstant.plusSeconds(SWITCH_OFF_DELAY1);
            Instant heatPumpSwitchOn2 = startInstant.plusSeconds(SWITCH_ON_DELAY2);
            Instant heatPumpSwitchOff2 = startInstant.plusSeconds(SWITCH_OFF_DELAY2);
            this.traceMessage("Heat pump tester waits until start.\n");

            ac.waitUntilStart();

            long delayToSwitchOn1 = ac.nanoDelayUntilInstant(heatPumpSwitchOn1);
            long delayToSwitchOff1 = ac.nanoDelayUntilInstant(heatPumpSwitchOff1);

            long delayToSwitchOn2 = ac.nanoDelayUntilInstant(heatPumpSwitchOn2);
            long delayToSwitchOff2 = ac.nanoDelayUntilInstant(heatPumpSwitchOff2);

            AbstractComponent o = this;

            this.scheduleTaskOnComponent(
                    new AbstractComponent.AbstractTask() {
                        @Override
                        public void run() {
                            try {
                                o.traceMessage("dimmer lamp switches on1.\n");
                                userOutboundPort.switchOn();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }, delayToSwitchOn1, TimeUnit.NANOSECONDS);

            this.scheduleTaskOnComponent(
                    new AbstractComponent.AbstractTask() {
                        @Override
                        public void run() {
                            try {
                                o.traceMessage("dimmer lamp switches off1.\n");
                                userOutboundPort.switchOff();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }, delayToSwitchOff1, TimeUnit.NANOSECONDS);

            this.scheduleTaskOnComponent(
                    new AbstractComponent.AbstractTask() {
                        @Override
                        public void run() {
                            try {
                                o.traceMessage("dimmer lamp switches on2.\n");
                                userOutboundPort.switchOn();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }, delayToSwitchOn2, TimeUnit.NANOSECONDS);


            this.scheduleTaskOnComponent(
                    new AbstractComponent.AbstractTask() {
                        @Override
                        public void run() {
                            try {
                                o.traceMessage("dimmer lamp switches off2.\n");
                                userOutboundPort.switchOff();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }, delayToSwitchOff2, TimeUnit.NANOSECONDS);
        }

    }
}
