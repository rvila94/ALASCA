package equipments.HeatPump.simulations.sil;

import equipments.HeatPump.simulations.events.*;
import equipments.HeatPump.simulations.HeatPumpCoupledModel;
import equipments.HeatPump.simulations.HeatPumpElectricityModel;
import equipments.HeatPump.simulations.HeatPumpHeatingModel;
import fr.sorbonne_u.components.hem2025e3.equipments.heater.sil.ExternalTemperatureSILModel;
import fr.sorbonne_u.devs_simulation.architectures.RTArchitecture;
import fr.sorbonne_u.devs_simulation.hioa.architectures.RTAtomicHIOA_Descriptor;
import fr.sorbonne_u.devs_simulation.hioa.architectures.RTCoupledHIOA_Descriptor;
import fr.sorbonne_u.devs_simulation.hioa.models.vars.VariableSink;
import fr.sorbonne_u.devs_simulation.hioa.models.vars.VariableSource;
import fr.sorbonne_u.devs_simulation.models.architectures.AbstractAtomicModelDescriptor;
import fr.sorbonne_u.devs_simulation.models.architectures.CoupledModelDescriptor;
import fr.sorbonne_u.devs_simulation.models.architectures.RTAtomicModelDescriptor;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.events.EventSink;
import fr.sorbonne_u.devs_simulation.models.events.EventSource;
import fr.sorbonne_u.devs_simulation.models.events.ReexportedEvent;
import fr.sorbonne_u.exceptions.PreconditionException;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * The class <code>equipments.HeatPump.simulations.sil.LocalSILSimulationArchitectures</code>.
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
public abstract class LocalSILSimulationArchitectures {

    protected static void add_connections_all(
            Map<EventSource, EventSink[]> map,
            Class <? extends EventI> eventType) {
        final EventSource source =
                new EventSource(HeatPumpStateModel.URI, eventType);
        final EventSink sink_electricity =
                new EventSink(HeatPumpElectricityModel.URI, eventType);
        final EventSink sink_heating =
                new EventSink(HeatPumpHeatingModel.URI, eventType);

        map.put(source, new EventSink[] { sink_electricity, sink_heating });
    }

    protected static void add_connections_heating(
            Map<EventSource, EventSink[]> map,
            Class <? extends EventI> eventType) {
        final EventSource source =
                new EventSource(HeatPumpStateModel.URI, eventType);
        final EventSink sink_heating =
                new EventSink(HeatPumpHeatingModel.URI, eventType);

        map.put(source, new EventSink[] { sink_heating });
    }


    protected static void add_connections_electricity(
            Map<EventSource, EventSink[]> map,
            Class <? extends EventI> eventType) {
        final EventSource source =
                new EventSource(HeatPumpStateModel.URI, eventType);
        final EventSink sink_electricity =
                new EventSink(HeatPumpElectricityModel.URI, eventType);

        map.put(source, new EventSink[] { sink_electricity });
    }

    protected static void add_reexported_event(
            Map<Class<? extends EventI>, ReexportedEvent> map,
            Class <? extends EventI> eventType
    ) {
        map.put(
                eventType,
                new ReexportedEvent(HeatPumpStateModel.URI, eventType)
        );
    }

    protected static void add_binding(
            Map<VariableSource, VariableSink[]> map,
            String name,
            Class<?> type,
            String exportingURI,
            String importingURI
            ) {
        final VariableSource source = new VariableSource(name, type, exportingURI);
        final VariableSink sink = new VariableSink(name, type, importingURI);

        map.put(source, new VariableSink[]{ sink });
    }

    public static RTArchitecture createHeatPumpSIL_Architecture4UnitTest(
            String architectureURI,
            String rootModelURI,
            TimeUnit simulatedTimeUnit,
            double accelerationFactor
    ) throws Exception
    {
        assert architectureURI != null && !architectureURI.isEmpty():
                new PreconditionException(
                        "architectureURI == null || architectureURI.isEmpty()");
        assert rootModelURI != null && !rootModelURI.isEmpty():
                new PreconditionException(
                        "rootModelURI == null || rootModelURI.isEmpty()"
                );
        assert simulatedTimeUnit != null:
                new PreconditionException("simulatedTimeUnit == null");
        assert accelerationFactor > 0.0:
                new PreconditionException("accelerationFactor <= 0.0");

        Map<String, AbstractAtomicModelDescriptor> atomicModelDescriptors =
                new HashMap<>();

        atomicModelDescriptors.put(
                HeatPumpElectricityModel.URI,
                RTAtomicHIOA_Descriptor.create(
                        HeatPumpElectricityModel.class,
                        HeatPumpElectricityModel.URI,
                        simulatedTimeUnit,
                        null,
                        accelerationFactor));

        atomicModelDescriptors.put(
                HeatPumpHeatingModel.URI,
                RTAtomicHIOA_Descriptor.create(
                        HeatPumpHeatingModel.class,
                        HeatPumpHeatingModel.URI,
                        simulatedTimeUnit,
                        null,
                        accelerationFactor
                ));
        atomicModelDescriptors.put(
                ExternalTemperatureSILModel.URI,
                RTAtomicHIOA_Descriptor.create(
                        ExternalTemperatureSILModel.class,
                        ExternalTemperatureSILModel.URI,
                        simulatedTimeUnit,
                        null,
                        accelerationFactor));

        atomicModelDescriptors.put(
                HeatPumpStateModel.URI,
                RTAtomicModelDescriptor.create(
                        HeatPumpStateModel.class,
                        HeatPumpStateModel.URI,
                        simulatedTimeUnit,
                        null,
                        accelerationFactor));

        // the set of submodels of the coupled model, given by their URIs
        Set<String> submodels = new HashSet<String>();
        submodels.add(HeatPumpStateModel.URI);
        submodels.add(HeatPumpElectricityModel.URI);
        submodels.add(HeatPumpHeatingModel.URI);
        submodels.add(ExternalTemperatureSILModel.URI);

        Map<EventSource, EventSink[]> connections =
                new HashMap<EventSource,EventSink[]>();

        add_connections_electricity(connections, SwitchOnEvent.class);
        add_connections_electricity(connections, SwitchOffEvent.class);
        add_connections_electricity(connections, SetPowerEvent.class);
        add_connections_all(connections, StartHeatingEvent.class);
        add_connections_all(connections, StopHeatingEvent.class);
        add_connections_all(connections, StartCoolingEvent.class);
        add_connections_all(connections, StopCoolingEvent.class);

        Map<VariableSource, VariableSink[]> bindings =
                new HashMap<>();

        add_binding(bindings,
                "externalTemperature", Double.class,
                ExternalTemperatureSILModel.URI, HeatPumpHeatingModel.URI);

        Map<String, CoupledModelDescriptor> coupledModelDescriptors =
                new HashMap<>();

        coupledModelDescriptors.put(
                rootModelURI,
                new RTCoupledHIOA_Descriptor(
                        HeatPumpCoupledModel.class,
                        rootModelURI,
                        submodels,
                        null,
                        null,
                        connections,
                        null,
                        null,
                        null,
                        bindings,
                        accelerationFactor
                ));

        RTArchitecture architecture =
                new RTArchitecture(
                        architectureURI,
                        rootModelURI,
                        atomicModelDescriptors,
                        coupledModelDescriptors,
                        simulatedTimeUnit,
                        accelerationFactor);

        return architecture;
    }

