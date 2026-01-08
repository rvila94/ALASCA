package equipments.fan.simulations;

import equipments.fan.FanImplementationI.FanMode;
import equipments.fan.FanImplementationI.FanState;

/**
 * The interface <code>FanOperationI</code> declares operations that
 * simulation models must implement to have events associated with the models
 * execute on them.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p>Created on : 2026-01-03</p>
 * 
 * @author	<a href="mailto:Rodrigo.Vila@etu.sorbonne-universite.fr">Rodrigo Vila</a>
 * @author	<a href="mailto:Damien.Ribeiro@etu.sorbonne-universite.fr">Damien Ribeiro</a>
*/
public interface		FanSimulationOperationI
{
	/**
	 * turn on the fan
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 */
	public void			turnOn();

	/**
	 * turn off the fan.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 */
	public void			turnOff();

	/**
	 * set the fan in high mode.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 */
	public void			setHigh();
	
	/**
	 * set the fan in medium mode.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 */
	public void			setMedium();

	/**
	 * set the fan in low mode.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 */
	public void			setLow();
	
	/**
    * gets the current state of the fan in the simulator
    *
    * <p><strong>Contract</strong></p>
    *
    * <pre>
    *  pre {@code true}        // no precondition
    *  post {@code true}       // no postcondition
    * </pre>
    * @return FanState    the current state of the fan
    */
   public FanState getState();
   
   /**
   * gets the current mode of the fan in the simulator
   *
   * <p><strong>Contract</strong></p>
   *
   * <pre>
   *  pre {@code true}        // no precondition
   *  post {@code true}       // no postcondition
   * </pre>
   * @return FanState    the current mode of the fan
   */
  public FanMode getMode();
}
