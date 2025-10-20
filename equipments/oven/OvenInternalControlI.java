package equipments.oven;

/**
 * The interface <code>OvenInternalControlI</code> defines the signatures of
 * the services offered by the oven to its internal temperature controller.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p>
 * The internal control interface provides methods that allow a temperature
 * regulator to manage the heating cycles of the oven.
 * It is intended for use by internal controllers rather than external users.
 * </p>
 *
 * <p>
 * This control includes checking whether the oven is currently heating, and
 * starting or stopping the heating process. These operations are used to
 * automatically regulate the internal temperature to maintain the target
 * temperature, similar to a real oven.
 * </p>
 *
 * <p><strong>Invariants</strong></p>
 * 
 * <pre>
 * invariant	{@code true}	// no more invariant
 * </pre>
 * 
 * <p>Created on : 2025-10-10</p>
 * 
 * @author	<a href="mailto:rodrigo.vila@etu.sorbonne-universite.fr">Rodrigo Vila</a>
 * @author	<a href="mailto:damien.ribeiro@etu.sorbonne-universite.fr">Damien Ribeiro</a>
 */
public interface		OvenInternalControlI
extends		OvenTemperatureI
{
	// -------------------------------------------------------------------------
	// Internal control methods
	// -------------------------------------------------------------------------

	/**
	 * Return true if the oven is currently heating.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code on()}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @return				true if the oven's heating element is active.
	 * @throws Exception	if the oven is not accessible or an internal error occurs.
	 */
	public boolean		heating() throws Exception;

	/**
	 * Start heating.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code on()}
	 * pre	{@code heating()}
	 * post	{@code heating()}
	 * </pre>
	 *
	 * @throws Exception	if an error occurs while activating heating.
	 */
	public void			startHeating() throws Exception;

	/**
	 * Stop heating.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code on()}
	 * pre	{@code heating()}
	 * post	{@code !heating()}
	 * </pre>
	 *
	 * @throws Exception	if an error occurs while deactivating heating.
	 */
	public void			stopHeating() throws Exception;
}
