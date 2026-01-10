package equipments.HeatPump;

import equipments.HeatPump.connections.HeatPumpActuatorOutboundPort;
import equipments.HeatPump.connections.HeatPumpControllerInboundPort;
import equipments.HeatPump.connections.HeatPumpExternalControlOutboundPort;
import equipments.HeatPump.interfaces.*;
import fr.sorbonne_u.alasca.physical_data.SignalData;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import fr.sorbonne_u.components.annotations.RequiredInterfaces;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import fr.sorbonne_u.exceptions.PostconditionException;
import fr.sorbonne_u.exceptions.PreconditionException;

import java.util.concurrent.TimeUnit;

/**
 * The class <code>equipments.HeatPump.HeatPumpController</code>.
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
        HeatPumpExternalControlCI.class,
        HeatPumpActuatorCI.class
})
@OfferedInterfaces(offered = {
        HeatPumpControllerCI.class
})
public class HeatPumpController extends AbstractComponent implements HeatPumpControllerI {

    public static boolean VERBOSE = false;

    /** when tracing, x coordinate of the window relative position.			*/
    public static int X_RELATIVE_POSITION = 0;
    /** when tracing, y coordinate of the window relative position.			*/
    public static int Y_RELATIVE_POSITION = 0;

    protected static final int NB_THREADS = 1;
    protected static final int NB_SCHEDULABLE_THREADS = 1;
    protected static final String REFLECTION_INBOUND_URI = "HEAT-PUMP-CONTROLLER-REFLECTION-INBOUND-URI";

    public static final String CONTROLLER_INBOUND_URI = "HEAT-PUMP-CONTROLLER-INBOUND-URI";

    protected double hysteresis;
    protected double heating_threshold;
    protected double cooling_threshold;

    protected HeatPumpActuatorOutboundPort actuator_port;
    protected HeatPumpExternalControlOutboundPort external_port;

    protected String heatPumpExternalURI;
    protected String heatPumpActuatorURI;
    protected String externalCCName;
    protected String actuatorCCName;

    protected HeatPumpControllerInboundPort controller_port;

    protected long controlPeriod;

    protected TimeUnit time_unit;

    protected HeatPumpUserI.State current_state;

    protected HeatPumpController(
            String heatPumpExternalURI,
            String heatPumpActuatorURI,
            String externalCCName,
            String actuatorCCName,
            String heatPumpURI,
            double hysteresis,
            double heating_threshold,
            double cooling_threshold,
            double controlPeriod) throws Exception {
        this(REFLECTION_INBOUND_URI,
                heatPumpExternalURI,
                heatPumpActuatorURI,
                externalCCName,
                actuatorCCName,
                heatPumpURI, hysteresis, heating_threshold, cooling_threshold, controlPeriod);
    }

    /**
     * Description
     *
     * <p><strong>Contract</strong></p>
     *
     * <pre>
     *  pre {@code hysteresis > 0.0}
     *  pre {@code cooling_threshold < heating_threshold}
     *  post {@code true} // no postcondition
     * </pre>
     * @param reflectionInboundPortURI
     * @param heatPumpExternalURI
     * @param heatPumpActuatorURI
     * @param externalCCName
     * @param hysteresis
     */
    protected HeatPumpController(
            String reflectionInboundPortURI,
            String heatPumpExternalURI,
            String heatPumpActuatorURI,
            String externalCCName,
            String actuatorCCName,
            String heatPumpURI,
            double hysteresis,
            double heating_threshold,
            double cooling_threshold,
            double controlPeriod) throws Exception {
        super(reflectionInboundPortURI, NB_THREADS, NB_SCHEDULABLE_THREADS);

        this.current_state = HeatPumpUserI.State.Off;

        this.controlPeriod = (long) (controlPeriod * TimeUnit.SECONDS.toNanos(1));
        this.time_unit = TimeUnit.NANOSECONDS;

        this.hysteresis = hysteresis;
        this.heating_threshold = heating_threshold;
        this.cooling_threshold = cooling_threshold;

        this.heatPumpExternalURI = heatPumpExternalURI;
        this.heatPumpActuatorURI = heatPumpActuatorURI;
        this.externalCCName = externalCCName;
        this.actuatorCCName = actuatorCCName;

        this.external_port = new HeatPumpExternalControlOutboundPort(this);
        this.external_port.publishPort();

        this.actuator_port = new HeatPumpActuatorOutboundPort(this);
        this.actuator_port.publishPort();

        this.controller_port = new HeatPumpControllerInboundPort(heatPumpURI, this);
        this.controller_port.publishPort();
    }

    protected Double getCurrentTemperature() throws Exception {

        final SignalData<Double> current_temperature = this.external_port.getCurrentTemperature();

        return current_temperature.getMeasure().getData();
    }

    protected void tracing(String message) {
        if (VERBOSE)
            this.logMessage(message);
    }

    /**
     * @see equipments.HeatPump.interfaces.HeatPumpControllerI#startControlling
     */
    @Override
    public void startControlling() throws Exception {
        assert this.current_state == HeatPumpUserI.State.Off :
                new PreconditionException("this.current_state != State.Off");

        this.current_state = HeatPumpUserI.State.On;

        this.scheduleTask(
                owner -> ((HeatPumpController)owner).controlLoop(),
                this.controlPeriod,
                time_unit
        );

        assert this.current_state == HeatPumpUserI.State.On :
                new PostconditionException("this.current_state != State.On");
    }

    /**
     * @see equipments.HeatPump.interfaces.HeatPumpControllerI#stopControlling
     */
    @Override
    public void stopControlling() throws Exception {
        assert this.current_state != HeatPumpUserI.State.Off :
                new PreconditionException("this.current_state == State.Off");

        synchronized ( this ) {
            this.current_state = HeatPumpUserI.State.Off;
        }

        assert this.current_state == HeatPumpUserI.State.Off :
                new PostconditionException("this.current_state != State.Off");
    }

    protected enum ActionState {
        StartHeating,
        StartCooling,
        StopHeating,
        StopCooling,
        Idle
    }

    protected ActionState recommendedAction() throws Exception {

        Double current_temperature = this.getCurrentTemperature();

        ActionState result = ActionState.Idle;

        if ( current_temperature < this.heating_threshold - this.hysteresis
                && this.current_state == HeatPumpUserI.State.On ) {
            result = ActionState.StartHeating;
        } else if ( current_temperature > this.cooling_threshold + this.hysteresis
                && this.current_state == HeatPumpUserI.State.On ) {
            result = ActionState.StartCooling;
        } else if ( this.current_state == HeatPumpUserI.State.Heating ) {
            result = ActionState.StopHeating;
        } else if ( this.current_state == HeatPumpUserI.State.Cooling ) {
            result = ActionState.StopCooling;
        }

        return result;
    }

    protected void updateState() throws Exception {

        ActionState recommended = this.recommendedAction();

        switch (recommended) {
            case StartHeating:
                this.actuator_port.startHeating();
                this.current_state = HeatPumpUserI.State.Heating;
                break;
            case StartCooling:
                this.actuator_port.startCooling();
                this.current_state = HeatPumpUserI.State.Cooling;
                break;
            case StopHeating:
                this.actuator_port.stopHeating();
                this.current_state = HeatPumpUserI.State.On;
                break;
            case StopCooling:
                this.actuator_port.stopCooling();
                this.current_state = HeatPumpUserI.State.On;
                break;
            default:
        }

    }

    @Override
    public synchronized void start() throws ComponentStartException {

        super.start();

        try {
            this.doPortConnection(
                    this.external_port.getPortURI(),
                    this.heatPumpExternalURI,
                    this.externalCCName
            );
            this.doPortConnection(
                    this.actuator_port.getPortURI(),
                    this.heatPumpActuatorURI,
                    this.actuatorCCName
            );
        } catch (Exception e) {
            throw new ComponentStartException(e);
        }

    }

    @Override
    public synchronized void finalise() throws Exception
    {
        this.doPortDisconnection(this.external_port.getPortURI());
        this.doPortDisconnection(this.actuator_port.getPortURI());

        super.finalise();
    }

    @Override
    public synchronized void shutdown() throws ComponentShutdownException
    {
        try {
            this.external_port.unpublishPort();
            this.actuator_port.unpublishPort();

            this.controller_port.unpublishPort();
        } catch (Exception e) {
            throw new ComponentShutdownException(e) ;
        }

        super.shutdown();
    }

    public void controlLoop() {

        synchronized ( this ) {

            if (this.current_state != HeatPumpUserI.State.Off) {

                try {
                    this.updateState();
                } catch (Exception e) {
                    StringBuilder builder = new StringBuilder();
                    builder.append("Error: ");
                    builder.append(e.getMessage());
                    this.tracing(builder.toString());
                }

                this.scheduleTask(
                        owner -> ((HeatPumpController) owner).controlLoop(),
                        this.controlPeriod,
                        time_unit
                );

            }
        }

    }
}
