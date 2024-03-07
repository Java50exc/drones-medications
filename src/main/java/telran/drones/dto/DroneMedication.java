package telran.drones.dto;

import static telran.drones.api.ValidationConstants.*;

import java.util.Objects;

import jakarta.validation.constraints.*;


public record DroneMedication(
		@NotBlank(message = MISSING_DRONE_NUMBER_MESSAGE) @Size(max= MAX_DRONE_NUMBER_LENGTH, message = ILLEGAL_DRONE_NUMBER_LENGTH_MESSAGE) String droneNumber, 
		@NotBlank(message = MISSING_MEDICATION_CODE_MESSAGE) @Pattern(regexp = MEDICATION_CODE_PATTERN, message = WRONG_MEDICATION_CODE_MESSAGE) String medicationCode) {

	@Override
	public int hashCode() {
		return Objects.hash(droneNumber, medicationCode);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DroneMedication other = (DroneMedication) obj;
		return Objects.equals(droneNumber, other.droneNumber) && Objects.equals(medicationCode, other.medicationCode);
	}
	
	

}
