package equipments.hem;


import fr.sorbonne_u.components.connectors.AbstractConnector;
import fr.sorbonne_u.components.hem2025.bases.AdjustableCI;
import equipments.oven.OvenExternalControlJava4CI;
import fr.sorbonne_u.exceptions.PostconditionException;
import fr.sorbonne_u.exceptions.PreconditionException;

/**
 * The class <code>OvenConnector</code> *manually* implements a connector
 * bridging the gap between the given generic component interface
 * {@code AdjustableCI} and the actual component interface offered by the
 * oven component.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p>
 * The code given here illustrates how a connector can be used to implement
 * a required interface given some offered interface that is different.
 * The objective is to be able to automatically generate such a connector
 * at run-time from an XML descriptor of the required adjustments.
 * </p>
 * 
 * <p><strong>Implementation Invariants</strong></p>
 * 
 * <pre>
 * invariant	{@code currentMode >= 0 && currentMode <= MAX_MODE}
 * </pre>
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
public class			OvenConnector
extends		AbstractConnector
implements	AdjustableCI
{
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------
	
	public static final int		MAX_MODE = 3;
	
	public static final double DEFROST_POWER_LEVEL = 500.0;
	
	public static final double	MIN_ADMISSIBLE_TEMP = 20.0;
	public static final double	MAX_ADMISSIBLE_DELTA = 50.0;

	/** the current mode of the oven.										*/
	protected int		currentMode;
	/** true if the oven has been suspended, false otherwise.				*/
	protected boolean	isSuspended;

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * create an instance of connector.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code !suspended}
	 * post	{@code currentMode() == MAX_MODE}
	 * </pre>
	 *
	 */
	public				OvenConnector()
	{
		super();
		this.currentMode = 2; // CUSTOM
		this.isSuspended = false;
	}

	// -------------------------------------------------------------------------
	// Internal methods
	// -------------------------------------------------------------------------

	/**
	 * compute and return the power level associated with the {@code mode}.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code mode > 0 && newMode <= MAX_MODE}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param mode			a mode for the Oven.
	 * @return				the power consumption for {@code newMode} in amperes.
	 * @throws Exception	<i>to do</i>.
	 */
	protected double	computePowerLevel(int mode) throws Exception
	{
		assert	mode > 0 && mode <= MAX_MODE :
				new PreconditionException("mode > 0 && mode <= MAX_MODE");
		
		double power;
		if (mode == 1) { // DEFROST
	        power = DEFROST_POWER_LEVEL;

	    } else {
	        power = ((OvenExternalControlJava4CI) this.offering)
	                    .getMaxPowerLevelJava4();
	    }

        return power;
	}

	/**
	 * set the Oven at this power level.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code newPowerLevel >= 0.0}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param newPowerLevel	a new power level to be set on the Oven.
	 * @throws Exception	<i>to do</i>.
	 */
	protected void		setNewPowerLevel(double newPowerLevel) throws Exception
	{
		assert	newPowerLevel >= 0.0 :
				new PreconditionException("newPowerLevel >= 0.0");
		
		double maxPowerLevel =
				((OvenExternalControlJava4CI)this.offering).
													getMaxPowerLevelJava4();
		
		if (newPowerLevel > maxPowerLevel) {
			newPowerLevel = maxPowerLevel;
		}
		((OvenExternalControlJava4CI)this.offering).
									setCurrentPowerLevelJava4(newPowerLevel);
	}

	/**
	 * compute and set the power level associated with the {@code newMode}.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code newMode >= 0 && newMode <= MAX_MODE}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param newMode		a new mode for the Oven.
	 * @throws Exception	<i>to do</i>.
	 */
	protected void		computeAndSetNewPowerLevel(int newMode) throws Exception
	{
		double newPowerLevel = this.computePowerLevel(newMode);
		this.setNewPowerLevel(newPowerLevel);
	}

	// -------------------------------------------------------------------------
	// Methods
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.components.hem2025.bases.AdjustableCI#maxMode()
	 */
	@Override
	public int			maxMode() throws Exception
	{
		return MAX_MODE;
	}

	/**
	 * @see fr.sorbonne_u.components.hem2025.bases.AdjustableCI#upMode()
	 */
	@Override
	public boolean		upMode() throws Exception
	{
		assert	!this.suspended() : new PreconditionException("!suspended()");
		assert	this.currentMode() < MAX_MODE :
				new PreconditionException("currentMode() < MAX_MODE");

		try {
			this.computeAndSetNewPowerLevel(this.currentMode + 1);
			this.currentMode++;
		} catch(Exception e) {
			return false;
		}
		return true;
	}

	/**
	 * @see fr.sorbonne_u.components.hem2025.bases.AdjustableCI#downMode()
	 */
	@Override
	public boolean		downMode() throws Exception
	{
		assert	!this.suspended() : new PreconditionException("!suspended()");
		assert	this.currentMode() > 0 :
				new PreconditionException("currentMode() > 0");

		try {
			this.computeAndSetNewPowerLevel(this.currentMode - 1);
			this.currentMode--;
		} catch(Exception e) {
			return false;
		}
		return true;
	}

	/**
	 * @see fr.sorbonne_u.components.hem2025.bases.AdjustableCI#setMode(int)
	 */
	@Override
	public boolean		setMode(int modeIndex) throws Exception
	{
		assert	!this.suspended() : new PreconditionException("!suspended()");
		assert	modeIndex > 0 && modeIndex <= this.maxMode() :
				new PreconditionException(
						"modeIndex > 0 && modeIndex <= maxMode()");

		try {
			this.computeAndSetNewPowerLevel(modeIndex);
			this.currentMode = modeIndex;
		} catch(Exception e) {
			return false;
		}
		return true;
	}

	/**
	 * @see fr.sorbonne_u.components.hem2025.bases.AdjustableCI#currentMode()
	 */
	@Override
	public int			currentMode() throws Exception
	{
		assert	!suspended() : new PreconditionException("!suspended()");

		return this.currentMode;
	}

	/**
	 * @see fr.sorbonne_u.components.hem2025.bases.AdjustableCI#getModeConsumption(int)
	 */
	@Override
	public double		getModeConsumption(int modeIndex) throws Exception
	{
		assert	modeIndex > 0 && modeIndex <= this.maxMode() :
				new PreconditionException(
						"modeIndex > 0 && modeIndex <= maxMode()");

		return this.computePowerLevel(modeIndex);
	}

	/**
	 * @see fr.sorbonne_u.components.hem2025.bases.AdjustableCI#suspended()
	 */
	@Override
	public boolean		suspended() throws Exception
	{
		return this.isSuspended;
	}

	/**
	 * @see fr.sorbonne_u.components.hem2025.bases.AdjustableCI#suspend()
	 */
	@Override
	public boolean		suspend() throws Exception
	{
		assert	!this.suspended() : new PreconditionException("!suspended()");

		try {
			((OvenExternalControlJava4CI)this.offering).
												setCurrentPowerLevelJava4(0.0);
			this.isSuspended = true;
		} catch(Exception e) {
			return false;
		}
		return true;
	}

	/**
	 * @see fr.sorbonne_u.components.hem2025.bases.AdjustableCI#resume()
	 */
	@Override
	public boolean		resume() throws Exception
	{
		assert	this.suspended() : new PreconditionException("suspended()");

		try {
			this.computeAndSetNewPowerLevel(this.currentMode);
			this.isSuspended = false;
		} catch(Exception e) {
			return false;
		}
		return true;
	}

	/**
	 * @see fr.sorbonne_u.components.hem2025.bases.AdjustableCI#emergency()
	 */
	@Override
	public double		emergency() throws Exception
	{
		assert	this.suspended() : new PreconditionException("suspended()");

		double currentTemperature =
					((OvenExternalControlJava4CI)this.offering).
												getCurrentTemperatureJava4();
		double targetTemperature =
					((OvenExternalControlJava4CI)this.offering).
												getTargetTemperatureJava4();
		double delta = Math.abs(targetTemperature - currentTemperature);
		double ret = -1.0;
		if (currentTemperature < OvenConnector.MIN_ADMISSIBLE_TEMP ||
							delta >= OvenConnector.MAX_ADMISSIBLE_DELTA) {
			ret = 1.0;
		} else {
			ret = delta/OvenConnector.MAX_ADMISSIBLE_DELTA;
		}

		assert	ret >= 0.0 && ret <= 1.0 :
				new PostconditionException("return >= 0.0 && return <= 1.0");

		return ret;
	}
}
