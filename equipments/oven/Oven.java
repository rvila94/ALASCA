package equipments.oven;

import java.util.HashMap;
import java.util.Map;

import fr.sorbonne_u.alasca.physical_data.Measure;
import fr.sorbonne_u.alasca.physical_data.SignalData;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import equipments.oven.connections.OvenExternalControlInboundPort;
import equipments.oven.connections.OvenInternalControlInboundPort;
import equipments.oven.connections.OvenUserInboundPort;
import fr.sorbonne_u.exceptions.AssertionChecking;
import fr.sorbonne_u.exceptions.ImplementationInvariantException;
import fr.sorbonne_u.exceptions.InvariantException;
import fr.sorbonne_u.exceptions.PostconditionException;
import fr.sorbonne_u.exceptions.PreconditionException;

/**
 * The class <code>Oven</code> implements an oven component.
 *
 * <p><strong>Description</strong></p>
 * 
 * A component simulating an oven device that can operate in several modes:
 * <ul>
 *   <li>{@code CUSTOM} where the user defines the target temperature,</li>
 *   <li>{@code DEFROST} where a gentle fixed temperature is used,</li>
 *   <li>{@code GRILL} where a high fixed temperature is used.</li>
 * </ul>
 * The oven can also start cooking after a delay, represented by the
 * {@code WAITING} state.
 * 
 * <p><strong>Implementation Invariants</strong></p>
 * 
 * <pre>
 * invariant {@code currentState != null}
 * invariant {@code currentMode != null}
 * invariant {@code targetTemperature != null && targetTemperature.getMeasurementUnit().equals(TEMPERATURE_UNIT)}
 * invariant {@code targetTemperature.getData() >= MIN_TARGET_TEMPERATURE.getData() && targetTemperature.getData() <= MAX_TARGET_TEMPERATURE.getData()}
 * invariant {@code currentPowerLevel == null || currentPowerLevel.getMeasure().getMeasurementUnit().equals(POWER_UNIT)}
 * invariant {@code currentPowerLevel == null || currentPowerLevel.getMeasure().getData() >= 0.0 && currentPowerLevel.getMeasure().getData() <= MAX_POWER_LEVEL.getData()}
 * </pre>
 * 
 * <p><strong>Invariants</strong></p>
 * 
 * <pre>
 * invariant {@code REFLECTION_INBOUND_PORT_URI != null && !REFLECTION_INBOUND_PORT_URI.isEmpty()}
 * invariant {@code USER_INBOUND_PORT_URI != null && !USER_INBOUND_PORT_URI.isEmpty()}
 * invariant {@code INTERNAL_CONTROL_INBOUND_PORT_URI != null && !INTERNAL_CONTROL_INBOUND_PORT_URI.isEmpty()}
 * invariant {@code EXTERNAL_CONTROL_INBOUND_PORT_URI != null && !EXTERNAL_CONTROL_INBOUND_PORT_URI.isEmpty()}
 * invariant {@code X_RELATIVE_POSITION >= 0}
 * invariant {@code Y_RELATIVE_POSITION >= 0}
 * </pre>
 * 
 * <p>Created on : 2025-10-10</p>
 * 
 * @author	<a href="mailto:Rodrigo.Vila@etu.sorbonne-universite.fr">Rodrigo Vila</a>
 * @author	<a href="mailto:Damien.Ribeiro@etu.sorbonne-universite.fr">Damien Ribeiro</a>
 */
