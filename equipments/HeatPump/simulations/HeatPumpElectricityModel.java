package equipments.HeatPump.simulations;

import equipments.HeatPump.HeatPump;
import equipments.HeatPump.interfaces.HeatPumpExternalControlI;
import equipments.HeatPump.interfaces.HeatPumpUserI;
import equipments.HeatPump.simulations.events.*;
import equipments.HeatPump.simulations.interfaces.CompleteModelI;
import equipments.HeatPump.simulations.interfaces.ElectricityModelI;
import equipments.HeatPump.simulations.interfaces.StateModelI;
import equipments.HeatPump.simulations.reports.HeatPumpElectricityReport;
import fr.sorbonne_u.components.cyphy.plugins.devs.AtomicSimulatorPlugin;
import fr.sorbonne_u.components.hem2025e1.equipments.meter.ElectricMeterImplementationI;
import fr.sorbonne_u.components.hem2025e2.utils.Electricity;
import fr.sorbonne_u.devs_simulation.exceptions.MissingRunParameterException;
import fr.sorbonne_u.devs_simulation.exceptions.NeoSim4JavaException;
import fr.sorbonne_u.devs_simulation.hioa.annotations.ExportedVariable;
import fr.sorbonne_u.devs_simulation.hioa.annotations.ModelExportedVariable;
import fr.sorbonne_u.devs_simulation.hioa.models.AtomicHIOA;
import fr.sorbonne_u.devs_simulation.hioa.models.vars.Value;
import fr.sorbonne_u.devs_simulation.models.annotations.ModelExternalEvents;
import fr.sorbonne_u.devs_simulation.models.events.Event;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.time.Duration;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.AtomicSimulatorI;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulationReportI;
import fr.sorbonne_u.devs_simulation.utils.Pair;
import fr.sorbonne_u.devs_simulation.utils.StandardLogger;
import fr.sorbonne_u.exceptions.PostconditionException;
import fr.sorbonne_u.exceptions.PreconditionException;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * The class <code>equipments.HeatPump.simulations.HeatPumpElectricityModel</code>.
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
 * <p>Created on : 2025-10-04</p>
 *
 * @author    <a href="mailto:Rodrigo.Vila@etu.sorbonne-universite.fr">Rodrigo Vila</a>
 * @author    <a href="mailto:Damien.Ribeiro@etu.sorbonne-universite.fr">Damien Ribeiro</a>
 */
@ModelExternalEvents(imported = {
        SwitchOnEvent.class,
        SwitchOffEvent.class,
        StartHeatingEvent.class,
        StopHeatingEvent.class,
        StartCoolingEvent.class,
        StopCoolingEvent.class,
        SetPowerEvent.class
})
@ModelExportedVariable(name = "currentTemperaturePower", type = Double.class)
@ModelExportedVariable(name = "currentIntensity", type = Double.class)
public class HeatPumpElectricityModel extends AtomicHIOA implements CompleteModelI {

    // -------------------------------------------------------------------------
    // Constants
    // -------------------------------------------------------------------------


    /** when true, leaves a trace of the execution of the model.			*/
    public static boolean VERBOSE = true;

    /** URI for an instance model; works as long as only one instance is
     *  created.															*/
    public static final String URI = HeatPumpElectricityModel.class.
            getSimpleName();

    // -------------------------------------------------------------------------
    // Variables
    // -------------------------------------------------------------------------

    /** current state of the heat pump (ON, Heating, Cooling, OFF) */
    protected HeatPumpUserI.State currentState;

    /** true when the electricity consumption of the heat pump has changed
     *  after executing an external event; the external event changes the
     *  value of <code>currentState</code> and then an internal transition
     *  will be triggered by putting through in this variable which will
     *  update the variable <code>currentIntensity</code>.					*/
    protected boolean consumptionHasChanged;

    /** total consumption of the heat pump during the simulation in kwh.		*/
    protected double totalConsumption;

    // -------------------------------------------------------------------------
    // HIOA model variables
    // -------------------------------------------------------------------------

