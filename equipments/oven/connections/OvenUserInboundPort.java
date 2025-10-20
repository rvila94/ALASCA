package equipments.oven.connections;

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.interfaces.OfferedCI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;
import fr.sorbonne_u.exceptions.PreconditionException;
import fr.sorbonne_u.alasca.physical_data.Measure;
import fr.sorbonne_u.alasca.physical_data.SignalData;

import equipments.oven.OvenUserCI;
import equipments.oven.OvenUserI;
import equipments.oven.Oven.OvenMode;
import equipments.oven.Oven.OvenState;
import equipments.oven.OvenExternalControlI;
import equipments.oven.OvenTemperatureI;

/**
 * The class <code>OvenUserInboundPort</code> implements an inbound port for
 * the {@code OvenUserCI} component interface.
 *
 * <p><strong>Description</strong></p>
 * 
 * This inbound port delegates incoming calls on the Oven user interface to the
 * owning component implementing {@code OvenUserI}. It also exposes methods
 * coming from the oven external control and temperature interfaces by calling
 * the corresponding methods on the owner (casting to the appropriate internal
 * interfaces when needed).
 * 
 * <p><strong>Invariants</strong></p>
 * 
 * <pre>
 * invariant {@code true} // no more invariant
 * </pre>
 * 
 * <p>Created on : 2025-10-10</p>
 * 
 * @author	<a href="mailto:Rodrigo.Vila@etu.sorbonne-universite.fr">Rodrigo Vila</a>
 * @author	<a href="mailto:Damien.Ribeiro@etu.sorbonne-universite.fr">Damien Ribeiro</a>
 */
