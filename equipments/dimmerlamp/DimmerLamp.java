package equipments.dimmerlamp;

import equipments.dimmerlamp.connections.DimmerLampExternalJava4InboundPort;
import equipments.dimmerlamp.connections.DimmerLampUserInboundPort;
import equipments.dimmerlamp.interfaces.DimmerLampUserCI;
import equipments.dimmerlamp.interfaces.DimmerLampUserI;
import equipments.dimmerlamp.interfaces.DimmerLampExternalI;
import equipments.dimmerlamp.interfaces.DimmerLampExternalJava4CI;
import equipments.dimmerlamp.simulations.events.SetPowerLampEvent;
import equipments.dimmerlamp.simulations.events.SwitchOffLampEvent;
import equipments.dimmerlamp.simulations.events.SwitchOnLampEvent;
import equipments.hem.RegistrationOutboundPort;
import fr.sorbonne_u.alasca.physical_data.Measure;
import fr.sorbonne_u.alasca.physical_data.MeasurementUnit;
import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import fr.sorbonne_u.components.annotations.RequiredInterfaces;
import fr.sorbonne_u.components.cyphy.AbstractCyPhyComponent;
import fr.sorbonne_u.components.cyphy.ExecutionMode;
import fr.sorbonne_u.components.cyphy.annotations.LocalArchitecture;
import fr.sorbonne_u.components.cyphy.annotations.SIL_Simulation_Architectures;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import fr.sorbonne_u.components.hem2025.bases.RegistrationCI;
import fr.sorbonne_u.components.utils.tests.TestScenario;
import fr.sorbonne_u.devs_simulation.models.annotations.ModelExternalEvents;
import fr.sorbonne_u.exceptions.AssertionChecking;
import fr.sorbonne_u.exceptions.PostconditionException;
import fr.sorbonne_u.exceptions.PreconditionException;

import java.io.File;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

