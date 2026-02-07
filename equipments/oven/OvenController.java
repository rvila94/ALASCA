package equipments.oven;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.*;
import fr.sorbonne_u.components.exceptions.*;

import java.util.concurrent.TimeUnit;

import equipments.oven.connections.OvenActuatorOutboundPort;
import equipments.oven.connections.OvenControllerInboundPort;
import equipments.oven.connections.OvenExternalControlOutboundPort;

@RequiredInterfaces(required = {
        OvenExternalControlCI.class,
        OvenActuatorCI.class
})
@OfferedInterfaces(offered = {
        OvenControllerCI.class
})
public class OvenController
extends AbstractComponent
implements OvenControllerI {

    // -------------------------------------------------------------------------
    // Constants and configuration
    // -------------------------------------------------------------------------

    protected static final int NB_THREADS = 1;
    protected static final int NB_SCHEDULABLE_THREADS = 1;

    protected static final String REFLECTION_INBOUND_URI =
            "OVEN-CONTROLLER-REFLECTION-INBOUND-URI";

    public static final String CONTROLLER_INBOUND_URI =
            "OVEN-CONTROLLER-INBOUND-URI";

    protected final long controlPeriod;
    protected final TimeUnit timeUnit;

    // -------------------------------------------------------------------------
    // Ports
    // -------------------------------------------------------------------------

    protected OvenExternalControlOutboundPort externalPort;
    protected OvenActuatorOutboundPort actuatorPort;
    protected OvenControllerInboundPort controllerPort;

    protected final String ovenExternalURI;
    protected final String ovenActuatorURI;
    protected final String externalCCName;
    protected final String actuatorCCName;

    // -------------------------------------------------------------------------
    // Internal state
    // -------------------------------------------------------------------------

    protected enum ControlState {
        Off,
        On,
        Heating
    }

    protected ControlState currentState;

    // -------------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------------

    protected OvenController(
            String ovenExternalURI,
            String ovenActuatorURI,
            String externalCCName,
            String actuatorCCName,
            String ovenURI,
            double controlPeriod,
            double accelerationFactor
    ) throws Exception {

        super(REFLECTION_INBOUND_URI, NB_THREADS, NB_SCHEDULABLE_THREADS);

        this.ovenExternalURI = ovenExternalURI;
        this.ovenActuatorURI = ovenActuatorURI;
        this.externalCCName = externalCCName;
        this.actuatorCCName = actuatorCCName;

        this.controlPeriod =
                (long)((controlPeriod * TimeUnit.SECONDS.toNanos(1)) / accelerationFactor);
        this.timeUnit = TimeUnit.NANOSECONDS;

        this.currentState = ControlState.Off;

        this.externalPort = new OvenExternalControlOutboundPort(this);
        this.externalPort.publishPort();

        this.actuatorPort = new OvenActuatorOutboundPort(this);
        this.actuatorPort.publishPort();

        this.controllerPort =
                new OvenControllerInboundPort(ovenURI, this);
        this.controllerPort.publishPort();
    }

    // -------------------------------------------------------------------------
    // Control lifecycle
    // -------------------------------------------------------------------------

    @Override
    public void startControlling() throws Exception {
        assert this.currentState == ControlState.Off;

        this.currentState = ControlState.On;

        this.scheduleTask(
                owner -> ((OvenController) owner).controlLoop(),
                this.controlPeriod,
                this.timeUnit
        );
    }

    @Override
    public void stopControlling() throws Exception {
        synchronized (this) {
            this.currentState = ControlState.Off;
        }
    }

    // -------------------------------------------------------------------------
    // Control logic
    // -------------------------------------------------------------------------

    protected void updateState() throws Exception {

    	boolean ovenIsHeating =
                this.externalPort.getCurrentPowerLevel()
                    .getMeasure()
                    .getData() > 0.0;

        if (ovenIsHeating && this.currentState != ControlState.Heating) {
            this.actuatorPort.startHeating();
            this.currentState = ControlState.Heating;
        } 
        else if (!ovenIsHeating && this.currentState == ControlState.Heating) {
            this.actuatorPort.stopHeating();
            this.currentState = ControlState.On;
        }
    }


    protected void controlLoop() {

        synchronized (this) {
            if (this.currentState != ControlState.Off) {
                try {
                    this.updateState();
                } catch (Exception e) {
                    this.logMessage("Control error: " + e.getMessage());
                }

                this.scheduleTask(
                        owner -> ((OvenController) owner).controlLoop(),
                        this.controlPeriod,
                        this.timeUnit
                );
            }
        }
    }

    // -------------------------------------------------------------------------
    // Component lifecycle
    // -------------------------------------------------------------------------

    @Override
    public synchronized void start() throws ComponentStartException {
        super.start();
        try {
            this.doPortConnection(
                    this.externalPort.getPortURI(),
                    this.ovenExternalURI,
                    this.externalCCName
            );
            this.doPortConnection(
                    this.actuatorPort.getPortURI(),
                    this.ovenActuatorURI,
                    this.actuatorCCName
            );
        } catch (Exception e) {
            throw new ComponentStartException(e);
        }
    }

    @Override
    public synchronized void finalise() throws Exception {
        this.doPortDisconnection(this.externalPort.getPortURI());
        this.doPortDisconnection(this.actuatorPort.getPortURI());
        super.finalise();
    }

    @Override
    public synchronized void shutdown() throws ComponentShutdownException {
        try {
            this.externalPort.unpublishPort();
            this.actuatorPort.unpublishPort();
            this.controllerPort.unpublishPort();
        } catch (Exception e) {
            throw new ComponentShutdownException(e);
        }
        super.shutdown();
    }
}
