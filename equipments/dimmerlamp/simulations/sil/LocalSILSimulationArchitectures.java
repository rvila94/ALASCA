package equipments.dimmerlamp.simulations.sil;

import equipments.HeatPump.simulations.sil.HeatPumpStateModel;
import equipments.dimmerlamp.simulations.DimmerLampCoupledModel;
import equipments.dimmerlamp.simulations.DimmerLampElectricityModel;
import equipments.dimmerlamp.simulations.events.SetPowerLampEvent;
import equipments.dimmerlamp.simulations.events.SwitchOffLampEvent;
import equipments.dimmerlamp.simulations.events.SwitchOnLampEvent;
import fr.sorbonne_u.components.hem2025e2.equipments.hairdryer.mil.HairDryerCoupledModel;
import fr.sorbonne_u.devs_simulation.architectures.RTArchitecture;
import fr.sorbonne_u.devs_simulation.hioa.architectures.RTAtomicHIOA_Descriptor;
import fr.sorbonne_u.devs_simulation.models.architectures.AbstractAtomicModelDescriptor;
import fr.sorbonne_u.devs_simulation.models.architectures.CoupledModelDescriptor;
import fr.sorbonne_u.devs_simulation.models.architectures.RTAtomicModelDescriptor;
import fr.sorbonne_u.devs_simulation.models.architectures.RTCoupledModelDescriptor;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.events.EventSink;
import fr.sorbonne_u.devs_simulation.models.events.EventSource;
import fr.sorbonne_u.devs_simulation.models.events.ReexportedEvent;
import fr.sorbonne_u.exceptions.PostconditionException;
import fr.sorbonne_u.exceptions.PreconditionException;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * The class <code>equipments.dimmerlamp.simulations.sil.LocalSILSimulationArchitectures</code>.
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
public abstract class LocalSILSimulationArchitectures
{

    protected static void add_simple_connection(
            Map<EventSource,EventSink[]> map,
            Class <? extends EventI> eventType) {
        final EventSource source =
                new EventSource(DimmerLampStateModel.URI, eventType);
        final EventSink sink =
                new EventSink(DimmerLampElectricityModel.URI, eventType);

        map.put(source, new EventSink[] { sink });
    }

    protected static void add_reexported_event(
            Map<Class<? extends EventI>, ReexportedEvent> map,
            Class <? extends EventI> eventType
    ) {
        map.put(
                eventType,
                new ReexportedEvent(DimmerLampStateModel.URI, eventType)
        );
    }

    public static RTArchitecture createDimmerLampSIL_Architecture4UnitTest(
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
                DimmerLampElectricityModel.URI,
                RTAtomicHIOA_Descriptor.create(
                        DimmerLampElectricityModel.class,
                        DimmerLampElectricityModel.URI,
                        simulatedTimeUnit,
                        null,
                        accelerationFactor));

        atomicModelDescriptors.put(
                DimmerLampStateModel.URI,
                RTAtomicModelDescriptor.create(
                        DimmerLampStateModel.class,
                        DimmerLampStateModel.URI,
                        simulatedTimeUnit,
                        null,
                        accelerationFactor));

        Map<String, CoupledModelDescriptor> coupledModelDescriptors =
                new HashMap<>();

        // the set of submodels of the coupled model, given by their URIs
        Set<String> submodels = new HashSet<String>();
        submodels.add(DimmerLampElectricityModel.URI);
        submodels.add(DimmerLampStateModel.URI);

        Map<EventSource, EventSink[]> connections =
                new HashMap<EventSource,EventSink[]>();

        add_simple_connection(connections, SwitchOnLampEvent.class);
        add_simple_connection(connections, SwitchOffLampEvent.class);
        add_simple_connection(connections, SetPowerLampEvent.class);

        coupledModelDescriptors.put(
                rootModelURI,
                new RTCoupledModelDescriptor(
                        DimmerLampCoupledModel.class,
                        rootModelURI,
                        submodels,
                        null,
                        null,
                        connections,
                        null,
                        accelerationFactor));

        RTArchitecture result =
                new RTArchitecture(
                        architectureURI,
                        rootModelURI,
                        atomicModelDescriptors,
                        coupledModelDescriptors,
                        simulatedTimeUnit,
                        accelerationFactor);

        assert result.getArchitectureURI().equals(architectureURI):
                new PostconditionException("!result.getArchitectureURI().equals(architectureURI)");
        assert result.getRootModelURI().equals(rootModelURI):
                new PostconditionException("!result.getRootModelURI().equals(rootModelURI");
        assert result.getSimulationTimeUnit().equals(simulatedTimeUnit):
                new PostconditionException("!result.getSimulationTimeUnit().equals(simulatedTimeUnit)");

        return result;
    }

    public static RTArchitecture	createDimmerLampSIL_Architecture4IntegrationTest(
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
                rootModelURI,
                RTAtomicModelDescriptor.create(
                        DimmerLampStateModel.class,
                        rootModelURI,
                        simulatedTimeUnit,
                        null,
                        accelerationFactor));

        Map<String, CoupledModelDescriptor> coupledModelDescriptors =
                new HashMap<>();

        RTArchitecture result =
                new RTArchitecture(
                        architectureURI,
                        rootModelURI,
                        atomicModelDescriptors,
                        coupledModelDescriptors,
                        simulatedTimeUnit,
                        accelerationFactor);

        assert result.getArchitectureURI().equals(architectureURI):
                new PostconditionException("!result.getArchitectureURI().equals(architectureURI)");
        assert result.getRootModelURI().equals(rootModelURI):
                new PostconditionException("!result.getRootModelURI().equals(rootModelURI");
        assert result.getSimulationTimeUnit().equals(simulatedTimeUnit):
                new PostconditionException("!result.getSimulationTimeUnit().equals(simulatedTimeUnit)");

        return result;
    }

}