    public static RTArchitecture	createHeatPumpSIL_Architecture4IntegrationTest(
            String architectureURI,
            String rootModelURI,
            TimeUnit simulatedTimeUnit,
            double accelerationFactor
    ) throws Exception
    {
        assert architectureURI != null && !architectureURI.isEmpty():
                new PreconditionException(
                        "architectureURI == null || architectureURI.isEmpty()");
        assert rootModelURI != null && !rootModelURI.isEmpty():
                new PreconditionException(
                        "rootModelURI == null || rootModelURI.isEmpty()"
                );
        assert simulatedTimeUnit != null:
                new PreconditionException("simulatedTimeUnit == null");
        assert accelerationFactor > 0.0:
                new PreconditionException("accelerationFactor <= 0.0");

        Map<String, AbstractAtomicModelDescriptor> atomicModelDescriptors =
                new HashMap<>();

        atomicModelDescriptors.put(
                HeatPumpHeatingModel.URI,
                RTAtomicHIOA_Descriptor.create(
                        HeatPumpHeatingModel.class,
                        HeatPumpHeatingModel.URI,
                        simulatedTimeUnit,
                        null,
                        accelerationFactor
                ));
        atomicModelDescriptors.put(
                ExternalTemperatureSILModel.URI,
                RTAtomicHIOA_Descriptor.create(
                        ExternalTemperatureSILModel.class,
                        ExternalTemperatureSILModel.URI,
                        simulatedTimeUnit,
                        null,
                        accelerationFactor));

        atomicModelDescriptors.put(
                HeatPumpStateModel.URI,
                RTAtomicModelDescriptor.create(
                        HeatPumpStateModel.class,
                        HeatPumpStateModel.URI,
                        simulatedTimeUnit,
                        null,
                        accelerationFactor));

        // the set of submodels of the coupled model, given by their URIs
        Set<String> submodels = new HashSet<String>();
        submodels.add(HeatPumpStateModel.URI);
        submodels.add(HeatPumpHeatingModel.URI);
        submodels.add(ExternalTemperatureSILModel.URI);

        Map<Class<? extends EventI>,ReexportedEvent> reexported =
                new HashMap<Class<? extends EventI>,ReexportedEvent>();

        add_reexported_event(reexported, SwitchOnEvent.class);
        add_reexported_event(reexported, SwitchOffEvent.class);
        add_reexported_event(reexported, SetPowerEvent.class);
        add_reexported_event(reexported, StartHeatingEvent.class);
        add_reexported_event(reexported, StopHeatingEvent.class);
        add_reexported_event(reexported, StartCoolingEvent.class);
        add_reexported_event(reexported, StopCoolingEvent.class);

        Map<EventSource, EventSink[]> connections =
                new HashMap<EventSource,EventSink[]>();

        add_connections_heating(connections, StartHeatingEvent.class);
        add_connections_heating(connections, StopHeatingEvent.class);
        add_connections_heating(connections, StartCoolingEvent.class);
        add_connections_heating(connections, StopCoolingEvent.class);

        Map<VariableSource, VariableSink[]> bindings =
                new HashMap<>();

        add_binding(bindings,
                "externalTemperature", Double.class,
                ExternalTemperatureSILModel.URI, HeatPumpHeatingModel.URI);

        Map<String, CoupledModelDescriptor> coupledModelDescriptors =
                new HashMap<>();

        coupledModelDescriptors.put(
                rootModelURI,
                new RTCoupledHIOA_Descriptor(
                        HeatPumpCoupledModel.class,
                        rootModelURI,
                        submodels,
                        null,
                        reexported,
                        connections,
                        null,
                        null,
                        null,
                        bindings,
                        accelerationFactor
                ));

        RTArchitecture architecture =
                new RTArchitecture(
                        architectureURI,
                        rootModelURI,
                        atomicModelDescriptors,
                        coupledModelDescriptors,
                        simulatedTimeUnit,
                        accelerationFactor);

        return architecture;
    }

}