    /** current power of the heat pump between
     * {@code HeatPump.MIN_REQUIRED_POWER_LEVEL} and {@code HeatPump.MAX_POWER_LEVEL}
     * in the power unit used by the heat pump*/
    @ExportedVariable(type = Double.class)
    protected Value<Double> currentTemperaturePower = new Value(this);

    /** current intensity in the power unit defined by the electric meter.	*/
    @ExportedVariable(type = Double.class)
    protected final Value<Double> currentIntensity = new Value<Double>(this);

    // -------------------------------------------------------------------------
    // Invariants
    // -------------------------------------------------------------------------

    // TODO

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * create an atomic hybrid input/output model with the given URI (if null,
     * one will be generated) and to be run by the given simulator using the
     * given time unit for its clock.
     *
     * <p><strong>Contract</strong></p>
     *
     * <pre>
     * pre	{@code uri == null || !uri.isEmpty()}
     * pre	{@code simulatedTimeUnit != null}
     * pre	{@code simulationEngine != null && !simulationEngine.isModelSet()}
     * pre	{@code simulationEngine instanceof AtomicEngine}
     * post	{@code !isDebugModeOn()}
     * post	{@code getURI() != null && !getURI().isEmpty()}
     * post	{@code uri == null || getURI().equals(uri)}
     * post	{@code getSimulatedTimeUnit().equals(simulatedTimeUnit)}
     * post	{@code getSimulationEngine().equals(simulationEngine)}
     * </pre>
     *
     * @param uri               unique identifier of the model.
     * @param simulatedTimeUnit time unit used for the simulation clock.
     * @param simulationEngine  simulation engine enacting the model.
     */
    public HeatPumpElectricityModel(String uri, TimeUnit simulatedTimeUnit, AtomicSimulatorI simulationEngine) {
        super(uri, simulatedTimeUnit, simulationEngine);
        this.getSimulationEngine().setLogger(new StandardLogger());
    }

    // -------------------------------------------------------------------------
    // Private functions
    // -------------------------------------------------------------------------

    private double computeIntensity() {
        // TODO Impl invariants
        return this.currentTemperaturePower.getValue() / HeatPumpExternalControlI.TENSION.getData();
    }

    private double computePower() {
        // TODO impl invariants
        return HeatPumpExternalControlI.TENSION.getData() * this.currentIntensity.getValue();
    }

    private void computeTotalConsumption(Duration duration) {
        final double power = this.computePower();
        this.totalConsumption += Electricity.computeConsumption(duration, power);
    }

    // -------------------------------------------------------------------------
    // Simulation methods
    // -------------------------------------------------------------------------

    /**
     * @see StateModelI#getCurrentState
     */
    @Override
    public HeatPumpUserI.State getCurrentState() {
        return currentState;
    }

    /**
     * @see StateModelI#setCurrentState
     */
    @Override
    public void setCurrentState(HeatPumpUserI.State state) {
        assert state != null :
                new PreconditionException("state == null");

        this.currentState = state;

        assert this.currentState == state :
                new PostconditionException("this.currentState != state");

        assert HeatPumpElectricityModel.implementationInvariants(this):
                new NeoSim4JavaException(
                        "HeatPumpElectricityModel.implementationInvariants(this)");
        assert HeatPumpElectricityModel.invariants(this) :
                new NeoSim4JavaException(
                        "HeatPumpElectricityModel.invariants(this)");
    }

    /**
     * @see fr.sorbonne_u.devs_simulation.models.interfaces.ModelI#setSimulationRunParameters
     */
    @Override
    public void setSimulationRunParameters(Map<String, Object> simParams) throws MissingRunParameterException {

        super.setSimulationRunParameters(simParams);

        // this gets the reference on the owner component which is required
        // to have simulation models able to make the component perform some
        // operations or tasks or to get the value of variables held by the
        // component when necessary.
        if (simParams.containsKey(
                AtomicSimulatorPlugin.OWNER_RUNTIME_PARAMETER_NAME)) {
            // by the following, all of the logging will appear in the owner
            // component logger
            this.getSimulationEngine().setLogger(
                    AtomicSimulatorPlugin.createComponentLogger(simParams));
        }
    }

