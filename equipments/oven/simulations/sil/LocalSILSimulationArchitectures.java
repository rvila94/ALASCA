package equipments.oven.simulations.sil;

import equipments.oven.simulations.OvenCoupledModel;
import equipments.oven.simulations.OvenElectricityModel;
import equipments.oven.simulations.OvenTemperatureModel;
import equipments.oven.simulations.events.*;

import fr.sorbonne_u.devs_simulation.architectures.RTArchitecture;
import fr.sorbonne_u.devs_simulation.hioa.architectures.RTAtomicHIOA_Descriptor;
import fr.sorbonne_u.devs_simulation.hioa.architectures.RTCoupledHIOA_Descriptor;
import fr.sorbonne_u.devs_simulation.hioa.models.vars.VariableSink;
import fr.sorbonne_u.devs_simulation.hioa.models.vars.VariableSource;
import fr.sorbonne_u.devs_simulation.models.architectures.AbstractAtomicModelDescriptor;
import fr.sorbonne_u.devs_simulation.models.architectures.CoupledModelDescriptor;
import fr.sorbonne_u.devs_simulation.models.architectures.RTAtomicModelDescriptor;
import fr.sorbonne_u.devs_simulation.models.events.*;

import java.util.*;
import java.util.concurrent.TimeUnit;

public abstract class LocalSILSimulationArchitectures {

	// FIXME
	
    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    protected static void add_connections_all(
            Map<EventSource, EventSink[]> map,
            Class<? extends EventI> eventType)
    {
        EventSource source =
                new EventSource(OvenStateModel.URI, eventType);

        EventSink sinkElectricity =
                new EventSink(OvenElectricityModel.URI, eventType);

        EventSink sinkTemperature =
                new EventSink(OvenTemperatureModel.URI, eventType);

        map.put(source, new EventSink[]{ sinkElectricity, sinkTemperature });
    }

    protected static void add_connections_electricity(
            Map<EventSource, EventSink[]> map,
            Class<? extends EventI> eventType)
    {
        EventSource source =
                new EventSource(OvenStateModel.URI, eventType);

        EventSink sink =
                new EventSink(OvenElectricityModel.URI, eventType);

        map.put(source, new EventSink[]{ sink });
    }

    protected static void add_connections_temperature(
            Map<EventSource, EventSink[]> map,
            Class<? extends EventI> eventType)
    {
        EventSource source =
                new EventSource(OvenStateModel.URI, eventType);

        EventSink sink =
                new EventSink(OvenTemperatureModel.URI, eventType);

        map.put(source, new EventSink[]{ sink });
    }

    protected static void add_reexported_event(
            Map<Class<? extends EventI>, ReexportedEvent> map,
            Class<? extends EventI> eventType)
    {
        map.put(
                eventType,
                new ReexportedEvent(OvenStateModel.URI, eventType)
        );
    }

    protected static void add_binding(
            Map<VariableSource, VariableSink[]> map,
            String name,
            Class<?> type,
            String exportingURI,
            String importingURI)
    {
        VariableSource source =
                new VariableSource(name, type, exportingURI);
        VariableSink sink =
                new VariableSink(name, type, importingURI);

        map.put(source, new VariableSink[]{ sink });
    }

    // -------------------------------------------------------------------------
    // SIL UNIT TEST ARCHITECTURE
    // -------------------------------------------------------------------------

