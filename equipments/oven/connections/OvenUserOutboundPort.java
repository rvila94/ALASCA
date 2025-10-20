package equipments.oven.connections;

import fr.sorbonne_u.alasca.physical_data.Measure;
import fr.sorbonne_u.alasca.physical_data.SignalData;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;
import equipments.oven.Oven.OvenMode;
import equipments.oven.Oven.OvenState;
import equipments.oven.OvenUserCI;

/**
 * The class <code>OvenUserOutboundPort</code> implements an outbound port for
 * the {@code OvenUserCI} component interface.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p><strong>Implementation Invariants</strong></p>
 * 
 * <pre>
 * invariant	{@code true}	// no more invariant
 * </pre>
 * 
 * <p><strong>Invariants</strong></p>
 * 
 * <pre>
 * invariant	{@code true}	// no more invariant
 * </pre>
 * 
 * <p>Created on : 2025-10-10</p>
 * 
 * @author	<a href="mailto:Rodrigo.Vila@etu.sorbonne-universite.fr">Rodrigo Vila</a>
 * @author	<a href="mailto:Damien.Ribeiro@etu.sorbonne-universite.fr">Damien Ribeiro</a>
 */
public class OvenUserOutboundPort
extends AbstractOutboundPort
implements OvenUserCI
{
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------
	private static final long serialVersionUID = 1L;
	
	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------
	/**
	 * create an outbound port.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code owner != null}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param owner			component that owns this port.
	 * @throws Exception 	<i>to do</i>.
	 */
	public OvenUserOutboundPort(ComponentI owner) throws Exception {
		super(OvenUserCI.class, owner);
	}

	/**
	 * create an outbound port.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code uri != null && !uri.isEmpty()}
	 * pre	{@code owner != null}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param uri			unique identifier of the port.
	 * @param owner			component that owns this port.
	 * @throws Exception 	<i>to do</i>.
	 */
	public OvenUserOutboundPort(String uri, ComponentI owner) throws Exception {
		super(uri, OvenUserCI.class, owner);
	}

	// -------------------------------------------------------------------------
	// Methods
	// -------------------------------------------------------------------------

	@Override
	public boolean on() throws Exception {
		return ((OvenUserCI) this.getConnector()).on();
	}

	@Override
	public void switchOn() throws Exception {
		((OvenUserCI) this.getConnector()).switchOn();
	}

	@Override
	public void switchOff() throws Exception {
		((OvenUserCI) this.getConnector()).switchOff();
	}

	@Override
	public void setTargetTemperature(Measure<Double> target) throws Exception {
		((OvenUserCI) this.getConnector()).setTargetTemperature(target);
	}
	
	@Override
	public void setMode(equipments.oven.Oven.OvenMode mode) throws Exception {
		((OvenUserCI) this.getConnector()).setMode(mode);
	}

	@Override
	public void startCooking(double delayInSeconds) throws Exception {
		((OvenUserCI) this.getConnector()).startCooking(delayInSeconds);
	}

	@Override
	public void stopCooking() throws Exception {
		((OvenUserCI) this.getConnector()).stopCooking();
	}

	@Override
	public Measure<Double> getTargetTemperature() throws Exception {
		return ((OvenUserCI) this.getConnector()).getTargetTemperature();
	}

	@Override
	public SignalData<Double> getCurrentTemperature() throws Exception {
		return ((OvenUserCI) this.getConnector()).getCurrentTemperature();
	}

	@Override
	public Measure<Double> getMaxPowerLevel() throws Exception {
		return ((OvenUserCI) this.getConnector()).getMaxPowerLevel();
	}

	@Override
	public void setCurrentPowerLevel(Measure<Double> powerLevel) throws Exception {
		((OvenUserCI) this.getConnector()).setCurrentPowerLevel(powerLevel);
	}

	@Override
	public SignalData<Double> getCurrentPowerLevel() throws Exception {
		return ((OvenUserCI) this.getConnector()).getCurrentPowerLevel();
	}

	@Override
	public OvenState getState() throws Exception {
		return ((OvenUserCI) this.getConnector()).getState();
	}

	@Override
	public OvenMode getMode() throws Exception {
		return ((OvenUserCI) this.getConnector()).getMode();
	}	
}
