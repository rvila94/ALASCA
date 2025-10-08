package equipments.dimmerlamp;

import equipments.dimmerlamp.connections.DimmerLampInboundPort;
import equipments.dimmerlamp.connections.DimmerLampOutboundPort;
import fr.sorbonne_u.alasca.physical_data.Measure;
import fr.sorbonne_u.alasca.physical_data.MeasurementUnit;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.RequiredInterfaces;
import fr.sorbonne_u.components.connectors.ConnectorI;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import fr.sorbonne_u.components.hem2025.tests_utils.TestsStatistics;
import fr.sorbonne_u.components.hem2025e1.CVMIntegrationTest;
import fr.sorbonne_u.exceptions.PreconditionException;
import fr.sorbonne_u.utils.aclocks.*;
import jdk.nashorn.internal.runtime.ECMAException;

import java.security.Permission;
import java.util.Random;

/**
 * The class <code>equipments.dimmerlamp.DimmerLampTester</code>.
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

@RequiredInterfaces(required = {DimmerLampCI.class, ClocksServerCI.class})
public class DimmerLampTester
extends AbstractComponent {

    public static boolean VERBOSE = false;

    protected static final int NUMBER_THREADS = 1;
    protected static final int NUMBER_SCHEDULABLE_THREADS = 0;

    protected static final String BASE_REFLECTION_INBOUND_PORT = "DIMMER-LAMP-TESTER-INBOUND-PORT";

    protected DimmerLampOutboundPort outbound;

    protected String dimmerLampInboundPortURI;

    protected TestsStatistics statistics;

    protected final boolean isUnitTest;

    protected final String connectorClassName;

    public DimmerLampTester(boolean isUnitTest, String lampInboundPortURI, ConnectorI connectorClass) throws Exception {
        this(isUnitTest, BASE_REFLECTION_INBOUND_PORT, lampInboundPortURI, connectorClass);
    }

    public DimmerLampTester(
            boolean isUnitTest,
            String reflectionInboundPortURI,
            String lampInboundPortURI,
            ConnectorI connectorClass) throws Exception {
        super(reflectionInboundPortURI, NUMBER_THREADS, NUMBER_SCHEDULABLE_THREADS);

        assert lampInboundPortURI != null && ! lampInboundPortURI.isEmpty() : new PreconditionException("lampURI == null || lamp.isEmpty()");

        this.isUnitTest = isUnitTest;
        this.statistics = new TestsStatistics();

        this.outbound = new DimmerLampOutboundPort(this);
        this.outbound.publishPort();
        this.dimmerLampInboundPortURI = lampInboundPortURI;

        this.connectorClassName = connectorClass.getClass().getCanonicalName();
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
            boolean result = ! this.outbound.isOn();
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
        // TODO ?????
        this.logMessage("   And the dimmer lamp is off");

        try {
            this.logMessage("   When the user switches on the dimmer lamp");
            this.outbound.switchOff();
            boolean result = ! this.outbound.isOn();
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
        // TODO ?????
        this.logMessage("   And the dimmer lamp is on");

        try {
            this.logMessage("   When the user switches off the dimmer lamp");
            this.outbound.switchOff();
            boolean result = ! this.outbound.isOn();
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
    }

    /**
     * test of the {@code setVariationPower} and {@code getCurrentPowerLevel} method when the fan is off.
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
            Measure<Integer> result = this.outbound.getCurrentPowerLevel();

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
            Measure<Integer> random_power_level = new Measure<>(rd.nextInt(), MeasurementUnit.WATTS);
            this.outbound.setVariationPower(random_power_level);
            Measure<Integer> result = this.outbound.getCurrentPowerLevel();

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
    }

    public void runAllUnitTests() {
        this.testInitialState();
        this.testSwitchOn();
        this.testSwitchOff();
        this.testManipulatePower();
    }

    @Override
    public synchronized void start() throws ComponentStartException {

        super.start();

        try {
            this.doPortConnection(
                    this.outbound.getPortURI(),
                    this.dimmerLampInboundPortURI,
                    this.connectorClassName
            );
        } catch (Exception e) {
            throw new ComponentStartException(e);
        }

    }


    @Override
    public synchronized void	finalise() throws Exception
    {
        this.doPortDisconnection(this.outbound.getPortURI());
        super.finalise();
    }

    @Override
    public synchronized void	shutdown() throws ComponentShutdownException
    {
        try {
            this.outbound.unpublishPort();
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
            clocksServerOutboundPort = null;
        }

    }
}
