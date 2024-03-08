package telran.drones;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static telran.drones.api.DronesValidationErrorMessages.DRONE_NUMBER_WRONG_LENGTH;
import static telran.drones.api.DronesValidationErrorMessages.MAX_DRONE_NUMBER_LENGTH;
import static telran.drones.api.DronesValidationErrorMessages.MISSING_DRONE_NUMBER;

import java.net.URI;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import telran.drones.api.DronesValidationErrorMessages;
import telran.drones.api.ServiceExceptionMessages;
import telran.drones.api.UrlConstants;
import telran.drones.dto.*;
import telran.drones.exceptions.*;
import telran.exceptions.NotFoundException;
import telran.exceptions.controller.DronesExceptionsController;
import telran.drones.service.DronesService;

record DroneDtoWrongEnum(String number, String modelType) {

}

record DroneItemsAmountDto (String number, int amount) {}
@WebMvcTest
class DronesControllerTest {
	@Autowired
	MockMvc mockMvc;
	@MockBean
	DronesService dronesService;
	@Autowired
	ObjectMapper mapper;
	private static final String HOST = "http://localhost:8080/";
	private static final String DRONE_NUMBER_1 = "DRONE-1";
	private static final String MEDICATION_CODE = "MED_1";
	static final String URL_DRONE_REGISTER = HOST + UrlConstants.DRONES;
	private static final String URL_DRONE_LOAD = HOST + UrlConstants.LOAD_DRONE;
	private static final String URL_CHECK_DRONE_MED = HOST + UrlConstants.CHECK_MEDICATION_ITEMS;
	private static final String URL_CHECK_AVAILABLE_DRONES = HOST + UrlConstants.CHECK_AVAILABLE_DRONES;
	private static final String URL_CHECK_BATTERY_CAPACITY = HOST + UrlConstants.CHECK_BATTERY_CAPACITY;
	private static final String URL_CHECK_ITEMS_AMOUNT = HOST + UrlConstants.CHECK_DRONE_LOADED_ITEM_AMOUNTS;
	private static final String CONTROLLER_TEST = "Controller:";
	DroneDto droneDto1 = new DroneDto(DRONE_NUMBER_1, ModelType.Cruiserweight);
	DroneDtoWrongEnum droneDtoWrongFields = new DroneDtoWrongEnum(DRONE_NUMBER_1, "KUKU");
	DroneDto droneDtoMissingFields = new DroneDto(null, null);
	DroneMedication droneMedication = new DroneMedication(DRONE_NUMBER_1, MEDICATION_CODE);
	DroneMedication droneMedicationWrongFields = new DroneMedication(new String(new char[1000]), "mED_1");
	DroneMedication droneMedicationMissingFields = new DroneMedication(null, null);
	String[] errorMessagesDroneWrongFields = {DronesExceptionsController.JSON_TYPE_MISMATCH_MESSAGE};
	String[] errorMessagesDroneMissingFields = { DronesValidationErrorMessages.MISSING_DRONE_NUMBER,
			DronesValidationErrorMessages.MISSING_MODEL};
	String[] errorMessagesDroneMedicationWrongFields = {
			DronesValidationErrorMessages.DRONE_NUMBER_WRONG_LENGTH,
			DronesValidationErrorMessages.WRONG_MEDICATION_CODE
	};
	String[] errorMessagesDroneMedicationMissingFields = {
			DronesValidationErrorMessages.MISSING_DRONE_NUMBER,
			DronesValidationErrorMessages.MISSING_MEDICATION_CODE,
	};
	
	List<DroneItemsAmount> itemsAmount = List.of(new DroneItemsAmount() {
		@Override
		public String getNumber() { return DRONE_NUMBER_1; }
		@Override
		public long getAmount() { return 1; }
	});
	