    public static RTArchitecture createOvenSIL_Architecture4UnitTest(
            String architectureURI,
            String rootModelURI,
            TimeUnit simulatedTimeUnit,
            double accelerationFactor
    ) throws Exception
    {
        // Preconditions
        assert architectureURI != null && !architectureURI.isEmpty();
        assert rootModelURI != null && !rootModelURI.isEmpty();
        assert simulatedTimeUnit != null;
        assert accelerationFactor > 0.0;

        // Atomic models
        Map<String, AbstractAtomicModelDescriptor> atomicModels =
                new HashMap<>();

        atomicModels.put(
                OvenElectricityModel.URI,
                RTAtomicHIOA_Descriptor.create(
                        OvenElectricityModel.class,
                        OvenElectricityModel.URI,
                        simulatedTimeUnit,
                        null,
                        accelerationFactor));

        atomicModels.put(
                OvenTemperatureModel.URI,
                RTAtomicHIOA_Descriptor.create(
                        OvenTemperatureModel.class,
                        OvenTemperatureModel.URI,
                        simulatedTimeUnit,
                        null,
                        accelerationFactor));

        atomicModels.put(
                OvenStateModel.URI,
                RTAtomicModelDescriptor.create(
                        OvenStateModel.class,
                        OvenStateModel.URI,
                        simulatedTimeUnit,
                        null,
                        accelerationFactor));

        // Submodels
        Set<String> submodels = new HashSet<>();
        submodels.add(OvenStateModel.URI);
        submodels.add(OvenElectricityModel.URI);
        submodels.add(OvenTemperatureModel.URI);

        // Event connections
        Map<EventSource, EventSink[]> connections = new HashMap<>();

        add_connections_electricity(connections, SwitchOnOven.class);
        add_connections_electricity(connections, SetPowerOven.class);

        add_connections_all(connections, SwitchOffOven.class);
        add_connections_all(connections, HeatOven.class);
        add_connections_all(connections, DoNotHeatOven.class);

        add_connections_temperature(connections, SetTargetTemperatureOven.class);
        add_connections_temperature(connections, OpenDoorOven.class);
        add_connections_temperature(connections, CloseDoorOven.class);
        add_connections_temperature(connections, SetModeOven.class);

        // Variable bindings
        Map<VariableSource, VariableSink[]> bindings = new HashMap<>();

        add_binding(
                bindings,
                "currentMode",
                equipments.oven.Oven.OvenMode.class,
                OvenTemperatureModel.URI,
                OvenElectricityModel.URI);
        
        add_binding(
                bindings,
                "currentHeatingPower",
                Double.class,
                OvenElectricityModel.URI,
                OvenTemperatureModel.URI);

        // Coupled model
        Map<String, CoupledModelDescriptor> coupledModels =
                new HashMap<>();

        coupledModels.put(
                rootModelURI,
                new RTCoupledHIOA_Descriptor(
                        OvenCoupledModel.class,
                        rootModelURI,
                        submodels,
                        null,
                        null,
                        connections,
                        null,
                        null,
                        null,
                        bindings,
                        accelerationFactor));

        return new RTArchitecture(
                architectureURI,
                rootModelURI,
                atomicModels,
                coupledModels,
                simulatedTimeUnit,
                accelerationFactor);
    }

    // -------------------------------------------------------------------------
    // SIL INTEGRATION TEST ARCHITECTURE
    // -------------------------------------------------------------------------

    public static RTArchitecture createOvenSIL_Architecture4IntegrationTest(
            String architectureURI,
            String rootModelURI,
            TimeUnit simulatedTimeUnit,
            double accelerationFactor
    ) throws Exception
    {
        assert architectureURI != null && !architectureURI.isEmpty();
        assert rootModelURI != null && !rootModelURI.isEmpty();
        assert simulatedTimeUnit != null;
        assert accelerationFactor > 0.0;

        Map<String, AbstractAtomicModelDescriptor> atomicModels =
                new HashMap<>();

        atomicModels.put(
                OvenTemperatureModel.URI,
                RTAtomicHIOA_Descriptor.create(
                        OvenTemperatureModel.class,
                        OvenTemperatureModel.URI,
                        simulatedTimeUnit,
                        null,
                        accelerationFactor));

        atomicModels.put(
                OvenStateModel.URI,
                RTAtomicModelDescriptor.create(
                        OvenStateModel.class,
                        OvenStateModel.URI,
                        simulatedTimeUnit,
                        null,
                        accelerationFactor));

        Set<String> submodels = new HashSet<>();
        submodels.add(OvenStateModel.URI);
        submodels.add(OvenTemperatureModel.URI);

        Map<Class<? extends EventI>, ReexportedEvent> reexported =
                new HashMap<>();

        add_reexported_event(reexported, SwitchOnOven.class);
        add_reexported_event(reexported, SwitchOffOven.class);
        add_reexported_event(reexported, HeatOven.class);
        add_reexported_event(reexported, DoNotHeatOven.class);
        add_reexported_event(reexported, SetModeOven.class);
        add_reexported_event(reexported, SetTargetTemperatureOven.class);
        add_reexported_event(reexported, OpenDoorOven.class);
        add_reexported_event(reexported, CloseDoorOven.class);

        Map<EventSource, EventSink[]> connections = new HashMap<>();

        add_connections_temperature(connections, HeatOven.class);
        add_connections_temperature(connections, DoNotHeatOven.class);
        add_connections_temperature(connections, SetTargetTemperatureOven.class);
        add_connections_temperature(connections, SetModeOven.class);

        Map<String, CoupledModelDescriptor> coupledModels =
                new HashMap<>();

        coupledModels.put(
                rootModelURI,
                new RTCoupledHIOA_Descriptor(
                        OvenCoupledModel.class,
                        rootModelURI,
                        submodels,
                        null,
                        reexported,
                        connections,
                        null,
                        null,
                        null,
                        null,
                        accelerationFactor));

        return new RTArchitecture(
                architectureURI,
                rootModelURI,
                atomicModels,
                coupledModels,
                simulatedTimeUnit,
                accelerationFactor);
    }
}
