package equipments.oven;


/**
 * The interface <code>OvenImplementationI</code> defines the services that must
 * be implemented by a class representing an oven component.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p>
 * This interface defines the internal behavior of a programmable oven, 
 * including its operational states, mode settings, temperature control,
 * and optional delayed start feature.
 * </p>
 * 
 * <p><strong>Oven States:</strong></p>
 * <ul>
 *   <li><b>OFF</b> – the oven is powered off.</li>
 *   <li><b>ON</b> – the oven is on and ready to operate.</li>
 *   <li><b>PROGRAMMED</b> – a delayed program is scheduled but not yet started.</li>
 *   <li><b>COOKING</b> – the oven is actively heating.</li>
 * </ul>
 * 
 * <p><strong>Oven Modes:</strong></p>
 * <ul>
 *   <li><b>DEFROST</b> - temperature = 80°C</li>
 *   <li><b>GRILL</b> - temperature = 220°C</li>
 *   <li><b>CUSTOM</b> – user-defined temperature</li>
 * </ul>
 * 
 * <p><strong>Functional overview:</strong></p>
 * <ul>
 *   <li>Turn on/off the oven</li>
 *   <li>Switch between modes or set a custom temperature</li>
 *   <li>Program a delayed start with duration</li>
 *   <li>Stop a running or scheduled program</li>
 * </ul>
 * 
 * <p><strong>Implementation Invariants</strong></p>
 * 
 * <pre>
 * invariant getTemperature() >= 50 && getTemperature() <= 300
 * invariant getState() != null
 * invariant getMode() != null
 * </pre>
 * 
 * <p>Created on : 2025-10-08</p>
 * 
 * @author	<a href="mailto:Rodrigo.Vila@etu.sorbonne-universite.fr">Rodrigo Vila</a>
 * @author	<a href="mailto:Damien.Ribeiro@etu.sorbonne-universite.fr">Damien Ribeiro</a>
 */
public interface OvenImplementationI {

	// -------------------------------------------------------------------------
	// Inner interfaces and types
	// -------------------------------------------------------------------------

	/**
	 * The enumeration <code>OvenState</code> describes the operation
	 * states of the oven.
	 *
	 * <p><strong>Description</strong></p>
	 * 
	 * <p>Created on : 2025-10-08</p>
	 * 
	 * @author	<a href="mailto:Rodrigo.Vila@etu.sorbonne-universite.fr">Rodrigo Vila</a>
	 * @author	<a href="mailto:Damien.Ribeiro@etu.sorbonne-universite.fr">Damien Ribeiro</a>
	 */
	public static enum OvenState {
		/** Oven is on. */
		ON,
		/** Oven is off. */
		OFF,
		/** Oven is programmed to have a delayed start. */
		PROGRAMMED,
		/** Oven is cooking. */
		COOKING
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
	 * <p>Created on : 2025-10-08</p>
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
	// Component services signatures
	// -------------------------------------------------------------------------

	/**
	 * Return the current operational state of the oven.
	 *
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 * 
	 * @return the current {@code OvenState}.
	 * @throws Exception <i>to do</i>.
	 */
	public OvenState getState() throws Exception;

	/**
	 * Return the current mode of the oven.
	 *
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 * 
	 * @return the current {@code OvenMode}.
	 * @throws Exception <i>to do</i>.
	 */
	public OvenMode getMode() throws Exception;

	/**
	 * Return the current target temperature of the oven.
	 *
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 * 
	 * @return the target temperature in Celsius degrees.
	 * @throws Exception <i>to do</i>.
	 */
	public int getTemperature() throws Exception;
	
	/**
	 * Return true if the oven is currently cooking.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 * 
	 * @return true if {@code getState() == OvenState.COOKING}.
	 * @throws Exception <i>to do</i>.
	 */
	public boolean isCooking() throws Exception;
	
	/**
	 * Turn the oven on.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre  {@code getState() == OvenState.OFF}
	 * post {@code getState() == OvenState.ON}
	 * </pre>
	 *
	 * @throws Exception <i>to do</i>.
	 */
	public void turnOn() throws Exception;

	/**
	 * Stop any cooking or programmed task and turn the oven off
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre  {@code getState() != OvenState.OFF}
	 * post {@code getState() == OvenState.OFF}
	 * </pre>
	 *
	 * @throws Exception <i>to do</i>.
	 */
	public void turnOff() throws Exception;

	/**
	 * Set the oven mode to DEFROST (= 80°C).
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre  {@code getState() == OvenState.ON || getState() == OvenState.PROGRAMMED}
	 * pre  (@code getMode() != OvenMode.DEFROST)
	 * post {@code getMode() == OvenMode.DEFROST && getTemperature() == 80}
	 * </pre>
	 *
	 * @throws Exception <i>to do</i>.
	 */
	public void setDefrost() throws Exception;

	/**
	 * Set the oven mode to GRILL (= 220°C).
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre  {@code getState() == OvenState.ON || getState() == OvenState.PROGRAMMED}
	 * pre  (@code getMode() != OvenMode.GRILL)
	 * post {@code getMode() == OvenMode.GRILL && getTemperature() == 220}
	 * </pre>
	 *
	 * @throws Exception <i>to do</i>.
	 */
	public void setGrill() throws Exception;

	/**
	 * Set a custom temperature for manual operation.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre  {@code getState() == OvenState.ON}
	 * pre  {@code 50 <= temperature && temperature <= 300}
	 * pre  (@code getMode() == OvenMode.CUSTOM && getTemperature() == temperature)
	 * post {@code getMode() == OvenMode.CUSTOM && getTemperature() == temperature}
	 * </pre>
	 *
	 * @param temperature desired temperature in Celsius degrees.
	 * @throws Exception <i>to do</i>.
	 */
	public void setTemperature(int temperature) throws Exception;

	/**
	 * Start immediate cooking for the given duration.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre  {@code getState() == OvenState.ON}
	 * pre  {@code durationInSeconds > 0}
	 * post {@code getState() == OvenState.COOKING}
	 * </pre>
	 *
	 * @param durationInSeconds	duration of the cooking phase (in seconds)
	 * @throws Exception	<i>to do</i>.
	 */
	public void startCooking(int durationInSeconds) throws Exception;
	
	/**
	 * Program a delayed start with a given duration.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre  {@code getState() == OvenState.ON}
	 * pre  {@code delayInSeconds > 0 && durationInSeconds > 0}
	 * post {@code getState() == OvenState.PROGRAMMED}
	 * </pre>
	 *
	 * @param delayInSeconds delay before starting (in seconds).
	 * @param durationInSeconds duration of the cooking phase (in seconds).
	 * @throws Exception <i>to do</i>.
	 */
	public void programCooking(int delayInSeconds, int durationInSeconds) throws Exception;

	/**
	 * Stops any ongoing or programmed cooking cycle.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre  {@code getState() == OvenState.PROGRAMMED || getState() == OvenState.COOKING}
	 * post {@code getState() == OvenState.ON}
	 * </pre>
	 *
	 * @throws Exception <i>to do</i>.
	 */
	public void stopProgram() throws Exception;
	
	
}
