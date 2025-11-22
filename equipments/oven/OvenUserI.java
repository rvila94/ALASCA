package equipments.oven;

import fr.sorbonne_u.alasca.physical_data.Measure;

/**
 * The interface <code>OvenUserI</code> declares the signatures of the oven
 * component services corresponding to the actions a user can perform on
 * the oven.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p>
 * This interface defines the methods that a user or a local controller can
 * invoke directly on the oven, such as turning it on or off, setting the
 * desired temperature, or selecting a mode (custom, grill, defrost).
 * It extends {@link OvenExternalControlI}, giving access to power and
 * temperature control while enforcing user-level interaction semantics.
 * </p>
 * 
 * <p><strong>Invariants</strong></p>
 * 
 * <pre>
 * invariant	{@code true}	// no more invariant.
 * </pre>
 * 
 * <p>Created on : 2025-10-10</p>
 * 
 * @author	<a href="mailto:rodrigo.vila@etu.sorbonne-universite.fr">Rodrigo Vila</a>
 * @author	<a href="mailto:damien.ribeiro@etu.sorbonne-universite.fr">Damien Ribeiro</a>
 */
public interface		OvenUserI
extends		OvenExternalControlI
{
	// -------------------------------------------------------------------------
	// User actions
	// -------------------------------------------------------------------------

	/**
	 * return true if the oven is currently turned on.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @return				true if the oven is on.
	 * @throws Exception	if an error occurs while checking the state.
	 */
	public boolean		on() throws Exception;

	/**
	 * switch on the oven.
	 * 
	 * <p><strong>Description</strong></p>
	 * 
	 * Turns the oven on and sets it to its default mode (CUSTOM) with
	 * a default temperature (e.g. 0°C).
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code !on()}
	 * post	{@code on()}
	 * </pre>
	 *
	 * @throws Exception	if an error occurs while switching on.
	 */
	public void			switchOn() throws Exception;

	/**
	 * switch off the oven.
	 * 
	 * <p><strong>Description</strong></p>
	 * 
	 * Stops any active or programmed cooking cycle, resets mode back to CUSTOM and switches the oven off.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code on()}
	 * post {@code getMode() == OvenMode.CUSTOM}
	 * post	{@code !on()}
	 * </pre>
	 *
	 * @throws Exception	if an error occurs while switching off.
	 */
	public void			switchOff() throws Exception;

	/**
	 * Set a new target temperature for the oven (only in {@code CUSTOM} mode).
	 * 
	 * <p><strong>Description</strong></p>
	 * 
	 * Allows the user to define a target temperature that the oven will
	 * maintain during operation. This is only permitted in {@code CUSTOM}
	 * mode. In other modes (e.g., {@code GRILL}, {@code DEFROST}), the
	 * temperature is fixed and cannot be modified manually.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre  {@code on()}
	 * pre  {@code getMode() == OvenMode.CUSTOM}
	 * pre  {@code target != null && TEMPERATURE_UNIT.equals(target.getMeasurementUnit())}
	 * pre  {@code target.getData() >= MIN_TARGET_TEMPERATURE.getData() && target.getData() <= MAX_TARGET_TEMPERATURE.getData()}
	 * post {@code getTargetTemperature().equals(target)}
	 * </pre>
	 * 
	 * @param target the new target temperature to be set.
	 * @throws Exception if preconditions are violated or an error occurs.
	 */
	public void			setTargetTemperature(Measure<Double> target
	) throws Exception;

	/**
	 * Set the current oven mode.
	 * 
	 * <p><strong>Description</strong></p>
	 * 
	 * Switches the oven to the specified mode. If the mode is {@code GRILL} or
	 * {@code DEFROST}, the target temperature is automatically adjusted to
	 * predefined values. If the mode is {@code CUSTOM}, the target temperature
	 * remains unchanged until explicitly set by the user.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre  {@code on()}
	 * pre  {@code mode != null}
	 * pre  {@code getMode() != mode}
	 * post {@code getMode().equals(mode)}
	 * </pre>
	 *
	 * @param mode the new oven mode.
	 * @throws Exception if an error occurs or if preconditions are violated.
	 */
	public void setMode(Oven.OvenMode mode) throws Exception;
	
	/**
	 * Start the oven cooking cycle, immediately or after a specified delay.
	 * 
	 * <p><strong>Description</strong></p>
	 * 
	 * If {@code delayInSeconds == 0}, the oven starts heating immediately.
	 * Otherwise, it transitions to the {@code WAITING} state and will start
	 * heating after the given delay.
	 *
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre  {@code on()}
	 * pre  {@code getCurrentState() != OvenState.HEATING}
	 * pre  {@code getCurrentState() != OvenState.WAITING}
	 * pre  {@code delayInSeconds >= 0}
	 * post {@code delayInSeconds == 0 ⇒ getCurrentState() == OvenState.HEATING}
	 * post {@code delayInSeconds > 0 ⇒ getCurrentState() == OvenState.WAITING}
	 * </pre>
	 *
	 * @param delayInSeconds  delay before cooking starts (0 for immediate start).
	 * @throws Exception      if preconditions are violated or an error occurs.
	 */
	public void startCooking(double delayInSeconds) throws Exception;

	/**
	 * Stops a previously scheduled or active cooking
	 *
	 * <p><strong>Description</strong></p>
	 * 
	 * This method is used to cancel a delayed or ongoing cooking cycle initiated by the user.
	 * <ul>
	 *   <li>If the oven is currently in the {@code WAITING} state (a delayed start is pending),
	 *       the scheduled heating is cancelled, and the oven returns to the {@code ON} state.</li>
	 *   <li>If the oven has already started heating (state {@code HEATING}), the heating process
	 *       is stopped, and the oven also returns to the {@code ON} state.</li>
	 * </ul>
	 * 
	 * In both cases, this method ensures that any active timer or scheduled task is properly cancelled.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre  {@code getCurrentState() == OvenState.WAITING || getCurrentState() == OvenState.HEATING}
	 * post {@code getCurrentState() == OvenState.ON}
	 * </pre>
	 *
	 * @throws Exception if an error occurs while cancelling the delayed or active heating cycle.
	 */
	public void stopCooking() throws Exception;

	
	/**
	 * Return the state of the oven.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre  {@code true}
	 * post {@code return != null}
	 * </pre>
	 * 
	 * @return the current {@code OvenState} of the oven.
	 * @throws Exception if an error occurs.
	 */
	public Oven.OvenState getState() throws Exception;
	
	/**
	 * Return the mode of the oven.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre  {@code true}
	 * post {@code return != null}
	 * </pre>
	 * 
	 * @return the current {@code OvenMode} of the oven.
	 * @throws Exception if an error occurs.
	 */
	public Oven.OvenMode getMode() throws Exception;

}