	@Test
	@DisplayName(CONTROLLER_TEST + TestDisplayNames.REGISTER_DRONE_NORMAL)
		void testDroneRegisterNormal() throws Exception{
			when(dronesService.registerDrone(droneDto1)).thenReturn(droneDto1);
			String droneJSON = mapper.writeValueAsString(droneDto1);
			String response = mockMvc.perform(post(URL_DRONE_REGISTER ).contentType(MediaType.APPLICATION_JSON)
					.content(droneJSON)).andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
			assertEquals(droneJSON, response);
			
		}
	@Test
	@DisplayName(CONTROLLER_TEST + TestDisplayNames.REGISTER_DRONE_MISSING_FIELDS)
	void testDroneRegisterMissingFields() throws Exception {
		String droneJSON = mapper.writeValueAsString(droneDtoMissingFields);
		String response = mockMvc
				.perform(post(URL_DRONE_REGISTER).contentType(MediaType.APPLICATION_JSON).content(droneJSON))
				.andExpect(status().isBadRequest()).andReturn().getResponse().getContentAsString();
		assertErrorMessages(response, errorMessagesDroneMissingFields);
	}
	private void assertErrorMessages(String response, String[] expectedMessages) {
		String[] actualMessages = response.split(";");
		Arrays.sort(actualMessages);
		Arrays.sort(expectedMessages);
		assertArrayEquals(expectedMessages, actualMessages);
	}
	@Test
	@DisplayName(CONTROLLER_TEST + TestDisplayNames.REGISTER_DRONE_VALIDATION_VIOLATION)
	void testDronRegisterWrongFields() throws Exception {
		String droneJSON = mapper.writeValueAsString(droneDtoWrongFields);
		String response = mockMvc
				.perform(post(URL_DRONE_REGISTER).contentType(MediaType.APPLICATION_JSON).content(droneJSON))
				.andExpect(status().isBadRequest()).andReturn().getResponse().getContentAsString();
		assertErrorMessages(response, errorMessagesDroneWrongFields);
	}
	@Test
	@DisplayName(CONTROLLER_TEST + TestDisplayNames.REGISTER_DRONE_ALREADY_EXISTS)
	void testDroneRegisterAlreadyExists() throws Exception{
		when(dronesService.registerDrone(droneDto1)).thenThrow(new DroneAlreadyExistException());
		String droneJSON = mapper.writeValueAsString(droneDto1);
		String response = mockMvc.perform(post(URL_DRONE_REGISTER ).contentType(MediaType.APPLICATION_JSON)
				.content(droneJSON)).andExpect(status().isBadRequest()).andReturn().getResponse().getContentAsString();
		assertEquals(ServiceExceptionMessages.DRONE_ALREADY_EXISTS, response);
		
	}
	@Test
	@DisplayName(CONTROLLER_TEST + TestDisplayNames.LOAD_DRONE_NORMAL)
	void loadMedicationNormal() throws Exception{
		when(dronesService.loadDrone(droneMedication)).thenReturn(droneMedication);
		String logDtoJSON = mapper.writeValueAsString(droneMedication);
		String droneMedicationJSON = mapper.writeValueAsString(droneMedication);
		String response = mockMvc.perform(post(URL_DRONE_LOAD).contentType(MediaType.APPLICATION_JSON)
				.content(droneMedicationJSON)).andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
		assertEquals(logDtoJSON, response);
		
	}
	@Test
	@DisplayName(CONTROLLER_TEST + TestDisplayNames.LOAD_DRONE_MEDICATION_NOT_FOUND)
	void loadMedicationMedicationNotFound() throws Exception {
		serviceExceptionRequest(new MedicationNotFoundException(), 404, ServiceExceptionMessages.MEDICATION_NOT_FOUND);

	}
	@Test
	@DisplayName(CONTROLLER_TEST + TestDisplayNames.LOAD_DRONE_NOT_FOUND)
	void loadMedicationDroneNotFound() throws Exception {
		serviceExceptionRequest(new DroneNotFoundException(), 404, ServiceExceptionMessages.DRONE_NOT_FOUND);

	}
	@Test
	@DisplayName(CONTROLLER_TEST + TestDisplayNames.LOAD_DRONE_LOW_BATTERY_CAPCITY)
	void loadMedicationLowBatteryCapacity() throws Exception {
		serviceExceptionRequest(new LowBatteryCapacityException(), 400,
				ServiceExceptionMessages.LOW_BATTERY_CAPACITY);

	}

