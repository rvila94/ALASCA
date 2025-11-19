package equipments.oven;

import fr.sorbonne_u.alasca.physical_data.Measure;
import fr.sorbonne_u.alasca.physical_data.MeasurementUnit;
import fr.sorbonne_u.alasca.physical_data.SignalData;
import fr.sorbonne_u.exceptions.AssertionChecking;

/**
 * The interface <code>OvenTemperatureI</code> declares the signatures of
 * the services accessing the current and target temperatures of the oven.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p>
 * This interface defines the expected temperature-related operations for
 * an oven: reading the current temperature and accessing the configured
 * target temperature. It also declares reasonable physical limits for
 * the appliance, as well as invariant conditions.
 * </p>
 *
 * <p><strong>Invariants</strong></p>
 * 
 * <pre>
 * invariant	{@code MIN_TARGET_TEMPERATURE != null && MIN_TARGET_TEMPERATURE.getMeasurementUnit().equals(TEMPERATURE_UNIT)}
 * invariant	{@code MAX_TARGET_TEMPERATURE != null && MAX_TARGET_TEMPERATURE.getMeasurementUnit().equals(TEMPERATURE_UNIT)}
 * </pre>
 * 
 * <p>Created on : 2025-10-10</p>
 * 
 * @author	<a href="mailto:rodrigo.vila@etu.sorbonne-universite.fr">Rodrigo Vila</a>
 * @author	<a href="mailto:damien.ribeiro@etu.sorbonne-universite.fr">Damien Ribeiro</a>
 */
public interface		OvenTemperatureI
{
	// -------------------------------------------------------------------------
	// Constants
	// -------------------------------------------------------------------------

	/** Temperature measurement unit for ovens (in Celsius). */
	public static final MeasurementUnit	TEMPERATURE_UNIT =
													MeasurementUnit.CELSIUS;

	/** Minimal target temperature for the oven in Celsius (0°C). */
	public static final Measure<Double>	MIN_TARGET_TEMPERATURE =
												new Measure<>(
														0.0,
														TEMPERATURE_UNIT);

	/** Maximal target temperature for the oven in Celsius (300°C). */
	public static final Measure<Double>	MAX_TARGET_TEMPERATURE =
												new Measure<>(
														300.0,
														TEMPERATURE_UNIT);

	// -------------------------------------------------------------------------
	// Invariants
	// -------------------------------------------------------------------------

	/**
	 * return true if the static invariants are observed, false otherwise.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @return	true if the invariants are observed, false otherwise.
	 */
	public static boolean	staticInvariants()
	{
		boolean ret = true;
		ret &= AssertionChecking.checkStaticInvariant(
				MIN_TARGET_TEMPERATURE != null &&
					MIN_TARGET_TEMPERATURE.getMeasurementUnit().equals(
															TEMPERATURE_UNIT),
				OvenTemperatureI.class,
				"MIN_TARGET_TEMPERATURE != null && MIN_TARGET_TEMPERATURE."
				+ "getMeasurementUnit().equals(TEMPERATURE_UNIT)");
		ret &= AssertionChecking.checkStaticInvariant(
				MAX_TARGET_TEMPERATURE != null &&
					MAX_TARGET_TEMPERATURE.getMeasurementUnit().equals(
															TEMPERATURE_UNIT),
				OvenTemperatureI.class,
				"MAX_TARGET_TEMPERATURE != null && MAX_TARGET_TEMPERATURE."
				+ "getMeasurementUnit().equals(TEMPERATURE_UNIT)");
		return ret;
	}

	// -------------------------------------------------------------------------
	// Signatures
	// -------------------------------------------------------------------------

	/**
	 * Get the current target temperature of the oven.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code return != null && OvenTemperatureI.TEMPERATURE_UNIT.equals(return.getMeasurementUnit())}
	 * post	{@code return.getData() >= OvenTemperatureI.MIN_TARGET_TEMPERATURE.getData() && return.getData() <= OvenTemperatureI.MAX_TARGET_TEMPERATURE.getData()}
	 * </pre>
	 *
	 * @return				the current target temperature.
	 * @throws Exception	if an error occurs during access.
	 */
	public Measure<Double>	getTargetTemperature() throws Exception;

	/**
	 * Return the current temperature measured by the oven's internal sensor.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code on()}
	 * post	{@code return != null}
	 * </pre>
	 *
	 * @return				the current temperature measured by the thermostat.
	 * @throws Exception	if an error occurs during measurement.
	 */
	public SignalData<Double>	getCurrentTemperature() throws Exception;
}
