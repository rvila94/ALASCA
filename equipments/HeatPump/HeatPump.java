package equipments.HeatPump;

import equipments.HeatPump.compressor.CompressorOutboundPort;
import equipments.HeatPump.powerRepartitionPolicy.PowerRepartitionPolicyI;
import equipments.HeatPump.temperatureSensor.TemperatureSensorOutboundPort;
import equipments.HeatPump.connections.HeatPumpExternalJava4InboundPort;
import equipments.HeatPump.connections.HeatPumpInternalControlInboundPort;
import equipments.HeatPump.connections.HeatPumpUserInboundPort;
import equipments.HeatPump.interfaces.*;
import equipments.HeatPump.temperatureSensor.TemperatureSensorCI;
import equipments.HeatPump.compressor.CompressorCI;
import equipments.hem.RegistrationOutboundPort;
import fr.sorbonne_u.alasca.physical_data.Measure;
import fr.sorbonne_u.alasca.physical_data.MeasurementUnit;
import fr.sorbonne_u.alasca.physical_data.SignalData;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import fr.sorbonne_u.components.annotations.RequiredInterfaces;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import fr.sorbonne_u.components.hem2025.bases.RegistrationCI;
import fr.sorbonne_u.exceptions.AssertionChecking;
import fr.sorbonne_u.exceptions.PostconditionException;
import fr.sorbonne_u.exceptions.PreconditionException;

import java.io.File;

