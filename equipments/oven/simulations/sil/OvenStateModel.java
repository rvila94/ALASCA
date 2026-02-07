package equipments.oven.simulations.sil;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import equipments.oven.Oven.OvenMode;
import equipments.oven.Oven.OvenState;
import equipments.oven.simulations.OvenSimulationOperationI;
import equipments.oven.simulations.events.DelayedStartOven;
import equipments.oven.simulations.events.CancelDelayedStartOven;
import equipments.oven.simulations.events.CloseDoorOven;
import equipments.oven.simulations.events.DoNotHeatOven;
import equipments.oven.simulations.events.HeatOven;
import equipments.oven.simulations.events.OpenDoorOven;
import equipments.oven.simulations.events.OvenEventI;
import equipments.oven.simulations.events.SetModeOven;
import equipments.oven.simulations.events.SetPowerOven;
import equipments.oven.simulations.events.SwitchOffOven;
import equipments.oven.simulations.events.SwitchOnOven;
import fr.sorbonne_u.components.cyphy.plugins.devs.AtomicSimulatorPlugin;
import fr.sorbonne_u.devs_simulation.exceptions.MissingRunParameterException;
import fr.sorbonne_u.devs_simulation.models.AtomicModel;
import fr.sorbonne_u.devs_simulation.models.annotations.ModelExternalEvents;
import fr.sorbonne_u.devs_simulation.models.events.Event;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.time.Duration;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.AtomicSimulatorI;
import fr.sorbonne_u.devs_simulation.utils.StandardLogger;

/**
 * The class <code>OvenStateModel</code> defines a simulation model
 * tracking the state changes on a oven.
 *
 * <p><strong>Description</strong></p>
 *
 * <p>
 *
 * </p>
 *
 * <p><strong>Invariants</strong></p>
 *
 * <pre>
 * </pre>
 *
 * <p>Created on : 2026-01-03</p>
 *
 * @author    <a href="mailto:Rodrigo.Vila@etu.sorbonne-universite.fr">Rodrigo Vila</a>
 * @author    <a href="mailto:Damien.Ribeiro@etu.sorbonne-universite.fr">Damien Ribeiro</a>
 */
