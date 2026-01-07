package equipments.HeatPump.simulations;

import equipments.HeatPump.HeatPump;
import equipments.HeatPump.interfaces.HeatPumpUserI;
import equipments.HeatPump.simulations.events.*;
import equipments.HeatPump.simulations.interfaces.StateModelI;
import equipments.HeatPump.simulations.reports.HeatPumpTemperatureReport;
import fr.sorbonne_u.alasca.physical_data.MeasurementUnit;
import fr.sorbonne_u.devs_simulation.exceptions.NeoSim4JavaException;
import fr.sorbonne_u.devs_simulation.hioa.annotations.ImportedVariable;
import fr.sorbonne_u.devs_simulation.hioa.annotations.InternalVariable;
import fr.sorbonne_u.devs_simulation.hioa.annotations.ModelImportedVariable;
import fr.sorbonne_u.devs_simulation.hioa.models.AtomicHIOA;
import fr.sorbonne_u.devs_simulation.hioa.models.vars.DerivableValue;
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
import java.util.concurrent.TimeUnit;

/**
 * The class <code>equipments.HeatPump.simulations.HeatPumpHeatingModel</code>.
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
        StartHeatingEvent.class,
        StopHeatingEvent.class,
        StartCoolingEvent.class,
        StopCoolingEvent.class
})
@ModelImportedVariable(name = "externalTemperature", type = Double.class)
@ModelImportedVariable(name = "currentTemperaturePower", type = Double.class)
public class HeatPumpHeatingModel extends AtomicHIOA implements StateModelI {

    // -------------------------------------------------------------------------
    // Variables
    // -------------------------------------------------------------------------

    public static String URI = HeatPumpHeatingModel.class.getSimpleName();

    public static boolean VERBOSE = true;

    // -------------------------------------------------------------------------
    // Simulation Variables
    // -------------------------------------------------------------------------

    /** integration step as a duration, including the time unit.			*/
    protected final Duration integrationStep;

    /** accumulator to compute the mean external temperature for the simulation
     * report. */
    protected double temperatureAcc;
    /** the simulation time of start used to compute the mean temperature */
    protected Time start;

    protected HeatPumpTemperatureReport report;

    protected HeatPumpUserI.State currentState;

    // -------------------------------------------------------------------------
    // Simulation Run Parameters
    // -------------------------------------------------------------------------

    /** temperature of the room (house) when the simulation begins */
    protected static final String ROOM_TEMP_PARAM = "STEP_MEAN_DURATION";
    public static double INITIAL_ROOM_TEMP = 19.005;

    /** */
    protected static double INSULATION_TRANSFER_CONSTATNT = 12.5;

    protected static double MIN_HEATING_TRANSFER_CONSTANT = 40.0;

    /** integration step for the differential equation(assumed in hours).	*/
    protected static double STEP = 1.0/60.;    // 1 minute

    // -------------------------------------------------------------------------
    // HIOA model variables
    // -------------------------------------------------------------------------


    /** current external temperature in Celsius */
    @ImportedVariable(type = Double.class)
    protected Value<Double> externalTemperature;

    /** the current power used by the heat pump to modulate the temperature for the
     * simulation report */
    @ImportedVariable(type = Double.class)
    protected Value<Double> currentTemperaturePower;

    /** current temperature in the room */
    @InternalVariable(type = Double.class)
    protected final DerivableValue<Double> currentTemperature =
            new DerivableValue<>(this);

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
    public HeatPumpHeatingModel(String uri, TimeUnit simulatedTimeUnit, AtomicSimulatorI simulationEngine) {
        super(uri, simulatedTimeUnit, simulationEngine);

        this.integrationStep = new Duration(STEP, simulatedTimeUnit);
        this.getSimulationEngine().setLogger(new StandardLogger());
    }

    // -------------------------------------------------------------------------
    // Private Methods
    // -------------------------------------------------------------------------

    /**
     *
     * Compute the duration of the simulation until time t
     *
     * <p><strong>Contract</strong></p>
     *
     * <pre>
     *  pre {@code start.}
     *  post {@code true} // no postcondition
     * </pre>
     * @param time
     * @return
     */
    private double computeAccumulatedTime(Time time) {
        assert start.lessThanOrEqual(time) :
                new PreconditionException("! start.lessThanOrEqual(time)");

        return time.subtract(this.start).getSimulatedDuration();
    }

    private void setNewReport(Time time) {
        final double meanTemperature = this.temperatureAcc / this.computeAccumulatedTime(time);

        this.report = new HeatPumpTemperatureReport(this.getURI(), meanTemperature);
    }

    private Time computeNextStateTime(double previousTime, double elapsedTime) {
        double t = previousTime +
                elapsedTime;

        return new Time(t, this.getSimulatedTimeUnit());
    }

    // -------------------------------------------------------------------------
    // Simulation Methods
    // -------------------------------------------------------------------------

    @Override
    public HeatPumpUserI.State getCurrentState() {
        return this.currentState;
    }

    @Override
    public void setCurrentState(HeatPumpUserI.State state) {
        assert state != null :
                new PreconditionException("state == null");

        this.currentState = state;

        assert this.currentState == state :
                new PostconditionException("this.currentState != state");

        // TODO check invariants
    }

    /**
     *
     * Compute the heating performance coefficient for a heat pump
     * Using the coefficient of performance : https://en.wikipedia.org/wiki/Coefficient_of_performance
     * We assume that the efficiency of the pump is optimal (Carnot efficiency)
     *
     * <p><strong>Contract</strong></p>
     *
     * <pre>
     *  pre {@code true} // no precondition
     *  post {@code true} // no postcondition
     * </pre>
     * @return double the performance coefficient
     */
    protected double heatingPerformanceCoeff() {

        Time t = this.currentTemperature.getTime();

        // When heating, the heat tank is the inside of the house
        double heat_tank = this.currentTemperature.evaluateAt(t);

        // When heating, the cold tank is the outside of the house
        double cold_tank = this.externalTemperature.evaluateAt(t);

        // formula is true only if the heat pump efficiency is optimal
        return heat_tank / (heat_tank - cold_tank);
    }

    /**
     *
     * Compute the cooling performance coefficient for a heat pump
     * Using the coefficient of performance : https://en.wikipedia.org/wiki/Coefficient_of_performance
     * We assume that the efficiency of the pump is optimal (Carnot efficiency)
     *
     * <p><strong>Contract</strong></p>
     *
     * <pre>
     *  pre {@code true} // no precondition
     *  post {@code true} // no postcondition
     * </pre>
     * @return double the performance coefficient
     */
    protected double coolingPerformanceCoeff() {
        Time t = this.currentTemperature.getTime();

        // When cooling, the heat tank is the outside of the house
        double heat_tank = this.externalTemperature.evaluateAt(t);

        // When heating, the cold tank is the inside of the house
        double cold_tank = this.currentTemperature.evaluateAt(t);

        return cold_tank / (heat_tank - cold_tank);
    }

    protected double currentHeatTransfertConstant()
    {
        double c = 1.0 / (MIN_HEATING_TRANSFER_CONSTANT *
                HeatPump.MAX_POWER_LEVEL.getData());
        return 1.0 / (c * this.currentTemperaturePower.getValue());
    }

    protected double computeDerivatives(double current)
    {
        double result = 0.;

        Time t = this.getCurrentStateTime();
        double external_temp = this.externalTemperature.evaluateAt(t);

        double current_power = this.currentTemperaturePower.evaluateAt(t);

        if (this.currentState == HeatPumpUserI.State.Heating
                && current_power > HeatPump.MIN_REQUIRED_POWER_LEVEL.getData()) {
            double perf_coeff = heatingPerformanceCoeff();

            // We used a similar equation to the one of heater temperature model
            // Heating contribution : current warmth source, external_temp cold source
            // perf_coeff * power => how much heat emanates from the heat pump
            result = ((current - external_temp) + perf_coeff * current_power) /
                    this.currentHeatTransfertConstant();
        } else if (this.currentState == HeatPumpUserI.State.Cooling
                && current_power > HeatPump.MIN_REQUIRED_POWER_LEVEL.getData()) {
            double perf_coeff = coolingPerformanceCoeff();

            // We use the opposite sign with the perf coeff as to decrease the temperature
            // external_temp source of warmth, current source of cold
            result = ((external_temp - current) - perf_coeff * current_power) /
                    this.currentHeatTransfertConstant();
        }

        result += (external_temp - current) / INSULATION_TRANSFER_CONSTATNT;

        return result;
    }

    protected double computeNewTemperature(double deltaT)
    {
        Time previous_step = this.currentTemperature.getTime();
        double previous_temp = this.currentTemperature.evaluateAt(previous_step);
        double result = previous_temp;

        if (deltaT > 0) {
            result += deltaT * this.currentTemperature.getFirstDerivative();
        }

        // we do the means of the temperature so as to take into account the fact
        // that the temperature was varying between the two computations
        this.temperatureAcc += ((previous_temp + result) / 2) * deltaT;

        if ( VERBOSE ) {
            StringBuilder builder = new StringBuilder();
            builder.append("current internal temperature: ");
            builder.append(result);
            builder.append(" at ");
            builder.append(this.getCurrentStateTime().toString());
            this.logMessage(builder.toString());
        }

        return result;
    }

    // -------------------------------------------------------------------------
    // DEVS simulation protocol
    // -------------------------------------------------------------------------

    /**
     * @see fr.sorbonne_u.devs_simulation.models.interfaces.ModelI#getFinalReport
     */
    @Override
    public SimulationReportI getFinalReport() {
        return report;
    }

    /**
     * @see fr.sorbonne_u.devs_simulation.models.interfaces.ModelI#timeAdvance
     */
    @Override
    public Duration timeAdvance() {
        // when the heat pump is working (neither on or off) the heat pump is working continuously
        return this.integrationStep;
    }

    /**
     * @see fr.sorbonne_u.devs_simulation.models.interfaces.AtomicModelI#output
     */
    @Override
    public ArrayList<EventI> output() {
        // This model is an event sink, it does not send events to other models
        return null;
    }

    /**
     * @see fr.sorbonne_u.devs_simulation.hioa.models.interfaces.VariableInitialisationI#useFixpointInitialiseVariables
     */
    @Override
    public boolean useFixpointInitialiseVariables() {
        // This model has HIOA model variables
        return true;
    }

    /**
     * <pre>
     *     pre {@code true} // no precondition
     *     post {@code !this.currentTemperature.isInitialised() || externalTemperature.isInitialised()}
     * </pre>
     * @see fr.sorbonne_u.devs_simulation.hioa.models.interfaces.VariableInitialisationI#fixpointInitialiseVariables
     */
    @Override
    public Pair<Integer, Integer> fixpointInitialiseVariables() {

        Pair<Integer, Integer> result = new Pair<>(0, 0);

        // The currentTemperature variable depends on the external Temperature variable, therefore we need to wait
        // for the external Temperature variable to be initialised
        if (!this.currentTemperature.isInitialised() && this.externalTemperature.isInitialised()) {
            double derivative = this.computeDerivatives(INITIAL_ROOM_TEMP);
            this.currentTemperature.initialise(INITIAL_ROOM_TEMP, derivative);
            result = new Pair<>(1, 0);
        } else if (!this.externalTemperature.isInitialised()) {
            result = new Pair<>(0, 1);
        }

        // TODO check invariants

        assert !this.currentTemperature.isInitialised() || externalTemperature.isInitialised() :
                new PostconditionException("this.currentTemperature.isInitialised() && !externalTemperature.isInitialised()");

        return result;
    }

    /**
     * @see fr.sorbonne_u.devs_simulation.hioa.models.AtomicHIOA#endSimulation(fr.sorbonne_u.devs_simulation.models.time.Time)
     */
    @Override
    public void endSimulation(Time endTime)
    {
        this.setNewReport(endTime);

        if (VERBOSE) {
            this.logMessage("end of the simulation");
        }

        super.endSimulation(endTime);
    }

    @Override
    public void initialiseState(Time initialTime)
    {
        super.initialiseState(initialTime);

        this.temperatureAcc = 0.0;
        this.start = initialTime;
        this.currentState = HeatPumpUserI.State.On;

        this.report = null;

        if (VERBOSE) {
            this.logMessage("simulation starts");
        }

        // TODO check invariants

    }

    @Override
    public void userDefinedInternalTransition(Duration elapsedTime) {
        super.userDefinedInternalTransition(elapsedTime);



        double elapsed_time = elapsedTime.getSimulatedDuration();

        double newTemp = this.computeNewTemperature(elapsed_time);
        double newDerivative = this.computeDerivatives(newTemp);
        Time newTime = this.computeNextStateTime(this.currentTemperature.getTime().getSimulatedTime(), elapsed_time);

        this.currentTemperature.setNewValue(newTemp, newDerivative, newTime);



        if (VERBOSE) {
            StringBuilder builder = new StringBuilder("new temperature: ");
            builder.append(this.currentTemperature.getValue());
            builder.append(" ");
            builder.append(MeasurementUnit.CELSIUS);
            builder.append(" and derivative: ");
            builder.append(this.currentTemperature.getFirstDerivative());
            builder.append(".");
            this.logMessage(builder.toString());
        }

        // TODO check invariants
    }

    @Override
    public void			userDefinedExternalTransition(Duration elapsedTime) {
        super.userDefinedExternalTransition(elapsedTime);

        ArrayList<EventI> currentEvents = this.getStoredEventAndReset();
        assert currentEvents != null && currentEvents.size() == 1 :
                new NeoSim4JavaException("currentEvents == null || currentEvent.size() != 1");

        Event event = (Event) currentEvents.get(0);
        assert event instanceof AbstractHeatPumpEvent :
            new NeoSim4JavaException("events is not an instance of AbstractHeatPumpEvent");

        double elapsed_time = elapsedTime.getSimulatedDuration();

        double newTemp = this.computeNewTemperature(elapsed_time);
        double newDerivative = this.computeDerivatives(newTemp);
        Time newTime = this.computeNextStateTime(this.currentTemperature.getTime().getSimulatedTime(), elapsed_time);

        this.currentTemperature.setNewValue(newTemp, newDerivative, newTime);

        if (VERBOSE) {
            StringBuilder builder = new StringBuilder("execute the external event: ");
            builder.append(event.eventAsString());
            builder.append(".");
            this.logMessage(builder.toString());
        }

        event.executeOn(this);

        // TODO check invariants
    }

}
