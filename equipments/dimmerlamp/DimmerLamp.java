package equipments.dimmerlamp;

import equipments.dimmerlamp.connections.DimmerLampExternalJava4InboundPort;
import equipments.dimmerlamp.connections.DimmerLampUserInboundPort;
import equipments.dimmerlamp.interfaces.DimmerLampExternalI;
import equipments.dimmerlamp.interfaces.DimmerLampExternalJava4CI;
import equipments.dimmerlamp.interfaces.DimmerLampUserCI;
import equipments.dimmerlamp.interfaces.DimmerLampUserI;
import fr.sorbonne_u.alasca.physical_data.Measure;
import fr.sorbonne_u.alasca.physical_data.MeasurementUnit;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import fr.sorbonne_u.components.annotations.RequiredInterfaces;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.hem2025.bases.RegistrationCI;
import fr.sorbonne_u.exceptions.AssertionChecking;
import fr.sorbonne_u.exceptions.PostconditionException;
import fr.sorbonne_u.exceptions.PreconditionException;

@RequiredInterfaces(required = {RegistrationCI.class})
@OfferedInterfaces(offered = {DimmerLampUserCI.class, DimmerLampExternalJava4CI.class})
public class DimmerLamp
extends AbstractComponent
implements DimmerLampUserI, DimmerLampExternalI {

    protected enum LampState {
        OFF,
        ON
    }

    public static boolean VERBOSE = false;

    // Maximum value for the variator
    protected static final Measure<Integer> MAX_POWER_VARIATION = new Measure<>(100, MeasurementUnit.RAW);
    // Minimum value for the variator
    protected static final Measure<Integer> MIN_POWER_VARIATION = new Measure<>(0, MeasurementUnit.RAW);
    public static final Measure<Integer> BASE_POWER_VARIATION = new Measure<>(50, MeasurementUnit.RAW);
    protected static final Measure<Integer> FAKE_POWER = new Measure<>(100, MeasurementUnit.WATTS);

    protected static final String BASE_REFLECTION_INBOUND_PORT_URI = "REFLECTION-DIMMER-LAMP-URI";
    protected static final String BASE_USER_INBOUND_PORT_URI = "USER-DIMMER-LAMP-URI";
    protected static final String BASE_EXTERNAL_INBOUND_PORT_URI = "EXTERNAL-DIMMER-LAMP-URI";
    protected static final int NUMBER_THREADS = 1;
    protected static final int NUMBER_SCHEDULABLE_THREADS = 0;

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

        int lamp_variation = lamp.power_variation.getData();
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

    // varies between 0 and 100
    protected Measure<Integer> power_variation;
    protected LampState state;

    protected DimmerLampUserInboundPort userInbound;
    protected DimmerLampExternalJava4InboundPort externalInbound;

    protected DimmerLamp(
            String reflectionInboundPortURI,
            String userInboundPortURI,
            String externalInboundPortURI
    ) throws Exception {
        super(reflectionInboundPortURI, NUMBER_THREADS, NUMBER_SCHEDULABLE_THREADS);

        assert userInboundPortURI != null : new PreconditionException("userInboundPortURI == null");

        // TODO demander par rapport à la fonction initialise
        this.state = LampState.OFF;
        this.power_variation = BASE_POWER_VARIATION;

        this.userInbound = new DimmerLampUserInboundPort(userInboundPortURI, this);
        this.userInbound.publishPort();

        this.externalInbound = new DimmerLampExternalJava4InboundPort(externalInboundPortURI, this);
        this.externalInbound.publishPort();

        assert DimmerLamp.invariants(this)
                : new PostconditionException("DimmerLamp invariants are not respected");
        assert DimmerLamp.implementationInvariants(this)
                : new PostconditionException("DimmerLamp implementations invariants are not respected");
    }

    protected DimmerLamp() throws Exception {
        // TODO might have to change the number of threads to 2
        this(BASE_REFLECTION_INBOUND_PORT_URI, BASE_USER_INBOUND_PORT_URI, BASE_EXTERNAL_INBOUND_PORT_URI);
    }

    @Override
    public void switchOn() throws Exception {
        assert this.state == LampState.OFF : new PreconditionException("Lamp is already on");

        this.state = LampState.ON;

        assert this.state == LampState.ON : new PostconditionException("Lamp is off");
    }

    @Override
    public void switchOff() throws Exception {
        assert this.state == LampState.ON : new PreconditionException("Lamp is already off");

        this.state = LampState.OFF;

        assert this.state == LampState.OFF : new PostconditionException("Lamp is on");
    }

    @Override
    public boolean isOn() throws Exception {
        return this.state == LampState.ON;
    }

    @Override
    public void setVariationPower(Measure<Integer> variation) throws Exception {
        assert this.state == LampState.ON : new PreconditionException("Lamp is off");
        assert 0 <= variation.getData() && variation.getData() <= 100 :
                new PreconditionException("0 > variation.getData() || variation.getData() > 100");

        this.power_variation = variation;
    }

    @Override
    public Measure<Integer> getCurrentPowerLevel() throws Exception {
        return new Measure<>(this.power_variation.getData(), MeasurementUnit.WATTS);
    }


    public void shutdown() throws ComponentShutdownException {
        try {
            this.userInbound.unpublishPort();
            this.externalInbound.unpublishPort();
        } catch (Exception e) {
            throw new ComponentShutdownException(e) ;
        }
        super.shutdown();
    }

}
