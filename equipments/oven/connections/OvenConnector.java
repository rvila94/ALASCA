package equipments.oven.connections;

import equipments.oven.*;
import fr.sorbonne_u.components.connectors.AbstractConnector;

/**
 * The class <code>OvenConnector</code> implements a connector for
 * the <code>OvenUserCI</code> component interface.
 *
 * <p><strong>Description</strong></p>
 * 
 * This connector delegates all method calls from an outbound port
 * to the inbound port of the <code>Oven</code> component,
 * allowing other components to control the oven remotely
 * (turn on/off, set modes, program cooking, etc.).
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
 * <p>Created on : 2025-10-08</p>
 * 
 * @author	<a href="mailto:Rodrigo.Vila@etu.sorbonne-universite.fr">Rodrigo Vila</a>
 * @author	<a href="mailto:Damien.Ribeiro@etu.sorbonne-universite.fr">Damien Ribeiro</a>
 */
public class OvenConnector
extends AbstractConnector
implements OvenUserCI
{
	// -------------------------------------------------------------------------
	// Methods
	// -------------------------------------------------------------------------

	@Override
	public OvenState getState() throws Exception {
		return ((OvenUserCI)this.offering).getState();
	}

	@Override
	public OvenMode getMode() throws Exception {
		return ((OvenUserCI)this.offering).getMode();
	}

	@Override
	public int getTemperature() throws Exception {
		return ((OvenUserCI)this.offering).getTemperature();
	}

	@Override
	public boolean isCooking() throws Exception {
		return ((OvenUserCI)this.offering).isCooking();
	}

	@Override
	public void turnOn() throws Exception {
		((OvenUserCI)this.offering).turnOn();
	}

	@Override
	public void turnOff() throws Exception {
		((OvenUserCI)this.offering).turnOff();
	}

	@Override
	public void setDefrost() throws Exception {
		((OvenUserCI)this.offering).setDefrost();
	}

	@Override
	public void setGrill() throws Exception {
		((OvenUserCI)this.offering).setGrill();
	}

	@Override
	public void setTemperature(int temperature) throws Exception {
		((OvenUserCI)this.offering).setTemperature(temperature);
	}

	@Override
	public void startCooking(int durationInSeconds) throws Exception {
		((OvenUserCI)this.offering).startCooking(durationInSeconds);
	}

	@Override
	public void programCooking(int delayInSeconds, int durationInSeconds) throws Exception {
		((OvenUserCI)this.offering).programCooking(delayInSeconds, durationInSeconds);
	}

	@Override
	public void stopProgram() throws Exception {
		((OvenUserCI)this.offering).stopProgram();
	}
}