public class OvenUserInboundPort
extends AbstractInboundPort
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
	 * create an inbound port.
	 * 
	 * <p><strong>Contract</strong></p>
	 * <pre>
	 * pre {@code owner != null}
	 * pre {@code owner instanceof OvenUserI}
	 * post {@code true}
	 * </pre>
	 *
	 * @param owner component that owns this port.
	 * @throws Exception <i>to do</i>.
	 */
	public OvenUserInboundPort(ComponentI owner) throws Exception {
		super(OvenUserCI.class, owner);
		assert owner instanceof OvenUserI :
			new PreconditionException("owner instanceof OvenUserI");
	}

	/**
	 * create an inbound port.
	 * 
	 * <p><strong>Contract</strong></p>
	 * <pre>
	 * pre {@code uri != null && !uri.isEmpty()}
	 * pre {@code owner != null}
	 * pre {@code owner instanceof OvenUserI}
	 * post {@code true}
	 * </pre>
	 *
	 * @param uri unique identifier of the port.
	 * @param owner component that owns this port.
	 * @throws Exception <i>to do</i>.
	 */
	public OvenUserInboundPort(String uri, ComponentI owner) throws Exception {
		super(uri, OvenUserCI.class, owner);
		assert owner instanceof OvenUserI :
			new PreconditionException("owner instanceof OvenUserI");
	}

	/**
	 * create an inbound port with a custom implemented interface.
	 * 
	 * <p><strong>Contract</strong></p>
	 * <pre>
	 * pre {@code implementedInterface != null && OvenUserCI.class.isAssignableFrom(implementedInterface)}
	 * pre {@code owner != null}
	 * pre {@code owner instanceof OvenUserI}
	 * post {@code true}
	 * </pre>
	 *
	 * @param implementedInterface interface implemented by this port.
	 * @param owner component that owns this port.
	 * @throws Exception <i>to do</i>.
	 */
	public OvenUserInboundPort(Class<? extends OfferedCI> implementedInterface, ComponentI owner)
	throws Exception {
		super(implementedInterface, owner);
		assert owner instanceof OvenUserI :
			new PreconditionException("owner instanceof OvenUserI");
	}

	/**
	 * create an inbound port with URI and custom implemented interface.
	 * 
	 * <p><strong>Contract</strong></p>
	 * <pre>
	 * pre {@code uri != null && !uri.isEmpty()}
	 * pre {@code implementedInterface != null && OvenUserCI.class.isAssignableFrom(implementedInterface)}
	 * pre {@code owner != null}
	 * pre {@code owner instanceof OvenUserI}
	 * post {@code true}
	 * </pre>
	 *
	 * @param uri unique identifier of the port.
	 * @param implementedInterface interface implemented by this port.
	 * @param owner component that owns this port.
	 * @throws Exception <i>to do</i>.
	 */
	public OvenUserInboundPort(
			String uri,
			Class<? extends OfferedCI> implementedInterface,
			ComponentI owner
	) throws Exception {
		super(uri, implementedInterface, owner);
		assert owner instanceof OvenUserI :
			new PreconditionException("owner instanceof OvenUserI");
	}

	// -------------------------------------------------------------------------
	// Methods
	// -------------------------------------------------------------------------

	/**
	 * @see equipments.oven.OvenUserCI#on()
	 */
	@Override
	public boolean on() throws Exception {
		return this.getOwner().handleRequest(o -> ((OvenUserI)o).on());
	}

	/**
	 * @see equipments.oven.OvenUserCI#switchOn()
	 */
	@Override
	public void switchOn() throws Exception {
		this.getOwner().handleRequest(o -> { ((OvenUserI)o).switchOn(); return null; });
	}

	/**
	 * @see equipments.oven.OvenUserCI#switchOff()
	 */
	@Override
	public void switchOff() throws Exception {
		this.getOwner().handleRequest(o -> { ((OvenUserI)o).switchOff(); return null; });
	}

	/**
	 * @see equipments.oven.OvenUserCI#setTargetTemperature(fr.sorbonne_u.alasca.physical_data.Measure)
	 */
	@Override
	public void setTargetTemperature(Measure<Double> target) throws Exception {
		this.getOwner().handleRequest(o -> { ((OvenUserI)o).setTargetTemperature(target); return null; });
	}

	/**
	 * @see equipments.oven.OvenUserCI#setMode(equipments.oven.OvenMode)
	 */
	@Override
	public void setMode(OvenMode mode) throws Exception {
		this.getOwner().handleRequest(o -> { ((OvenUserI)o).setMode(mode); return null; });
	}
	
	/**
	 * @see equipments.oven.OvenUserCI#startCooking(double)
	 */
	@Override
	public void startCooking(double delayInSeconds) throws Exception {
		this.getOwner().handleRequest(o -> { ((OvenUserI)o).startCooking(delayInSeconds); return null; });
	}
	
	/**
	 * @see equipments.oven.OvenUserCI#stopCooking()
	 */
	@Override
	public void stopCooking() throws Exception {
		this.getOwner().handleRequest(o -> { ((OvenUserI)o).stopCooking(); return null; });
	}
	
	@Override
	public Measure<Double> getMaxPowerLevel() throws Exception {
		return this.getOwner().handleRequest(o -> ((OvenExternalControlI)o).getMaxPowerLevel());
	}

	@Override
	public void setCurrentPowerLevel(Measure<Double> powerLevel) throws Exception {
		this.getOwner().handleRequest(o -> { ((OvenExternalControlI)o).setCurrentPowerLevel(powerLevel); return null; });
	}

	@Override
	public SignalData<Double> getCurrentPowerLevel() throws Exception {
		return this.getOwner().handleRequest(o -> ((OvenExternalControlI)o).getCurrentPowerLevel());
	}

	@Override
	public Measure<Double> getTargetTemperature() throws Exception {
		return this.getOwner().handleRequest(o -> ((OvenTemperatureI)o).getTargetTemperature());
	}

	@Override
	public SignalData<Double> getCurrentTemperature() throws Exception {
		return this.getOwner().handleRequest(o -> ((OvenTemperatureI)o).getCurrentTemperature());
	}

	@Override
	public OvenState getState() throws Exception {
		return this.getOwner().handleRequest(o -> ((OvenUserI)o).getState());
	}

	@Override
	public OvenMode getMode() throws Exception {
		return this.getOwner().handleRequest(o -> ((OvenUserI)o).getMode());
	}
}