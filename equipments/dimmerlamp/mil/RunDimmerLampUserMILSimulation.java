package equipments.dimmerlamp.mil;

import equipments.dimmerlamp.mil.events.SetPowerLampEvent;
import equipments.dimmerlamp.mil.events.SwitchOffLampEvent;
import equipments.dimmerlamp.mil.events.SwitchOnLampEvent;
import fr.sorbonne_u.devs_simulation.architectures.Architecture;
import fr.sorbonne_u.devs_simulation.architectures.ArchitectureI;
import fr.sorbonne_u.devs_simulation.hioa.architectures.AtomicHIOA_Descriptor;
import fr.sorbonne_u.devs_simulation.models.architectures.AbstractAtomicModelDescriptor;
import fr.sorbonne_u.devs_simulation.models.architectures.AtomicModelDescriptor;
import fr.sorbonne_u.devs_simulation.models.architectures.CoupledModelDescriptor;
import fr.sorbonne_u.devs_simulation.models.events.EventSink;
import fr.sorbonne_u.devs_simulation.models.events.EventSource;
import fr.sorbonne_u.devs_simulation.models.time.Duration;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import fr.sorbonne_u.devs_simulation.simulators.SimulationEngine;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulationReportI;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * The class <code>equipments.dimmerlamp.mil.RunDimmerLampUserMILSimulation</code>.
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
public class RunDimmerLampUserMILSimulation {

    public static void main(String[] args)
    {
        Time.setPrintPrecision(4);
        Duration.setPrintPrecision(4);

        try {

            Map<String, AbstractAtomicModelDescriptor> atomicModelDescriptors =
                    new HashMap<>();

            // the dimmer lamp models simulating its electricity consumption is an
            // atomic HIOA model hence we use an AtomicHIOA_Descriptor(s)
            atomicModelDescriptors.put(
                    DimmerLampElectricityModel.URI,
                    AtomicHIOA_Descriptor.create(
                            DimmerLampElectricityModel.class,
                            DimmerLampElectricityModel.URI,
                            DimmerLampSimulationConfigurationI.TIME_UNIT,
                            null));
            // for atomic model, we use an AtomicModelDescriptor
            atomicModelDescriptors.put(
                    DimmerLampUserModel.URI,
                    AtomicModelDescriptor.create(
                            DimmerLampUserModel.class,
                            DimmerLampUserModel.URI,
                            DimmerLampSimulationConfigurationI.TIME_UNIT,
                            null));

            // map that will contain the coupled model descriptors to construct
            // the simulation architecture
            Map<String, CoupledModelDescriptor> coupledModelDescriptors =
                    new HashMap<>();

            // the set of submodels of the coupled model, given by their URIs
            Set<String> submodels = new HashSet<>();
            submodels.add(DimmerLampElectricityModel.URI);
            submodels.add(DimmerLampUserModel.URI);

            // event exchanging connections between exporting and importing
            // models
            Map<EventSource, EventSink[]> connections =
                    new HashMap<>();

            connections.put(
                    new EventSource(DimmerLampUserModel.URI,
                            SwitchOnLampEvent.class),
                    new EventSink[]{
                            new EventSink(DimmerLampElectricityModel.URI,
                                    SwitchOnLampEvent.class)
                    }
            );
            connections.put(
                    new EventSource(DimmerLampUserModel.URI,
                            SetPowerLampEvent.class),
                    new EventSink[]{
                            new EventSink(DimmerLampElectricityModel.URI,
                                    SetPowerLampEvent.class)
                    }
            );

            connections.put(
                    new EventSource(DimmerLampUserModel.URI,
                            SwitchOffLampEvent.class),
                    new EventSink[]{
                            new EventSink(DimmerLampElectricityModel.URI,
                                    SwitchOffLampEvent.class)
                    }
            );

            // coupled model descriptor
            coupledModelDescriptors.put(
                    DimmerLampCoupledModel.URI,
                    new CoupledModelDescriptor(
                            DimmerLampCoupledModel.class,
                            DimmerLampCoupledModel.URI,
                            submodels,
                            null,
                            null,
                            connections,
                            null));

            // simulation architecture
            ArchitectureI architecture =
                    new Architecture(
                            DimmerLampCoupledModel.URI,
                            atomicModelDescriptors,
                            coupledModelDescriptors,
                            DimmerLampSimulationConfigurationI.TIME_UNIT);

            // create the simulator from the simulation architecture
            SimulatorI se = architecture.constructSimulator();
            // this add additional time at each simulation step in
            // standard simulations (useful when debugging)
            SimulationEngine.SIMULATION_STEP_SLEEP_TIME = 0L;
            // run a simulation with the simulation beginning at 0.0 and
            // ending at 512.0
            se.doStandAloneSimulation(0.0, 512.0);
            SimulationReportI sr = se.getSimulatedModel().getFinalReport();
            System.out.println(sr);
            System.exit(0);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
