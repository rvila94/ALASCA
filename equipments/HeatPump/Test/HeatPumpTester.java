package equipments.HeatPump.Test;

import equipments.HeatPump.HeatPump;
import equipments.HeatPump.compressor.Compressor;
import equipments.HeatPump.connections.*;
import equipments.HeatPump.connections.*;
import equipments.HeatPump.interfaces.HeatPumpExternalControlCI;
import equipments.HeatPump.interfaces.HeatPumpInternalControlCI;
import equipments.HeatPump.interfaces.HeatPumpUserCI;
import equipments.HeatPump.temperatureSensor.TemperatureSensor;
import fr.sorbonne_u.alasca.physical_data.Measure;
import fr.sorbonne_u.alasca.physical_data.MeasurementUnit;
import fr.sorbonne_u.alasca.physical_data.SignalData;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.RequiredInterfaces;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import fr.sorbonne_u.components.hem2025.tests_utils.TestsStatistics;
import fr.sorbonne_u.components.hem2025e1.CVMIntegrationTest;
import fr.sorbonne_u.utils.aclocks.*;

import java.time.Instant;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * The class <code>Test.HeatPump.equipments.HeatPumpTester</code>.
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
@RequiredInterfaces(required = {
        HeatPumpUserCI.class,
        HeatPumpInternalControlCI.class,
        HeatPumpExternalControlCI.class,
        ClocksServerCI.class
})
public class HeatPumpTester
extends AbstractComponent {

    public static boolean VERBOSE = false;
    public static int X_RELATIVE_POSITION = 0;
    public static int Y_RELATIVE_POSITION = 0;
    /**	in clock-driven scenario, the delay from the start instant at which
     *  the heatPump is switched on for the first time.											*/
    public static final int SWITCH_ON_DELAY1 = 2;
    /**	in clock-driven scenario, the delay from the start instant at which
     *  the heat pump is put in heating mode.                                  */
    public static final int     SET_HEATING_DELAY = 5;
    /**	in clock-driven scenario, the delay from the start instant at which
     *  the heat pump stops heating.                                  */
    public static final int SWITCH_OFF_DELAY1 = 8;
    /**	in clock-driven scenario, the delay from the start instant at which
     *  the heatPump is switched on for the second time.											*/
    public static final int SWITCH_ON_DELAY2 = 11;
    /**	in clock-driven scenario, the delay from the start instant at which
     *  the heat pump is put in heating mode.                                  */
    public static final int     SET_COOLING_DELAY = 14;
    /**	in clock-driven scenario, the delay from the start instant at which
     *  the heat pump stops heating.                                  */
    public static final Measure<Double> COOLING_TEMPERATURE = new Measure<>(15.0, MeasurementUnit.CELSIUS);
    /** in clock-driven scenario, temperature used in the cooling and heating
     *  scenarios
     */
    public static final Measure<Double> Heating_TEMPERATURE = new Measure<>(31., MeasurementUnit.CELSIUS);
    /**	in clock-driven scenario, the delay from the start instant at which
     *  the heatPump is switched off for the second time. */
    public static final int SWITCH_OFF_DELAY2 = 16;
    protected final static String REFLECTION_INBOUND_URI = "HEATPUMP-TESTER-URI";
    protected final static int NUMBER_THREADS = 1;
    protected final static int NUMBER_SCHEDULABLE_THREADS = 1;

    protected boolean isUnitTest;

    protected HeatPumpUserOutboundPort userOutboundPort;
    protected String userInboundPortURI;
    protected HeatPumpInternalControlOutboundPort internalOutboundPort;
    protected String internalInboundPortURI;
    protected HeatPumpExternalControlOutboundPort externalOutboundPort;
    protected String externalExternalPortURI;

    protected TestsStatistics statistics;

    protected HeatPumpTester(
            boolean isUnitTest,
            String heatPumpUserInboundURI,
            String heatPumpInternalInboundURI,
            String heatPumpExternalInboundURI) throws Exception {
        super(REFLECTION_INBOUND_URI, NUMBER_THREADS, NUMBER_SCHEDULABLE_THREADS);
        this.isUnitTest = isUnitTest;

        this.userOutboundPort = new HeatPumpUserOutboundPort(this);
        this.userOutboundPort.publishPort();
        this.userInboundPortURI = heatPumpUserInboundURI;

        this.internalOutboundPort = new HeatPumpInternalControlOutboundPort(this);
        this.internalOutboundPort.publishPort();
        this.internalInboundPortURI = heatPumpInternalInboundURI;

        this.externalOutboundPort = new HeatPumpExternalControlOutboundPort(this);
        this.externalOutboundPort.publishPort();
        this.externalExternalPortURI = heatPumpExternalInboundURI;

        this.statistics = new TestsStatistics();

        if (HeatPumpTester.VERBOSE) {
            this.tracer.get().setTitle("Heat pump test component");
            this.tracer.get().setRelativePosition(X_RELATIVE_POSITION,
                    Y_RELATIVE_POSITION);
            this.toggleTracing();
        }
    }

    @Override
    public synchronized void start() throws ComponentStartException {

        super.start();

        try {
            this.doPortConnection(
                    this.userOutboundPort.getPortURI(),
                    this.userInboundPortURI,
                    HeatPumpUserConnector.class.getCanonicalName()
            );

            this.doPortConnection(
                    this.internalOutboundPort.getPortURI(),
                    this.internalInboundPortURI,
                    HeatPumpInternalControlConnector.class.getCanonicalName()
            );

            this.doPortConnection(
                    this.externalOutboundPort.getPortURI(),
                    this.externalExternalPortURI,
                    HeatPumpExternalControlConnector.class.getCanonicalName()
            );
        } catch (Exception e) {
            throw new ComponentStartException(e);
        }

    }


    @Override
    public synchronized void	finalise() throws Exception
    {
        this.doPortDisconnection(this.userOutboundPort.getPortURI());
        this.doPortDisconnection(this.internalOutboundPort.getPortURI());
        this.doPortDisconnection(this.externalOutboundPort.getPortURI());
        super.finalise();
    }

    @Override
    public synchronized void	shutdown() throws ComponentShutdownException
    {
        try {
            this.userOutboundPort.unpublishPort();
            this.internalOutboundPort.unpublishPort();
            this.externalOutboundPort.unpublishPort();
        } catch (Exception e) {
            throw new ComponentShutdownException(e) ;
        }
        super.shutdown();
    }

    /**
     * test of the {@code isOn} method when the heat pump is off.
     *
     * <p><strong>Description</strong></p>
     *
     * <p>Gherkin specification:</p>
     * <pre>
     * Feature: Getting the state of the heat pump
     *
     *   Scenario: getting the state when off
     *     Given the heat pump is initialised and never been used yet
     *     When the heat pump has not been used yet
     *     Then the heat pump is off
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

        this.logMessage("Feature: Getting the state of the heat pump");
        this.logMessage("   Scenario: getting the state when off");
        this.logMessage("   Given the heat pump is initialised and never been used yet");
        this.logMessage("   When the heat pump has not been used yet");

        try {
            boolean result = ! this.userOutboundPort.on();
            if (result) {
                this.logMessage("   Then the heat pump is off");
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
     * test of the {@code SwitchOn} method when the heat pump is off.
     *
     * <p><strong>Description</strong></p>
     *
     * <p>Gherkin specification:</p>
     * <pre>
     * Feature: Switching on the heat pump
     *
     *   Scenario: Switching on the heat pump when off
     *     Given the heat pump is initialised
     *     And the heat pump is off
     *     When the user switches on the lamp
     *     Then the heat pump is on
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
        this.logMessage("Feature: Switching on the heat pump");
        this.logMessage("   Scenario: Switching off the heat pump when off");
        this.logMessage("   Given the heat pump is initialised");
        this.logMessage("   And the heat pump is off");

        try {
            this.logMessage("   When the user switches on the heat pump");
            this.userOutboundPort.switchOn();
            boolean result = this.userOutboundPort.on();
            if (result) {
                this.logMessage("   Then the heat pump is on");
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
     * test of the {@code startHeating}, {@code stopHeating}, {@code heating} method when the heat pump is off.
     *
     * <p><strong>Description</strong></p>
     *
     * <p>Gherkin specification:</p>
     * <pre>
     * Feature: Heat pump's heating state
     *
     *   Scenario: Switching the state of the pump to heating
     *      Given the heat pump is initialised
     *      And the heat pump is on
     *      When the user puts the state to heating
     *      Then the state of the heat pump is the heating state
     *   Scenario: Stops heating state
     *      Given the heat pump is initialised
     *      And the heat pump is on
     *      And the state of the heat pump is the heating state
     *      When the user wants the heat pump to stop heating
     *      Then the state of the heat pump is not the heating state
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
    public void testHeatingState() {

        this.logMessage("Scenario: Switching the state of the pump to heating");
        this.logMessage("Given the heat pump is initialised");
        this.logMessage("And the heat pump is on");
        try {
            this.logMessage("When the user puts the state to heating");
            this.internalOutboundPort.startHeating();
            boolean result = this.internalOutboundPort.heating();
            if (result) {
                this.logMessage("   Then the state of the heat pump is the heating state");
            } else {
                this.logMessage("   but was not");
                this.statistics.incorrectResult();
            }
        } catch (Exception e) {
            this.logMessage("The exeception " + e + "has been raised");
            this.statistics.incorrectResult();
        }

        this.statistics.updateStatistics();

        this.logMessage("Scenario: Stops heating state");
        this.logMessage("   Given the heat pump is initialised");
        this.logMessage("   And the heat pump is on");
        this.logMessage("   And the state of the heat pump is the heating state");
        try {
            this.logMessage("   When the user wants the heat pump to stop heating");
            this.internalOutboundPort.stopHeating();
            boolean result = ! this.internalOutboundPort.heating();
            if (result) {
                this.logMessage("   Then the state of the heat pump is not the heating state");
            } else {
                this.logMessage("   but was not");
                this.statistics.incorrectResult();
            }
        } catch (Exception e) {
            this.logMessage("The exeception " + e + "has been raised");
            this.statistics.incorrectResult();
        }

        this.statistics.updateStatistics();

    }

    /**
     * test of the {@code startCooling}, {@code stopCooling} and {@code cooling} method when the heat pump is off.
     *
     * <p><strong>Description</strong></p>
     *
     * <p>Gherkin specification:</p>
     * <pre>
     * Feature: Heat pump's heating state
     *
     *   Scenario: Switching the state of the pump to cooling
     *      Given the heat pump is initialised
     *      And the heat pump is on
     *      When the user puts the state to cooling
     *      Then the state of the heat pump is the cooling state
     *   Scenario:
     *      Given the heat pump is initialised
     *      And the heat pump is on
     *      And the state of the heat pump is the cooling state
     *      When the user wants the heat pump to stop cooling
     *      Then the state of the heat pump is not the cooling state
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
    public void testCoolingState() {

        this.logMessage("Scenario: Switching the state of the pump to cooling");
        this.logMessage("Given the heat pump is initialised");
        this.logMessage("And the heat pump is on");
        try {
            this.logMessage("When the user puts the state to cooling");
            this.internalOutboundPort.startCooling();
            boolean result = this.internalOutboundPort.cooling();
            if (result) {
                this.logMessage("   Then the state of the heat pump is the cooling state");
            } else {
                this.logMessage("   but was not");
                this.statistics.incorrectResult();
            }
        } catch (Exception e) {
            this.logMessage("The exeception " + e + "has been raised");
            this.statistics.incorrectResult();
        }

        this.statistics.updateStatistics();

        this.logMessage("Scenario: Stops cooling state");
        this.logMessage("   Given the heat pump is initialised");
        this.logMessage("   And the heat pump is on");
        this.logMessage("   And the state of the heat pump is the cooling state");
        try {
            this.logMessage("   When the user wants the heat pump to stop cooling");
            this.internalOutboundPort.stopCooling();
            boolean result = ! this.internalOutboundPort.cooling();
            if (result) {
                this.logMessage("   Then the state of the heat pump is not the cooling state");
            } else {
                this.logMessage("   but was not");
                this.statistics.incorrectResult();
            }
        } catch (Exception e) {
            this.logMessage("The exeception " + e + "has been raised");
            this.statistics.incorrectResult();
        }

        this.statistics.updateStatistics();

    }

    /**
     * test of the {@code SwitchOn} method when the heat pump is off.
     *
     * <p><strong>Description</strong></p>
     *
     * <p>Gherkin specification:</p>
     * <pre>
     * Feature: Switching on the heat pump
     *
     *   Scenario: Switching on the heat pump when on
     *     Given the heat pump is initialised
     *     And the heat pump is on
     *     When the user switches off the lamp
     *     Then the heat pump is off
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

        this.logMessage("Feature: Switching off the heat pump");
        this.logMessage("   Scenario: Switching off the heat pump when on");
        this.logMessage("   Given the heat pump is initialised");
        this.logMessage("   And the heat pump is on");

        try {
            this.logMessage("   When the user switches off the heat pump");
            this.userOutboundPort.switchOff();
            boolean result = ! this.userOutboundPort.on();
            if (result) {
                this.logMessage("   Then the heat pump is off");
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
     * test getting and setting the target temperature of the heat pump.
     *
     * <p><strong>Description</strong></p>
     *
     * <pre>
     * Feature: getting and setting the target temperature of the heat pump");
     *   Scenario: getting the target temperature through the user interface when just initialised
     *     Given the heat pump is initialised
     *     And the heat pump has not been used yet
     *     And the heat pump is on
     *     When I get the target temperature through the user interface
     *     Then the target temperature of the heat pump is the heat pump standard target temperature
     *   Scenario: getting the target temperature through the internal control interface when just initialised
     *     Given the heat pump is initialised
     *     And the heat pump has not been used yet
     *     And the heat pump is on
     *     When I get the target temperature through the internal control interface
     *     Then the target temperature of the heat pump is the heat pump standard target temperature
     *   Scenario: getting the target temperature through the external control interface when just initialised
     *      Given the heat pump is initialised
     *      And the heat pump has not been used yet
     *      And the heat pump is on
     *      When I get the target temperature through the external control interface
     *      Then the target temperature of the heat pump is the heat pump standard target temperature
     *   Scenario: setting the target temperature of the heat pump when on
     *     Given the heat pump is initialised
     *     And the heat pump is on
     *     When I set the temperature at any given temperature between -50 and 50 Celsius inclusive
     *     Then the target temperature obtained through the user interface of the heat pump is the given temperature
     *   Scenario: setting the target temperature of the heat pump when on
     *      Given the heat pump is initialised
     *      And the heat pump is on
     *      When I set the temperature at any given temperature between -50 and 50 Celsius inclusive
     *      Then the target temperature obtained through the internal interface of the heat pump is the given temperature
     *   Scenario: setting the target temperature of the heat pump when on
     *      Given the heat pump is initialised
     *      And the heat pump is on
     *      When I set the temperature at any given temperature between -50 and 50 Celsius inclusive
     *      Then the target temperature obtained through the external interface of the heat pump is the given temperature
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
    public void testTargetTemperature() {

        this.logMessage("Feature: getting and setting the target temperature"
                + " of the heat pump");

        this.logMessage("  Scenario: getting the target temperature through the"
                + " user interface when just initialised");
        this.logMessage("    Given the heat pump is initialised");
        this.logMessage("    And the heat pump has not been used yet");
        this.logMessage("    And the heat pump is on");
        boolean result;
        Measure<Double> temperature = null;
        Random rd = new Random();

        try {
            this.userOutboundPort.switchOn();
            result = this.userOutboundPort.on();
            if (!result) {
                this.logMessage("     but was: off");
                this.statistics.failedCondition();
            }
            this.logMessage("    When I get the target temperature through the "
                    + "user interface");
            temperature = this.userOutboundPort.getTargetTemperature();
            if (temperature.getData() ==
                    Compressor.STD_TARGET_TEMPERATURE.getData()
                    && temperature.getMeasurementUnit().equals(
                    MeasurementUnit.CELSIUS)) {
                this.logMessage("    Then the target temperature of the heat pump"
                        + " is the heat pump standard target temperature");
            } else {
                this.logMessage("     but was: " + temperature.getData());
                this.statistics.incorrectResult();
            }
            this.userOutboundPort.switchOff();
        } catch (Exception e) {
            this.statistics.incorrectResult();
            this.logMessage("     but the exception " + e + " has been raised");
        }

        this.statistics.updateStatistics();

        this.logMessage("  Scenario: getting the target temperature through the"
                + " internal interface when just initialised");
        this.logMessage("    Given the heat pump is initialised");
        this.logMessage("    And the heat pump has not been used yet");
        this.logMessage("    And the heat pump is on");

        try {
            this.userOutboundPort.switchOn();
            result = this.userOutboundPort.on();
            if (!result) {
                this.logMessage("     but was: off");
                this.statistics.failedCondition();
            }
            this.logMessage("    When I get the target temperature through the "
                    + "internal interface");
            temperature = this.internalOutboundPort.getTargetTemperature();
            if (temperature.getData() ==
                    Compressor.STD_TARGET_TEMPERATURE.getData()
                    && temperature.getMeasurementUnit().equals(
                    MeasurementUnit.CELSIUS)) {
                this.logMessage("    Then the target temperature of the heat pump"
                        + " is the heat pump standard target temperature");
            } else {
                this.logMessage("     but was: " + temperature.getData());
                this.statistics.incorrectResult();
            }
            this.userOutboundPort.switchOff();
        } catch (Exception e) {
            this.statistics.incorrectResult();
            this.logMessage("     but the exception " + e + " has been raised");
        }

        this.statistics.updateStatistics();


        this.logMessage("  Scenario: getting the target temperature through the"
                + " external interface when just initialised");
        this.logMessage("    Given the heat pump is initialised");
        this.logMessage("    And the heat pump has not been used yet");
        this.logMessage("    And the heat pump is on");

        try {
            this.userOutboundPort.switchOn();
            result = this.userOutboundPort.on();
            if (!result) {
                this.logMessage("     but was: off");
                this.statistics.failedCondition();
            }
            this.logMessage("    When I get the target temperature through the "
                    + "external interface");
            temperature = this.externalOutboundPort.getTargetTemperature();
            if (temperature.getData() ==
                    Compressor.STD_TARGET_TEMPERATURE.getData()
                    && temperature.getMeasurementUnit().equals(
                    MeasurementUnit.CELSIUS)) {
                this.logMessage("    Then the target temperature of the heat pump"
                        + " is the heat pump standard target temperature");
            } else {
                this.logMessage("     but was: " + temperature.getData());
                this.statistics.incorrectResult();
            }
            this.userOutboundPort.switchOff();
        } catch (Exception e) {
            this.statistics.incorrectResult();
            this.logMessage("     but the exception " + e + " has been raised");
        }

        this.statistics.updateStatistics();

        this.logMessage("  Scenario: setting the target temperature of the "
                + "heatPump when on");
        this.logMessage("    Given the heatPump is initialised");
        this.logMessage("    And the heatPump is on");
        try {
            this.userOutboundPort.switchOn();
            result = this.userOutboundPort.on();
            if (!result) {
                this.logMessage("     but was: off");
                this.statistics.failedCondition();
            }
            this.logMessage("    When I set the temperature at any given "
                    + "temperature between -50 and 50 Celsius inclusive");
            double random_temperature = rd.nextDouble() * HeatPump.MAX_TARGET_TEMPERATURE.getData();
            this.userOutboundPort.setTargetTemperature(
                    new Measure<Double>(random_temperature, HeatPump.TEMPERATURE_UNIT));
            temperature = this.userOutboundPort.getTargetTemperature();
            if (temperature.getData() == random_temperature &&
                    temperature.getMeasurementUnit().equals(MeasurementUnit.CELSIUS)) {
                this.logMessage("    Then the target temperature obtained through the user interface of the heatPump"
                        + " is the given temperature");
            } else {
                this.statistics.incorrectResult();
                this.logMessage("     but was not: " + temperature.getData());
            }
            this.userOutboundPort.switchOff();
        } catch (Exception e) {
            this.statistics.incorrectResult();
            this.logMessage("     but the exception " + e + " has been raised");
        }

        this.statistics.updateStatistics();

        this.logMessage("  Scenario: setting the target temperature of the "
                + "heatPump when on");
        this.logMessage("    Given the heatPump is initialised");
        this.logMessage("    And the heatPump is on");
        try {
            this.userOutboundPort.switchOn();
            result = this.userOutboundPort.on();
            if (!result) {
                this.logMessage("     but was: off");
                this.statistics.failedCondition();
            }
            this.logMessage("    When I set the temperature at any given "
                    + "temperature between -50 and 50 Celsius inclusive");
            double random_temperature = rd.nextDouble() * HeatPump.MAX_TARGET_TEMPERATURE.getData();
            this.userOutboundPort.setTargetTemperature(
                    new Measure<Double>(random_temperature, HeatPump.TEMPERATURE_UNIT));
            temperature = this.internalOutboundPort.getTargetTemperature();
            if (temperature.getData() == random_temperature &&
                    temperature.getMeasurementUnit().equals(MeasurementUnit.CELSIUS)) {
                this.logMessage("    Then the target temperature obtained through the internal interface of the heatPump"
                        + " is the given temperature");
            } else {
                this.statistics.incorrectResult();
                this.logMessage("     but was not: " + temperature.getData());
            }
            this.userOutboundPort.switchOff();
        } catch (Exception e) {
            this.statistics.incorrectResult();
            this.logMessage("     but the exception " + e + " has been raised");
        }

        this.statistics.updateStatistics();

        this.logMessage("  Scenario: setting the target temperature of the "
                + "heatPump when on");
        this.logMessage("    Given the heatPump is initialised");
        this.logMessage("    And the heatPump is on");
        try {
            this.userOutboundPort.switchOn();
            result = this.userOutboundPort.on();
            if (!result) {
                this.logMessage("     but was: off");
                this.statistics.failedCondition();
            }
            this.logMessage("    When I set the temperature at any given "
                    + "temperature between -50 and 50 Celsius inclusive");
            double random_temperature = rd.nextDouble() * HeatPump.MAX_TARGET_TEMPERATURE.getData();
            this.userOutboundPort.setTargetTemperature(
                    new Measure<Double>(random_temperature, HeatPump.TEMPERATURE_UNIT));
            temperature = this.externalOutboundPort.getTargetTemperature();
            if (temperature.getData() == random_temperature &&
                    temperature.getMeasurementUnit().equals(MeasurementUnit.CELSIUS)) {
                this.logMessage("    Then the target temperature obtained through the external interface of the heatPump"
                        + " is the given temperature");
            } else {
                this.statistics.incorrectResult();
                this.logMessage("     but was not: " + temperature.getData());
            }
            this.userOutboundPort.switchOff();
        } catch (Exception e) {
            this.statistics.incorrectResult();
            this.logMessage("     but the exception " + e + " has been raised");
        }

        this.statistics.updateStatistics();
    }

    /**
     * test getting the current temperature in the room of the heat pump.
     *
     * <p><strong>Description</strong></p>
     *
     * <pre>
     * Feature: getting the current temperature in the room of the heat pump";
     *   Scenario: getting the current temperature when on";
     *     Given the heat pump is initialised";
     *     And the heat pump has not been used yet";
     *     And the heat pump is on";
     *     When I get the current temperature through user interface of the heat pump";
     *     Then the current temperature is the heat pump standard current temperature";
     * Feature: getting the current temperature in the room of the heat pump";
     *    Scenario: getting the current temperature when on";
     *       Given the heat pump is initialised";
     *       And the heat pump has not been used yet";
     *       And the heat pump is on";
     *       When I get the current temperature through internal interface of the heat pump";
     *       Then the current temperature is the heat pump standard current temperature";
     * Feature: getting the current temperature in the room of the heat pump";
     *      Scenario: getting the current temperature when on";
     *        Given the heat pump is initialised";
     *        And the heat pump has not been used yet";
     *        And the heat pump is on";
     *        When I get the current temperature through external interface of the heat pump";
     *        Then the current temperature is the heat pump standard current temperature";
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
    public void testCurrentTemperature() {
        this.logMessage("Feature: getting the current temperature"
                + " in the room of the heat pump");

        this.logMessage("  Scenario: getting the current temperature through "
                + "the user interface when on");
        this.logMessage("    Given the heat pump is initialised");
        this.logMessage("    And the heat pump has not been used yet");
        this.logMessage("    And the heat pump is on");
        boolean result;
        SignalData<Double> temperature = null;

        try {
            this.userOutboundPort.switchOn();
            result = this.userOutboundPort.on();
            if (!result) {
                this.logMessage("     but was: off");
                this.statistics.failedCondition();
            }
            this.logMessage("    When I get the current temperature of the "
                    + "HeatPump through the user interface");
            temperature = this.userOutboundPort.getCurrentTemperature();
            if (temperature.getMeasure().getData() ==
                    TemperatureSensor.FAKE_CURRENT_TEMPERATURE.getData() &&
                    temperature.getMeasure().getMeasurementUnit().equals(
                            TemperatureSensor.FAKE_CURRENT_TEMPERATURE.
                                    getMeasurementUnit())) {
                this.logMessage("    Then the current temperature is the HeatPump"
                        + " standard current temperature");
            } else {
                this.logMessage("     but was: " + temperature.getMeasure().getData());
                this.statistics.incorrectResult();
            }
            this.userOutboundPort.switchOff();
        } catch (Exception e) {
            this.statistics.incorrectResult();
            this.logMessage("     but the exception " + e + " has been raised");
        }

        this.statistics.updateStatistics();

        this.logMessage("  Scenario: getting the current temperature through "
                + "the user interface when on");
        this.logMessage("    Given the heat pump is initialised");
        this.logMessage("    And the heat pump has not been used yet");
        this.logMessage("    And the heat pump is on");

        try {
            this.userOutboundPort.switchOn();
            result = this.userOutboundPort.on();
            if (!result) {
                this.logMessage("     but was: off");
                this.statistics.failedCondition();
            }
            this.logMessage("    When I get the current temperature of the "
                    + "HeatPump through the internal interface");
            temperature = this.internalOutboundPort.getCurrentTemperature();
            if (temperature.getMeasure().getData() ==
                    TemperatureSensor.FAKE_CURRENT_TEMPERATURE.getData() &&
                    temperature.getMeasure().getMeasurementUnit().equals(
                            TemperatureSensor.FAKE_CURRENT_TEMPERATURE.
                                    getMeasurementUnit())) {
                this.logMessage("    Then the current temperature is the HeatPump"
                        + " standard current temperature");
            } else {
                this.logMessage("     but was: " + temperature.getMeasure().getData());
                this.statistics.incorrectResult();
            }
            this.userOutboundPort.switchOff();
        } catch (Exception e) {
            this.statistics.incorrectResult();
            this.logMessage("     but the exception " + e + " has been raised");
        }

        this.statistics.updateStatistics();

        this.logMessage("  Scenario: getting the current temperature through "
                + "the user interface when on");
        this.logMessage("    Given the heat pump is initialised");
        this.logMessage("    And the heat pump has not been used yet");
        this.logMessage("    And the heat pump is on");

        try {
            this.userOutboundPort.switchOn();
            result = this.userOutboundPort.on();
            if (!result) {
                this.logMessage("     but was: off");
                this.statistics.failedCondition();
            }
            this.logMessage("    When I get the current temperature of the "
                    + "HeatPump through the external interface");
            temperature = this.externalOutboundPort.getCurrentTemperature();
            if (temperature.getMeasure().getData() ==
                    TemperatureSensor.FAKE_CURRENT_TEMPERATURE.getData() &&
                    temperature.getMeasure().getMeasurementUnit().equals(
                            TemperatureSensor.FAKE_CURRENT_TEMPERATURE.
                                    getMeasurementUnit())) {
                this.logMessage("    Then the current temperature is the HeatPump"
                        + " standard current temperature");
            } else {
                this.logMessage("     but was: " + temperature.getMeasure().getData());
                this.statistics.incorrectResult();
            }
            this.userOutboundPort.switchOff();
        } catch (Exception e) {
            this.statistics.incorrectResult();
            this.logMessage("     but the exception " + e + " has been raised");
        }

        this.statistics.updateStatistics();
    }

    /**
     * test getting and setting the power level of the heat pump.
     *
     * <p><strong>Description</strong></p>
     *
     * <pre>
     * Feature: getting and setting the power level of the heat pump
     *   Scenario: getting the maximum power level through the external control interface
     *     Given the heat pump is initialised
     *     When I get the maximum power level through the external control interface
     *     Then the result is the heat pump maximum power level
     *   Scenario: getting the current power level through the external control interface when just initialised
     *     Given the heat pump is initialised
     *     And the heat pump has not been used yet
     *     And the heat pump is on
     *     When I get the current power level through the external control interface
     *     Then the result is the heat pump standard power level
     *   Scenario: setting the power level to a given level between minimumRequiredPower and the maximum power level through the external control interface
     *     Given the heat pump is initialised
     *     And the heat pump is on
     *     When I set the current power level through the external control interface to a given level between minimum required power level and the maximum power level
     *     Then the current power level is the given power level
     *   Scenario: setting the power level to the maximum power level through the external control interface
     *     Given the heat pump is initialised
     *     And the heat pump is on
     *     When I set the current power level through the external control interface to the maximum power level
     *     Then the current power level is the maximum power level
     *    Scenario: setting the power level to 0.0
     *      Given the heat pump is initialised
     *      And the heat pump is on
     *      When I set the current power level to 0.0
     *      Then the current power level is 0.0
     *    Scenario: setting the power level to the minimum required power level
     *      Given the heat pump is initialised
     *      And the heat pump is on
     *      When I set the current power level to the minimum required power level
     *      Then the current power level is the minimum required power level
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
    public void testPowerLevel() {
        this.logMessage("Feature: getting and setting the power level of the"
                + " heat pump");

        this.logMessage("  Scenario: getting the maximum power level through "
                + "the user interface");
        this.logMessage("    Given the heat pump is initialised");
        this.logMessage("    And the heat pump is on");

        Measure<Double> powerLevel = null;
        Random rd = new Random();
        boolean result = false;
        try {
            this.userOutboundPort.switchOn();
            result = this.userOutboundPort.on();
            if (!result) {
                this.logMessage("     but was: off");
                this.statistics.failedCondition();
            }
            this.logMessage("    When I get the maximum power level through the"
                    + " external control interface");
            double max_power = HeatPump.MAX_POWER_LEVEL.getData()
                    + Compressor.MAX_POWER_LEVEL.getData()
                    + TemperatureSensor.MAX_POWER_LEVEL.getData();
            double epsilon = 1e-6;
            powerLevel = this.externalOutboundPort.getMaximumPower();
            if (powerLevel.getData() <= max_power + epsilon &&
                    powerLevel.getData() >= max_power - epsilon &&
                    powerLevel.getMeasurementUnit().equals(
                            HeatPump.MAX_POWER_LEVEL.getMeasurementUnit())) {
                this.logMessage("    Then the result is the heat pump maximum "
                        + "power level");
            } else {
                this.statistics.incorrectResult();
                this.logMessage("     but was: " + powerLevel.getData());
            }
            this.userOutboundPort.switchOff();
        } catch (Exception e) {
            this.statistics.incorrectResult();
            this.logMessage("     but the exception " + e + " has been raised");
        }

        this.logMessage("Scenario: getting the current power level through the external control interface when just initialised");
        this.logMessage("   Given the heat pump is initialised");
        this.logMessage("   And the heat pump has not been used yet");
        this.logMessage("   And the heat pump is on");

        try {
            this.userOutboundPort.switchOn();
            result = this.userOutboundPort.on();
            if (!result) {
                this.logMessage("     but was: off");
                this.statistics.failedCondition();
            }
            this.logMessage("   When I get the current power level through the external control interface ");
            powerLevel = this.externalOutboundPort.getCurrentPower().getMeasure();
            double epsilon = 1e-6;
            double standard_power = HeatPump.STANDARD_POWER_LEVEL.getData()
                    + Compressor.STANDARD_POWER_LEVEL.getData()
                    + TemperatureSensor.STANDARD_POWER_LEVEL.getData();
            if (powerLevel.getData() <= standard_power + epsilon &&
                    powerLevel.getData() >= standard_power - epsilon &&
                    powerLevel.getMeasurementUnit().equals(
                            HeatPump.STANDARD_POWER_LEVEL.getMeasurementUnit()
                    ))
            {
                this.logMessage("   Then the current power level is the given power level");
            } else {
                this.logMessage("   But was not: " + powerLevel.getData() + " " + standard_power);
                this.statistics.incorrectResult();
            }
            this.userOutboundPort.switchOff();
        } catch (Exception e) {
            this.statistics.incorrectResult();
            this.logMessage("     but the exception " + e + " has been raised");
        }

        this.statistics.updateStatistics();

        this.logMessage("Scenario: setting the power level to a given level between minimumRequiredPower and the maximum power level through the external control interface");
        this.logMessage("   Given the heat pump is initialised");
        this.logMessage("   And the heat pump is on");
        try {
            this.userOutboundPort.switchOn();
            result = this.userOutboundPort.on();
            if (!result) {
                this.logMessage("     but was: off");
                this.statistics.failedCondition();
            }
            this.logMessage("   When I set the current power level through the external control interface to a given level between minimum required power level and the maximum power level");
            double max_power_level = this.externalOutboundPort.getMaximumPower().getData();
            double min_required_level = this.externalOutboundPort.getMinimumRequiredPower().getData();
            double random_power_level = rd.nextDouble() * (max_power_level - min_required_level);
            if (random_power_level < min_required_level) {
                random_power_level = min_required_level;
            }
            this.externalOutboundPort.setCurrentPower(
                    new Measure<>(random_power_level, HeatPump.POWER_UNIT)
            );

            powerLevel = this.externalOutboundPort.getCurrentPower().getMeasure();
            final double epsilon = 1e-6;
            if (powerLevel.getData() <= random_power_level + epsilon &&
                    powerLevel.getData() >= random_power_level - epsilon &&
                    powerLevel.getMeasurementUnit().equals(
                            HeatPump.POWER_UNIT
                    )) {
                this.logMessage("   Then the current power level is the given power level");
            } else {
                this.logMessage("But was not");
                this.statistics.incorrectResult();
            }
            this.userOutboundPort.switchOff();
        } catch (Exception e) {
            this.statistics.incorrectResult();
            this.logMessage("     but the exception " + e + " has been raised");
        }

        this.statistics.updateStatistics();

        this.logMessage("Scenario: setting the power level to the maximum power level through the external control interface");
        this.logMessage("   Given the heat pump is initialised");
        this.logMessage("   And the heat pump is on");
        try {
            this.userOutboundPort.switchOn();
            result = this.userOutboundPort.on();
            if (!result) {
                this.logMessage("     but was: off");
                this.statistics.failedCondition();
            }
            this.logMessage("   When I set the current power level through the external control interface to the maximum power level");
            double max_power_level = this.externalOutboundPort.getMaximumPower().getData();
            this.externalOutboundPort.setCurrentPower(
                    new Measure<>(max_power_level, HeatPump.POWER_UNIT)
            );

            powerLevel = this.externalOutboundPort.getCurrentPower().getMeasure();
            double epsilon = 1e-6;
            if (powerLevel.getData() <= max_power_level + epsilon &&
                    powerLevel.getData() >= max_power_level - epsilon &&
                    powerLevel.getMeasurementUnit().equals(
                            HeatPump.STANDARD_POWER_LEVEL.getMeasurementUnit()
                    )) {
                this.logMessage("   Then the current power level is the maximum power level");
            } else {
                this.logMessage("But was not");
                this.statistics.incorrectResult();
            }
            this.userOutboundPort.switchOff();
        } catch (Exception e) {
            this.statistics.incorrectResult();
            this.logMessage("     but the exception " + e + " has been raised");
        }

        this.statistics.updateStatistics();

        this.logMessage("Scenario: setting the power level to 0.0");
        this.logMessage("   Given the heat pump is initialised");
        this.logMessage("   And the heat pump is on");
        try {
            this.userOutboundPort.switchOn();
            result = this.userOutboundPort.on();
            if (!result) {
                this.logMessage("     but was: off");
                this.statistics.failedCondition();
            }
            this.logMessage("   When I set the current power level to 0.0");
            this.externalOutboundPort.setCurrentPower(
                    new Measure<>(0., HeatPump.POWER_UNIT)
            );

            powerLevel = this.externalOutboundPort.getCurrentPower().getMeasure();
            double epsilon = 1e-6;
            if (powerLevel.getData() <= epsilon + epsilon &&
                    powerLevel.getData() >= -epsilon &&
                    powerLevel.getMeasurementUnit().equals(
                            HeatPump.STANDARD_POWER_LEVEL.getMeasurementUnit()
                    )) {
                this.logMessage("   Then the current power level is 0.0");
            } else {
                this.logMessage("But was not");
                this.statistics.incorrectResult();
            }
            this.userOutboundPort.switchOff();
        } catch (Exception e) {
            this.statistics.incorrectResult();
            this.logMessage("     but the exception " + e + " has been raised");
        }

        this.statistics.updateStatistics();

        this.logMessage("Scenario: setting the power level to the minimum required power level");
        this.logMessage("   Given the heat pump is initialised");
        this.logMessage("   And the heat pump is on");
        try {
            this.userOutboundPort.switchOn();
            result = this.userOutboundPort.on();
            if (!result) {
                this.logMessage("     but was: off");
                this.statistics.failedCondition();
            }
            double min_required_power = this.externalOutboundPort.getMinimumRequiredPower().getData();
            this.logMessage("   When I set the current power level to the minimum required power level");
            this.externalOutboundPort.setCurrentPower(
                    new Measure<>(min_required_power, HeatPump.POWER_UNIT)
            );

            powerLevel = this.externalOutboundPort.getCurrentPower().getMeasure();
            double epsilon = 1e-6;
            if (powerLevel.getData() <= min_required_power + epsilon &&
                    powerLevel.getData() >= min_required_power - epsilon &&
                    powerLevel.getMeasurementUnit().equals(
                            HeatPump.STANDARD_POWER_LEVEL.getMeasurementUnit()
                    )) {
                this.logMessage("   Then the current power level is the minimum required power level");
            } else {
                this.logMessage("But was not");
                this.statistics.incorrectResult();
            }
            this.userOutboundPort.switchOff();
        } catch (Exception e) {
            this.statistics.incorrectResult();
            this.logMessage("     but the exception " + e + " has been raised");
        }

        this.statistics.updateStatistics();
    }

    public void runAllTests() throws Exception {
        this.testInitialState();
        this.testSwitchOn();
        this.testHeatingState();
        this.testCoolingState();
        this.testSwitchOff();
        this.testCurrentTemperature();
        this.testTargetTemperature();
        this.testPowerLevel();

        this.statistics.statisticsReport(this);
    }

    @Override
    public synchronized void execute() throws Exception {

        if (this.isUnitTest) {
            runAllTests();
        } else {
            ClocksServerOutboundPort clocksServerOutboundPort =
                    new ClocksServerOutboundPort(this);
            clocksServerOutboundPort.publishPort();
            this.doPortConnection(
                    clocksServerOutboundPort.getPortURI(),
                    ClocksServer.STANDARD_INBOUNDPORT_URI,
                    ClocksServerConnector.class.getCanonicalName());
            this.traceMessage("heatPump tester gets the clock.\n");
            AcceleratedClock ac =
                    clocksServerOutboundPort.getClock(
                            CVMIntegrationTest.CLOCK_URI);

            this.doPortDisconnection(
                    clocksServerOutboundPort.getPortURI());
            clocksServerOutboundPort.unpublishPort();

            Instant startInstant = ac.getStartInstant();
            Instant heatPumpSwitchOn1 = startInstant.plusSeconds(SWITCH_ON_DELAY1);
            Instant heatPumpStartHeating = startInstant.plusSeconds(SET_HEATING_DELAY);
            Instant heatPumpSwitchOff1 = startInstant.plusSeconds(SWITCH_OFF_DELAY1);
            Instant heatPumpSwitchOn2 = startInstant.plusSeconds(SWITCH_ON_DELAY2);
            Instant heatPumpStartCooling = startInstant.plusSeconds(SET_COOLING_DELAY);
            Instant heatPumpSwitchOff2 = startInstant.plusSeconds(SWITCH_OFF_DELAY2);
            this.traceMessage("Heat pump tester waits until start.\n");

            ac.waitUntilStart();
            this.traceMessage("Heat pump tester schedules switch on1, heating and off1.\n");
            long delayToSwitchOn1 = ac.nanoDelayUntilInstant(heatPumpSwitchOn1);
            long delaytoStartHeating = ac.nanoDelayUntilInstant(heatPumpStartHeating);
            long delayToSwitchOff1 = ac.nanoDelayUntilInstant(heatPumpSwitchOff1);
            this.traceMessage("Heat pump tester schedules switch on2, cooling and off2.\n");
            long delayToSwitchOn2 = ac.nanoDelayUntilInstant(heatPumpSwitchOn2);
            long delayToStartCooling = ac.nanoDelayUntilInstant(heatPumpStartCooling);
            long delayToSwitchOff2 = ac.nanoDelayUntilInstant(heatPumpSwitchOff2);

            AbstractComponent o = this;

            this.scheduleTaskOnComponent(
                    new AbstractComponent.AbstractTask() {
                        @Override
                        public void run() {
                            try {
                                o.traceMessage("heatPump switches on1.\n");
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
                                o.traceMessage("heatPump starts heating.\n");
                                userOutboundPort.setTargetTemperature(Heating_TEMPERATURE);
                                internalOutboundPort.startHeating();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }, delaytoStartHeating, TimeUnit.NANOSECONDS);

            this.scheduleTaskOnComponent(
                    new AbstractComponent.AbstractTask() {
                        @Override
                        public void run() {
                            try {
                                o.traceMessage("heatPump switches off1.\n");
                                internalOutboundPort.stopHeating();
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
                                o.traceMessage("heatPump switches on2.\n");
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
                                o.traceMessage("heatPump starts cooling.\n");
                                userOutboundPort.setTargetTemperature(COOLING_TEMPERATURE);
                                internalOutboundPort.startCooling();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }, delayToStartCooling, TimeUnit.NANOSECONDS);

            this.scheduleTaskOnComponent(
                    new AbstractComponent.AbstractTask() {
                        @Override
                        public void run() {
                            try {
                                o.traceMessage("heatPump switches off2.\n");
                                internalOutboundPort.stopCooling();
                                userOutboundPort.switchOff();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }, delayToSwitchOff2, TimeUnit.NANOSECONDS);
        }
    }

}