@ModelExternalEvents(
        imported = {CloseDoorOven.class, DoNotHeatOven.class, HeatOven.class,
                	OpenDoorOven.class, SetModeOven.class, SetPowerOven.class,
                	SwitchOffOven.class,
                	SwitchOnOven.class, DelayedStartOven.class, CancelDelayedStartOven.class},
        exported = {CloseDoorOven.class, DoNotHeatOven.class, HeatOven.class,
            		OpenDoorOven.class, SetModeOven.class, SetPowerOven.class,
            		SwitchOffOven.class,
            		SwitchOnOven.class, DelayedStartOven.class, CancelDelayedStartOven.class}
)
public class OvenStateModel 
extends AtomicModel
implements OvenSimulationOperationI
{
	
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	private static final long serialVersionUID = 1L;

    public static final String URI =
        OvenStateModel.class.getSimpleName();

    public static boolean VERBOSE = true;

    protected OvenState currentState = OvenState.OFF;
    
    public Duration remainingDelay = null;

    protected EventI toBeReemitted;

    // ---------------------------------------------------------------------
    // Constructor
    // ---------------------------------------------------------------------

    public OvenStateModel(
        String uri,
        TimeUnit simulatedTimeUnit,
        AtomicSimulatorI simulationEngine
    )
    {
        super(uri, simulatedTimeUnit, simulationEngine);
        this.getSimulationEngine().setLogger(new StandardLogger());
    }

    // ---------------------------------------------------------------------
    // Methods
    // ---------------------------------------------------------------------

    @Override
    public OvenState getState() {
        return this.currentState;
    }

    @Override
    public void setState(OvenState state) {
        this.currentState = state;
    }

    @Override
    public OvenMode getMode() {
		return null;
    	// Nothing to be done here
    }

    @Override
    public void setMode(OvenMode mode) {
    	// Nothing to be done here
    }

    @Override
    public void setTargetTemperature(double temperature) {
    	// Nothing to be done here
    }

    @Override
    public void setCurrentPowerLevel(double power) {
    	// Nothing to be done here
    }

    @Override
    public void setDoorOpen(boolean open) {
    	// Nothing to be done here
    }

    // ---------------------------------------------------------------------
    // DEVS protocol
    // ---------------------------------------------------------------------
    
    @Override
    public void setSimulationRunParameters(
        Map<String, Object> simParams
    ) throws MissingRunParameterException
    {
        super.setSimulationRunParameters(simParams);

        if (simParams.containsKey(
                AtomicSimulatorPlugin.OWNER_RUNTIME_PARAMETER_NAME)) {

            this.getSimulationEngine().setLogger(
                AtomicSimulatorPlugin.createComponentLogger(simParams));
        }
    }

    
    @Override
    public void initialiseState(Time initialTime)
    {
        super.initialiseState(initialTime);

        this.currentState = OvenState.OFF;

        if (VERBOSE) {
            this.logMessage("Oven SIL simulation begins.");
        }
    }

    @Override
    public Duration timeAdvance()
    {	
    	this.logMessage(
                "[timeAdvance] state=" + currentState +
                " remainingDelay=" + remainingDelay
            );
    	
    	if (this.currentState == OvenState.WAITING 
    										&& this.remainingDelay != null) {
            return this.remainingDelay;
        }
    	/*if (this.toBeReemitted != null) {
            return Duration.zero(getSimulatedTimeUnit());
        }*/
        return Duration.INFINITY;
    }

    @Override
    public ArrayList<EventI> output()
    {
    	this.logMessage(
                "[output] state=" + currentState +
                " remainingDelay=" + remainingDelay +
                " t=" + this.getCurrentStateTime()
            );
    	ArrayList<EventI> ret = new ArrayList<>();
    	
    	if (this.currentState == OvenState.WAITING && this.remainingDelay != null) {
            HeatOven heat = new HeatOven(this.getCurrentStateTime());
            ret.add(heat);
        }
    	
    	// TODO : Tu peux decomenter ceci pour observer le moment du declenchement
    	/*if (this.timeAdvance().equals(this.remainingDelay)) {
            throw new RuntimeException(
                "OUTPUT called while timeAdvance == remainingDelay\n" +
                "state=" + currentState +
                " remainingDelay=" + remainingDelay +
                " t=" + this.getCurrentStateTime()
            );
        }*/
    	
        /*if (this.toBeReemitted != null) {
            ret.add(this.toBeReemitted);

            if (VERBOSE) {
                this.logMessage("output sends " + ret);
            }
        }*/
        return ret;
    }
    
    @Override
    public void internalTransition()
    {	
    	this.logMessage(
                "[internalTransition BEFORE] state=" + currentState +
                " remainingDelay=" + remainingDelay
            );
        if (this.currentState == OvenState.WAITING && this.remainingDelay != null) {
            this.remainingDelay = null;
            this.currentState = OvenState.ON;
        }
        this.logMessage(
                "[internalTransition AFTER] state=" + currentState +
                " remainingDelay=" + remainingDelay
            );

        /*if (this.toBeReemitted != null) {
            this.toBeReemitted = null;
        }*/
    }

    @Override
    public void userDefinedExternalTransition(Duration elapsedTime)
    {
        super.userDefinedExternalTransition(elapsedTime);

        this.logMessage(
                "[externalTransition BEFORE] state=" + currentState +
                " elapsed=" + elapsedTime
            );
        
        // Decremente le delay du start delay
        /*if (this.currentState == OvenState.WAITING && this.remainingDelay != null) {
            this.remainingDelay =
                this.remainingDelay.subtract(elapsedTime);

            if (this.remainingDelay.lessThanOrEqual(
                    Duration.zero(this.remainingDelay.getTimeUnit()))) {
                this.remainingDelay = Duration.zero(getSimulatedTimeUnit());
            }
        }*/
        
        ArrayList<EventI> currentEvents =
            this.getStoredEventAndReset();

        assert currentEvents != null && currentEvents.size() == 1;

        Event ce = (Event) currentEvents.get(0);
        assert ce instanceof OvenEventI;

        ce.executeOn(this);
        // this.toBeReemitted = ce;
        this.logMessage(
                "[externalTransition AFTER] state=" + currentState +
                " event=" + ce
            );
        if (VERBOSE) {
            this.logMessage("performing an external transition on " + ce);
        }
    }

    @Override
    public void endSimulation(Time endTime)
    {
        if (VERBOSE) {
            this.logMessage("Oven SIL simulation ends.");
        }
    }
}