    /**
     * @see ElectricityModelI#setCurrentPower
     */
    @Override
    public void setCurrentPower(double newPower, Time time) {
        assert HeatPump.MIN_REQUIRED_POWER_LEVEL.getData() <= newPower &&
                newPower <= HeatPump.MAX_POWER_LEVEL.getData() :
                new PreconditionException("HeatPump.MIN_REQUIRED_POWER_LEVEL.getData() > newPower ||" +
                        "newPower < HeatPump.MAX_POWER_LEVEL.getData()");

        assert time != null:
                new PreconditionException("time == null");

        double oldPower = this.currentTemperaturePower.getValue();
        this.currentTemperaturePower.setNewValue(newPower, time);
        if (newPower != oldPower) {
            // TODO see if can use toggleConsumptionPower
            this.consumptionHasChanged = true;
        }

        assert this.currentTemperaturePower.getValue() == newPower :
                new PostconditionException("this.currentPower.getValue != newPower");

        assert HeatPumpElectricityModel.implementationInvariants(this):
                new NeoSim4JavaException(
                        "HeatPumpElectricityModel.implementationInvariants(this)");
        assert HeatPumpElectricityModel.invariants(this) :
                new NeoSim4JavaException(
                        "HeatPumpElectricityModel.invariants(this)");

    }

    // -------------------------------------------------------------------------
    // DEVS simulation protocol
    // -------------------------------------------------------------------------

    @Override
    public void initialiseState(Time initialTime) {
        super.initialiseState(initialTime);

        this.currentState = HeatPumpUserI.State.Off;
        this.consumptionHasChanged = false;
        this.totalConsumption = 0.;

        if (VERBOSE) {
            this.logMessage("Simulation begins.");
        }

        assert HeatPumpElectricityModel.implementationInvariants(this) :
                new NeoSim4JavaException(
                        "HeatPumpElectricityModel.implementationsInvariants(this)");
        assert HeatPumpElectricityModel.invariants(this) :
                new NeoSim4JavaException(
                        "HeatPumpElectricityModel.invariants(this)"
                );
    }

    /**
     * @see fr.sorbonne_u.devs_simulation.models.AtomicModel#userDefinedInternalTransition(fr.sorbonne_u.devs_simulation.models.time.Duration)
     */
    @Override
    public void userDefinedInternalTransition(Duration elapsedTime) {
        super.userDefinedInternalTransition(elapsedTime);

        Time time = this.getCurrentStateTime();
        switch(this.currentState) {
            case Off:
                this.currentIntensity.setNewValue(0.0, time);
                break;
            case On:
                this.currentIntensity.setNewValue(HeatPump.MIN_REQUIRED_POWER_LEVEL.getData(), time);
                break;
            default:
                final double intensity = this.computeIntensity();
                this.currentIntensity.setNewValue(
                        intensity,
                        time);
        }
        // TODO TENSION EXTERNAL???
        if (VERBOSE) {
            StringBuilder builder = new StringBuilder("new consumption: ");
            builder.append(this.currentIntensity.getValue());
            builder.append(" ");
            builder.append(ElectricMeterImplementationI.POWER_UNIT);
            builder.append(" at ");
            builder.append(this.currentIntensity.getTime());
            builder.append(".");
            this.logMessage(builder.toString());
        }

        assert HeatPumpElectricityModel.implementationInvariants(this) :
                new NeoSim4JavaException(
                        "HeatPumpElectricityModel.implementationInvariants(this)");
        assert HeatPumpElectricityModel.invariants(this) :
                new NeoSim4JavaException(
                        "HeatPumpElectricityModel.invariants(this)");
    }

