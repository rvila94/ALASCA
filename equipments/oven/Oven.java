package equipments.oven;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.exceptions.AssertionChecking;
import fr.sorbonne_u.exceptions.ImplementationInvariantException;
import fr.sorbonne_u.exceptions.InvariantException;
import fr.sorbonne_u.exceptions.PreconditionException;
import equipments.oven.connections.OvenInboundPort;
import fr.sorbonne_u.alasca.physical_data.Measure;
import fr.sorbonne_u.alasca.physical_data.MeasurementUnit;

/**
 * The class <code>Oven</code> implements the oven component.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p>
 * The oven is a programmable appliance with multiple modes
 * (defrost, grill, custom temperature), an optional delayed start,
 * and internal states (on, off, programmed, cooking).
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
 * <p><strong>Component invariants</strong></p>
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
@OfferedInterfaces(offered = { OvenUserCI.class })
public class Oven
extends AbstractComponent
implements OvenImplementationI
{
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	public static final String REFLECTION_INBOUND_PORT_URI = "OVEN-RIP-URI";
	public static final String INBOUND_PORT_URI = "OVEN-INBOUND-PORT-URI";

	public static boolean VERBOSE = false;
	public static int X_RELATIVE_POSITION = 0;
	public static int Y_RELATIVE_POSITION = 0;

	// --- Power data (used later for consumption simulation) ---
	public static final Measure<Double> POWER_IN_WATTS =
			new Measure<Double>(2000.0, MeasurementUnit.WATTS);
	public static final Measure<Double> VOLTAGE =
			new Measure<Double>(220.0, MeasurementUnit.VOLTS);

	protected static final OvenState INITIAL_STATE = OvenState.OFF;
	protected static final OvenMode INITIAL_MODE = OvenMode.CUSTOM;

	protected OvenState currentState;
	protected OvenMode currentMode;
	protected int currentTemperature;
	protected boolean isCooking;
	protected int remainingTimeSeconds;
	protected int programmedDelaySeconds;

	protected OvenInboundPort oip;

	// -------------------------------------------------------------------------
	// Invariants
	// -------------------------------------------------------------------------
	
	protected static boolean implementationInvariants(Oven o) {
		assert o != null : new PreconditionException("o != null");

		boolean ret = true;
		ret &= AssertionChecking.checkInvariant(
				INITIAL_STATE != null, Oven.class, o, "INITIAL_STATE != null");
		ret &= AssertionChecking.checkInvariant(
				INITIAL_MODE != null, Oven.class, o, "INITIAL_MODE != null");
		ret &= AssertionChecking.checkInvariant(
				o.currentState != null, Oven.class, o, "o.currentState != null");
		ret &= AssertionChecking.checkInvariant(
				o.currentMode != null, Oven.class, o, "o.currentMode != null");
		return ret;
	}
	
	protected static boolean invariants(Oven o) {
		assert o != null : new PreconditionException("o != null");

		boolean ret = true;
		ret &= AssertionChecking.checkImplementationInvariant(
				REFLECTION_INBOUND_PORT_URI != null && 
										!REFLECTION_INBOUND_PORT_URI.isEmpty(),
				Oven.class, o, "REFLECTION_INBOUND_PORT_URI != null && !isEmpty()");
		ret &= AssertionChecking.checkImplementationInvariant(
				INBOUND_PORT_URI != null && !INBOUND_PORT_URI.isEmpty(),
				Oven.class, o, "INBOUND_PORT_URI != null && !isEmpty()");
		ret &= AssertionChecking.checkImplementationInvariant(
				X_RELATIVE_POSITION >= 0, Oven.class, o, "X_RELATIVE_POSITION >= 0");
		ret &= AssertionChecking.checkImplementationInvariant(
				Y_RELATIVE_POSITION >= 0, Oven.class, o, "Y_RELATIVE_POSITION >= 0");
		return ret;
	}

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------
	
	protected Oven() throws Exception {
		this(INBOUND_PORT_URI);
	}
	
	protected Oven(String ovenInboundPortURI) throws Exception {
		this(REFLECTION_INBOUND_PORT_URI, ovenInboundPortURI);
	}
		
	protected Oven(
			String reflectionInboundPortURI, 
			String ovenInboundPortURI
			) throws Exception 
	{
		super(reflectionInboundPortURI, 1, 0);
		this.initialise(ovenInboundPortURI);
	}
	
	protected void initialise(String ovenInboundPortURI) throws Exception {
		assert ovenInboundPortURI != null : new PreconditionException("ovenInboundPortURI != null");
		assert !ovenInboundPortURI.isEmpty() : new PreconditionException("!ovenInboundPortURI.isEmpty()");

		this.currentState = INITIAL_STATE;
		this.currentMode = INITIAL_MODE;
		this.currentTemperature = 0;
		this.isCooking = false;
		this.remainingTimeSeconds = 0;
		this.programmedDelaySeconds = 0;

		this.oip = new OvenInboundPort(ovenInboundPortURI, this);
		this.oip.publishPort();

		if (Oven.VERBOSE) {
			this.tracer.get().setTitle("Oven component");
			this.tracer.get().setRelativePosition(X_RELATIVE_POSITION, Y_RELATIVE_POSITION);
			this.toggleTracing();
		}

		assert Oven.implementationInvariants(this) : 
        	new ImplementationInvariantException("Oven.implementationInvariants(this)");
		assert Oven.invariants(this) : 
        	new InvariantException("Oven.invariants(this)");
	}

	// -------------------------------------------------------------------------
	// Component life-cycle
	// -------------------------------------------------------------------------

	@Override
	public synchronized void shutdown() throws ComponentShutdownException {
		try {
			this.oip.unpublishPort();
		} catch (Throwable e) {
			throw new ComponentShutdownException(e);
		}
		super.shutdown();
	}

	// -------------------------------------------------------------------------
	// Component services implementation
	// -------------------------------------------------------------------------

	@Override
	public OvenState getState() throws Exception {
		if (Oven.VERBOSE) {
            this.traceMessage("Oven returns its state : " + this.currentState + ".\n");
        }
		return this.currentState;
	}

	@Override
	public OvenMode getMode() throws Exception {
		if (Oven.VERBOSE) {
            this.traceMessage("Oven returns its mode : " + this.currentMode + ".\n");
        }
		return this.currentMode;
	}

	@Override
	public int getTemperature() throws Exception {
		return this.currentTemperature;
	}

	@Override
	public boolean isCooking() throws Exception {
		return this.isCooking;
	}

	@Override
	public void turnOn() throws Exception {
		if (Oven.VERBOSE) this.traceMessage("Oven is turned on.\n");

		assert this.getState() == OvenState.OFF :
				new PreconditionException("getState() == OvenState.OFF");

		this.currentState = OvenState.ON;
		this.currentMode = INITIAL_MODE;
		this.currentTemperature = 0;
		this.isCooking = false;
	}

	@Override
	public void turnOff() throws Exception {
		if (Oven.VERBOSE) this.traceMessage("Oven is turned off.\n");

		assert this.getState() != OvenState.OFF :
				new PreconditionException("getState() != OvenState.OFF");

		this.stopProgram();
		this.currentState = OvenState.OFF;
		this.isCooking = false;
	}

	@Override
	public void setDefrost() throws Exception {
		if (Oven.VERBOSE) this.traceMessage("Oven is set to DEFROST mode.\n");

		assert this.getState() == OvenState.ON || this.getState() == OvenState.PROGRAMMED :
				new PreconditionException("getState() == ON || PROGRAMMED");
		assert this.getMode() != OvenMode.DEFROST :
				new PreconditionException("getMode() != OvenMode.DEFROST");

		this.currentMode = OvenMode.DEFROST;
		this.currentTemperature = 80;
	}

	@Override
	public void setGrill() throws Exception {
		if (Oven.VERBOSE) this.traceMessage("Oven is set to GRILL mode.\n");

		assert this.getState() == OvenState.ON || this.getState() == OvenState.PROGRAMMED :
				new PreconditionException("getState() == ON || PROGRAMMED");
		assert this.getMode() != OvenMode.GRILL :
				new PreconditionException("getMode() != OvenMode.GRILL");

		this.currentMode = OvenMode.GRILL;
		this.currentTemperature = 220;
	}

	@Override
	public void setTemperature(int temperature) throws Exception {
		if (Oven.VERBOSE) this.traceMessage("Oven temperature set to " + temperature + "°C.\n");

		assert this.getState() == OvenState.ON :
				new PreconditionException("getState() == OvenState.ON");
		assert temperature >= 50 && temperature <= 300 :
				new PreconditionException("50 <= temperature <= 300");
		assert !(this.getMode() == OvenMode.CUSTOM 
										&& this.getTemperature() == temperature) :
				new PreconditionException(" getMode() == OvenMode.CUSTOM && "
											+ "getTemperature() == temperature");

		this.currentMode = OvenMode.CUSTOM;
		this.currentTemperature = temperature;
	}

	@Override
	public void startCooking(int durationInSeconds) throws Exception {
		if (Oven.VERBOSE)
			this.traceMessage("Oven started cooking for " + durationInSeconds + " seconds.\n");

		assert this.getState() == OvenState.ON :
				new PreconditionException("getState() == OvenState.ON");
		assert durationInSeconds > 0 :
				new PreconditionException("durationInSeconds > 0");

		this.currentState = OvenState.COOKING;
		this.isCooking = true;
		this.remainingTimeSeconds = durationInSeconds;		
	}

	@Override
	public void programCooking(int delayInSeconds, int durationInSeconds) throws Exception {
		if (Oven.VERBOSE)
			this.traceMessage("Oven programmed: starts in " + delayInSeconds +
										"s for " + durationInSeconds + "s.\n");

		assert this.getState() == OvenState.ON :
				new PreconditionException("getState() == OvenState.ON");
		assert delayInSeconds > 0 && durationInSeconds > 0 :
				new PreconditionException("delayInSeconds > 0 && durationInSeconds > 0");

		this.programmedDelaySeconds = delayInSeconds;
		this.remainingTimeSeconds = durationInSeconds;
		this.currentState = OvenState.PROGRAMMED;
	}

	@Override
	public void stopProgram() throws Exception {
		if (Oven.VERBOSE) this.traceMessage("Oven program stopped.\n");

		assert this.getState() == OvenState.PROGRAMMED 
				|| this.getState() == OvenState.COOKING :
				new PreconditionException("getState() == PROGRAMMED || COOKING");

		this.programmedDelaySeconds = 0;
		this.remainingTimeSeconds = 0;
		this.isCooking = false;
		this.currentState = OvenState.ON;			
	}
}
