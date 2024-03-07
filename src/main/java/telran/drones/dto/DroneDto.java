package telran.drones.dto;

import jakarta.validation.constraints.*;
import static telran.drones.api.ValidationConstants.*;

public record DroneDto(
		@NotBlank(message = MISSING_DRONE_NUMBER_MESSAGE) @Size(max= MAX_DRONE_NUMBER_LENGTH, message = ILLEGAL_DRONE_NUMBER_LENGTH_MESSAGE) String number, 
		@NotNull(message = MISSING_MODEL_TYPE_MESSAGE) ModelType modelType) {

}
