package telran.drones.api;

public interface ValidationConstants {
	int MAX_DRONE_NUMBER_LENGTH = 100;
	String MISSING_DRONE_NUMBER_MESSAGE = "Missing drone number";
	String ILLEGAL_DRONE_NUMBER_LENGTH_MESSAGE = "Maximal drone number length: " + MAX_DRONE_NUMBER_LENGTH;
	String MISSING_MEDICATION_CODE_MESSAGE = "Missing medication code";
	String MISSING_MODEL_TYPE_MESSAGE = "Missing model type";
	String WRONG_MEDICATION_CODE_MESSAGE = "Wrong medication code: allowed only upper case letters, underscore and numbers";
	String MEDICATION_CODE_PATTERN = "^[A-Z0-9_]+$";
	

}
