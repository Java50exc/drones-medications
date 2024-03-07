package telran.drones.dto;

import jakarta.validation.constraints.*;
import static telran.drones.api.ValidationConstants.*;

import java.util.Objects;

public record DroneDto(
		@NotBlank(message = MISSING_DRONE_NUMBER_MESSAGE) @Size(max= MAX_DRONE_NUMBER_LENGTH, message = ILLEGAL_DRONE_NUMBER_LENGTH_MESSAGE) String number, 
		@NotNull(message = MISSING_MODEL_TYPE_MESSAGE) ModelType modelType) {

	@Override
	public int hashCode() {
		return Objects.hash(modelType, number);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DroneDto other = (DroneDto) obj;
		return modelType == other.modelType && Objects.equals(number, other.number);
	}
	
	

}
