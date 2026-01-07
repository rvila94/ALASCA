package equipments.dimmerlamp.simulations.sil;

import equipments.dimmerlamp.mil.*;
import equipments.dimmerlamp.simulations.DimmerLampCoupledModel;
import equipments.dimmerlamp.simulations.DimmerLampElectricityModel;
import equipments.dimmerlamp.simulations.DimmerLampSimulationConfigurationI;
import equipments.dimmerlamp.simulations.DimmerLampUserModel;
import equipments.dimmerlamp.simulations.events.SetPowerLampEvent;
import equipments.dimmerlamp.simulations.events.SwitchOffLampEvent;
import equipments.dimmerlamp.simulations.events.SwitchOnLampEvent;
import fr.sorbonne_u.components.hem2025e2.equipments.hairdryer.mil.HairDryerSimulationConfigurationI;
import fr.sorbonne_u.devs_simulation.architectures.ArchitectureI;
import fr.sorbonne_u.devs_simulation.architectures.RTArchitecture;
import fr.sorbonne_u.devs_simulation.hioa.architectures.RTAtomicHIOA_Descriptor;
import fr.sorbonne_u.devs_simulation.models.architectures.AbstractAtomicModelDescriptor;
import fr.sorbonne_u.devs_simulation.models.architectures.CoupledModelDescriptor;
import fr.sorbonne_u.devs_simulation.models.architectures.RTAtomicModelDescriptor;
import fr.sorbonne_u.devs_simulation.models.architectures.RTCoupledModelDescriptor;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
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
 * The class <code>equipments.dimmerlamp.simulations.sil.RunDimmerLampSimpleUserSILSimulator</code>.
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
public class RunDimmerLampSimpleUserSILSimulator {

    // -------------------------------------------------------------------------
    // Constants
    // -------------------------------------------------------------------------

    /** the acceleration factor used in the real time MIL simulations.	 	*/
    public static final double		ACCELERATION_FACTOR = 3600.0;

    public static final Time START_TIME =
            new Time(0., DimmerLampSimulationConfigurationI.TIME_UNIT);
    public static final Duration SIMULATION_DURATION =
            new Duration(0.5, DimmerLampSimulationConfigurationI.TIME_UNIT);

    // -------------------------------------------------------------------------
    // Methods
    // -------------------------------------------------------------------------

    protected static void add_simple_connection(
            Map<EventSource, EventSink[]> map,
            Class <? extends EventI> eventType,
            String source_uri,
            String dest_uri) {
        final EventSource source =
                new EventSource(source_uri, eventType);
        final EventSink sink =
                new EventSink(dest_uri, eventType);

        map.put(source, new EventSink[] { sink });
    }

    public static void	main(String[] args) {
        Time.setPrintPrecision(4);
        Duration.setPrintPrecision(4);

        try {

            Map<String, AbstractAtomicModelDescriptor> atomicModelDescriptors =
                    new HashMap<>();
            atomicModelDescriptors.put(
                    DimmerLampElectricityModel.URI,
                    RTAtomicHIOA_Descriptor.create(
                            DimmerLampElectricityModel.class,
                            DimmerLampElectricityModel.URI,
                            DimmerLampSimulationConfigurationI.TIME_UNIT,
                            null,
                            ACCELERATION_FACTOR));
            // for atomic model, we use an AtomicModelDescriptor
            atomicModelDescriptors.put(
                    DimmerLampStateModel.URI,
                    RTAtomicModelDescriptor.create(
                            DimmerLampStateModel.class,
                            DimmerLampStateModel.URI,
                            DimmerLampSimulationConfigurationI.TIME_UNIT,
                            null,
                            ACCELERATION_FACTOR));
            atomicModelDescriptors.put(
                    DimmerLampUserModel.URI,
                    RTAtomicModelDescriptor.create(
                            DimmerLampUserModel.class,
                            DimmerLampUserModel.URI,
                            DimmerLampSimulationConfigurationI.TIME_UNIT,
                            null,
                            ACCELERATION_FACTOR));

            Map<String, CoupledModelDescriptor> coupledModelDescriptors =
                    new HashMap<>();

            // the set of submodels of the coupled model, given by their URIs
            Set<String> submodels = new HashSet<>();
            submodels.add(DimmerLampStateModel.URI);
            submodels.add(DimmerLampElectricityModel.URI);
            submodels.add(DimmerLampUserModel.URI);

            Map<EventSource, EventSink[]> connections =
                    new HashMap<>();

            add_simple_connection(
                    connections, SwitchOnLampEvent.class,
                    DimmerLampStateModel.URI, DimmerLampElectricityModel.URI);
            add_simple_connection(
                    connections, SwitchOffLampEvent.class,
                    DimmerLampStateModel.URI, DimmerLampElectricityModel.URI);
            add_simple_connection(
                    connections, SetPowerLampEvent.class,
                    DimmerLampStateModel.URI, DimmerLampElectricityModel.URI);

            add_simple_connection(
                    connections, SwitchOnLampEvent.class,
                    DimmerLampUserModel.URI, DimmerLampStateModel.URI);
            add_simple_connection(
                    connections, SwitchOffLampEvent.class,
                    DimmerLampUserModel.URI, DimmerLampStateModel.URI);
            add_simple_connection(
                    connections, SetPowerLampEvent.class,
                    DimmerLampUserModel.URI, DimmerLampStateModel.URI);

            // coupled model descriptor
            coupledModelDescriptors.put(
                    DimmerLampCoupledModel.URI,
                    new RTCoupledModelDescriptor(
                            DimmerLampCoupledModel.class,
                            DimmerLampCoupledModel.URI,
                            submodels,
                            null,
                            null,
                            connections,
                            null,
                            ACCELERATION_FACTOR));

            // simulation architecture
            ArchitectureI architecture =
                    new RTArchitecture(
                            DimmerLampCoupledModel.URI,
                            atomicModelDescriptors,
                            coupledModelDescriptors,
                            DimmerLampSimulationConfigurationI.TIME_UNIT);
            // create the simulator from the simulation architecture
            SimulatorI se = architecture.constructSimulator();
            // this add additional time at each simulation step in
            // standard simulations (useful when debugging)
            SimulationEngine.SIMULATION_STEP_SLEEP_TIME = 0L;

            long realTimeStart = System.currentTimeMillis() + 200;
            se.startRTSimulation(realTimeStart, START_TIME.getSimulatedTime(), SIMULATION_DURATION.getSimulatedDuration());
            long executionDuration =
                    new Double(
                            HairDryerSimulationConfigurationI.TIME_UNIT.toMillis(1)
                                    * (SIMULATION_DURATION.getSimulatedDuration() / ACCELERATION_FACTOR))
                            .longValue();
            Thread.sleep(executionDuration + 2000L);
            SimulationReportI sr = se.getSimulatedModel().getFinalReport();
            System.out.println(sr);

            System.exit(0);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
