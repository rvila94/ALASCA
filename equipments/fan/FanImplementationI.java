package equipments.fan;

import fr.sorbonne_u.alasca.physical_data.MeasurementUnit;
import fr.sorbonne_u.components.hem2025e1.equipments.hairdryer.HairDryer;
import fr.sorbonne_u.exceptions.AssertionChecking;

/**
 * The interface <code>FanImplementationI</code> defines the signatures
 * of services service implemented by the fan component.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p><strong>Invariants</strong></p>
 * 
 * <pre>
 * invariant	{@code true}	// no invariant
 * </pre>
 * 
 * <p>Created on : 2025-10-04</p>
 * 
 * @author	<a href="mailto:Rodrigo.Vila@etu.sorbonne-universite.fr">Rodrigo Vila</a>
 * @author	<a href="mailto:Damien.Ribeiro@etu.sorbonne-universite.fr">Damien Ribeiro</a>
 */
public interface FanImplementationI {

	// -------------------------------------------------------------------------
	// Inner interfaces and types
	// -------------------------------------------------------------------------

	/**
	 * The enumeration <code>FanState</code> describes the operation
	 * states of the fan.
	 *
	 * <p><strong>Description</strong></p>
	 * 
	 * <p>Created on : 2025-10-04</p>
	 * 
	 * @author	<a href="mailto:Rodrigo.Vila@etu.sorbonne-universite.fr">Rodrigo Vila</a>
	 * @author	<a href="mailto:Damien.Ribeiro@etu.sorbonne-universite.fr">Damien Ribeiro</a>
	 */
	public static enum	FanState
	{
		/** fan is on.												*/
		ON,
		/** fan is off.												*/
		OFF
	}
	
	/**
	 * The enumeration <code>FanMode</code> describes the operation
	 * modes of the fan.
	 *
	 * <p><strong>Description</strong></p>
	 * 
	 * <p>
	 * The fan can be either in <code>LOW</code> mode (low speed) or
	 * in <code>MEDIUM</code> mode (medium speed) or
	 * in <code>HIGH</code> mode (high speed).
	 * </p>
	 * 
	 * <p>Created on : 2025-10-04</p>
	 * 
	 * @author	<a href="mailto:Rodrigo.Vila@etu.sorbonne-universite.fr">Rodrigo Vila</a>
	 * @author	<a href="mailto:Damien.Ribeiro@etu.sorbonne-universite.fr">Damien Ribeiro</a>
	 */
	public static enum	FanMode
	{
		/** Low speed mode. */
        LOW,
        /** Medium speed mode. */
        MEDIUM,
        /** High speed mode. */
        HIGH
	}
	
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	/** measurement unit for power used in this appliance.					*/
	public static final MeasurementUnit	POWER_UNIT = MeasurementUnit.WATTS;
	/** measurement unit for tension used in this appliance.				*/
	public static final MeasurementUnit	TENSION_UNIT = MeasurementUnit.VOLTS;

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
	 * @return	true if the static invariants are observed, false otherwise.
	 */
	public static boolean	staticInvariants()
	{
		boolean ret = true;
		ret &= AssertionChecking.checkStaticInvariant(
				POWER_UNIT != null && TENSION_UNIT != null,
				HairDryer.class,
				"POWER_UNIT != null && TENSION_UNIT != null");
		return ret;
	}
	
	// -------------------------------------------------------------------------
	// Component services signatures
	// -------------------------------------------------------------------------

	/**
	 * return the current state of the fan.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @return				the current state of the fan.
	 * @throws Exception 	<i>to do</i>.
	 */
	public FanState	getState() throws Exception;

	/**
	 * return the current operation mode of the fan.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @return				the current state of the fan.
	 * @throws Exception 	<i>to do</i>.
	 */
	public FanMode	getMode() throws Exception;

	/**
	 * turn on the fan, put in the low speed mode.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code getState() == FanState.OFF}
	 * post	{@code getMode() == FanMode.LOW}
	 * post	{@code getState() == FanState.ON}
	 * </pre>
	 *
	 * @throws Exception	<i>to do</i>.
	 */
	public void			turnOn() throws Exception;

	/**
	 * turn off the fan.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code getState() == FanState.OFF}
	 * </pre>
	 *
	 * @throws Exception	<i>to do</i>.
	 */
	public void			turnOff() throws Exception;

	/**
	 * set the fan in high mode.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code getState() == FanState.ON}
	 * pre	{@code getMode() != FanMode.HIGH}
	 * post	{@code getMode() == FanMode.HIGH}
	 * </pre>
	 *
	 * @throws Exception	<i>to do</i>.
	 */
	public void			setHigh() throws Exception;
	
	/**
	 * set the fan in medium mode.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code getState() == FanState.ON}
	 * pre	{@code getMode() != FanMode.MEDIUM}
	 * post	{@code getMode() == FanMode.MEDIUM}
	 * </pre>
	 *
	 * @throws Exception	<i>to do</i>.
	 */
	public void			setMedium() throws Exception;

	/**
	 * set the fan in low mode.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code getState() == FanState.ON}
	 * pre	{@code getMode() != FanMode.LOW}
	 * post	{@code getMode() == FanMode.LOW}
	 * </pre>
	 *
	 * @throws Exception	<i>to do</i>.
	 */
	public void			setLow() throws Exception;
}

