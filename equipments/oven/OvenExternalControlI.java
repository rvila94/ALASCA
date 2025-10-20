package equipments.oven;

import fr.sorbonne_u.alasca.physical_data.Measure;
import fr.sorbonne_u.alasca.physical_data.MeasurementUnit;
import fr.sorbonne_u.alasca.physical_data.SignalData;

/**
 * The interface <code>OvenExternalControlI</code> declares the
 * signatures of the services accessible to external controllers for the oven.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p>
 * This interface defines the services that external controllers (e.g. an
 * energy manager or a smart home system) can use to interact with the oven.
 * It provides methods to get and set the power level, as well as to access
 * the current and target temperature.
 * </p>
 * 
 * <p>
 * The oven operates at a fixed voltage and has a defined maximum power level.
 * It can be modulated by the energy manager to reduce or increase its power
 * consumption according to global energy optimization strategies.
 * </p>
 *
 * <p><strong>Invariants</strong></p>
 * 
 * <pre>
 * invariant	{@code POWER_UNIT != null}
 * invariant	{@code TENSION_UNIT != null}
 * invariant	{@code MAX_POWER_LEVEL != null && MAX_POWER_LEVEL.getMeasurementUnit().equals(POWER_UNIT) && MAX_POWER_LEVEL.getData() > 0.0}
 * invariant	{@code VOLTAGE != null && VOLTAGE.getMeasurementUnit().equals(TENSION_UNIT) && VOLTAGE.getData() == 220.0}
 * invariant	{@code getCurrentPowerLevel().getData() <= getMaxPowerLevel().getData()}
 * </pre>
 * 
 * <p>Created on : 2025-10-10</p>
 * 
 * @author
 * 		<a href="mailto:rodrigo.vila@etu.sorbonne-universite.fr">Rodrigo Vila</a>
 * @author
 * 		<a href="mailto:damien.ribeiro@etu.sorbonne-universite.fr">Damien Ribeiro</a>
 */
public interface		OvenExternalControlI
extends		OvenTemperatureI
{
	// -------------------------------------------------------------------------
	// Constants
	// -------------------------------------------------------------------------

	/** measurement unit for power used by the oven.						*/
	public static final MeasurementUnit POWER_UNIT = MeasurementUnit.WATTS;

	/** measurement unit for tension used by the oven.						*/
	public static final MeasurementUnit TENSION_UNIT = MeasurementUnit.VOLTS;

	/** maximum power level of the oven in watts.							*/
	public static final Measure<Double> MAX_POWER_LEVEL =
											new Measure<>(2500.0, POWER_UNIT);

	/** operating voltage of the oven in volts.								*/
	public static final Measure<Double> VOLTAGE =
											new Measure<>(220.0, TENSION_UNIT);

	// -------------------------------------------------------------------------
	// Invariants
	// -------------------------------------------------------------------------

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
	public static boolean	invariants(OvenExternalControlI o)
	{
		assert	o != null :
				new fr.sorbonne_u.exceptions.PreconditionException("o != null");

		boolean ret = true;
		ret &= fr.sorbonne_u.exceptions.AssertionChecking.checkInvariant(
				POWER_UNIT != null,
				OvenExternalControlI.class, o,
				"POWER_UNIT != null");
		ret &= fr.sorbonne_u.exceptions.AssertionChecking.checkInvariant(
				TENSION_UNIT != null,
				OvenExternalControlI.class, o,
				"TENSION_UNIT != null");
		ret &= fr.sorbonne_u.exceptions.AssertionChecking.checkInvariant(
				MAX_POWER_LEVEL != null &&
					MAX_POWER_LEVEL.getMeasurementUnit().equals(POWER_UNIT) &&
					MAX_POWER_LEVEL.getData() > 0.0,
				OvenExternalControlI.class, o,
				"MAX_POWER_LEVEL != null && MAX_POWER_LEVEL.getMeasurementUnit()."
				+ "equals(POWER_UNIT) && MAX_POWER_LEVEL.getData() > 0.0");
		ret &= fr.sorbonne_u.exceptions.AssertionChecking.checkInvariant(
				VOLTAGE != null &&
					VOLTAGE.getMeasurementUnit().equals(TENSION_UNIT) &&
					VOLTAGE.getData() == 220.0,
				OvenExternalControlI.class, o,
				"VOLTAGE != null && VOLTAGE.getMeasurementUnit().equals("
				+ "TENSION_UNIT) && VOLTAGE.getData() == 220.0");
		return ret;
	}

	// -------------------------------------------------------------------------
	// Signatures
	// -------------------------------------------------------------------------

	/**
	 * return the maximum power of the oven in the power unit used by the oven.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code return != null && return.getData() > 0.0 && return.getMeasurementUnit().equals(POWER_UNIT)}
	 * </pre>
	 *
	 * @return				the maximum power of the oven.
	 * @throws Exception	if an error occurs.
	 */
	public Measure<Double> getMaxPowerLevel() throws Exception;

	/**
	 * set the current power level of the oven; if the requested level exceeds
	 * the maximum power level, the power is set to the maximum.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code on()}
	 * pre	{@code powerLevel != null && powerLevel.getData() >= 0.0 && powerLevel.getMeasurementUnit().equals(POWER_UNIT)}
	 * post	{@code powerLevel.getData() > getMaxPowerLevel().getData() || getCurrentPowerLevel().getData() == powerLevel.getData()}
	 * post	{@code powerLevel.getData() <= getMaxPowerLevel().getData() || getCurrentPowerLevel().getData() == Oven.MAX_POWER_LEVEL.getData()}
	 * </pre>
	 *
	 * @param powerLevel	the new power level to set.
	 * @throws Exception	if an error occurs while changing the power level.
	 */
	public void	setCurrentPowerLevel(
		Measure<Double> powerLevel
	) throws Exception;

	/**
	 * return the current power level of the oven in watts.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code on()}
	 * post	{@code return != null && return.getMeasure().getMeasurementUnit().equals(POWER_UNIT)}
	 * post	{@code return.getMeasure().getData() >= 0.0 && return.getMeasure().getData() <= getMaxPowerLevel().getData()}
	 * </pre>
	 *
	 * @return				the current power level of the oven.
	 * @throws Exception	if an error occurs while reading the power level.
	 */
	public SignalData<Double> getCurrentPowerLevel() throws Exception;
}

