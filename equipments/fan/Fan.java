package equipments.fan;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.exceptions.AssertionChecking;
import fr.sorbonne_u.exceptions.ImplementationInvariantException;
import fr.sorbonne_u.exceptions.InvariantException;
import fr.sorbonne_u.exceptions.PreconditionException;
import equipments.fan.connections.FanInboundPort;
import fr.sorbonne_u.alasca.physical_data.Measure;
import fr.sorbonne_u.alasca.physical_data.MeasurementUnit;

/**
 * The class <code>Fan</code> implements the fan component.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p>
 * The fan is an uncontrollable appliance, hence it does not connect
 * with the household energy manager. However, it will connect later
 * to the electric panel to take its (simulated) electricity consumption into account.
 * </p>
 *
 * <p><strong>Invariants</strong></p>
 * 
 * <pre>
 * invariant	{@code INITIAL_STATE != null}
 * invariant	{@code INITIAL_MODE != null}
 * invariant	{@code currentState != null}
 * invariant	{@code currentMode != null}
 * </pre>
 * 
 * <p><strong>Invariants</strong></p>
 * 
 * <pre>
 * invariant	{@code REFLECTION_INBOUND_PORT_URI != null && !REFLECTION_INBOUND_PORT_URI.isEmpty()}
 * invariant	{@code INBOUND_PORT_URI != null && !INBOUND_PORT_URI.isEmpty()}
 * invariant	{@code X_RELATIVE_POSITION >= 0}
 * invariant	{@code Y_RELATIVE_POSITION >= 0}
 * </pre>
 * 
 * <p>Created on : 2025-10-04</p>
 * 
 * @author	<a href="mailto:Rodrigo.Vila@etu.sorbonne-universite.fr">Rodrigo Vila</a>
 * @author	<a href="mailto:Damien.Ribeiro@etu.sorbonne-universite.fr">Damien Ribeiro</a>
 */
