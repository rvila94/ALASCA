package equipments.fan;

import fr.sorbonne_u.components.interfaces.OfferedCI;
import fr.sorbonne_u.components.interfaces.RequiredCI;

/**
 * The component interface <code>FanUserCI</code> defines the services a
 * fan component offers and that can be required from it.
 *
 * <p><strong>Description</strong></p>
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
public interface FanUserCI 
extends		OfferedCI,
			RequiredCI,
			FanImplementationI
{
	/**
     * @see fr.sorbonne_u.components.hem2025e1.equipments.fan.FanImplementationI#getState()
     */
    @Override
    public FanState getState() throws Exception;

    /**
     * @see fr.sorbonne_u.components.hem2025e1.equipments.fan.FanImplementationI#getMode()
     */
    @Override
    public FanMode getMode() throws Exception;

    /**
     * @see fr.sorbonne_u.components.hem2025e1.equipments.fan.FanImplementationI#turnOn()
     */
    @Override
    public void turnOn() throws Exception;

    /**
     * @see fr.sorbonne_u.components.hem2025e1.equipments.fan.FanImplementationI#turnOff()
     */
    @Override
    public void turnOff() throws Exception;
    
    /**
     * @see fr.sorbonne_u.components.hem2025e1.equipments.fan.FanImplementationI#setHigh()
     */
    @Override
    public void setHigh() throws Exception;
    
    /**
     * @see fr.sorbonne_u.components.hem2025e1.equipments.fan.FanImplementationI#setMedium()
     */
    @Override
    public void setMedium() throws Exception;
    
    /**
     * @see fr.sorbonne_u.components.hem2025e1.equipments.fan.FanImplementationI#setLow()
     */
    @Override
    public void setLow() throws Exception;
    
    /**
     * @see fr.sorbonne_u.components.hem2025e1.equipments.fan.FanImplementationI#startOscillation()
     */
    @Override
    public void startOscillation() throws Exception;
    
    /**
     * @see fr.sorbonne_u.components.hem2025e1.equipments.fan.FanImplementationI#stopOscillation()
     */
    @Override
    public void stopOscillation() throws Exception;
    
    /**
     * @see fr.sorbonne_u.components.hem2025e1.equipments.fan.FanImplementationI#isOscillating()
     */
    @Override
    public boolean isOscillating() throws Exception;
}
