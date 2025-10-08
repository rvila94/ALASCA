package equipments.dimmerlamp;

import fr.sorbonne_u.alasca.physical_data.Measure;

public interface DimmerLampI {

    void switchOn() throws Exception;
    void switchOff() throws Exception;
    void setVariationPower(Measure<Integer> variationPower) throws Exception;

    boolean isOn() throws Exception;

    Measure<Integer> getCurrentPowerLevel() throws Exception;


}
