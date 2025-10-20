package equipments.oven.connections;

import fr.sorbonne_u.alasca.physical_data.Measure;
import fr.sorbonne_u.alasca.physical_data.SignalData;
import fr.sorbonne_u.components.connectors.AbstractConnector;
import equipments.oven.Oven.OvenMode;
import equipments.oven.Oven.OvenState;
import equipments.oven.OvenUserCI;

/**
 * The class <code>OvenUserConnector</code> implements a connector for the
 * {@code OvenUserCI} component interface.
 *
 * <p><strong>Description</strong></p>
 * 
 * This connector enables a user component (e.g. a smart home manager or
 * a user interface) to communicate with an {@code Oven} component through
 * the {@code OvenUserCI} interface.
 * 
 * <p><strong>Implementation Invariants</strong></p>
 * 
 * <pre>
 * invariant	{@code true}
 * </pre>
 * 
 * <p><strong>Invariants</strong></p>
 * 
 * <pre>
 * invariant	{@code true}
 * </pre>
 * 
 * <p>Created on : 2025-10-10</p>
 * 
 * @author	<a href="mailto:Rodrigo.Vila@etu.sorbonne-universite.fr">Rodrigo Vila</a>
 * @author	<a href="mailto:Damien.Ribeiro@etu.sorbonne-universite.fr">Damien Ribeiro</a>
 */
public class			OvenUserConnector
extends		AbstractConnector
implements	OvenUserCI
{
	// -------------------------------------------------------------------------
	// Methods
	// -------------------------------------------------------------------------

	/**
	 * @see equipments.oven.OvenUserCI#on()
	 */
	@Override
	public boolean		on() throws Exception
	{
		return ((OvenUserCI)this.offering).on();
	}

	/**
	 * @see equipments.oven.OvenUserCI#switchOn()
	 */
	@Override
	public void			switchOn() throws Exception
	{
		((OvenUserCI)this.offering).switchOn();
	}

	/**
	 * @see equipments.oven.OvenUserCI#switchOff()
	 */
	@Override
	public void			switchOff() throws Exception
	{
		((OvenUserCI)this.offering).switchOff();
	}

	/**
	 * @see equipments.oven.OvenUserCI#setTargetTemperature(fr.sorbonne_u.alasca.physical_data.Measure)
	 */
	@Override
	public void			setTargetTemperature(Measure<Double> target)
	throws Exception
	{
		((OvenUserCI)this.offering).setTargetTemperature(target);
	}
	
	@Override
	public void setMode(OvenMode mode) throws Exception 
	{
		((OvenUserCI)this.offering).setMode(mode);
	}

	@Override
	public void startCooking(double delayInSeconds) throws Exception {
		((OvenUserCI)this.offering).startCooking(delayInSeconds);
	}

	@Override
	public void stopCooking() throws Exception {
		((OvenUserCI)this.offering).stopCooking();
	}

	public Measure<Double>	getTargetTemperature() throws Exception
	{
		return ((OvenUserCI)this.offering).getTargetTemperature();
	}

	@Override
	public SignalData<Double>	getCurrentTemperature() throws Exception
	{
		return ((OvenUserCI)this.offering).getCurrentTemperature();
	}

	@Override
	public Measure<Double>	getMaxPowerLevel() throws Exception
	{
		return ((OvenUserCI)this.offering).getMaxPowerLevel();
	}

	@Override
	public void			setCurrentPowerLevel(Measure<Double> powerLevel)
	throws Exception
	{
		((OvenUserCI)this.offering).setCurrentPowerLevel(powerLevel);
	}

	@Override
	public SignalData<Double>	getCurrentPowerLevel() throws Exception
	{
		return ((OvenUserCI)this.offering).getCurrentPowerLevel();
	}

	@Override
	public OvenState getState() throws Exception {
		return ((OvenUserCI)this.offering).getState();
	}

	@Override
	public OvenMode getMode() throws Exception {
		return ((OvenUserCI)this.offering).getMode();
	}
}