    /**
     * @see fr.sorbonne_u.devs_simulation.models.AtomicModel#userDefinedExternalTransition(fr.sorbonne_u.devs_simulation.models.time.Duration)
     */
    @Override
    public void userDefinedExternalTransition(Duration elapsedTime) {
        super.userDefinedExternalTransition(elapsedTime);

        ArrayList<EventI> currentEvents = this.getStoredEventAndReset();
        assert currentEvents != null && currentEvents.size() == 1;

        Event event = (Event) currentEvents.get(0);
        assert event instanceof AbstractHeatPumpEvent;

        this.computeTotalConsumption(elapsedTime);

        if (VERBOSE) {
            StringBuilder builder = new StringBuilder("execute the external event: ");
            builder.append(event.eventAsString());
            builder.append(".");
            this.logMessage(builder.toString());
        }

        event.executeOn(this);

        assert HeatPumpElectricityModel.implementationInvariants(this) :
                new NeoSim4JavaException(
                        "HeatPumpElectricityModel.implementationInvariants(this)");
        assert HeatPumpElectricityModel.invariants(this) :
                new NeoSim4JavaException(
                        "HeatPumpElectricityModel.invariants(this)");
    }

    /**
     * @see fr.sorbonne_u.devs_simulation.models.interfaces.ModelI#getFinalReport
     */
    @Override
    public SimulationReportI getFinalReport() {
        return new HeatPumpElectricityReport(this.getURI(), this.totalConsumption);
    }

    /**
     * @see fr.sorbonne_u.devs_simulation.models.interfaces.ModelI#timeAdvance
     */
    @Override
    public Duration timeAdvance() {
        Duration ret;

        if (this.consumptionHasChanged) {
            // transition to a new state after duration 0
            this.consumptionHasChanged = false;
            ret = Duration.zero(this.getSimulatedTimeUnit());
        } else {
            ret = Duration.INFINITY;
        }

        assert HeatPumpElectricityModel.implementationInvariants(this) :
                new NeoSim4JavaException(
                        "HeatPumpElectricityModel.implementationInvariants(this)");
        assert HeatPumpElectricityModel.invariants(this) :
                new NeoSim4JavaException(
                        "HeatPumpElectricityModel.invariants(this)");

        return ret;
    }

    /**
     * @see fr.sorbonne_u.devs_simulation.models.interfaces.AtomicModelI#output
     */
    @Override
    public ArrayList<EventI> output() {
        return null;
    }

    /**
     * @see fr.sorbonne_u.devs_simulation.hioa.models.interfaces.VariableInitialisationI#useFixpointInitialiseVariables
     */
    @Override
    public boolean useFixpointInitialiseVariables() {
        return true;
    }

    /**
     * @see fr.sorbonne_u.devs_simulation.hioa.models.interfaces.VariableInitialisationI#fixpointInitialiseVariables
     */
    @Override
    public Pair<Integer, Integer> fixpointInitialiseVariables() {
        Pair<Integer, Integer> res;

        if (!this.currentIntensity.isInitialised() || !this.currentTemperaturePower.isInitialised()) {
            // TODO gotta do it better
            this.currentIntensity.initialise(0.);
            this.currentTemperaturePower.initialise(HeatPump.MIN_REQUIRED_POWER_LEVEL.getData());

            if (VERBOSE) {
                this.logMessage("Initialisation of HIOA model variables");
                StringBuilder builder = new StringBuilder();
                builder.append(this.currentIntensity.getValue());
                builder.append(" ");
                builder.append(ElectricMeterImplementationI.POWER_UNIT);
                builder.append(" at ");
                builder.append(this.currentIntensity.getTime());
                builder.append(" seconds.");
                this.logMessage(builder.toString());
            }

            res = new Pair<>(2, 0);
        } else {
            res = new Pair<>(0, 0);
        }

        return res;
    }

    /**
     * @see fr.sorbonne_u.devs_simulation.hioa.models.AtomicHIOA#endSimulation(fr.sorbonne_u.devs_simulation.models.time.Time)
     */
    @Override
    public void endSimulation(Time endTime)
    {
        Duration d = endTime.subtract(this.getCurrentStateTime());
        this.computeTotalConsumption(d);

        if (VERBOSE) {
            this.logMessage("simulation ends.");
        }

        super.endSimulation(endTime);
    }


}