@OfferedInterfaces(offered={OvenUserCI.class, OvenInternalControlCI.class, OvenExternalControlCI.class})
public class			Oven
extends		AbstractComponent
implements	OvenUserI, OvenInternalControlI
{
	// -------------------------------------------------------------------------
	// Inner types
	// -------------------------------------------------------------------------

	/**
	 * The enumeration <code>OvenState</code> describes the operation
	 * states of the oven.
	 *
	 * <p><strong>Description</strong></p>
	 * 
	 * <p>Created on : 2025-10-10</p>
	 * 
	 * @author	<a href="mailto:Rodrigo.Vila@etu.sorbonne-universite.fr">Rodrigo Vila</a>
	 * @author	<a href="mailto:Damien.Ribeiro@etu.sorbonne-universite.fr">Damien Ribeiro</a>
	 */
	 public static enum OvenState {
		/** Oven is off. */
		OFF,
		/** Oven is on. */
		ON,
		/** Oven is in delayed start. */
		WAITING,
		/** Oven is cooking. */
		HEATING
	}

	/**
	 * The enumeration <code>OvenMode</code> describes the operation
	 * modes of the oven.
	 *
	 * <p><strong>Description</strong></p>
	 * 
	 * <p>
	 * The oven can be either in <code>CUSTOM</code> mode (user defines temperature) or
	 * in <code>DEFROST</code> mode (temperature = 80°C) or
	 * in <code>GRILL</code> mode (temperature = 220°C).
	 * </p>
	 * 
	 * <p>Created on : 2025-10-10</p>
	 * 
	 * @author	<a href="mailto:Rodrigo.Vila@etu.sorbonne-universite.fr">Rodrigo Vila</a>
	 * @author	<a href="mailto:Damien.Ribeiro@etu.sorbonne-universite.fr">Damien Ribeiro</a>
	 */
	 public static enum OvenMode {
		/** User defines the temperature. */
		CUSTOM, 
		/** Temperature set at 80°C. */
		DEFROST, 
		/** Temperature set at 220°C. */
		GRILL
	}

	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	/** URIs for component ports. */
	public static final String REFLECTION_INBOUND_PORT_URI = "OVEN-RIP-URI";
	public static final String USER_INBOUND_PORT_URI = "OVEN-USER-INBOUND-PORT-URI";
	public static final String INTERNAL_CONTROL_INBOUND_PORT_URI = "OVEN-INTERNAL-CONTROL-INBOUND-PORT-URI";
	public static final String EXTERNAL_CONTROL_INBOUND_PORT_URI = "OVEN-EXTERNAL-CONTROL-INBOUND-PORT-URI";

	/** Inbound ports. */
	protected OvenUserInboundPort ouip;
	protected OvenInternalControlInboundPort oicip;
	protected OvenExternalControlInboundPort oecip;

	/** Tracing. */
	public static boolean VERBOSE = false;
	public static int X_RELATIVE_POSITION = 0;
	public static int Y_RELATIVE_POSITION = 0;

	/** Default temperatures per mode. */
	protected static final Map<OvenMode, Measure<Double>> MODE_TEMPERATURES = new HashMap<>();
	static {
		MODE_TEMPERATURES.put(OvenMode.DEFROST, new Measure<>(80.0, TEMPERATURE_UNIT));
		MODE_TEMPERATURES.put(OvenMode.GRILL, new Measure<>(220.0, TEMPERATURE_UNIT));
	}
	
	/** Fake temperature when not connected to a simulator. */
	public static final SignalData<Double> FAKE_CURRENT_TEMPERATURE =
		new SignalData<>(new Measure<>(100.0, TEMPERATURE_UNIT));

	/** Oven state and parameters. */
	protected OvenState currentState;
	protected OvenMode currentMode;
	protected SignalData<Double> currentPowerLevel;
	protected Measure<Double> targetTemperature;

	// -------------------------------------------------------------------------
	// Invariants
	// -------------------------------------------------------------------------

	/**
	 * return true if the implementation invariants are observed, false
	 * otherwise.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code o != null}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param o	instance to be tested.
	 * @return	true if the implementation invariants are observed, false otherwise.
	 */
	protected static boolean	implementationInvariants(Oven o)
	{
		assert	o != null : new PreconditionException("o != null");

		boolean ret = true;
		ret &= AssertionChecking.checkImplementationInvariant(
				o.currentState != null,
				Oven.class, o,
				"o.currentState != null");
		ret &= AssertionChecking.checkImplementationInvariant(
				o.currentMode != null,
				Oven.class, o,
				"o.currentMode != null");
		ret &= AssertionChecking.checkImplementationInvariant(
				o.targetTemperature != null &&
				o.targetTemperature.getMeasurementUnit().equals(TEMPERATURE_UNIT),
				Oven.class, o,
				"o.targetTemperature != null && o.targetTemperature.getMeasurementUnit().equals(TEMPERATURE_UNIT)");
		ret &= AssertionChecking.checkImplementationInvariant(
				o.targetTemperature.getData() >= MIN_TARGET_TEMPERATURE.getData() &&
				o.targetTemperature.getData() <= MAX_TARGET_TEMPERATURE.getData(),
				Oven.class, o,
				"o.targetTemperature.getData() >= MIN_TARGET_TEMPERATURE.getData() && "
				+ "o.targetTemperature.getData() <= MAX_TARGET_TEMPERATURE.getData()");
		ret &= AssertionChecking.checkImplementationInvariant(
				o.currentPowerLevel == null ||
				(o.currentPowerLevel.getMeasure().getData() >= 0.0 &&
				 o.currentPowerLevel.getMeasure().getData() <= MAX_POWER_LEVEL.getData()),
				Oven.class, o,
				"currentPowerLevel == null || "
				+ "(currentPowerLevel.getMeasure().getData() >= 0.0 && "
				+ "currentPowerLevel.getMeasure().getData() <= MAX_POWER_LEVEL.getData())");
		return ret;
	}

	/**
	 * return true if the invariants are observed, false otherwise.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code o != null}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param o	instance to be tested.
	 * @return	true if the invariants are observed, false otherwise.
	 */
	protected static boolean	invariants(Oven o)
	{
		assert	o != null : new PreconditionException("o != null");

		boolean ret = true;

		ret &= OvenTemperatureI.invariants(o);
		ret &= OvenExternalControlI.invariants(o);
		ret &= AssertionChecking.checkInvariant(
				REFLECTION_INBOUND_PORT_URI != null &&
									!REFLECTION_INBOUND_PORT_URI.isEmpty(),
				Oven.class, o,
				"REFLECTION_INBOUND_PORT_URI != null && "
								+ "!REFLECTION_INBOUND_PORT_URI.isEmpty()");
		ret &= AssertionChecking.checkInvariant(
				USER_INBOUND_PORT_URI != null && !USER_INBOUND_PORT_URI.isEmpty(),
				Oven.class, o,
				"USER_INBOUND_PORT_URI != null && !USER_INBOUND_PORT_URI.isEmpty()");
		ret &= AssertionChecking.checkInvariant(
				INTERNAL_CONTROL_INBOUND_PORT_URI != null &&
								!INTERNAL_CONTROL_INBOUND_PORT_URI.isEmpty(),
				Oven.class, o,
				"INTERNAL_CONTROL_INBOUND_PORT_URI != null && "
							+ "!INTERNAL_CONTROL_INBOUND_PORT_URI.isEmpty()");
		ret &= AssertionChecking.checkInvariant(
				EXTERNAL_CONTROL_INBOUND_PORT_URI != null &&
								!EXTERNAL_CONTROL_INBOUND_PORT_URI.isEmpty(),
				Oven.class, o,
				"EXTERNAL_CONTROL_INBOUND_PORT_URI != null && "
							+ "!EXTERNAL_CONTROL_INBOUND_PORT_URI.isEmpty()");

		ret &= AssertionChecking.checkInvariant(
				X_RELATIVE_POSITION >= 0,
				Oven.class, o,
				"X_RELATIVE_POSITION >= 0");
		ret &= AssertionChecking.checkInvariant(
				Y_RELATIVE_POSITION >= 0,
				Oven.class, o,
				"Y_RELATIVE_POSITION >= 0");
		return ret;
	}

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------
	
	/**
	 * create a new oven.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 * 
	 * @throws Exception <i>to do</i>.
	 */
	protected Oven() throws Exception {
		this(USER_INBOUND_PORT_URI, INTERNAL_CONTROL_INBOUND_PORT_URI, EXTERNAL_CONTROL_INBOUND_PORT_URI);
	}
	
	/**
	 * create a new oven.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code ovenUserInboundPortURI != null && !ovenUserInboundPortURI.isEmpty()}
	 * pre	{@code ovenInternalControlInboundPortURI != null && !ovenInternalControlInboundPortURI.isEmpty()}
	 * pre	{@code ovenExternalControlInboundPortURI != null && !ovenExternalControlInboundPortURI.isEmpty()}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 * 
	 * @param ovenUserInboundPortURI				URI of the inbound port to call the oven component for user interactions.
	 * @param ovenInternalControlInboundPortURI		URI of the inbound port to call the oven component for internal control.
	 * @param ovenExternalControlInboundPortURI		URI of the inbound port to call the oven component for external control.
	 * @throws Exception							<i>to do</i>.
	 */
	protected Oven(
			String userInboundPortURI, 
			String internalControlInboundPortURI, 
			String externalControlInboundPortURI
			) throws Exception 
	{
		super(1, 0);
		this.initialise(userInboundPortURI, internalControlInboundPortURI, externalControlInboundPortURI);
	}
	
	/**
	 * create a new oven.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code reflectionInboundPortURI != null && !reflectionInboundPortURI.isEmpty()}
	 * pre	{@code ovenUserInboundPortURI != null && !ovenUserInboundPortURI.isEmpty()}
	 * pre	{@code ovenInternalControlInboundPortURI != null && !ovenInternalControlInboundPortURI.isEmpty()}
	 * pre	{@code ovenExternalControlInboundPortURI != null && !ovenExternalControlInboundPortURI.isEmpty()}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 * 
	 * @param reflectionInboundPortURI				URI of the reflection inbound port of the component.
	 * @param ovenUserInboundPortURI				URI of the inbound port to call the oven component for user interactions.
	 * @param ovenInternalControlInboundPortURI		URI of the inbound port to call the oven component for internal control.
	 * @param ovenExternalControlInboundPortURI		URI of the inbound port to call the oven component for external control.
	 * @throws Exception							<i>to do</i>.
	 */
	protected Oven(
			String reflectionInboundPortURI, 
			String userInboundPortURI, 
			String internalControlInboundPortURI, 
			String externalControlInboundPortURI
			) throws Exception 
	{
		super(reflectionInboundPortURI, 1, 0);
		this.initialise(userInboundPortURI, internalControlInboundPortURI, externalControlInboundPortURI);
	}
	
	/**
	 * create a new oven.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code ovenUserInboundPortURI != null && !ovenUserInboundPortURI.isEmpty()}
	 * pre	{@code ovenInternalControlInboundPortURI != null && !ovenInternalControlInboundPortURI.isEmpty()}
	 * pre	{@code ovenExternalControlInboundPortURI != null && !ovenExternalControlInboundPortURI.isEmpty()}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param ovenUserInboundPortURI				URI of the inbound port to call the oven component for user interactions.
	 * @param ovenInternalControlInboundPortURI		URI of the inbound port to call the oven component for internal control.
	 * @param ovenExternalControlInboundPortURI		URI of the inbound port to call the oven component for external control.
	 * @throws Exception							<i>to do</i>.
	 */
	protected void initialise(
			String ovenUserInboundPortURI, 
			String ovenInternalControlInboundPortURI, 
			String ovenExternalControlInboundPortURI
			) throws Exception 
	{
		assert	ovenUserInboundPortURI != null && !ovenUserInboundPortURI.isEmpty();
		assert	ovenInternalControlInboundPortURI != null && !ovenInternalControlInboundPortURI.isEmpty();
		assert	ovenExternalControlInboundPortURI != null && !ovenExternalControlInboundPortURI.isEmpty();
		
		this.currentState = OvenState.OFF;
		this.currentMode = OvenMode.CUSTOM;
		this.currentPowerLevel = new SignalData<>(new Measure<>(0.0, POWER_UNIT));
		this.targetTemperature = new Measure<>(0.0, TEMPERATURE_UNIT);

		this.ouip = new OvenUserInboundPort(ovenUserInboundPortURI, this);
		this.ouip.publishPort();
		this.oicip = new OvenInternalControlInboundPort(ovenInternalControlInboundPortURI, this);
		this.oicip.publishPort();
		this.oecip = new OvenExternalControlInboundPort(ovenExternalControlInboundPortURI, this);
		this.oecip.publishPort();

		if (VERBOSE) {
			this.tracer.get().setTitle("Oven component");
			this.tracer.get().setRelativePosition(X_RELATIVE_POSITION, 
													Y_RELATIVE_POSITION);
			this.toggleTracing();
		}

		assert Oven.implementationInvariants(this) : 
					new ImplementationInvariantException(
							"Oven.implementationInvariants(this)");
		assert Oven.invariants(this) : 
					new InvariantException("Oven.invariants(this)");
	}

	// -------------------------------------------------------------------------
	// Component life-cycle
	// -------------------------------------------------------------------------

	@Override
	public synchronized void shutdown() throws ComponentShutdownException {
		try {
			this.ouip.unpublishPort();
			this.oicip.unpublishPort();
			this.oecip.unpublishPort();
		} catch (Throwable e) {
			throw new ComponentShutdownException(e);
		}
		super.shutdown();
	}

	// -------------------------------------------------------------------------
	// Component services implementation
	// -------------------------------------------------------------------------

	@Override
	public boolean on() throws Exception {
		if (Oven.VERBOSE) {
			this.traceMessage("Oven returns its state: " +
											this.currentState + ".\n");
		}
		return 	this.currentState == OvenState.ON ||
				this.currentState == OvenState.HEATING ||
				this.currentState == OvenState.WAITING;
	}

	@Override
	public void switchOn() throws Exception {
		if (Oven.VERBOSE) {
			this.traceMessage("Oven switches on.\n");
		}

		assert	!this.on() : new PreconditionException("!on()");

		this.currentState = OvenState.ON;

		assert	 this.on() : new PostconditionException("on()");
	}

	@Override
	public void switchOff() throws Exception {
		if (Oven.VERBOSE) {
			this.traceMessage("Oven switches off.\n");
		}

		assert	this.on() : new PreconditionException("on()");
			
		// TODO: eventuellement rajouter try/catch ici
		if (this.getState() != OvenState.ON)
			stopCooking();
			
		
		this.currentMode  = OvenMode.CUSTOM;
		this.currentState = OvenState.OFF;

		assert	 !this.on() : new PostconditionException("!on()");
		assert getMode() == OvenMode.CUSTOM :
			new PostconditionException("getMode() == OvenMode.CUSTOM");
		assert getState() == OvenState.OFF :
			new PostconditionException("getState() == OvenState.OFF");
	}	

	@Override
	public void setTargetTemperature(Measure<Double> target) throws Exception {
	    assert this.on() :
	        new PreconditionException("on()");
	    assert this.currentMode == OvenMode.CUSTOM :
	        new PreconditionException("currentMode == OvenMode.CUSTOM");
	    assert target != null &&
	           TEMPERATURE_UNIT.equals(target.getMeasurementUnit()) :
	        new PreconditionException("target != null && TEMPERATURE_UNIT.equals(target.getMeasurementUnit())");
	    assert target.getData() >= MIN_TARGET_TEMPERATURE.getData() &&
	           target.getData() <= MAX_TARGET_TEMPERATURE.getData() :
	        new PreconditionException("target outside of valid range");

	    if (Oven.VERBOSE) {
	        this.traceMessage("Oven sets new target temperature: " + target + ".\n");
	    }

	    this.targetTemperature = target;

	    assert getTargetTemperature().equals(target) :
	        new PostconditionException("getTargetTemperature().equals(target)");
	}

	@Override
	public Measure<Double> getTargetTemperature() throws Exception {
		if (Oven.VERBOSE) {
			this.traceMessage("Oven returns its target"
							+ " temperature " + this.targetTemperature + ".\n");
		}

		Measure<Double> ret = this.targetTemperature;

		assert	ret != null && TEMPERATURE_UNIT.equals(ret.getMeasurementUnit()) :
				new PostconditionException(
						"return != null && TEMPERATURE_UNIT.equals("
						+ "return.getMeasurementUnit())");
		assert	ret.getData() >= MIN_TARGET_TEMPERATURE.getData() &&
							ret.getData() <= MAX_TARGET_TEMPERATURE.getData() :
				new PostconditionException(
						"return.getData() >= MIN_TARGET_TEMPERATURE.getData() "
						+ "&& return.getData() <= MAX_TARGET_TEMPERATURE.getData()");

		return ret;
	}

	@Override
	public SignalData<Double> getCurrentTemperature() throws Exception {
		assert	this.on() : new PreconditionException("on()");

		SignalData<Double> currentTemperature = FAKE_CURRENT_TEMPERATURE;
		if (Oven.VERBOSE) {
			this.traceMessage("Oven returns the current"
							+ " temperature " + currentTemperature + ".\n");
		}

		return  currentTemperature;
	}
	
	@Override
	public void setMode(OvenMode mode) throws Exception {
		assert this.on() : new PreconditionException("on()");
	    assert mode != null : new PreconditionException("mode != null");
	    assert this.currentMode != mode :
	        new PreconditionException("currentMode != mode");

	    if (Oven.VERBOSE) {
	        this.traceMessage("Oven switching mode from " + this.currentMode +
	                          " to " + mode + ".\n");
	    }
	    
	    if (mode != OvenMode.CUSTOM) {
	        this.targetTemperature = MODE_TEMPERATURES.get(mode);
	    }
	    this.currentMode = mode;

	    assert this.currentMode == mode :
	        new PostconditionException("currentMode == mode");
	}
	
	@Override
	public void startCooking(double delayInSeconds) throws Exception {
	    assert this.on() :
	        new PreconditionException("on()");
	    assert this.getState() != OvenState.HEATING :
	        new PreconditionException("getState() != HEATING");
	    assert this.getState() != OvenState.WAITING :
	        new PreconditionException("getState() != WAITING");
	    assert delayInSeconds >= 0 :
	        new PreconditionException("delayInSeconds >= 0");

	    if (Oven.VERBOSE)
	        this.traceMessage("Oven starts cooking with delay " + delayInSeconds + "s.\n");

	    if (delayInSeconds == 0) {
	        this.startHeating();
	        
	        assert this.getState() == OvenState.HEATING :
	            new PostconditionException("getState() == HEATING");
	    } else {
	        this.currentState = OvenState.WAITING;

	        assert this.getState() == OvenState.WAITING :
	            new PostconditionException("getState() == WAITING");
	    }
	}

	@Override
	public void stopCooking() throws Exception {
	    assert this.getState() == OvenState.WAITING || this.getState() == OvenState.HEATING
	        : new PreconditionException("getState() == WAITING || getState() == HEATING");

	    if (Oven.VERBOSE) {
	        this.traceMessage("Oven cancels delayed or ongoing start.\n");
	    }

	    if (this.getState() == OvenState.WAITING) {
		    this.currentState = OvenState.ON;
	    } else if (this.getState() == OvenState.HEATING) {
	        this.stopHeating();
	    }

	    assert this.getState() == OvenState.ON
	        : new PostconditionException("getState() == ON");
	}


	@Override
	public Measure<Double> getMaxPowerLevel() throws Exception {
		if (Oven.VERBOSE) {
			this.traceMessage("Oven returns its max power level " + 
					MAX_POWER_LEVEL + ".\n");
		}

		return MAX_POWER_LEVEL;
	}

	@Override
	public void setCurrentPowerLevel(Measure<Double> powerLevel) throws Exception {
		if (Oven.VERBOSE) {
			this.traceMessage("Oven sets its power level to " + 
														powerLevel + ".\n");
		}

		assert	this.on() : new PreconditionException("on()");
		assert	powerLevel != null && powerLevel.getData() >= 0.0 &&
							powerLevel.getMeasurementUnit().equals(POWER_UNIT) :
				new PreconditionException(
						"powerLevel != null && powerLevel.getData() >= 0.0 && "
						+ "powerLevel.getMeasurementUnit().equals(POWER_UNIT)");

		if (powerLevel.getData() <= getMaxPowerLevel().getData()) {
			this.currentPowerLevel = new SignalData<>(powerLevel);
		} else {
			this.currentPowerLevel = new SignalData<>(MAX_POWER_LEVEL);
		}

		assert	powerLevel.getData() > getMaxPowerLevel().getData() ||
						getCurrentPowerLevel().getMeasure().getData() ==
														powerLevel.getData() :
				new PostconditionException(
						"powerLevel.getData() > getMaxPowerLevel().getData() "
						+ "|| getCurrentPowerLevel().getData() == "
						+ "powerLevel.getData()");
	}

	@Override
	public SignalData<Double> getCurrentPowerLevel() throws Exception {
		if (Oven.VERBOSE) {
			this.traceMessage("Oven returns its current power level " + 
					this.currentPowerLevel + ".\n");
		}

		assert	this.on() : new PreconditionException("on()");

		SignalData<Double> ret = this.currentPowerLevel;

		assert	ret != null && ret.getMeasure().getMeasurementUnit().
															equals(POWER_UNIT) :
				new PreconditionException(
						"return != null && return.getMeasure()."
						+ "getMeasurementUnit().equals(POWER_UNIT)");
		assert	ret.getMeasure().getData() >= 0.0 &&
					ret.getMeasure().getData() <= getMaxPowerLevel().getData() :
				new PostconditionException(
							"return.getMeasure().getData() >= 0.0 && "
							+ "return.getMeasure().getData() <= "
							+ "getMaxPowerLevel().getData()");

		return ret;
	}

	@Override
	public boolean heating() throws Exception {
		if (Oven.VERBOSE) {
			this.traceMessage("Oven returns its heating status " + 
						(this.currentState == OvenState.HEATING) + ".\n");
		}

		assert	this.on() : new PreconditionException("on()");

		return this.currentState == OvenState.HEATING;
	}

	@Override
	public void startHeating() throws Exception {
		if (Oven.VERBOSE) {
			this.traceMessage("Oven starts heating.\n");
		}
		assert	this.on() : new PreconditionException("on()");
		assert	!this.heating() : new PreconditionException("!heating()");

		this.currentState = OvenState.HEATING;

		assert	this.heating() : new PostconditionException("heating()");
	}

	@Override
	public void stopHeating() throws Exception {
		if (Oven.VERBOSE) {
			this.traceMessage("Oven stops heating.\n");
		}
		assert	this.on() : new PreconditionException("on()");
		assert	this.heating() : new PreconditionException("heating()");

		this.currentState = OvenState.ON;

		assert	!this.heating() : new PostconditionException("!heating()");
	}
	
	@Override
	public OvenState getState() throws Exception {
		if (Oven.VERBOSE)
			this.traceMessage("Oven returns its state: " + this.currentState + ".\n");
		return this.currentState;
	}

	@Override
	public OvenMode getMode() throws Exception {
		if (Oven.VERBOSE)
			this.traceMessage("Oven returns its mode: " + this.currentMode + ".\n");
		return this.currentMode;
	}
}