/**
 * The class <code>HeatPump.equipments.HeatPump</code>.
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
        TemperatureSensorCI.class,
        CompressorCI.class,
        RegistrationCI.class
})
@OfferedInterfaces(offered = {
        HeatPumpUserCI.class,
        HeatPumpInternalControlCI.class,
        HeatPumpExternalJava4InboundPort.class})
public class HeatPump
extends AbstractComponent
implements HeatPumpUserI,
        HeatPumpInternalControlI,
        HeatPumpExternalControlI {

    public static boolean VERBOSE = false;

    /** when tracing, x coordinate of the window relative position.			*/
    public static int X_RELATIVE_POSITION = 0;
    /** when tracing, y coordinate of the window relative position.			*/
    public static int Y_RELATIVE_POSITION = 0;

    protected static final String REFLECTION_INBOUND_URI = "HEAT-PUMP-REFLECTION-INBOUND-URI";

    public static final String EQUIPMENT_UID = "1A100354";

    protected static final File PATH_TO_CONNECTOR_DESCRIPTOR =
            new File("src/connectorGenerator/heatpump-descriptor.xml");

    /** measurement unit for power used by the heat pump					      */
    public static final MeasurementUnit POWER_UNIT = MeasurementUnit.WATTS;

    /** measurement unit for the temperature of fluids and air			          */
    public static final MeasurementUnit TEMPERATURE_UNIT = MeasurementUnit.CELSIUS;

    /** maximum power level of the heat pump, in watts                                */
    public static final Measure<Double> MAX_POWER_LEVEL = new Measure<>(400., POWER_UNIT);
    /** minimum power level of the heat pump, in watts */
    protected static final Measure<Double> MIN_POWER_LEVEL = new Measure<>(0., POWER_UNIT);
    /** standard power level of the heat pump, in watts                               */
    public static final Measure<Double> STANDARD_POWER_LEVEL = new Measure<>(50., POWER_UNIT);
    /** minimum power required for the device to function */
    public static final Measure<Double> MIN_REQUIRED_POWER_LEVEL = new Measure<>(10., POWER_UNIT);

    /** maximum temperature target for the heat pump, in celsius                       */
    public static final Measure<Double> MAX_TARGET_TEMPERATURE = new Measure<>(50., TEMPERATURE_UNIT);

    /** minimum temperature target for the heat pump, in celsius                       */
    protected static final Measure<Double> MIN_TARGET_TEMPERATURE = new Measure<>(-50., TEMPERATURE_UNIT);

    protected static final int NUMBER_THREADS = 2;
    protected static final int NUMBER_SCHEDULABLE_THREADS = 0;

    protected CompressorOutboundPort compressorBoundPort;
    protected String compressorInboundURI;

    protected TemperatureSensorOutboundPort temperatureOutboundPort;
    protected String bufferTankInboundURI;

    protected String compressorConnectorClassName;

    protected String bufferConnectorClassName;

    protected State pumpState;

    protected SignalData<Double> currentPower;

    protected HeatPumpUserInboundPort userInboundPort;
    protected HeatPumpInternalControlInboundPort internalInboundPort;
    protected HeatPumpExternalJava4InboundPort externalInboundPort;

    protected RegistrationOutboundPort registrationOutboundPort;
    protected String registrationHEMURI;
    protected String registrationHEMConnectorClassName;

    protected boolean isUnitTest;

    protected HeatPump(
            String compressorURI,
            String bufferTankURI,
            String compressorCcName,
            String bufferCcName,
            String userInboundURI,
            String internalInboundURI,
            String externalInboundURI,
            String registrationHEMURI,
            String registrationHEMCcName) throws Exception {
        this(REFLECTION_INBOUND_URI,
                compressorURI,
                bufferTankURI,
                compressorCcName,
                bufferCcName,
                userInboundURI,
                internalInboundURI,
                externalInboundURI,
                registrationHEMURI,
                registrationHEMCcName);
    }

    protected HeatPump(
            String reflectionInboundPortURI,
            String compressorURI,
            String bufferTankURI,
            String compressorCcName,
            String bufferCcName,
            String userInboundURI,
            String internalInboundURI,
            String externalInboundURI,
            String registrationHEMURI,
            String registrationHEMCcName) throws Exception {
        super(reflectionInboundPortURI, NUMBER_THREADS, NUMBER_SCHEDULABLE_THREADS);

        this.isUnitTest = false;

        this.pumpState = State.Off;
        this.currentPower = new SignalData<>(STANDARD_POWER_LEVEL);

        this.compressorBoundPort = new CompressorOutboundPort(this);
        this.compressorBoundPort.publishPort();
        this.compressorInboundURI = compressorURI;

        this.temperatureOutboundPort = new TemperatureSensorOutboundPort(this);
        this.temperatureOutboundPort.publishPort();
        this.bufferTankInboundURI = bufferTankURI;

        this.compressorConnectorClassName = compressorCcName;
        this.bufferConnectorClassName = bufferCcName;

        this.userInboundPort = new HeatPumpUserInboundPort(userInboundURI, this);
        this.userInboundPort.publishPort();

        this.internalInboundPort = new HeatPumpInternalControlInboundPort(internalInboundURI, this);
        this.internalInboundPort.publishPort();

        this.externalInboundPort = new HeatPumpExternalJava4InboundPort(externalInboundURI, this);
        this.externalInboundPort.publishPort();

        this.registrationOutboundPort = new RegistrationOutboundPort(this);
        this.registrationOutboundPort.publishPort();
        this.registrationHEMURI = registrationHEMURI;
        this.registrationHEMConnectorClassName = registrationHEMCcName;

        if (HeatPump.VERBOSE) {
            this.tracer.get().setTitle("HeatPump component");
            this.tracer.get().setRelativePosition(X_RELATIVE_POSITION,
                    Y_RELATIVE_POSITION);
            this.toggleTracing();
        }
    }

    protected HeatPump(
            String compressorURI,
            String bufferTankURI,
            String compressorCcName,
            String bufferCcName,
            String userInboundURI,
            String internalInboundURI,
            String externalInboundURI
    ) throws Exception {
        super(REFLECTION_INBOUND_URI, NUMBER_THREADS, NUMBER_SCHEDULABLE_THREADS);
        this.isUnitTest = true;

        this.pumpState = State.Off;
        this.currentPower = new SignalData<>(STANDARD_POWER_LEVEL);

        this.compressorBoundPort = new CompressorOutboundPort(this);
        this.compressorBoundPort.publishPort();
        this.compressorInboundURI = compressorURI;

        this.temperatureOutboundPort = new TemperatureSensorOutboundPort(this);
        this.temperatureOutboundPort.publishPort();
        this.bufferTankInboundURI = bufferTankURI;

        this.compressorConnectorClassName = compressorCcName;
        this.bufferConnectorClassName = bufferCcName;

        this.userInboundPort = new HeatPumpUserInboundPort(userInboundURI, this);
        this.userInboundPort.publishPort();

        this.internalInboundPort = new HeatPumpInternalControlInboundPort(internalInboundURI, this);
        this.internalInboundPort.publishPort();

        this.externalInboundPort = new HeatPumpExternalJava4InboundPort(externalInboundURI, this);
        this.externalInboundPort.publishPort();

        if (HeatPump.VERBOSE) {
            this.tracer.get().setTitle("HeatPump component");
            this.tracer.get().setRelativePosition(X_RELATIVE_POSITION,
                    Y_RELATIVE_POSITION);
            this.toggleTracing();
        }

    }


    protected static boolean implementationInvariants(HeatPump pump) throws Exception {

        assert pump != null : new PreconditionException("pump == null");

        boolean invariant_check = true;
        invariant_check &= AssertionChecking.checkInvariant(
                MAX_POWER_LEVEL.getData() >= 0 &&
                        MAX_POWER_LEVEL.getData() <= 100,
                HeatPump.class, pump,
                "MAX_POWER_VARIATION < 0 || MAX_POWER_VARIATION > 100"
        );
        invariant_check &= AssertionChecking.checkInvariant(
                MIN_POWER_LEVEL.getData() >= 0 &&
                        MIN_POWER_LEVEL.getData() <= 100,
                HeatPump.class, pump,
                "MIN_POWER_VARIATION < 0 || MIN_POWER_VARIATION > 100"
        );
        invariant_check &= AssertionChecking.checkInvariant(
                MIN_POWER_LEVEL.getData() <= MAX_POWER_LEVEL.getData(),
                HeatPump.class, pump,
                "MIN_POWER_VARIATION > MAX_POWER_VARIATION"
        );

        double pump_power = pump.getCurrentPower().getMeasure().getData();
        invariant_check &= AssertionChecking.checkInvariant(
                pump_power <= MAX_POWER_LEVEL.getData()
                        && (pump_power == MIN_POWER_LEVEL.getData()
                        || pump_power >= MIN_REQUIRED_POWER_LEVEL.getData()),
                HeatPump.class, pump,
                "pump_power > MAX_POWER_LEVEL.getData()\n" +
                        " || (pump_power != MIN_POWER_LEVEL.getData()\n" +
                        " && pump_power < MIN_REQUIRED_POWER_LEVEL.getData()"
        );

        invariant_check &= AssertionChecking.checkInvariant(
                pump.state != null,
                HeatPump.class, pump,
                "pump == null"
        );

        return invariant_check;
    }

    /**
     *
     * Methods indicating if the heat pump is in cooling mode
     *
     * <p><strong>Contract</strong></p>
     *
     * <pre>
     *  pre {@code on()}
     * </pre>
     * @return true if the heat pump is heating
     * @throws Exception
     */
    @Override
    public boolean heating() throws Exception {
        assert this.on() :
                new PreconditionException("The HeatPump is off");

        boolean res = this.pumpState == State.Heating;

        if (HeatPump.VERBOSE) {
            this.traceMessage("The heat pump returns its heating status: " + res + "\n");
        }

        return res;
    }

    /**
     *
     * Methods indicating if the heat pump is in cooling mode
     *
     * <p><strong>Contract</strong></p>
     *
     * <pre>
     *  pre {@code on()}
     * </pre>
     * @return true if the heat pump is cooling
     * @throws Exception
     */
    @Override
    public boolean cooling() throws Exception {
        assert this.on() :
                new PreconditionException("The Heat Pump is off\n");

        boolean res = this.pumpState == State.Cooling;

        if (HeatPump.VERBOSE) {
            this.traceMessage("The Heat pump returns its cooling status: " + res + "\n");
        }

        return res;
    }

    /**
     *
     * Method changing the state of the heat pump to heating
     *
     * <p><strong>Contract</strong></p>
     *
     * <pre>
     *  pre {@code on()}
     *  pre {@code !heating()}
     *  post {@code heating(}
     * </pre>
     * @throws Exception
     */
    @Override
    public void startHeating() throws Exception {
        assert this.on() :
                new PreconditionException("The Heat Pump is off\n");
        assert !this.heating() :
                new PreconditionException("The Heat pump is already heating\n");

        if (HeatPump.VERBOSE) {
            this.traceMessage("The heat pump starts heating\n");
            this.traceMessage("The heat pump signals its compressor to start heating\n");
        }

        this.pumpState = State.Heating;
        this.compressorBoundPort.startCompressing();

        if (HeatPump.VERBOSE) {
            this.traceMessage("The heat pump successfully completed the call to the compressor\n");
        }

        assert this.heating() :
                new PostconditionException("The Heat pump is not heating");
    }

    /**
     *
     * Method changing the state of the heat pump
     * The state will not be heating after using this method
     *
     * <p><strong>Contract</strong></p>
     *
     * <pre>
     *  pre {@code on()}
     *  pre {@code heating()}
     *  post {@code !heating()}
     * </pre>
     * @throws Exception
     */
    @Override
    public void stopHeating() throws Exception {
        assert this.on() :
                new PreconditionException("The heat pump is off");
        assert this.heating() :
                new PreconditionException("The heat pump is not heating");

        if (HeatPump.VERBOSE) {
            this.traceMessage("The heat pump stops heating\n");
            this.traceMessage("The heat pump signals the compressor to become idle\n");
        }

        this.pumpState = State.On;
        this.compressorBoundPort.switchOff();

        if (HeatPump.VERBOSE) {
            this.traceMessage("The heat pump successfully completed the call to the compressor\n");
        }

        assert !this.heating():
                new PostconditionException("The heat pump is still heating");
    }

    /**
     *
     * Method changing the state of the heat pump
     * The state will be "cooling" after using this method
     *
     * <p><strong>Contract</strong></p>
     *
     * <pre>
     *  pre {@code on()}
     *  pre {@code !cooling()}
     *  post {@code cooling()}
     * </pre>
     * @throws Exception
     */
    @Override
    public void startCooling() throws Exception {
        assert this.on():
                new PreconditionException("The heat pump is off");
        assert !this.cooling():
                new PreconditionException("The heat pump is already in cooling mode");

        if (HeatPump.VERBOSE) {
            this.traceMessage("The heat pump starts cooling\n");
            this.traceMessage("The heat pump signals its compressor to start relaxing\n");
        }

        this.pumpState = State.Cooling;
        this.compressorBoundPort.startRelaxing();

        if (HeatPump.VERBOSE) {
            this.traceMessage("The heat pump successfully completed the call to the compressor\n");
        }

        assert this.cooling():
                new PostconditionException("The heat pump is not in cooling mode");
    }

    /**
     *
     * Method changing the state of the heat pump.
     * The state will not be "cooling" after using this method
     *
     * <p><strong>Contract</strong></p>
     *
     * <pre>
     *  pre {@code on()}
     *  pre {@code cooling()}
     *  post {@code !cooling()}
     * </pre>
     * @throws Exception
     */
    @Override
    public void stopCooling() throws Exception {
        assert this.on():
                new PreconditionException("The heat pump is off");
        assert this.cooling():
                new PreconditionException("The heat pump is not in cooling mode");

        if (HeatPump.VERBOSE) {
            this.traceMessage("The heat pump stops cooling\n");
            this.traceMessage("The heat pump signals the compressor to become idle\n");
        }

        this.pumpState = State.On;
        this.compressorBoundPort.switchOff();

        if (HeatPump.VERBOSE) {
            this.traceMessage("The heat pump successfully completed the call to the compressor\n");
        }

        assert !this.cooling():
                new PostconditionException("The heat pump is still in cooling mode");
    }

    /**
     *
     * Methods indicating if the heat pump is on
     *
     * <p><strong>Contract</strong></p>
     *
     * <pre>
     *  null
     * </pre>
     * @return true if the heat pump is on
     * @throws Exception
     */
    @Override
    public boolean on() throws Exception {
        boolean res = this.pumpState != State.Off;

        if (HeatPump.VERBOSE) {
            this.traceMessage("The heat pump indicates if it is on: " + res);
        }

        return res;
    }

    /**
     *
     * Switches off the heat pump
     *
     * <p><strong>Contract</strong></p>
     *
     * <pre>
     *  pre  {@code on()}
     *  post {@code !on()}
     * </pre>
     * @throws Exception
     */
    @Override
    public void switchOff() throws Exception {
        assert this.on() :
                new PreconditionException("The heat pump is off\n");

        if (HeatPump.VERBOSE) {
            this.traceMessage("The heat pump turns off\n");
            this.traceMessage("The temperature sensor is turned off\n");
        }

        this.pumpState = State.Off;
        this.temperatureOutboundPort.switchOff();

        if (HeatPump.VERBOSE) {
            this.traceMessage("The heat pump successfully completed the call to the temperature sensor\n");
            this.traceMessage("The compressor becomes idle\n");
        }

        this.compressorBoundPort.switchOff();

        if (HeatPump.VERBOSE) {
            this.traceMessage("The heat pump successfully completed the call to the compressor\n");
            this.traceMessage("The heat pump unregisters from the home energy manager\n");
        }

        if (! this.isUnitTest) {
            this.registrationOutboundPort.unregister(HeatPump.EQUIPMENT_UID);

            if (HeatPump.VERBOSE) {
                this.traceMessage("The heat pump successfully completed the call to the home energy manager\n");
            }
        }
        assert !this.on() :
                new PostconditionException("The heat pump is still off\n");
    }

    /**
     *
     * Method Switching on the heat pump
     *
     * <p><strong>Contract</strong></p>
     *
     * <pre>
     *  pre {@code !on()}
     *  post {@code on()}
     * </pre>
     * @throws Exception
     */
    @Override
    public void switchOn() throws Exception {
        assert !this.on() :
                new PreconditionException("The heat pump is on\n");

        if (HeatPump.VERBOSE) {
            this.traceMessage("The heat pump is turned on\n");
            this.traceMessage("The temperatureSensor is turned on\n");
        }

        this.pumpState = State.On;
        this.temperatureOutboundPort.switchOn();

        if (HeatPump.VERBOSE) {
            this.traceMessage("The heat pump successfully completed the call to the temperature sensor\n");
            this.traceMessage("The heat pump register into the home energy manager\n");
        }

        if (! this.isUnitTest) {
            boolean registration = this.registrationOutboundPort.register(
                    EQUIPMENT_UID,
                    this.externalInboundPort.getPortURI(),
                    PATH_TO_CONNECTOR_DESCRIPTOR.getAbsolutePath());

            if (HeatPump.VERBOSE) {
                this.traceMessage("The heat pump successfully completed the registration: " + registration);
            }
        }

        assert this.on() :
                new PostconditionException("The heat pump is still off");
    }

    /**
     *
     * Method returning the current state of the heat pump
     *
     * <p><strong>Contract</strong></p>
     *
     * <pre>
     *  null
     * </pre>
     * @return The state of the heat pump
     * @throws Exception
     */
    @Override
    public State getState() throws Exception {

        if (HeatPump.VERBOSE) {
            this.traceMessage("The heat pump returns its state\n");
        }

        return this.pumpState;
    }


    /**
     *
     * Method setting the target temperature of the heat pump
     *
     * <p><strong>Contract</strong></p>
     *
     * <pre>
     *  pre {@code on()}
     *  pre {@code temperature != null}
     *  pre {@code temperature.getMeasurementUnit() == TEMPERATURE_UNIT}
     *  pre {@code MIN_TARGET_TEMPERATURE.getData() <= temperature.getData() && temperature.getData <= MAX_TARGET_TEMPERATURE}
     *  post {@code this.target_temperature().equals(target)}
     * </pre>
     * @param temperature
     * @throws Exception
     */
    public void setTargetTemperature(Measure<Double> temperature) throws Exception {
        assert this.on() :
                new PreconditionException("The heat pump is off");
        assert temperature != null:
                new PreconditionException("Temperature == null");
        assert MIN_TARGET_TEMPERATURE.getData() <= temperature.getData()
                && temperature.getData() <= MAX_TARGET_TEMPERATURE.getData() :
                new PreconditionException("MIN_TARGET_TEMPERATURE.getData() <= temperature.getData()" +
                        "&& temperature.getData <= MAX_TARGET_TEMPERATURE.getData()");

        if (HeatPump.VERBOSE) {
            this.traceMessage("The heat Pump calls the compressor to change the target temperature.\n");
        }

        compressorBoundPort.setTargetTemperature(temperature);

        if (HeatPump.VERBOSE) {
            this.traceMessage("The heat pump successfully completed the call to the compressor\n");
        }

        assert this.getTargetTemperature().equals(temperature) :
            new PostconditionException("The target temperature of the heat pump is not equal to the target");
    }

    /**
     * Method returning the current power used by the heat pump
     *
     * <p><strong>Contract</strong></p>
     *
     * <pre>
     *  pre {@code on()}
     *  post {@code ret.getMeasure().getData() >= 0.0 && ret.getMeasure().getData() <= getMaximumPower().getData()}
     * </pre>
     *
     * @return
     * @throws Exception
     */
    @Override
    public SignalData<Double> getCurrentPower() throws Exception {

        assert this.on():
                new PreconditionException("heat pump is off");

        if (HeatPump.VERBOSE) {
            this.traceMessage("The heat pump gets the current power level used by the temperature sensor");
        }

        Measure<Double> sensorPowerLevel = this.temperatureOutboundPort.getCurrentPower().getMeasure();

        if (HeatPump.VERBOSE) {
            this.traceMessage("The heat pump successfully completed the call to the temperature sensor: "
                    + sensorPowerLevel.getData() + "W\n");
            this.traceMessage("The heat pump gets the current power level used by the compressor");
        }


        Measure<Double> compressorPowerLevel = this.compressorBoundPort.getCurrentPower().getMeasure();
        Measure<Double> pumpPowerLevel = this.currentPower.getMeasure();

        Measure<Double> new_measure = new Measure<>(
                sensorPowerLevel.getData() +
                        compressorPowerLevel.getData() +
                        pumpPowerLevel.getData(), POWER_UNIT);
        SignalData<Double> ret = new SignalData<>(new_measure);

        if (HeatPump.VERBOSE) {
            this.traceMessage("The heat pump successfully completed the call to the compressor: "
                    + compressorPowerLevel.getData() + "W\n");
            this.traceMessage("The heat pump adds those power levels to its own power level: "
                    + new_measure.getData() + "W\n");
        }

        final double epsilon = 1e-6;
        assert ret.getMeasure().getData() >= -epsilon && ret.getMeasure().getData() <= getMaximumPower().getData() + epsilon :
            new PostconditionException("ret.getMeasure().getData() < 0.0 || ret.getMeasure().getData() > getMaximumPower().getData()");

        return ret;
    }

    /**
     *
     * Returns the maximum level of power
     *
     * <p><strong>Contract</strong></p>
     *
     * <pre>
     *   post {@code return != null}
     * </pre>
     * @return
     * @throws Exception
     */
    @Override
    public Measure<Double> getMaximumPower() throws Exception {

        if (HeatPump.VERBOSE) {
            this.traceMessage("The heat pump gets the maximum power level of the compressor\n");
        }

        Measure<Double> maxPowerCompressor = compressorBoundPort.getMaximumPower();

        if (HeatPump.VERBOSE) {
            this.traceMessage("The heat pump successfully completed the call to the compressor: "
                    + maxPowerCompressor.getData() + "W\n");
            this.traceMessage("The heat pump successfully gets the maximum power level of the temperature sensor\n");
        }

        Measure<Double> maxPowerSensor = temperatureOutboundPort.getMaximumPower();

        Measure<Double> res = new Measure<>(
                MAX_POWER_LEVEL.getData() + maxPowerCompressor.getData() + maxPowerSensor.getData(),
                POWER_UNIT);

        if (HeatPump.VERBOSE) {
            this.traceMessage("The heat pump successfully completed the call to the temperature sensor: "
                    + maxPowerSensor.getData() + "W\n");
            this.traceMessage("The heat pump adds those power levels to its own maximum power level: "
                    + res.getData() + "W\n");
        }

        assert res != null :
                new PostconditionException("res == null");

        return res;
    }

    /**
     *
     * Returns the current temperature of the smart home
     *
     * <p><strong>Contract</strong></p>
     *
     * <pre>
     *  pre {@code on()}
     *  post {@code return != null}
     * </pre>
     * @return
     * @throws Exception
     */
    @Override
    public SignalData<Double> getCurrentTemperature() throws Exception {

        assert this.on() :
                new PreconditionException("The heat pump is off");

        if (HeatPump.VERBOSE) {
            this.traceMessage("The heat pump gets the current temperature from the temperature sensor\n");
        }

        Measure<Double> temperature = this.temperatureOutboundPort.getTemperature();
        SignalData<Double> res = new SignalData<>(temperature);

        if (HeatPump.VERBOSE) {
            this.traceMessage("The heat pump successfully completed the call to the temperature sensor:"
                    + temperature.getData() + "°C\n");
        }

        assert res != null :
                new PostconditionException("res == null");

        return res;
    }

    @Override
    public Measure<Double> getTargetTemperature() throws Exception {

        assert this.on() :
                new PreconditionException("!on()");

        if (HeatPump.VERBOSE) {
            this.traceMessage("the heat pump gets the targetTemperature from the compressor\n");
        }

        Measure<Double> target = this.compressorBoundPort.getTargetTemperature();

        if (HeatPump.VERBOSE) {
            this.traceMessage("the heat pump successfully completed the call to the compressor: "
                    + target.getData() + "°C\n");
        }
        return target;
    }

    /**
     *
     * returns the minimum power required to make the device work
     *
     * <p><strong>Contract</strong></p>
     *
     * <pre>
     *  post {@code return != null}
     * </pre>
     * @return
     * @throws Exception
     */
    @Override
    public Measure<Double> getMinimumRequiredPower() throws Exception {

        if (HeatPump.VERBOSE) {
            this.traceMessage("The heat pump gets the minimum power from the compressor\n");
        }

        Measure<Double> minPowerCompressor = compressorBoundPort.getMinimumRequiredPower();

        if (HeatPump.VERBOSE) {
            this.traceMessage("The heat successfully completed the call to the compressor: "
                    + minPowerCompressor.getData() + "W\n");
            this.traceMessage("The heat pump gets the minimum power from the temperature sensor\n");
        }

        Measure<Double> minPowerSensor = temperatureOutboundPort.getMinimumRequiredPower();

        Measure<Double> res = new Measure<>(
                minPowerCompressor.getData() + minPowerSensor.getData() + MIN_REQUIRED_POWER_LEVEL.getData(),
                POWER_UNIT
        );

        if (HeatPump.VERBOSE) {
            this.traceMessage("The heat pump successfully completed the call to the temperature sensor: "
                    + minPowerSensor.getData() + "W\n");
            this.traceMessage("The heat pump adds those power levels to its own minimum power level: "
                    + res.getData() + "W\n");
        }

        return res;
    }

    private void basePowerRepartitionPolicy(Measure<Double> power) throws Exception {
        double surplus = power.getData() - this.getMinimumRequiredPower().getData();

        Measure<Double> min_compressor_measure = compressorBoundPort.getMinimumRequiredPower();
        Measure<Double> min_sensor_measure = temperatureOutboundPort.getMinimumRequiredPower();

        double min_power_compressor = min_compressor_measure.getData();
        double min_power_sensor = min_sensor_measure.getData();

        Measure<Double> new_power_measure;

        if (surplus > 0) {
            double powerShare = surplus / 3.;

            new_power_measure = new Measure<>(HeatPump.MIN_REQUIRED_POWER_LEVEL.getData() + powerShare, POWER_UNIT);
            compressorBoundPort.setPower(new Measure<>(min_power_compressor + powerShare, POWER_UNIT));
            temperatureOutboundPort.setPower(new Measure<>(min_power_sensor + powerShare, POWER_UNIT));
        } else {
            new_power_measure = MIN_REQUIRED_POWER_LEVEL;
            compressorBoundPort.setPower(min_compressor_measure);
            temperatureOutboundPort.setPower(min_sensor_measure);

        }

        if (HeatPump.VERBOSE) {
            this.traceMessage("the power set for the compressor is: "
                    + compressorBoundPort.getCurrentPower().getMeasure().getData() + "W\n");
            this.traceMessage("the power set for the compressor is: "
                    + temperatureOutboundPort.getCurrentPower().getMeasure().getData() + "W\n");
        }

        this.currentPower = new SignalData<>(new_power_measure);
    }

    /**
     *
     * Sets the power of the heat pump
     * and provides energy to its components.
     *
     * <p><strong>Contract</strong></p>
     *
     * <pre>
     *  pre {@code on()}
     *  pre {@code power != null}
     *  pre {@code power.getData == 0. || power.getData() >= getMinimumRequiredPower().getData()}
     *  pre {@code power.getData() <= getMaximumPower().getDate()}
     *  post {@code getCurrentPower().getMeasure().getData() == power.getData()}
     * </pre>
     * @throws Exception
     */
    @Override
    public void setCurrentPower(Measure<Double> power) throws Exception {

        assert this.on() :
                new PreconditionException("!on()");
        assert power != null :
                new PreconditionException("power == null");
        assert power.getData() == 0. || power.getData() >= this.getMinimumRequiredPower().getData() :
                new PreconditionException("power provided is different than 0 but inferior to the minimum required");
        assert power.getData() <= this.getMaximumPower().getData() :
                new PreconditionException("power provided is superior the maximum supported");

        if (power.getData() == 0.) {

            if (HeatPump.VERBOSE) {
                this.traceMessage("Power is not provided to the component");
            }

            this.currentPower = new SignalData<>(power);
            this.compressorBoundPort.setPower(power);
            this.temperatureOutboundPort.setPower(power);
        } else {
            basePowerRepartitionPolicy(power);
        }

        final double eps = 1e-6;
        assert getCurrentPower().getMeasure().getData() >= power.getData() - eps
                && power.getData() + eps >= getCurrentPower().getMeasure().getData():
               new PostconditionException("current power is not equals to the power provided");
    }

    @Override
    public void setCurrentPower(Measure<Double> power, PowerRepartitionPolicyI policy) throws Exception {
        assert on():
                new PreconditionException("device is off");
        assert power != null :
                new PreconditionException("power == null");
        assert power.getData() >= this.getMinimumRequiredPower().getData() :
                new PreconditionException("power provided is inferior to the minimum required");
        assert power.getData() <= this.getMaximumPower().getData() :
                new PreconditionException("power provided is superior the maximum supported");

        // TODO

        assert getCurrentPower().getMeasure().getData() == power.getData():
                new PostconditionException("current power is not equals to the power provided");
    }

    @Override
    public synchronized void start() throws ComponentStartException {

        super.start();

        try {
            // connection to the compressor
            this.doPortConnection(
                    this.compressorBoundPort.getPortURI(),
                    this.compressorInboundURI,
                    this.compressorConnectorClassName
            );
            // connection to the temperature sensor
            this.doPortConnection(
                    this.temperatureOutboundPort.getPortURI(),
                    this.bufferTankInboundURI,
                    this.bufferConnectorClassName
            );
            if (! this.isUnitTest) {
                this.doPortConnection(
                        this.registrationOutboundPort.getPortURI(),
                        this.registrationHEMURI,
                        this.registrationHEMConnectorClassName
                );
            }

        } catch (Exception e) {
            throw new ComponentStartException(e);
        }



    }

    @Override
    public synchronized void finalise() throws Exception
    {
        this.doPortDisconnection(this.compressorBoundPort.getPortURI());
        this.doPortDisconnection(this.temperatureOutboundPort.getPortURI());
        if (!this.isUnitTest) {
            this.doPortDisconnection(this.registrationOutboundPort.getPortURI());
        }
        super.finalise();
    }

    @Override
    public synchronized void shutdown() throws ComponentShutdownException
    {
        try {
            this.compressorBoundPort.unpublishPort();
            this.temperatureOutboundPort.unpublishPort();
            if (!this.isUnitTest) {
                this.registrationOutboundPort.unpublishPort();
            }

            this.userInboundPort.unpublishPort();
            this.internalInboundPort.unpublishPort();
            this.externalInboundPort.unpublishPort();
        } catch (Exception e) {
            throw new ComponentShutdownException(e) ;
        }

        super.shutdown();
    }

}
