package equipments.HeatPump.temperatureSensor;

import fr.sorbonne_u.alasca.physical_data.Measure;
import fr.sorbonne_u.alasca.physical_data.MeasurementUnit;
import fr.sorbonne_u.alasca.physical_data.SignalData;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.exceptions.PostconditionException;
import fr.sorbonne_u.exceptions.PreconditionException;

/**
 * The class <code>equipments.HeatPump.TemperatureSensor.TemperatureSensor</code>.
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
@OfferedInterfaces(offered = {TemperatureSensorCI.class})
public class TemperatureSensor
extends AbstractComponent
implements TemperatureSensorI {

    protected static final String REFLECTION_INBOUND_URI = "SENSOR-INBOUND-URI";

    /** measurement unit for power used by the heat pump					      */
    protected static final MeasurementUnit POWER_UNIT = MeasurementUnit.WATTS;

    /** measurement unit for the temperature of fluids and air			          */
    protected static final MeasurementUnit TEMPERATURE_UNIT = MeasurementUnit.CELSIUS;

    /** maximum power level of the compressor, in watts                                */
    public static final Measure<Double> MAX_POWER_LEVEL = new Measure<>(400., POWER_UNIT);
    /** standard power level of the compressor, in watts                               */
    public static final Measure<Double> STANDARD_POWER_LEVEL = new Measure<>(50., POWER_UNIT);
    /** minimum power required for the compressor to function */
    protected static final Measure<Double> MIN_REQUIRED_POWER_LEVEL = new Measure<>(10., POWER_UNIT);

    public static final Measure<Double> FAKE_CURRENT_TEMPERATURE = new Measure<>(10., TEMPERATURE_UNIT);

    protected static final int NUMBER_THREADS = 1;
    protected static final int NUMBER_SCHEDULABLE_THREADS = 0;

    protected SignalData<Double> currentPower;

    protected SensorState state;

    protected TemperatureSensorInboundPort inboundPort;

    protected TemperatureSensor(String inboundURI) throws Exception {
        this(REFLECTION_INBOUND_URI, inboundURI);
    }

    protected TemperatureSensor(String reflectionInboundPortURI, String inboundURI) throws Exception {
        super(reflectionInboundPortURI, NUMBER_THREADS, NUMBER_SCHEDULABLE_THREADS);

        this.state = SensorState.Off;
        this.currentPower = new SignalData<>(STANDARD_POWER_LEVEL);

        this.inboundPort = new TemperatureSensorInboundPort(inboundURI, this);
        this.inboundPort.publishPort();
    }


    @Override
    public boolean on() throws Exception {
        return this.state == SensorState.On;
    }

    @Override
    public void switchOn() throws Exception {
        assert !this.on() :
                new PreconditionException("Device is on");

        this.state = SensorState.On;

        assert this.on():
                new PreconditionException("Device is off");
    }

    @Override
    public void switchOff() throws Exception {
        assert this.on() :
                new PreconditionException("Device is off");

        this.state = SensorState.Off;

        assert !this.on():
                new PreconditionException("Device is on");
    }

    @Override
    public SignalData<Double> getCurrentPower() throws Exception {
        assert this.on():
                new PreconditionException("heat pump is off");

        SignalData<Double> ret = this.currentPower;

        assert ret.getMeasure().getData() >= 0.0 && ret.getMeasure().getData() <= getMaximumPower().getData() :
                new PostconditionException("ret.getMeasure().getData() < 0.0 || ret.getMeasure().getData() > getMaximumPower().getData()");

        return ret;
    }

    @Override
    public Measure<Double> getTemperature() throws Exception {
        assert this.on():
                new PreconditionException("Device is off");

        Measure<Double> currentTemperature = FAKE_CURRENT_TEMPERATURE;

        return currentTemperature;
    }

    @Override
    public Measure<Double> getMinimumRequiredPower() throws Exception {
        return MIN_REQUIRED_POWER_LEVEL;
    }

    @Override
    public Measure<Double> getMaximumPower() throws Exception {
        return MAX_POWER_LEVEL;
    }

    @Override
    public void setPower(Measure<Double> power) throws Exception {
        assert this.on():
                new PreconditionException("device is off");
        assert power != null :
                new PreconditionException("power == null");
        assert power.getData() == 0. || power.getData() >= this.getMinimumRequiredPower().getData() :
                new PreconditionException("power provided is not zero but inferior to the minimum required");
        assert power.getData() <= this.getMaximumPower().getData() :
                new PreconditionException("power provided is superior the maximum supported");

        this.currentPower = new SignalData<>(power);

        //assert getCurrentPower().getMeasure().getData() == power.getData():
                //new PostconditionException("current power is not equals to the power provided");
    }

    @Override
    public synchronized void	shutdown() throws ComponentShutdownException
    {
        try {
            this.inboundPort.unpublishPort();
        } catch (Exception e) {
            throw new ComponentShutdownException(e) ;
        }

        super.shutdown();
    }
}
