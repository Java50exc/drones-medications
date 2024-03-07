package telran.drones.exceptions;
import static telran.drones.api.ServiceExceptionMessages.*;

@SuppressWarnings("serial")
public class DroneIllegalStateException extends IllegalStateException {
	public DroneIllegalStateException() {
		super(DRONE_ALREADY_EXISTS_MESSAGE);
	}

}
