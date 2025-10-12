package equipments.HeatPump;

import equipments.HeatPump.connections.HeatPumpExternalControlInboundPort;
import equipments.HeatPump.connections.HeatPumpInternalControlInboundPort;
import equipments.HeatPump.connections.HeatPumpUserInboundPort;
import equipments.HeatPump.powerRepartitionPolicy.PowerRepartitionPolicyI;
import equipments.HeatPump.temperatureSensor.TemperatureSensorCI;
import equipments.HeatPump.temperatureSensor.TemperatureSensorOutboundPort;
import equipments.HeatPump.compressor.CompressorCI;
import equipments.HeatPump.compressor.CompressorOutboundPort;
import equipments.HeatPump.interfaces.*;
import fr.sorbonne_u.alasca.physical_data.Measure;
import fr.sorbonne_u.alasca.physical_data.MeasurementUnit;
import fr.sorbonne_u.alasca.physical_data.SignalData;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import fr.sorbonne_u.components.annotations.RequiredInterfaces;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import fr.sorbonne_u.exceptions.PostconditionException;
import fr.sorbonne_u.exceptions.PreconditionException;

/**
 * The class <code>equipments.HeatPump.HeatPump</code>.
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
@RequiredInterfaces(required = {TemperatureSensorCI.class, CompressorCI.class})
@OfferedInterfaces(offered = {HeatPumpUserCI.class, HeatPumpInternalControlCI.class, HeatPumpExternalControlCI.class})
public class HeatPump
extends AbstractComponent
implements HeatPumpUserI,
           HeatPumpInternalControlI,
           HeatPumpExternalControlI {

    public static boolean VERBOSE = false;

    /** Standard URI of the port used to interface with the compressor */
    protected static final String COMPRESSOR_OUTBOUND_PORT_URI = "COMPRESSOR-OUTBOUND-URI";

    /** Standard URI of the port used to interface with the buffer tank */
    protected static final String BUFFER_TANK_OUTBOUND_PORT_URI = "BUFFER-TANK-OUTBOUND-URI";

    protected static final String REFLECTION_INBOUND_URI = "HEAT-PUMP-REFLECTION-INBOUND-URI";

    /** measurement unit for power used by the heat pump					      */
    protected static final MeasurementUnit POWER_UNIT = MeasurementUnit.WATTS;

    /** measurement unit for the temperature of fluids and air			          */
    protected static final MeasurementUnit TEMPERATURE_UNIT = MeasurementUnit.CELSIUS;

    /** maximum power level of the heat pump, in watts                                */
    protected static final Measure<Double> MAX_POWER_LEVEL = new Measure<>(100., POWER_UNIT);
    /** minimum power level of the heat pump, in watts                                */
    protected static final Measure<Double> MIN_POWER_LEVEL = new Measure<>(0., POWER_UNIT);
    /** standard power level of the heat pump, in watts                               */
    protected static final Measure<Double> STANDARD_POWER_LEVEL = new Measure<>(50., POWER_UNIT);
    /** minimum power required for the device to function */
    protected static final Measure<Double> MIN_REQUIRED_POWER_LEVEL = new Measure<>(10., POWER_UNIT);

    /** maximum temperature target for the heat pump, in celsius                       */
    protected static final Measure<Double> MAX_TARGET_TEMPERATURE = new Measure<>(-50., TEMPERATURE_UNIT);

    /** minimum temperature target for the heat pump, in celsius                       */
    protected static final Measure<Double> MIN_TARGET_TEMPERATURE = new Measure<>(50., TEMPERATURE_UNIT);

    /** standard temperature target for the heat pump, in celsius                      */
    protected static final Measure<Double> STD_TARGET_TEMPERATURE = new Measure<>(19., TEMPERATURE_UNIT);

    protected static final int NUMBER_THREADS = 1;
    protected static final int NUMBER_SCHEDULABLE_THREADS = 0;

    protected CompressorOutboundPort compressorBoundPort;
    protected String compressorInboundURI;

    protected TemperatureSensorOutboundPort bufferTankBoundPort;
    protected String bufferTankInboundURI;

    protected String compressorConnectorClassName;

    protected String bufferConnectorClassName;

    protected State pumpState;

    protected SignalData<Double> currentPower;

    protected Measure<Double> targetTemperature;

    protected HeatPumpUserInboundPort userInboundPort;
    protected HeatPumpInternalControlInboundPort internalInboundPort;
    protected HeatPumpExternalControlInboundPort externalInboundPort;

    protected HeatPump(
            String compressorURI,
            String bufferTankURI,
            String compressorCcName,
            String bufferCcName,
            String userInboundURI,
            String internalInboundURI,
            String externalInboundURI) throws Exception {
        this(REFLECTION_INBOUND_URI,
                compressorURI,
                bufferTankURI,
                compressorCcName,
                bufferCcName,
                userInboundURI,
                internalInboundURI,
                externalInboundURI);
    }

    protected HeatPump(
            String reflectionInboundPortURI,
            String compressorURI,
            String bufferTankURI,
            String compressorCcName,
            String bufferCcName,
            String userInboundURI,
            String internalInboundURI,
            String externalInboundURI) throws Exception {
        super(reflectionInboundPortURI, NUMBER_THREADS, NUMBER_SCHEDULABLE_THREADS);

        this.pumpState = State.Off;
        this.currentPower = new SignalData<>(MIN_POWER_LEVEL);
        this.targetTemperature = STD_TARGET_TEMPERATURE;

        this.compressorBoundPort = new CompressorOutboundPort(this);
        this.compressorBoundPort.publishPort();
        this.compressorInboundURI = compressorURI;

        this.bufferTankBoundPort = new TemperatureSensorOutboundPort(this);
        this.bufferTankBoundPort.publishPort();
        this.bufferTankInboundURI = bufferTankURI;

        this.compressorConnectorClassName = compressorCcName;
        this.bufferConnectorClassName = bufferCcName;

        this.userInboundPort = new HeatPumpUserInboundPort(userInboundURI, this);
        this.userInboundPort.publishPort();

        this.internalInboundPort = new HeatPumpInternalControlInboundPort(internalInboundURI, this);
        this.internalInboundPort.publishPort();

        this.externalInboundPort = new HeatPumpExternalControlInboundPort(externalInboundURI, this);
        this.externalInboundPort.publishPort();
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

        return this.pumpState == State.Heating;
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
                new PreconditionException("The Heat Pump is off");

        return this.pumpState == State.Cooling;
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
                new PreconditionException("The Heat Pump is off");
        assert !this.heating() :
                new PreconditionException("The Heat pump is already heating");

        this.pumpState = State.Heating;
        this.compressorBoundPort.startCompressing();

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

        this.pumpState = State.On;
        this.compressorBoundPort.switchOff();

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

        this.pumpState = State.Cooling;
        this.compressorBoundPort.startRelaxing();

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

        this.pumpState = State.On;
        this.compressorBoundPort.switchOff();

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
        return this.pumpState != State.Off;
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
                new PreconditionException("The heat pump is off");

        this.pumpState = State.Off;
        this.bufferTankBoundPort.switchOff();
        this.compressorBoundPort.switchOff();

        assert !this.on() :
                new PostconditionException("The heat pump is still off");
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
                new PreconditionException("The heat pump is on");

        this.pumpState = State.On;
        this.bufferTankBoundPort.switchOn();
        // we only turn on the compressor when

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

        this.targetTemperature = temperature;
        compressorBoundPort.setTargetTemperature(temperature);

        assert this.targetTemperature.equals(temperature) :
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

        Measure<Double> bufferPowerLevel = this.bufferTankBoundPort.getCurrentPower().getMeasure();
        Measure<Double> compressorPowerLevel = this.compressorBoundPort.getCurrentPower().getMeasure();

        Measure<Double> new_measure = new Measure<>(bufferPowerLevel.getData() + compressorPowerLevel.getData(), POWER_UNIT);
        SignalData<Double> ret = new SignalData<>(new_measure);

        assert ret.getMeasure().getData() >= 0.0 && ret.getMeasure().getData() <= getMaximumPower().getData() :
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

        Measure<Double> maxPowerCompressor = compressorBoundPort.getMaximumPower();
        Measure<Double> maxPowerSensor = bufferTankBoundPort.getMaximumPower();

        Measure<Double> res = new Measure<>(
                MAX_POWER_LEVEL.getData() + maxPowerCompressor.getData() + maxPowerSensor.getData(),
                POWER_UNIT);

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

        Measure<Double> temperature = this.bufferTankBoundPort.getTemperature();
        SignalData<Double> res = new SignalData<>(temperature);

        assert res != null :
                new PostconditionException("res == null");

        return res;
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

        Measure<Double> minPowerCompressor = compressorBoundPort.getMinimumRequiredPower();
        Measure<Double> minPowerSensor = bufferTankBoundPort.getMinimumRequiredPower();

        Measure<Double> res = new Measure<>(
                minPowerCompressor.getData() + minPowerSensor.getData() + MIN_REQUIRED_POWER_LEVEL.getData(),
                POWER_UNIT
        );

        return res;
    }

    private void basePowerRepartitionPolicy(Measure<Double> power) throws Exception {
        double surplus = power.getData() - this.getMaximumPower().getData();

        double min_power_compressor = compressorBoundPort.getMinimumRequiredPower().getData();
        double min_power_sensor = bufferTankBoundPort.getMinimumRequiredPower().getData();

        Measure<Double> new_power_measure;

        if (surplus > MAX_POWER_LEVEL.getData()) {
            new_power_measure = MAX_POWER_LEVEL;
            compressorBoundPort.setPower(new Measure<>(min_power_compressor + surplus / 2, POWER_UNIT));
            bufferTankBoundPort.setPower(new Measure<>(min_power_sensor + surplus / 2, POWER_UNIT));
        } else {
            new_power_measure = new Measure<>(MIN_REQUIRED_POWER_LEVEL.getData() + surplus, POWER_UNIT);
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
     *  pre {@code power != null}
     *  pre {@code power.getData() >= getMinimumRequiredPower().getData()}
     *  pre {@code power.getData() <= getMaximumPower().getDate()}
     *  post {@code getCurrentPower().getMeasure().getData() == power.getData()}
     * </pre>
     * @throws Exception
     */
    @Override
    public void setCurrentPower(Measure<Double> power) throws Exception {

        assert power != null :
                new PreconditionException("power == null");
        assert power.getData() >= this.getMinimumRequiredPower().getData() :
                new PreconditionException("power provided is inferior to the minimum required");
        assert power.getData() <= this.getMaximumPower().getData() :
                new PreconditionException("power provided is superior the maximum supported");

        basePowerRepartitionPolicy(power);

        assert getCurrentPower().getMeasure().getData() == power.getData():
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
                    this.bufferTankBoundPort.getPortURI(),
                    this.bufferTankInboundURI,
                    this.bufferConnectorClassName
            );
        } catch (Exception e) {
            throw new ComponentStartException(e);
        }

    }

    @Override
    public synchronized void	finalise() throws Exception
    {
        this.doPortDisconnection(this.compressorBoundPort.getPortURI());
        this.doPortDisconnection(this.bufferTankBoundPort.getPortURI());
        super.finalise();
    }

    @Override
    public synchronized void	shutdown() throws ComponentShutdownException
    {
        try {
            this.compressorBoundPort.unpublishPort();
            this.bufferTankBoundPort.unpublishPort();

            this.userInboundPort.unpublishPort();
            this.internalInboundPort.unpublishPort();
            this.externalInboundPort.unpublishPort();
        } catch (Exception e) {
            throw new ComponentShutdownException(e) ;
        }

        super.shutdown();
    }

}
