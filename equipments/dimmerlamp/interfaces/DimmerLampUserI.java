package equipments.dimmerlamp.interfaces;

public interface DimmerLampUserI {

    void switchOn() throws Exception;
    void switchOff() throws Exception;

    boolean isOn() throws Exception;


}
