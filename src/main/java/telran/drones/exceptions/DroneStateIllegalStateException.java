package telran.drones.exceptions;

import static telran.drones.api.ServiceExceptionMessages.*;

@SuppressWarnings("serial")
public class DroneStateIllegalStateException extends IllegalStateException {
	public DroneStateIllegalStateException() {
		super(DRONE_NOT_READY_MESSAGE);
	}

}