@SIL_Simulation_Architectures({
        @LocalArchitecture(
                uri = "silUnitTests",
                rootModelURI = "DIMMER_LAMP_COUPLED_MODEL",
                simulatedTimeUnit = TimeUnit.HOURS,
                externalEvents = @ModelExternalEvents()
        ),
        @LocalArchitecture(
                uri = "silIntegrationTests",
                rootModelURI = "DIMMER-LAMP-STATE-MODEL-URI",
                simulatedTimeUnit = TimeUnit.HOURS,
                externalEvents = @ModelExternalEvents(
                        exported = {
                                SwitchOnLampEvent.class,
                                SwitchOffLampEvent.class,
                                SetPowerLampEvent.class
                        }
                )
        )}
)
@RequiredInterfaces(required = {RegistrationCI.class})
@OfferedInterfaces(offered = {DimmerLampUserCI.class, DimmerLampExternalJava4CI.class})
public class DimmerLamp
extends AbstractCyPhyComponent
implements DimmerLampUserI, DimmerLampExternalI {

    // -------------------------------------------------------------------------
    // Constants
    // -------------------------------------------------------------------------

    public static boolean VERBOSE = false;

    /** when tracing, x coordinate of the window relative position.			*/
    public static int				X_RELATIVE_POSITION = 0;
    /** when tracing, y coordinate of the window relative position.			*/
    public static int				Y_RELATIVE_POSITION = 0;


    public static final String EQUIPMENT_UID = "1A1003584";

    protected static final File PATH_TO_CONNECTOR_DESCRIPTOR = new File("src/connectorGenerator/dimmerlamp-descriptor.xml");

    // Maximum value for the variator
    public static final Measure<Double> MAX_POWER_VARIATION = new Measure<>(100., MeasurementUnit.RAW);
    // Minimum value for the variator
    public static final Measure<Double> MIN_POWER_VARIATION = new Measure<>(0., MeasurementUnit.RAW);
    public static final Measure<Double> BASE_POWER_VARIATION = new Measure<>(50., MeasurementUnit.RAW);
    protected static final Measure<Double> FAKE_POWER = new Measure<>(100., MeasurementUnit.WATTS);

    public static final String BASE_REFLECTION_INBOUND_PORT_URI = "REFLECTION-DIMMER-LAMP-URI";
    public static final String BASE_USER_INBOUND_PORT_URI = "USER-DIMMER-LAMP-URI";
    public  static final String BASE_EXTERNAL_INBOUND_PORT_URI = "EXTERNAL-DIMMER-LAMP-URI";
    // TODO potential problem here
    protected static final int NUMBER_THREADS = 3;
    protected static final int NUMBER_SCHEDULABLE_THREADS = 3;

    // -------------------------------------------------------------------------
    // Variables
    // -------------------------------------------------------------------------

    private boolean isUnitTest;

    // varies between 0 and 100
    protected Measure<Double> power_variation;
    protected LampState state;

    protected DimmerLampUserInboundPort userInbound;
    protected DimmerLampExternalJava4InboundPort externalInbound;

    /*
        Registration
    */

    protected String registrationHEMURI;
    protected String registrationHEMConnectorClassName;
    protected RegistrationOutboundPort registrationPort;

    protected static boolean implementationInvariants(DimmerLamp lamp) {

        assert lamp != null : new PreconditionException("lamp == null");

        boolean invariant_check = true;
        invariant_check &= AssertionChecking.checkInvariant(
            MAX_POWER_VARIATION.getData() >= 0 &&
                    MAX_POWER_VARIATION.getData() <= 100,
                DimmerLamp.class, lamp,
                "MAX_POWER_VARIATION < 0 || MAX_POWER_VARIATION > 100"
        );
        invariant_check &= AssertionChecking.checkInvariant(
          MIN_POWER_VARIATION.getData() >= 0 &&
          MIN_POWER_VARIATION.getData() <= 100,
          DimmerLamp.class, lamp,
                      "MIN_POWER_VARIATION < 0 || MIN_POWER_VARIATION > 100"
        );
        invariant_check &= AssertionChecking.checkInvariant(
          MIN_POWER_VARIATION.getData() <= MAX_POWER_VARIATION.getData(),
          DimmerLamp.class, lamp,
          "MIN_POWER_VARIATION > MAX_POWER_VARIATION"
        );

        double lamp_variation = lamp.power_variation.getData();
        invariant_check &= AssertionChecking.checkInvariant(
                MIN_POWER_VARIATION.getData() <= lamp_variation
                        && lamp_variation <= MAX_POWER_VARIATION.getData(),
                DimmerLamp.class, lamp,
                "MIN_POWER_VARIATION.getData() > lamp_variation" +
                        " || lamp_variation < MAX_POWER_VARIATION.getData()"
        );

        invariant_check &= AssertionChecking.checkInvariant(
          lamp.state != null,
          DimmerLamp.class, lamp,
          "lamp == null"
        );

        return invariant_check;
    }

    protected static boolean invariants(DimmerLamp lamp) {

        assert lamp != null : new PreconditionException("lamp == null");

        boolean invariant_check = true;
        invariant_check &= AssertionChecking.checkInvariant(
                BASE_REFLECTION_INBOUND_PORT_URI != null &&
                        !BASE_REFLECTION_INBOUND_PORT_URI.isEmpty(),
                DimmerLamp.class, lamp,
                "BASE_REFLECTION_INBOUND_PORT_URI == null || "
                        + "BASE_REFLECTION_INBOUND_PORT_URI.isEmpty()"
        );

        invariant_check &= AssertionChecking.checkInvariant(
                BASE_USER_INBOUND_PORT_URI != null &&
                        !BASE_USER_INBOUND_PORT_URI.isEmpty(),
                DimmerLamp.class, lamp,
                "BASE_REFLECTION_INBOUND_PORT_URI == null || "
                        + "BASE_REFLECTION_INBOUND_PORT_URI.isEmpty()"
        );

        return invariant_check;
    }

    protected DimmerLamp(
            String reflectionInboundPortURI,
            String userInboundPortURI,
            String externalInboundPortURI,
            String registrationHEMURI,
            String registrationHemCcName,
            ExecutionMode mode,
            String clockURI
    ) throws Exception {
        super(reflectionInboundPortURI, NUMBER_THREADS, NUMBER_SCHEDULABLE_THREADS, mode, clockURI, null);

        assert userInboundPortURI != null : new PreconditionException("userInboundPortURI == null");

        this.state = LampState.OFF;
        this.power_variation = BASE_POWER_VARIATION;

        this.userInbound = new DimmerLampUserInboundPort(userInboundPortURI, this);
        this.userInbound.publishPort();

        this.externalInbound = new DimmerLampExternalJava4InboundPort(externalInboundPortURI, this);
        this.externalInbound.publishPort();

        this.registrationPort = new RegistrationOutboundPort(this);
        this.registrationPort.publishPort();
        this.registrationHEMURI = registrationHEMURI;
        this.registrationHEMConnectorClassName = registrationHemCcName;

        this.isUnitTest = false;

        if (DimmerLamp.VERBOSE) {
            this.tracer.get().setTitle("DimmerLamp Component");
            this.tracer.get().setRelativePosition(X_RELATIVE_POSITION, Y_RELATIVE_POSITION);

            this.toggleTracing();
        }

        assert DimmerLamp.invariants(this)
                : new PostconditionException("DimmerLamp invariants are not respected");
        assert DimmerLamp.implementationInvariants(this)
                : new PostconditionException("DimmerLamp implementations invariants are not respected");
    }

    protected DimmerLamp(
            String reflectionInboundPortURI,
            String userInboundPortURI,
            String externalInboundPortURI,
            String registrationHEMURI,
            String registrationHemCcName
    ) throws Exception {
        super(reflectionInboundPortURI, NUMBER_THREADS, NUMBER_SCHEDULABLE_THREADS);

        assert userInboundPortURI != null : new PreconditionException("userInboundPortURI == null");

        this.state = LampState.OFF;
        this.power_variation = BASE_POWER_VARIATION;

        this.userInbound = new DimmerLampUserInboundPort(userInboundPortURI, this);
        this.userInbound.publishPort();

        this.externalInbound = new DimmerLampExternalJava4InboundPort(externalInboundPortURI, this);
        this.externalInbound.publishPort();

        this.registrationPort = new RegistrationOutboundPort(this);
        this.registrationPort.publishPort();
        this.registrationHEMURI = registrationHEMURI;
        this.registrationHEMConnectorClassName = registrationHemCcName;

        this.isUnitTest = false;

        if (DimmerLamp.VERBOSE) {
            this.tracer.get().setTitle("DimmerLamp Component");
            this.tracer.get().setRelativePosition(X_RELATIVE_POSITION, Y_RELATIVE_POSITION);

            this.toggleTracing();
        }

        assert DimmerLamp.invariants(this)
                : new PostconditionException("DimmerLamp invariants are not respected");
        assert DimmerLamp.implementationInvariants(this)
                : new PostconditionException("DimmerLamp implementations invariants are not respected");
    }

    protected DimmerLamp(String registrationHEMURI,
                         String registrationHemCcName) throws Exception {
        this(BASE_REFLECTION_INBOUND_PORT_URI,
                BASE_USER_INBOUND_PORT_URI,
                BASE_EXTERNAL_INBOUND_PORT_URI,
                registrationHEMURI,
                registrationHemCcName);
    }

//    protected DimmerLamp(String registrationHEMURI,
//                         String registrationHemCcName,
//                         ExecutionMode executionMode) throws Exception {
//        ExecutionMode mode = AssertionChecking.assertTrueAndReturnOrThrow(
//                executionMode != null && executionMode.isTestWithoutSimulation(),
//                executionMode,
//                () -> new PreconditionException("executionMode == null || ! executionMode.isTestWithoutSimulation()")
//        );
//
//        this(registrationHEMURI, registrationHemCcName, mode);
//    }

//    protected DimmerLamp(String reflectionInboundURI,
//                         String registrationHEMURI,
//                         String registrationHemCcName,
//                         ExecutionMode executionMode) throws Exception {
//        super(reflectionInboundURI,
//                NUMBER_THREADS,
//                NUMBER_SCHEDULABLE_THREADS,
//                executionMode, )
//    }

    protected DimmerLamp(String registrationHEMURI,
                         String registrationHemCcName,
                         ExecutionMode mode,
                         TestScenario testScenario,
                         double accelerationFactor) throws Exception {
        super(BASE_REFLECTION_INBOUND_PORT_URI, NUMBER_THREADS, NUMBER_SCHEDULABLE_THREADS, mode,
                AssertionChecking.assertTrueAndReturnOrThrow(
                        testScenario != null,
                        testScenario.getClockURI(),
                        () -> new PreconditionException("testScenario != null")),
                testScenario,
                ((Supplier<Set<String>>)() ->
                { HashSet<String> hs = new HashSet<>();
                    hs.add(DimmerLampCyPhy.UNIT_TEST_URI);
                    hs.add(DimmerLampCyPhy.INTEGRATION_TEST_URI);
                    return hs;
                }).get(), accelerationFactor);

        this.state = LampState.OFF;
        this.power_variation = BASE_POWER_VARIATION;

        this.userInbound = new DimmerLampUserInboundPort(BASE_USER_INBOUND_PORT_URI, this);
        this.userInbound.publishPort();

        this.externalInbound = new DimmerLampExternalJava4InboundPort(BASE_EXTERNAL_INBOUND_PORT_URI, this);
        this.externalInbound.publishPort();

        this.isUnitTest = false;

        this.registrationPort = new RegistrationOutboundPort(this);
        this.registrationPort.publishPort();
        this.registrationHEMURI = registrationHEMURI;
        this.registrationHEMConnectorClassName = registrationHemCcName;

        if (DimmerLamp.VERBOSE) {
            this.tracer.get().setTitle("DimmerLamp Component");
            this.tracer.get().setRelativePosition(X_RELATIVE_POSITION, Y_RELATIVE_POSITION);

            this.toggleTracing();
        }

        assert DimmerLamp.invariants(this)
                : new PostconditionException("DimmerLamp invariants are not respected");
        assert DimmerLamp.implementationInvariants(this)
                : new PostconditionException("DimmerLamp implementations invariants are not respected");

    }

    protected DimmerLamp() throws Exception {
        super(BASE_REFLECTION_INBOUND_PORT_URI, NUMBER_THREADS, NUMBER_SCHEDULABLE_THREADS);

        this.state = LampState.OFF;
        this.power_variation = BASE_POWER_VARIATION;

        this.userInbound = new DimmerLampUserInboundPort(BASE_USER_INBOUND_PORT_URI, this);
        this.userInbound.publishPort();

        this.externalInbound = new DimmerLampExternalJava4InboundPort(BASE_EXTERNAL_INBOUND_PORT_URI, this);
        this.externalInbound.publishPort();

        this.isUnitTest = true;

        if (DimmerLamp.VERBOSE) {
            this.tracer.get().setTitle("DimmerLamp Component");
            this.tracer.get().setRelativePosition(X_RELATIVE_POSITION, Y_RELATIVE_POSITION);

            this.toggleTracing();
        }

        assert DimmerLamp.invariants(this)
                : new PostconditionException("DimmerLamp invariants are not respected");
        assert DimmerLamp.implementationInvariants(this)
                : new PostconditionException("DimmerLamp implementations invariants are not respected");


    }

    @Override
    public void start() throws ComponentStartException {
        super.start();
        try {
            if (!this.isUnitTest)
                this.doPortConnection(
                    this.registrationPort.getPortURI(),
                        this.registrationHEMURI,
                        this.registrationHEMConnectorClassName
                );
        } catch (Exception e) {
            new ComponentStartException(e);
        }
    }

    @Override
    public void switchOn() throws Exception {
        assert this.state.isOff() : new PreconditionException("Lamp is already on");


        if (DimmerLamp.VERBOSE) {
            this.logMessage("The dimmer lamp is switched on.\n");
        }

        this.state = LampState.ON;

        if (! this.isUnitTest) {

            boolean registered = this.registrationPort.register(
                    DimmerLamp.EQUIPMENT_UID,
                    this.externalInbound.getPortURI(),
                    DimmerLamp.PATH_TO_CONNECTOR_DESCRIPTOR.getAbsolutePath());

            if (DimmerLamp.VERBOSE) {
                this.logMessage("The Dimmer lamp successfully completed the registration: " + registered + ".\n");
            }

        }

        assert this.state.isOn(): new PostconditionException("Lamp is off");


    }

    @Override
    public void switchOff() throws Exception {
        assert this.state.isOn() : new PreconditionException("Lamp is already off");

        this.state = LampState.OFF;

        if (! this.isUnitTest) {
            this.registrationPort.unregister(DimmerLamp.EQUIPMENT_UID);

            this.logMessage("The dimmer lamp unregistered from the hem\n");
        }

        assert ! this.state.isOn() : new PostconditionException("Lamp is on");
    }

    @Override
    public boolean isOn() throws Exception {
        boolean res = this.state.isOn();

        if (DimmerLamp.VERBOSE) {
            this.logMessage("The dimmer lamp indicates if it is on: " + res + "\n");
        }

        return res;
    }

    @Override
    public void setPower(Measure<Double> variation) throws Exception {
        assert this.state.isOn() : new PreconditionException("Lamp is off");
        assert 0 <= variation.getData() && variation.getData() <= 100 :
                new PreconditionException("0 > variation.getData() || variation.getData() > 100");

        if (DimmerLamp.VERBOSE) {
            this.logMessage("The dimmer lamp sets its power to: " + variation + ".\n");
        }

        this.power_variation = variation;
    }

    @Override
    public Measure<Double> getCurrentPowerLevel() throws Exception {

        assert this.isOn() :
                new PreconditionException("!isOn()");

        Measure<Double> result = new Measure<>(this.power_variation.getData(), MeasurementUnit.WATTS);

        if (DimmerLamp.VERBOSE) {
            this.logMessage("The dimmer lamp returns its current power level: " + result + ".\n");
        }

        return result;
    }

    @Override
    public Measure<Double> getMaxPowerLevel() throws Exception {

        if (DimmerLamp.VERBOSE) {
            this.logMessage("The dimmer lamp returns its maximum power level: "
                    + MAX_POWER_VARIATION + ".\n");
        }

        return MAX_POWER_VARIATION;
    }

    @Override
    public void shutdown() throws ComponentShutdownException {
        try {
            this.userInbound.unpublishPort();
            this.externalInbound.unpublishPort();

            if (!this.isUnitTest) {
                this.registrationPort.unpublishPort();
            }
        } catch (Exception e) {
            throw new ComponentShutdownException(e) ;
        }
        super.shutdown();
    }

    @Override
    public synchronized void finalise() throws Exception {
        if (! this.isUnitTest) {
            this.doPortDisconnection(this.registrationPort.getPortURI());
        }
        super.finalise();
    }

}
