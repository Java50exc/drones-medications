package telran.drones.dto;

import static telran.drones.api.ValidationConstants.*;


import jakarta.validation.constraints.*;


public record DroneMedication(
		@NotBlank(message = MISSING_DRONE_NUMBER_MESSAGE) @Size(max= MAX_DRONE_NUMBER_LENGTH, message = ILLEGAL_DRONE_NUMBER_LENGTH_MESSAGE) String droneNumber, 
		@NotBlank(message = MISSING_MEDICATION_CODE_MESSAGE) @Pattern(regexp = MEDICATION_CODE_PATTERN, message = WRONG_MEDICATION_CODE_MESSAGE) String medicationCode) {

}
