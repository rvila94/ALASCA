package equipments.HeatPump.mil;

import equipments.HeatPump.mil.events.*;
import fr.sorbonne_u.components.cyphy.utils.tests.AbstractTestScenarioBasedAtomicModel;
import fr.sorbonne_u.components.cyphy.utils.tests.TestScenarioWithSimulation;
import fr.sorbonne_u.devs_simulation.exceptions.MissingRunParameterException;
import fr.sorbonne_u.devs_simulation.models.annotations.ModelExternalEvents;
import fr.sorbonne_u.devs_simulation.models.interfaces.ModelI;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.AtomicSimulatorI;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulationReportI;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * The class <code>equipments.HeatPump.mil.HeatPumpUnitTesterModel</code>.
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
@ModelExternalEvents(exported = {
        SwitchOnEvent.class,
        SwitchOffEvent.class,
        StartHeatingEvent.class,
        StopHeatingEvent.class,
        StartCoolingEvent.class,
        StopCoolingEvent.class,
        SetPowerEvent.class
})
public class HeatPumpUnitTesterModel extends AbstractTestScenarioBasedAtomicModel {

    // -------------------------------------------------------------------------
    // Constants
    // -------------------------------------------------------------------------

    public static final String	TEST_SCENARIO_RP_NAME = "HEAT_PUMP_TEST_SCENARIO";

    /** URI for an instance model; works as long as only one instance is
     *  created.															*/
    public static final String URI = HeatPumpUnitTesterModel.class.getSimpleName();

    /**
     * create an atomic model with the given URI (if null,  one will be
     * generated) and to be run by the given simulator using the given time
     * unit for its clock.
     *
     * <p><strong>Contract</strong></p>
     *
     * <pre>
     * pre	{@code uri == null || !uri.isEmpty()}
     * pre	{@code simulatedTimeUnit != null}
     * pre	{@code simulationEngine == null || !simulationEngine.isModelSet()}
     * pre	{@code simulationEngine == null || simulationEngine instanceof AtomicEngine}
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
    public HeatPumpUnitTesterModel(String uri, TimeUnit simulatedTimeUnit, AtomicSimulatorI simulationEngine) {
        super(uri, simulatedTimeUnit, simulationEngine);
    }

    // -------------------------------------------------------------------------
    // DEVS simulation protocol
    // -------------------------------------------------------------------------

    /**
     * @see fr.sorbonne_u.devs_simulation.models.interfaces.ModelI#setSimulationRunParameters(java.util.Map)
     */
    @Override
    public void			setSimulationRunParameters(
            Map<String, Object> simParams
    ) throws MissingRunParameterException
    {
        String testScenarioName = ModelI.createRunParameterName(this.getURI(),
                TEST_SCENARIO_RP_NAME);

        // Preconditions checking
        assert	simParams != null :
                new MissingRunParameterException("simParams != null");
        assert	simParams.containsKey(testScenarioName) :
                new MissingRunParameterException(testScenarioName);

        this.setTestScenario((TestScenarioWithSimulation) simParams.get(testScenarioName));
    }

    // -------------------------------------------------------------------------
    // Optional DEVS simulation protocol: simulation report
    // -------------------------------------------------------------------------

    /**
     * @see fr.sorbonne_u.devs_simulation.models.interfaces.ModelI#getFinalReport()
     */
    @Override
    public SimulationReportI getFinalReport()
    {
        return null;
    }
}