	@Test
	@DisplayName(CONTROLLER_TEST + TestDisplayNames.LOAD_DRONE_NOT_MATCHING_STATE)
	void loadMedicationNotMatchingState() throws Exception {
		serviceExceptionRequest(new IllegalDroneStateException(), 400, ServiceExceptionMessages.NOT_IDLE_STATE);

	}

	@Test
	@DisplayName(CONTROLLER_TEST + TestDisplayNames.LOAD_DRONE_NOT_MATCHING_WEIGHT)
	void loadMedicationNotMatchingWeight() throws Exception {
		serviceExceptionRequest(new IllegalMedicationWeightException(), 400, ServiceExceptionMessages.WEIGHT_LIMIT_VIOLATION);

	}

	@Test
	@DisplayName(CONTROLLER_TEST + TestDisplayNames.LOAD_DRONE_WRONG_FIELDS)
	void loadMedicationWrongFields() throws Exception {
		validationExceptionRequest(errorMessagesDroneMedicationWrongFields, droneMedicationWrongFields);
	}

	@Test
	@DisplayName(CONTROLLER_TEST + TestDisplayNames.LOAD_DRONE_MISSING_FIELDS)
	void loadMedicationMissingFields() throws Exception {
		validationExceptionRequest(errorMessagesDroneMedicationMissingFields, droneMedicationMissingFields);
	}

	private void serviceExceptionRequest(RuntimeException serviceException, int statusCode, String errorMessage)
			throws  Exception {
		when(dronesService.loadDrone(droneMedication)).thenThrow(serviceException);
		String droneMedicationJSON = mapper.writeValueAsString(droneMedication);
		String response = mockMvc.perform(post(URL_DRONE_LOAD ).contentType(MediaType.APPLICATION_JSON)
				.content(droneMedicationJSON)).andExpect(status().is(statusCode)).andReturn().getResponse().getContentAsString();
		assertEquals(errorMessage, response);
	}
	private void validationExceptionRequest(String[] errorMessages, DroneMedication droneMedicationDto)
			throws Exception {
		String droneMedicationJSON = mapper.writeValueAsString(droneMedicationDto);
		String response = mockMvc
				.perform(post(URL_DRONE_LOAD).contentType(MediaType.APPLICATION_JSON).content(droneMedicationJSON))
				.andExpect(status().isBadRequest()).andReturn().getResponse().getContentAsString();
		assertErrorMessages(response, errorMessages);
	}
//	
	
	@Test
	@DisplayName(CONTROLLER_TEST + TestDisplayNames.CHECK_MED_ITEMS_NORMAL)
	void checkMedicationItemsNormal() throws Exception{
		when(dronesService.checkMedicationItems(DRONE_NUMBER_1)).thenReturn(List.of(MEDICATION_CODE));
		String listJson = mapper.writeValueAsString(List.of(MEDICATION_CODE));
		System.out.println(URL_CHECK_DRONE_MED + DRONE_NUMBER_1);
		String response = mockMvc.perform(get(URL_CHECK_DRONE_MED + DRONE_NUMBER_1)).andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
		assertEquals(listJson, response);
	}
	
