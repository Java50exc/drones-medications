package telran.drones.exceptions;

import static telran.drones.api.ServiceExceptionMessages.*;

import telran.exceptions.NotFoundException;

@SuppressWarnings("serial")
public class MedicationNotFoundException extends NotFoundException {

	public MedicationNotFoundException() {
		super(MEDICATION_NOT_FOUND_MESSAGE);
	}

}
