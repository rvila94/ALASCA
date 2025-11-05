package equipments.oven.connections;

import equipments.oven.OvenInternalControlCI;
import fr.sorbonne_u.alasca.physical_data.Measure;
import fr.sorbonne_u.alasca.physical_data.SignalData;
import fr.sorbonne_u.components.connectors.AbstractConnector;
import equipments.oven.OvenInternalControlI;

/**
 * The class <code>OvenInternalControlConnector</code> implements a connector
 * for the {@code OvenInternalControlCI} component interface.
 *
 * <p><strong>Description</strong></p>
 * 
 * This connector is used internally between the oven component and its
 * thermostat controller. It allows the thermostat to monitor and control
 * the heating process inside the oven, such as starting or stopping the
 * heating elements and reading temperature values.
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
 * @author	
 * 	<a href="mailto:Rodrigo.Vila@etu.sorbonne-universite.fr">Rodrigo Vila</a>,
 * 	<a href="mailto:Damien.Ribeiro@etu.sorbonne-universite.fr">Damien Ribeiro</a>
 */
public class			OvenInternalControlConnector
extends		AbstractConnector
implements	OvenInternalControlCI
{
	/**
	 * @see OvenInternalControlI#heating()
	 */
	@Override
	public boolean		heating() throws Exception
	{
		return ((OvenInternalControlCI)this.offering).heating();
	}

	/**
	 * @see OvenInternalControlCI#getTargetTemperature()
	 */
	@Override
	public Measure<Double>	getTargetTemperature() throws Exception
	{
		return ((OvenInternalControlCI)this.offering).getTargetTemperature();
	}

	/**
	 * @see OvenInternalControlCI#getCurrentTemperature()
	 */
	@Override
	public SignalData<Double>	getCurrentTemperature() throws Exception
	{
		return ((OvenInternalControlCI)this.offering).getCurrentTemperature();
	}

	/**
	 * @see OvenInternalControlI#startHeating()
	 */
	@Override
	public void			startHeating() throws Exception
	{
		((OvenInternalControlCI)this.offering).startHeating();
	}

	/**
	 * @see OvenInternalControlI#stopHeating()
	 */
	@Override
	public void			stopHeating() throws Exception
	{
		((OvenInternalControlCI)this.offering).stopHeating();
	}
}
