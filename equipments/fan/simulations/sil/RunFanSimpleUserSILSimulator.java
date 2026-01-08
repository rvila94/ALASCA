package equipments.fan.simulations.sil;

import equipments.fan.simulations.FanCoupledModel;
import equipments.fan.simulations.FanElectricityModel;
import equipments.fan.simulations.FanSimpleUserModel;
import equipments.fan.simulations.FanSimulationConfigurationI;
import equipments.fan.simulations.events.SetHighFan;
import equipments.fan.simulations.events.SetLowFan;
import equipments.fan.simulations.events.SetMediumFan;
import equipments.fan.simulations.events.SwitchOffFan;
import equipments.fan.simulations.events.SwitchOnFan;
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
 * The class <code>RunFanUnitarySILSimulator</code> is the main class
 * used to run real time simulations on the software-in-the-loop models of the
 * fan in isolation based on test scenarios.
 * 
 * <p><strong>Description</strong></p>
 *
 * <p><strong>Implementation Invariants</strong></p>
 *
 ** <pre>
 * invariant	{@code true}	// no more invariant
 * </pre>
 * 
 * <p>Created on : 2026-01-03</p>
 *
 * @author    <a href="mailto:Rodrigo.Vila@etu.sorbonne-universite.fr">Rodrigo Vila</a>
 * @author    <a href="mailto:Damien.Ribeiro@etu.sorbonne-universite.fr">Damien Ribeiro</a>
 */
public class RunFanSimpleUserSILSimulator {

    // -------------------------------------------------------------------------
    // Constants
    // -------------------------------------------------------------------------

    /** the acceleration factor used in the real time MIL simulations.	 	*/
    public static final double		ACCELERATION_FACTOR = 3600.0;

    public static final Time START_TIME =
            new Time(0., FanSimulationConfigurationI.TIME_UNIT);
    public static final Duration SIMULATION_DURATION =
            new Duration(0.6, FanSimulationConfigurationI.TIME_UNIT);

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
                    FanElectricityModel.URI,
                    RTAtomicHIOA_Descriptor.create(
                            FanElectricityModel.class,
                            FanElectricityModel.URI,
                            FanSimulationConfigurationI.TIME_UNIT,
                            null,
                            ACCELERATION_FACTOR));
            // for atomic model, we use an AtomicModelDescriptor
            atomicModelDescriptors.put(
                    FanStateModel.URI,
                    RTAtomicModelDescriptor.create(
                            FanStateModel.class,
                            FanStateModel.URI,
                            FanSimulationConfigurationI.TIME_UNIT,
                            null,
                            ACCELERATION_FACTOR));
            atomicModelDescriptors.put(
                    FanSimpleUserModel.URI,
                    RTAtomicModelDescriptor.create(
                    		FanSimpleUserModel.class,
                    		FanSimpleUserModel.URI,
                            FanSimulationConfigurationI.TIME_UNIT,
                            null,
                            ACCELERATION_FACTOR));

            Map<String, CoupledModelDescriptor> coupledModelDescriptors =
                    new HashMap<>();

            // the set of submodels of the coupled model, given by their URIs
            Set<String> submodels = new HashSet<>();
            submodels.add(FanStateModel.URI);
            submodels.add(FanElectricityModel.URI);
            submodels.add(FanSimpleUserModel.URI);

            Map<EventSource, EventSink[]> connections =
                    new HashMap<>();

            add_simple_connection(
                    connections, SwitchOnFan.class,
                    FanStateModel.URI, FanElectricityModel.URI);
            add_simple_connection(
                    connections, SwitchOffFan.class,
                    FanStateModel.URI, FanElectricityModel.URI);
            add_simple_connection(
                    connections, SetHighFan.class,
                    FanStateModel.URI, FanElectricityModel.URI);
            add_simple_connection(
                    connections, SetMediumFan.class,
                    FanStateModel.URI, FanElectricityModel.URI);
            add_simple_connection(
                    connections, SetLowFan.class,
                    FanStateModel.URI, FanElectricityModel.URI);
            
            add_simple_connection(
                    connections, SwitchOnFan.class,
                    FanSimpleUserModel.URI, FanStateModel.URI);
            add_simple_connection(
                    connections, SwitchOffFan.class,
                    FanSimpleUserModel.URI, FanStateModel.URI);
            add_simple_connection(
                    connections, SetHighFan.class,
                    FanSimpleUserModel.URI, FanStateModel.URI);
            add_simple_connection(
                    connections, SetMediumFan.class,
                    FanSimpleUserModel.URI, FanStateModel.URI);
            add_simple_connection(
                    connections, SetLowFan.class,
                    FanSimpleUserModel.URI, FanStateModel.URI);
            
            // coupled model descriptor
            coupledModelDescriptors.put(
                    FanCoupledModel.URI,
                    new RTCoupledModelDescriptor(
                            FanCoupledModel.class,
                            FanCoupledModel.URI,
                            submodels,
                            null,
                            null,
                            connections,
                            null,
                            ACCELERATION_FACTOR));

            // simulation architecture
            ArchitectureI architecture =
                    new RTArchitecture(
                            FanCoupledModel.URI,
                            atomicModelDescriptors,
                            coupledModelDescriptors,
                            FanSimulationConfigurationI.TIME_UNIT);
            // create the simulator from the simulation architecture
            SimulatorI se = architecture.constructSimulator();
            // this add additional time at each simulation step in
            // standard simulations (useful when debugging)
            SimulationEngine.SIMULATION_STEP_SLEEP_TIME = 0L;

            long realTimeStart = System.currentTimeMillis() + 200;
            se.startRTSimulation(realTimeStart, START_TIME.getSimulatedTime(), SIMULATION_DURATION.getSimulatedDuration());
            long executionDuration =
                    new Double(
                            FanSimulationConfigurationI.TIME_UNIT.toMillis(1)
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
