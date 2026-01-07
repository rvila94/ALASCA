package equipments.dimmerlamp.simulations.events;

import equipments.dimmerlamp.DimmerLamp;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

import java.util.concurrent.TimeUnit;

/**
 * The class <code>equipments.dimmerlamp.simulations.events.mil.LampEventPriorityUnitTest</code>.
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
public class LampEventPriorityUnitTest {

    static LampPowerValue power_value1;
    static LampPowerValue power_value2;

    static SetPowerLampEvent power_event1;
    static SetPowerLampEvent power_event2;
    static SwitchOnLampEvent on_event1;
    static SwitchOnLampEvent on_event2;
    static SwitchOffLampEvent off_event1;
    static SwitchOffLampEvent off_event2;

    @BeforeAll
    static void initialise() {
        Time time1 = new Time(1., TimeUnit.HOURS);
        Time time2 = new Time(1., TimeUnit.SECONDS);

        power_value1 = new LampPowerValue(DimmerLamp.MIN_POWER_VARIATION.getData());
        power_value2 = new LampPowerValue(DimmerLamp.MAX_POWER_VARIATION.getData());

        power_event1 = new SetPowerLampEvent(time1, power_value1);
        power_event2 = new SetPowerLampEvent(time2, power_value2);

        on_event1 = new SwitchOnLampEvent(time1);
        on_event2 = new SwitchOnLampEvent(time2);

        off_event1 = new SwitchOffLampEvent(time1);
        off_event2 = new SwitchOffLampEvent(time2);
    }

    @Test
    void PriorityTest() {
        // priority over itself
        Assertions.assertTrue(power_event1.hasPriorityOver(power_event2));
        Assertions.assertTrue(power_event2.hasPriorityOver(power_event1));
        Assertions.assertTrue(on_event1.hasPriorityOver(on_event2));
        Assertions.assertTrue(on_event2.hasPriorityOver(on_event1));
        Assertions.assertTrue(off_event1.hasPriorityOver(off_event2));
        Assertions.assertTrue(off_event2.hasPriorityOver(off_event1));

        // SwitchOnLampEvent events have priority over all other types of events
        Assertions.assertTrue(on_event1.hasPriorityOver(off_event1));
        Assertions.assertTrue(on_event1.hasPriorityOver(off_event2));
        Assertions.assertTrue(on_event1.hasPriorityOver(power_event1));
        Assertions.assertTrue(on_event1.hasPriorityOver(power_event2));
        Assertions.assertTrue(on_event2.hasPriorityOver(off_event1));
        Assertions.assertTrue(on_event2.hasPriorityOver(off_event2));
        Assertions.assertTrue(on_event2.hasPriorityOver(power_event1));
        Assertions.assertTrue(on_event2.hasPriorityOver(power_event2));

        // SetPowerLampEvent events have priority only over SwitchOffLampEvent
        Assertions.assertTrue(power_event1.hasPriorityOver(off_event1));
        Assertions.assertTrue(power_event1.hasPriorityOver(off_event2));
        Assertions.assertFalse(power_event1.hasPriorityOver(on_event1));
        Assertions.assertFalse(power_event1.hasPriorityOver(on_event2));
        Assertions.assertTrue(power_event2.hasPriorityOver(off_event1));
        Assertions.assertTrue(power_event2.hasPriorityOver(off_event2));
        Assertions.assertFalse(power_event2.hasPriorityOver(on_event1));
        Assertions.assertFalse(power_event2.hasPriorityOver(on_event2));

        // SwitchOffLampEvent events have no priority over other types of events
        Assertions.assertFalse(off_event1.hasPriorityOver(on_event1));
        Assertions.assertFalse(off_event1.hasPriorityOver(on_event2));
        Assertions.assertFalse(off_event1.hasPriorityOver(power_event1));
        Assertions.assertFalse(off_event1.hasPriorityOver(power_event2));
        Assertions.assertFalse(off_event2.hasPriorityOver(on_event1));
        Assertions.assertFalse(off_event2.hasPriorityOver(on_event2));
        Assertions.assertFalse(off_event2.hasPriorityOver(power_event1));
        Assertions.assertFalse(off_event2.hasPriorityOver(power_event2));
    }

}
