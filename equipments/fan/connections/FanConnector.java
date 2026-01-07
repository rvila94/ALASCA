package equipments.fan.connections;

import equipments.fan.FanUserCI;
import fr.sorbonne_u.components.connectors.AbstractConnector;

/**
 * The class <code>FanConnector</code> implements a connector for
 * the <code>FanUserCI</code> component interface.
 *
 * <p><strong>Description</strong></p>
 * 
 * This connector delegates calls from outbound ports to the inbound port
 * of the Fan component, allowing other components to control the fan.
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
 * <p>Created on : 2025-10-04</p>
 * 
 * @author	<a href="mailto:Rodrigo.Vila@etu.sorbonne-universite.fr">Rodrigo Vila</a>
 * @author	<a href="mailto:Damien.Ribeiro@etu.sorbonne-universite.fr">Damien Ribeiro</a>
 */
public class FanConnector
extends AbstractConnector
implements FanUserCI
{
	@Override
	public FanState getState() throws Exception {
		return ((FanUserCI)this.offering).getState();
	}

	@Override
	public FanMode getMode() throws Exception {
		return ((FanUserCI)this.offering).getMode();
	}

	@Override
	public void turnOn() throws Exception {
		((FanUserCI)this.offering).turnOn();
	}

	@Override
	public void turnOff() throws Exception {
		((FanUserCI)this.offering).turnOff();
	}
	
	@Override
	public void setHigh() throws Exception {
		((FanUserCI)this.offering).setHigh();
	}
	
	@Override
	public void setMedium() throws Exception {
		((FanUserCI)this.offering).setMedium();
	}
	
	@Override
	public void setLow() throws Exception {
		((FanUserCI)this.offering).setLow();
	}

	@Override
	public void startOscillation() throws Exception {
		((FanUserCI)this.offering).startOscillation();
		
	}

	@Override
	public void stopOscillation() throws Exception {
		((FanUserCI)this.offering).stopOscillation();
		
	}

	@Override
	public boolean isOscillating() throws Exception {
		return ((FanUserCI)this.offering).isOscillating();
	}
}
