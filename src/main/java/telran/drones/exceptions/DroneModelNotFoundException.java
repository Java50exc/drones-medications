package telran.drones.exceptions;

import static telran.drones.api.ServiceExceptionMessages.*;

@SuppressWarnings("serial")
public class DroneModelNotFoundException extends NotFoundException {

	public DroneModelNotFoundException() {
		super(MODEL_NOT_FOUND_MESSAGE);

	}

}
