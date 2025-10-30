package equipments.oven;

/**
 * The class <code>OvenUserJava4CI</code> extends the component interface
 * {@code OvenUserCI} with signatures that can be used in Java 4.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p><strong>Invariants</strong></p>
 * 
 * <pre>
 * invariant	{@code true}	// no more invariant
 * </pre>
 * 
 * <p>Created on : 2025-10-18</p>
 * 
 * @author	<a href="mailto:Rodrigo.Vila@etu.sorbonne-universite.fr">Rodrigo Vila</a>
 * @author	<a href="mailto:Damien.Ribeiro@etu.sorbonne-universite.fr">Damien Ribeiro</a>
 */
public interface		OvenUserJava4CI
extends		OvenUserCI
{
	/**
	 * set the target temperature by calling the synonymous method
	 * {@code setTargetTemperature(Measure<Double>)}
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code target >= MIN_TARGET_TEMPERATURE.getData() && target <= MAX_TARGET_TEMPERATURE.getData()}
	 * post	{@code getTargetTemperatureJava4().equals(target)}
	 * </pre>
	 *
	 * @param target		target temperature in celsius.
	 * @throws Exception	<i>to do</i>.
	 */
	public void			setTargetTemperatureJava4(double target) throws Exception;

	/**
	 * get the maximum power level by calling the synonymous method
	 * {@code Measure<Double>	getMaxPowerLevel()}.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code return > 0.0}
	 * </pre>
	 *
	 * @return				the maximum power level.
	 * @throws Exception	<i>to do</i>.
	 */
	public double		getMaxPowerLevelJava4() throws Exception;

	/**
	 * set the current power level by calling the synonymous method
	 * {@code setCurrentPowerLevel(Measure<Double> powerLevel)};
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code powerLevel >= 0.0}
	 * post	{@code powerLevel > getMaxPowerLevelJava4() || getCurrentPowerLevelJava4() == powerLevel}
	 * post	{@code powerLevel <= getMaxPowerLevelJava4() || getCurrentPowerLevelJava4() == Oven.MAX_POWER_LEVEL.getData()}
	 * </pre>
	 *
	 * @param powerLevel	new power level in watts.
	 * @throws Exception	<i>to do</i>.
	 */
	public void			setCurrentPowerLevelJava4(double powerLevel)
	throws Exception;

	/**
	 * get the current power level by calling the synonymous method
	 * {@code SignalData<Double> getCurrentPowerLevel()}.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code return >= 0.0 && return <= getMaxPowerLevelJava4()}
	 * </pre>
	 *
	 * @return				the current power level in watts.
	 * @throws Exception	<i>to do</i>.
	 */
	public double		getCurrentPowerLevelJava4() throws Exception;

	/**
	 * get the target temperature by calling the synonymous method
	 * {@code Measure<Double> getTargetTemperature()}.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code return >= Oven.MIN_TARGET_TEMPERATURE.getData() && return <= Oven.MAX_TARGET_TEMPERATURE.getData()}
	 * </pre>
	 *
	 * @return				the target temperature in celsius.
	 * @throws Exception	<i>to do</i>.
	 */
	public double		getTargetTemperatureJava4() throws Exception ;

	/**
	 * get the current temperature by calling the synonymous method
	 * {@code SignalData<Double> getCurrentTemperature()}.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @return				the current temperature in celsius.
	 * @throws Exception	<i>to do</i>.
	 */
	public double		getCurrentTemperatureJava4() throws Exception;
}
// -----------------------------------------------------------------------------
