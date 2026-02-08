package equipments.HeatPump.compressor;

import fr.sorbonne_u.alasca.physical_data.Measure;
import fr.sorbonne_u.alasca.physical_data.MeasurementUnit;
import fr.sorbonne_u.alasca.physical_data.SignalData;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.exceptions.PostconditionException;
import fr.sorbonne_u.exceptions.PreconditionException;

/**
 * The class <code>equipments.HeatPump.Compressor.Compressor</code>.
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
@OfferedInterfaces(offered = {CompressorCI.class})
public class Compressor
extends AbstractComponent
implements CompressorI {

    protected static final String REFLECTION_INBOUND_URI = "COMPRESSOR--REFL-INBOUND-URI";

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

    /** maximum temperature target for the heat pump, in celsius                       */
    protected static final Measure<Double> MAX_TARGET_TEMPERATURE = new Measure<>(-50., TEMPERATURE_UNIT);

    /** minimum temperature target for the heat pump, in celsius                       */
    protected static final Measure<Double> MIN_TARGET_TEMPERATURE = new Measure<>(50., TEMPERATURE_UNIT);

    /** standard temperature target for the heat pump, in celsius                      */
    public static final Measure<Double> STD_TARGET_TEMPERATURE = new Measure<>(19., TEMPERATURE_UNIT);

    protected static final int NUMBER_THREADS = 1;
    protected static final int NUMBER_SCHEDULABLE_THREADS = 0;

    protected CompressorState state;
    protected SignalData<Double> currentPower;
    protected Measure<Double> target_temperature;

    protected CompressorInboundPort inboundPort;

    protected Compressor(String inboundURI) throws Exception {
        this(REFLECTION_INBOUND_URI, inboundURI);
    }

    protected Compressor(String reflectionInboundPortURI, String inboundURI) throws Exception {
        super(reflectionInboundPortURI, NUMBER_THREADS, NUMBER_SCHEDULABLE_THREADS);

        this.currentPower = new SignalData<>(STANDARD_POWER_LEVEL);
        this.target_temperature = STD_TARGET_TEMPERATURE;
        this.state = CompressorState.Off;

        this.inboundPort = new CompressorInboundPort(inboundURI, this);
        this.inboundPort.publishPort();
    }


    @Override
    public boolean compressing() throws Exception {
        return this.state == CompressorState.Compressing;
    }

    @Override
    public boolean relaxing() throws Exception {
        return this.state == CompressorState.Relaxing;
    }

    @Override
    public void startCompressing() throws Exception {
        this.state = CompressorState.Compressing;
    }

    @Override
    public void startRelaxing() throws Exception {
        this.state = CompressorState.Relaxing;
    }

    @Override
    public boolean on() throws Exception {
        return this.state != CompressorState.Off;
    }

    @Override
    public void switchOff() throws Exception {
        assert this.on():
                new PreconditionException("The compressor is off");

        this.state = CompressorState.Off;

        assert !this.on():
                new PreconditionException("The compressor is on");
    }

    @Override
    public void setTargetTemperature(Measure<Double> temperature) throws Exception {
        assert temperature != null :
                new PreconditionException("temperature == null");

        this.target_temperature = temperature;

        assert this.target_temperature.equals(temperature) :
                new PostconditionException("target_temperature of the compressor is different from the target");
    }

    @Override
    public SignalData<Double> getCurrentPower() throws Exception {

        SignalData<Double> res;
        res = this.currentPower;



        assert res.getMeasure().getData() >= 0.0 && res.getMeasure().getData() <= getMaximumPower().getData() :
                new PostconditionException("ret.getMeasure().getData() < 0.0 || ret.getMeasure().getData() > getMaximumPower().getData()");

        return res;
    }

    @Override
    public Measure<Double> getMinimumRequiredPower() throws Exception {
        return MIN_REQUIRED_POWER_LEVEL;
    }

    @Override
    public Measure<Double> getMaximumPower() {
        return MAX_POWER_LEVEL;
    }

    @Override
    public Measure<Double> getTargetTemperature() throws Exception {
        return this.target_temperature;
    }

    @Override
    public void setPower(Measure<Double> power) throws Exception {
        assert power != null :
                new PreconditionException("power == null");
        assert power.getData() == 0.0 || power.getData() >= this.getMinimumRequiredPower().getData() :
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
