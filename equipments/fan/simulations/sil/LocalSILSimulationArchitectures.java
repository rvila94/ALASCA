package equipments.fan.simulations.sil;

import equipments.fan.simulations.FanCoupledModel;
import equipments.fan.simulations.FanElectricityModel;
import equipments.fan.simulations.events.*;
import fr.sorbonne_u.devs_simulation.architectures.RTArchitecture;
import fr.sorbonne_u.devs_simulation.hioa.architectures.RTAtomicHIOA_Descriptor;
import fr.sorbonne_u.devs_simulation.models.architectures.AbstractAtomicModelDescriptor;
import fr.sorbonne_u.devs_simulation.models.architectures.CoupledModelDescriptor;
import fr.sorbonne_u.devs_simulation.models.architectures.RTAtomicModelDescriptor;
import fr.sorbonne_u.devs_simulation.models.architectures.RTCoupledModelDescriptor;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.events.EventSink;
import fr.sorbonne_u.devs_simulation.models.events.EventSource;
import fr.sorbonne_u.exceptions.PostconditionException;
import fr.sorbonne_u.exceptions.PreconditionException;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * The class <code>LocalSILSimulationArchitectures</code> defines the local
 * software-in-the-loop simulation architectures pertaining to the fan appliance
 * 
 * <p><strong>Description</strong></p>
 *
 * <p><strong>Implementation Invariants</strong></p>
 * 
 * <pre>
 * invariant	{@code true}	// no more invariant
 * </pre>
 * 
 * <p><strong>Invariants</strong></p>
 * 
 * <pre>
 * invariant	{@code true}	// no more invariant
 * </pre>
 *
 * <p>Created on : 2026-01-03</p>
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
                new EventSource(FanStateModel.URI, eventType);
        final EventSink sink =
                new EventSink(FanElectricityModel.URI, eventType);

        map.put(source, new EventSink[] { sink });
    }

    public static RTArchitecture createFanSIL_Architecture4UnitTest(
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
                FanElectricityModel.URI,
                RTAtomicHIOA_Descriptor.create(
                        FanElectricityModel.class,
                        FanElectricityModel.URI,
                        simulatedTimeUnit,
                        null,
                        accelerationFactor));

        atomicModelDescriptors.put(
                FanStateModel.URI,
                RTAtomicModelDescriptor.create(
                        FanStateModel.class,
                        FanStateModel.URI,
                        simulatedTimeUnit,
                        null,
                        accelerationFactor));

        Map<String, CoupledModelDescriptor> coupledModelDescriptors =
                new HashMap<>();

        // the set of submodels of the coupled model, given by their URIs
        Set<String> submodels = new HashSet<String>();
        submodels.add(FanElectricityModel.URI);
        submodels.add(FanStateModel.URI);

        Map<EventSource, EventSink[]> connections =
                new HashMap<EventSource,EventSink[]>();

        add_simple_connection(connections, SwitchOnFan.class);
        add_simple_connection(connections, SwitchOffFan.class);
        add_simple_connection(connections, SetHighFan.class);
        add_simple_connection(connections, SetMediumFan.class);
        add_simple_connection(connections, SetLowFan.class);

        coupledModelDescriptors.put(
                rootModelURI,
                new RTCoupledModelDescriptor(
                        FanCoupledModel.class,
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

    public static RTArchitecture	createFanSIL_Architecture4IntegrationTest(
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
                FanStateModel.URI,
                RTAtomicModelDescriptor.create(
                        FanStateModel.class,
                        FanStateModel.URI,
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
