package equipments.dimmerlamp;

import equipments.dimmerlamp.connections.DimmerLampInboundPort;
import fr.sorbonne_u.alasca.physical_data.Measure;
import fr.sorbonne_u.alasca.physical_data.MeasurementUnit;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.exceptions.AssertionChecking;
import fr.sorbonne_u.exceptions.PostconditionException;
import fr.sorbonne_u.exceptions.PreconditionException;

@OfferedInterfaces(offered={DimmerLampCI.class})
public class DimmerLamp
extends AbstractComponent
implements DimmerLampI {

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

    protected DimmerLampInboundPort inbound;

    protected DimmerLamp(
            String ReflectionInboundPortURI,
            String LampInboundPortURI
    ) throws Exception {
        super(ReflectionInboundPortURI, NUMBER_THREADS, NUMBER_SCHEDULABLE_THREADS);

        assert LampInboundPortURI != null : new PreconditionException("LampInboundPortURI == null");

        // TODO demander par rapport à la fonction initialise
        this.state = LampState.OFF;
        this.power_variation = BASE_POWER_VARIATION;

        this.inbound = new DimmerLampInboundPort(LampInboundPortURI, this);
        this.inbound.publishPort();

        assert DimmerLamp.invariants(this)
                : new PostconditionException("DimmerLamp invariants are not respected");
        assert DimmerLamp.implementationInvariants(this)
                : new PostconditionException("DimmerLamp implementations invariants are not respected");
    }

    protected DimmerLamp() throws Exception {
        // TODO might have to change the number of threads to 2
        this(BASE_REFLECTION_INBOUND_PORT_URI, BASE_USER_INBOUND_PORT_URI);
    }

    @Override
    public void switchOn() throws Exception {
        assert this.state == LampState.OFF : new PreconditionException("Lamp is already on");

        assert this.state == LampState.OFF : new PostconditionException("Lamp is off");
    }

    @Override
    public void switchOff() throws Exception {
        assert this.state == LampState.ON : new PreconditionException("Lamp is already off");

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

    public Measure<Integer> getCurrentPowerLevel() throws Exception {
        return new Measure<>(this.power_variation.getData(), MeasurementUnit.WATTS);
    }



    public void shutdown() throws ComponentShutdownException {
        try {
            this.inbound.unpublishPort();
        } catch (Exception e) {
            throw new ComponentShutdownException(e) ;
        }
        super.shutdown();
    }

}