@OfferedInterfaces(offered={FanUserCI.class})
public class Fan
extends AbstractComponent
implements FanImplementationI
{
    // -------------------------------------------------------------------------
    // Constants and variables
    // -------------------------------------------------------------------------

    /** URI of the fan inbound port used in tests. */
    public static final String REFLECTION_INBOUND_PORT_URI = "FAN-RIP-URI";	
    /** URI of the fan inbound port used in tests. */
    public static final String INBOUND_PORT_URI = "FAN-INBOUND-PORT-URI";

    /** when true, methods trace their actions. */
    public static boolean VERBOSE = false;
    /** when tracing, x coordinate of the window relative position. */
    public static int X_RELATIVE_POSITION = 0;
    /** when tracing, y coordinate of the window relative position. */
    public static int Y_RELATIVE_POSITION = 0;

    // --- (adjust if needed) ---
    public static final Measure<Double> HIGH_POWER_IN_WATTS =
            new Measure<Double>(75.0, MeasurementUnit.WATTS);
    public static final Measure<Double> MEDIUM_POWER_IN_WATTS =
            new Measure<Double>(50.0, MeasurementUnit.WATTS);
    public static final Measure<Double> LOW_POWER_IN_WATTS =
            new Measure<Double>(25.0, MeasurementUnit.WATTS);

    public static final Measure<Double> VOLTAGE =
            new Measure<Double>(220.0, MeasurementUnit.VOLTS);

    /** initial state of the fan. */
    protected static final FanState INITIAL_STATE = FanState.OFF;
    /** initial mode of the fan. */
    protected static final FanMode INITIAL_MODE = FanMode.LOW;

    /** current state (on, off) of the fan. */
    protected FanState currentState;
    /** current mode of operation (low, medium, high) of the fan. */
    protected FanMode currentMode;

    /** inbound port offering the <code>FanUserCI</code> interface. */
    protected FanInboundPort fip;

    // -------------------------------------------------------------------------
    // Invariants
    // -------------------------------------------------------------------------
    
    /**
	 * return true if the implementation invariants are observed, false otherwise.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code f != null}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param f	instance to be tested.
	 * @return		true if the implementation invariants are observed, false otherwise.
	 */
    protected static boolean implementationInvariants(Fan f) {
        assert f != null : new PreconditionException("f != null");

        boolean ret = true;
        ret &= AssertionChecking.checkInvariant(
                INITIAL_STATE != null, Fan.class, f,
                "INITIAL_STATE != null");
        ret &= AssertionChecking.checkInvariant(
                INITIAL_MODE != null, Fan.class, f, 
                "INITIAL_MODE != null");
        ret &= AssertionChecking.checkInvariant(
                f.currentState != null, Fan.class, f, 
                "f.currentState != null");
        ret &= AssertionChecking.checkInvariant(
                f.currentMode != null, Fan.class, f, 
                "f.currentMode != null");
        return ret;
    }
    
    /**
	 * return true if the invariants are observed, false otherwise.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code f != null}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param f	instance to be tested.
	 * @return		true if the invariants are observed, false otherwise.
	 */
    protected static boolean invariants(Fan f) {
        assert f != null : new PreconditionException("f != null");

        boolean ret = true;
        ret &= AssertionChecking.checkImplementationInvariant(
                REFLECTION_INBOUND_PORT_URI != null && 
                			!REFLECTION_INBOUND_PORT_URI.isEmpty(),
                Fan.class, f,
                "REFLECTION_INBOUND_PORT_URI != null && "
                		+ "!REFLECTION_INBOUND_PORT_URI.isEmpty()");
        ret &= AssertionChecking.checkImplementationInvariant(
                INBOUND_PORT_URI != null && !INBOUND_PORT_URI.isEmpty(),
                Fan.class, f,
                "INBOUND_PORT_URI != null && !INBOUND_PORT_URI.isEmpty()");
        ret &= AssertionChecking.checkImplementationInvariant(
                X_RELATIVE_POSITION >= 0, Fan.class, f, "X_RELATIVE_POSITION >= 0");
        ret &= AssertionChecking.checkImplementationInvariant(
                Y_RELATIVE_POSITION >= 0, Fan.class, f, "Y_RELATIVE_POSITION >= 0");
        return ret;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------
    
    /**
	 * create a fan component.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code getState() == FanState.OFF}
	 * post	{@code getMode() == FanMode.LOW}
	 * </pre>
	 * 
	 * @throws Exception	<i>to do</i>.
	 */
    protected Fan() throws Exception {
        this(INBOUND_PORT_URI);
    }
    
    /**
	 * create a fan component.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code fanInboundPortURI != null && !fanInboundPortURI.isEmpty()}
	 * post	{@code getState() == FanState.OFF}
	 * post	{@code getMode() == FanMode.LOW}
	 * </pre>
	 * 
	 * @param fanInboundPortURI	URI of the fan inbound port.
	 * @throws Exception				<i>to do</i>.
	 */
    protected Fan(String fanInboundPortURI) throws Exception {
        this(REFLECTION_INBOUND_PORT_URI, fanInboundPortURI);
    }
    
    /**
	 * create a fan component with the given reflection innbound port
	 * URI.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code reflectionInboundPortURI != null && !reflectionInboundPortURI.isEmpty()}
	 * pre	{@code fanInboundPortURI != null && !fanInboundPortURI.isEmpty()}
	 * post	{@code getState() == FanState.OFF}
	 * post	{@code getMode() == FanMode.LOW}
	 * </pre>
	 *
	 * @param reflectionInboundPortURI	URI of the reflection innbound port of the component.
	 * @param fanInboundPortURI	URI of the fan inbound port.
	 * @throws Exception				<i>to do</i>.
	 */
    protected Fan(
    		String reflectionInboundPortURI, 
    		String fanInboundPortURI
    		) throws Exception 
    {
        super(reflectionInboundPortURI, 1, 0);
        this.initialise(fanInboundPortURI);
    }
    
    /**
	 * initialise the fan component.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code fanInboundPortURI != null && !fanInboundPortURI.isEmpty()}
	 * post	{@code getState() == FanState.OFF}
	 * post	{@code getMode() == FanMode.LOW}
	 * </pre>
	 * 
	 * @param fanInboundPortURI	URI of the fan inbound port.
	 * @throws Exception				<i>to do</i>.
	 */
    protected void initialise(String fanInboundPortURI) throws Exception {
        assert fanInboundPortURI != null : 
        	new PreconditionException("fanInboundPortURI != null");
        
        assert !fanInboundPortURI.isEmpty() : 
        	new PreconditionException("!fanInboundPortURI.isEmpty()");

        this.currentState = INITIAL_STATE;
        this.currentMode = INITIAL_MODE;
        this.fip = new FanInboundPort(fanInboundPortURI, this);
        this.fip.publishPort();

        if (Fan.VERBOSE) {
            this.tracer.get().setTitle("Fan component");
            this.tracer.get().setRelativePosition(X_RELATIVE_POSITION, Y_RELATIVE_POSITION);
            this.toggleTracing();
        }

        assert Fan.implementationInvariants(this) : 
        	new ImplementationInvariantException("Fan.implementationInvariants(this)");
        
        assert Fan.invariants(this) : 
        	new InvariantException("Fan.invariants(this)");
    }

    // -------------------------------------------------------------------------
    // Component life-cycle
    // -------------------------------------------------------------------------

    @Override
    public synchronized void shutdown() throws ComponentShutdownException {
        try {
            this.fip.unpublishPort();
        } catch (Throwable e) {
            throw new ComponentShutdownException(e);
        }
        super.shutdown();
    }

    // -------------------------------------------------------------------------
    // Component services implementation
    // -------------------------------------------------------------------------
    
    @Override
    public FanState getState() throws Exception {
        if (Fan.VERBOSE) {
            this.traceMessage("Fan returns its state : " + 
            								this.currentState + ".\n");
        }
        return this.currentState;
    }

    @Override
    public FanMode getMode() throws Exception {
        if (Fan.VERBOSE) {
            this.traceMessage("Fan returns its mode : " + 
            								this.currentMode + ".\n");
        }
        return this.currentMode;
    }

    @Override
    public void turnOn() throws Exception {
        if (Fan.VERBOSE) {
            this.traceMessage("Fan is turned on.\n");
        }
        assert this.getState() == FanState.OFF : 
        	new PreconditionException("getState() == FanState.OFF");

        this.currentState = FanState.ON;
        this.currentMode = FanMode.LOW;
    }

    @Override
    public void turnOff() throws Exception {
        if (Fan.VERBOSE) {
            this.traceMessage("Fan is turned off.\n");
        }
        assert this.getState() == FanState.ON : 
        	new PreconditionException("getState() == FanState.ON");

        this.currentState = FanState.OFF;
    }
    
    @Override
    public void setHigh() throws Exception {
        if (Fan.VERBOSE) {
            this.traceMessage("Fan is set to high speed.\n");
        }
        assert this.getState() == FanState.ON : 
        	new PreconditionException("getState() == FanState.ON");

        this.currentMode = FanMode.HIGH;
    }
    
    @Override
    public void setMedium() throws Exception {
        if (Fan.VERBOSE) {
            this.traceMessage("Fan is set to medium speed.\n");
        }
        assert this.getState() == FanState.ON : 
        	new PreconditionException("getState() == FanState.ON");

        this.currentMode = FanMode.MEDIUM;
    }

    @Override
    public void setLow() throws Exception {
        if (Fan.VERBOSE) {
            this.traceMessage("Fan is set to low speed.\n");
        }
        assert this.getState() == FanState.ON : 
        	new PreconditionException("getState() == FanState.ON");

        this.currentMode = FanMode.LOW;
    }
}
