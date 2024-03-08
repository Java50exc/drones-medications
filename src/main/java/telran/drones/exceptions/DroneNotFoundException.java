package telran.drones.exceptions;

import static telran.drones.api.ServiceExceptionMessages.*;

import telran.exceptions.NotFoundException;

@SuppressWarnings("serial")
public class DroneNotFoundException extends NotFoundException {
	public DroneNotFoundException() {
		super(DRONE_NOT_FOUND_MESSAGE);
	}

}