	@Test
	@DisplayName(CONTROLLER_TEST + TestDisplayNames.CHECK_MED_ITEMS_DRONE_NOT_FOUND)
	void checkMedicationItemsDroneNotExists() throws Exception{
		when(dronesService.checkMedicationItems(DRONE_NUMBER_1)).thenThrow(new NotFoundException(ServiceExceptionMessages.DRONE_NOT_FOUND));
		
		String response = mockMvc.perform(get(URL_CHECK_DRONE_MED + DRONE_NUMBER_1)).andExpect(status().isNotFound()).andReturn().getResponse().getContentAsString();
		assertEquals(ServiceExceptionMessages.DRONE_NOT_FOUND, response);
	}
	
	@Test
	@DisplayName(CONTROLLER_TEST + TestDisplayNames.REGISTER_DRONE_PATH_VARIABLE_VALIDATION_VIOLATION)
	void checkMedicationItemsWrongNumber() throws Exception{
		String response = mockMvc.perform(get(URL_CHECK_DRONE_MED + new String(new char[1000]))).andExpect(status().isBadRequest()).andReturn().getResponse().getContentAsString();
		assertEquals(DronesValidationErrorMessages.DRONE_NUMBER_WRONG_LENGTH, response);
	}
	
	@Test
	@DisplayName(CONTROLLER_TEST + TestDisplayNames.AVAILABLE_DRONES)
	void checkAvailableDronesNormal() throws Exception{
		when(dronesService.checkAvailableDrones()).thenReturn(List.of(DRONE_NUMBER_1));
		String listJson = mapper.writeValueAsString(List.of(DRONE_NUMBER_1));
		String response = mockMvc.perform(get(URL_CHECK_AVAILABLE_DRONES)).andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
		assertEquals(listJson, response);
	}
	
	@Test
	@DisplayName(CONTROLLER_TEST + TestDisplayNames.CHECK_BATTERY_LEVEL_NORMAL)
	void checkBatteryCapacityNormal() throws Exception{
		when(dronesService.checkBatteryCapacity(DRONE_NUMBER_1)).thenReturn(100);
		String batteryJson = mapper.writeValueAsString(100);
		String response = mockMvc.perform(get(URL_CHECK_BATTERY_CAPACITY + DRONE_NUMBER_1)).andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
		assertEquals(batteryJson, response);
	}
	
	@Test
	@DisplayName(CONTROLLER_TEST + TestDisplayNames.CHECK_BATTERY_LEVEL_DRONE_NOT_FOUND)
	void checkBatteryCapacityDroneNotFound() throws Exception{
		when(dronesService.checkBatteryCapacity(DRONE_NUMBER_1)).thenThrow(new NotFoundException(ServiceExceptionMessages.DRONE_NOT_FOUND));
		
		String response = mockMvc.perform(get(URL_CHECK_BATTERY_CAPACITY + DRONE_NUMBER_1)).andExpect(status().isNotFound()).andReturn().getResponse().getContentAsString();
		assertEquals(ServiceExceptionMessages.DRONE_NOT_FOUND, response);
	}
	
	@Test
	@DisplayName(CONTROLLER_TEST + TestDisplayNames.REGISTER_DRONE_PATH_VARIABLE_VALIDATION_VIOLATION)
	void checkBatteryCapacityWrongNumber() throws Exception{		
		String response = mockMvc.perform(get(URL_CHECK_BATTERY_CAPACITY + new String(new char[1000]))).andExpect(status().isBadRequest()).andReturn().getResponse().getContentAsString();
		assertEquals(DronesValidationErrorMessages.DRONE_NUMBER_WRONG_LENGTH, response);
	}
	
	@Test
	@DisplayName(CONTROLLER_TEST + TestDisplayNames.CHECK_DRONES_ITEMS_AMOUNT)
	void checkDroneLoadedItemAmounts() throws Exception{	
		when(dronesService.checkDroneLoadedItemAmounts()).thenReturn(itemsAmount);
		String itemsAmountJson = mapper.writeValueAsString(itemsAmount);
		String response = mockMvc.perform(get(URL_CHECK_ITEMS_AMOUNT)).andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
		assertEquals(itemsAmountJson, response);
	}
	
	
	
	

	
	
}
