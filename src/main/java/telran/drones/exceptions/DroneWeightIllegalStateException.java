package telran.drones.exceptions;

import static telran.drones.api.ServiceExceptionMessages.*;

@SuppressWarnings("serial")
public class DroneWeightIllegalStateException extends IllegalStateException {
	public DroneWeightIllegalStateException() {
		super(DRONE_WEIGHT_NOT_MATCH_MESSAGE);
	}

}
