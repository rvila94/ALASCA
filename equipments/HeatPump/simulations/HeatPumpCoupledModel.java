package equipments.HeatPump.simulations;

import fr.sorbonne_u.devs_simulation.hioa.models.vars.StaticVariableDescriptor;
import fr.sorbonne_u.devs_simulation.hioa.models.vars.VariableSink;
import fr.sorbonne_u.devs_simulation.hioa.models.vars.VariableSource;
import fr.sorbonne_u.devs_simulation.models.CoupledModel;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.events.EventSink;
import fr.sorbonne_u.devs_simulation.models.events.EventSource;
import fr.sorbonne_u.devs_simulation.models.events.ReexportedEvent;
import fr.sorbonne_u.devs_simulation.models.interfaces.ModelI;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.CoordinatorI;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * The class <code>equipments.HeatPump.simulations.HeatPumpCoupledModel</code>.
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
public class HeatPumpCoupledModel extends CoupledModel {

    public static String URI = HeatPumpCoupledModel.class.getSimpleName();

    public HeatPumpCoupledModel(String uri,
                                TimeUnit simulatedTimeUnit,
                                CoordinatorI simulationEngine,
                                ModelI[] submodels,
                                Map<Class<? extends EventI>, EventSink[]> imported,
                                Map<Class<? extends EventI>, ReexportedEvent> reexported,
                                Map<EventSource, EventSink[]> connections) {
        super(uri,
                simulatedTimeUnit,
                simulationEngine,
                submodels,
                imported,
                reexported,
                connections);
    }

    public HeatPumpCoupledModel(String uri,
                                TimeUnit simulatedTimeUnit,
                                CoordinatorI simulationEngine,
                                ModelI[] submodels,
                                Map<Class<? extends EventI>, EventSink[]> imported,
                                Map<Class<? extends EventI>, ReexportedEvent> reexported,
                                Map<EventSource, EventSink[]> connections,
                                Map<StaticVariableDescriptor, VariableSink[]> importedVars,
                                Map<VariableSource, StaticVariableDescriptor> reexportedVars,
                                Map<VariableSource, VariableSink[]> bindings) {
        super(uri,
                simulatedTimeUnit,
                simulationEngine,
                submodels,
                imported,
                reexported,
                connections,
                importedVars,
                reexportedVars,
                bindings);
    }
}
