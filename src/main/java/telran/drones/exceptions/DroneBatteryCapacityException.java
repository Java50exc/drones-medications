package telran.drones.exceptions;

import static telran.drones.api.ServiceExceptionMessages.*;

@SuppressWarnings("serial")
public class DroneBatteryCapacityException extends IllegalStateException {
	public DroneBatteryCapacityException() {
		super(LOW_DRONE_BATTERY_CAPACITY_MESSAGE);
	}

}
