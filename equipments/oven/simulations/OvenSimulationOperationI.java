package equipments.oven.simulations;

import equipments.oven.Oven.OvenMode;
import equipments.oven.Oven.OvenState;

public interface OvenSimulationOperationI {


    public OvenState getState();
    public void setState(OvenState state);

    public OvenMode getMode();
    public void setMode(OvenMode mode);

    public void setTargetTemperature(double temperature);

    public void setCurrentPowerLevel(double power);

    public void setDoorOpen(boolean open);
}
