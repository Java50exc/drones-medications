package telran.cars;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import telran.drones.controller.DronesController;
import telran.drones.dto.DroneDto;
import telran.drones.dto.DroneMedication;
import telran.drones.dto.ModelType;
import telran.drones.exceptions.NotFoundException;
import telran.drones.service.DronesService;

import static telran.drones.api.ServiceExceptionMessages.*;
import static telran.drones.api.ValidationConstants.*;

import java.util.Arrays;

@WebMvcTest(DronesController.class)
class DronesControllerTest {
	@MockBean
	DronesService dronesService;
	@Autowired
	MockMvc mockMvc;
	@Autowired
	ObjectMapper mapper;
	
	private static final String REGISTER_DRONE_PATH = "http://localhost:8080/drones";
	private static final String LOAD_DRONE_PATH = "http://localhost:8080/drones/load";
	
	private static final String DRONE_NUMBER = "111";
	private static final String WRONG_DRONE_NUMBER = DRONE_NUMBER.repeat(100);
	private static final String MEDICATION_CODE = "AAA_55";
	private static final String WRONG_MEDICATION_CODE = "aaaAAA";
	
	
	
	
	
	private static final DroneDto droneDto = new DroneDto(DRONE_NUMBER, ModelType.Lightweight);
	private static final DroneDto droneDtoWrongNumber = new DroneDto(WRONG_DRONE_NUMBER, ModelType.Lightweight);
	private static final DroneDto droneDtoMissingAllFields = new DroneDto(null, null);
	
	private static final DroneMedication droneMedication = new DroneMedication(DRONE_NUMBER, MEDICATION_CODE);
	private static final DroneMedication droneMedicationWrongNumber = new DroneMedication(WRONG_DRONE_NUMBER, MEDICATION_CODE);
	private static final DroneMedication droneMedicationWrongCode = new DroneMedication(DRONE_NUMBER, WRONG_MEDICATION_CODE);
	private static final DroneMedication droneMedicationMissingAllFields = new DroneMedication(null, null);
	
	private static String[] droneDtoAllMessages = { MISSING_DRONE_NUMBER_MESSAGE, MISSING_MODEL_TYPE_MESSAGE };
	private static String[] droneMedicationAllMessages = { MISSING_DRONE_NUMBER_MESSAGE, MISSING_MEDICATION_CODE_MESSAGE };
	

	
	@Test
	void registerDrone_correctFlow_success() throws Exception {
		when(dronesService.registerDrone(droneDto)).thenReturn(droneDto);
		
		String droneJson = mapper.writeValueAsString(droneDto);
		String actualJson = mockMvc.perform(post(REGISTER_DRONE_PATH).contentType(MediaType.APPLICATION_JSON).content(droneJson))
				.andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
		
		assertEquals(droneJson, actualJson);	
	}
	
	@Test
	void loadDrone_correctFlow_success() throws Exception {
		when(dronesService.loadDrone(droneMedication)).thenReturn(droneMedication);
		
		String droneMedicationJson = mapper.writeValueAsString(droneMedication);
		String actualJson = mockMvc.perform(post(LOAD_DRONE_PATH).contentType(MediaType.APPLICATION_JSON).content(droneMedicationJson))
				.andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
		
		assertEquals(droneMedicationJson, actualJson);	
	}
	
	//handling service exceptions
	
	@Test
	void registerDrone_droneAlreadyExists_throwsException() throws Exception {
		when(dronesService.registerDrone(droneDto)).thenThrow(new IllegalStateException(DRONE_ALREADY_EXISTS_MESSAGE));
		
		String droneJson = mapper.writeValueAsString(droneDto);
		String response = mockMvc.perform(post(REGISTER_DRONE_PATH).contentType(MediaType.APPLICATION_JSON).content(droneJson))
				.andExpect(status().isBadRequest()).andReturn().getResponse().getContentAsString();
		
		assertEquals(DRONE_ALREADY_EXISTS_MESSAGE, response);
	}
	
	@Test
	void registerDrone_modelNotFound_throwsException() throws Exception {
		when(dronesService.registerDrone(droneDto)).thenThrow(new NotFoundException(MODEL_NOT_FOUND_MESSAGE));
		
		String droneJson = mapper.writeValueAsString(droneDto);
		String response = mockMvc.perform(post(REGISTER_DRONE_PATH).contentType(MediaType.APPLICATION_JSON).content(droneJson))
				.andExpect(status().isNotFound()).andReturn().getResponse().getContentAsString();
		
		assertEquals(MODEL_NOT_FOUND_MESSAGE, response);
	}
	
	@Test
	void loadDrone_droneNotFound_throwsException() throws Exception {
		when(dronesService.loadDrone(droneMedication)).thenThrow(new NotFoundException(DRONE_NOT_FOUND_MESSAGE));
		
		String droneMedicationJson = mapper.writeValueAsString(droneMedication);
		String response = mockMvc.perform(post(LOAD_DRONE_PATH).contentType(MediaType.APPLICATION_JSON).content(droneMedicationJson))
				.andExpect(status().isNotFound()).andReturn().getResponse().getContentAsString();
		
		assertEquals(DRONE_NOT_FOUND_MESSAGE, response);
	}
	
	@Test
	void loadDrone_medicationNotFound_throwsException() throws Exception {
		when(dronesService.loadDrone(droneMedication)).thenThrow(new NotFoundException(MEDICATION_NOT_FOUND_MESSAGE));
		
		String droneMedicationJson = mapper.writeValueAsString(droneMedication);
		String response = mockMvc.perform(post(LOAD_DRONE_PATH).contentType(MediaType.APPLICATION_JSON).content(droneMedicationJson))
				.andExpect(status().isNotFound()).andReturn().getResponse().getContentAsString();
		
		assertEquals(MEDICATION_NOT_FOUND_MESSAGE, response);
	}
	
	@Test
	void loadDrone_droneNotReadyToLoading_throwsException() throws Exception {
		when(dronesService.loadDrone(droneMedication)).thenThrow(new IllegalStateException(DRONE_NOT_READY_MESSAGE));
		
		String droneMedicationJson = mapper.writeValueAsString(droneMedication);
		String response = mockMvc.perform(post(LOAD_DRONE_PATH).contentType(MediaType.APPLICATION_JSON).content(droneMedicationJson))
				.andExpect(status().isBadRequest()).andReturn().getResponse().getContentAsString();
		
		assertEquals(DRONE_NOT_READY_MESSAGE, response);
	}
	
	@Test
	void loadDrone_lowDroneBattery_throwsException() throws Exception {
		when(dronesService.loadDrone(droneMedication)).thenThrow(new IllegalStateException(LOW_DRONE_BATTERY_CAPACITY_MESSAGE));
		
		String droneMedicationJson = mapper.writeValueAsString(droneMedication);
		String response = mockMvc.perform(post(LOAD_DRONE_PATH).contentType(MediaType.APPLICATION_JSON).content(droneMedicationJson))
				.andExpect(status().isBadRequest()).andReturn().getResponse().getContentAsString();
		
		assertEquals(LOW_DRONE_BATTERY_CAPACITY_MESSAGE, response);
	}
	
	@Test
	void loadDrone_droneWeightNotMatches_throwsException() throws Exception {
		when(dronesService.loadDrone(droneMedication)).thenThrow(new IllegalStateException(DRONE_WEIGHT_NOT_MATCH_MESSAGE));
		
		String droneMedicationJson = mapper.writeValueAsString(droneMedication);
		String response = mockMvc.perform(post(LOAD_DRONE_PATH).contentType(MediaType.APPLICATION_JSON).content(droneMedicationJson))
				.andExpect(status().isBadRequest()).andReturn().getResponse().getContentAsString();
		
		assertEquals(DRONE_WEIGHT_NOT_MATCH_MESSAGE, response);
	}
	
	//validation tests
	
	@Test
	void registerDrone_droneWithWrongNumberLength_throwsException() {
		testDtoValidation(droneDtoWrongNumber, REGISTER_DRONE_PATH, ILLEGAL_DRONE_NUMBER_LENGTH_MESSAGE);
	}
	
	@Test
	void loadDrone_wrongDroneNumberLength_throwsException() {
		testDtoValidation(droneMedicationWrongNumber, LOAD_DRONE_PATH, ILLEGAL_DRONE_NUMBER_LENGTH_MESSAGE);
	}
	
	
	@Test
	void loadDrone_wrongMedicationCode_throwsException() {
		testDtoValidation(droneMedicationWrongCode, LOAD_DRONE_PATH, WRONG_MEDICATION_CODE_MESSAGE);
	}
	
	@Test 
	void registerDrone_missingAllFields_throwsException() {
		Arrays.sort(droneDtoAllMessages);
		testDtoValidation(droneDtoMissingAllFields, REGISTER_DRONE_PATH, String.join(";", droneDtoAllMessages));
	}
	
	@Test 
	void loadDrone_missingAllFields_throwsException() {
		Arrays.sort(droneMedicationAllMessages);
		testDtoValidation(droneMedicationMissingAllFields, LOAD_DRONE_PATH, String.join(";", droneMedicationAllMessages));
	}
	
	
	private void testDtoValidation(Object dto, String path, String expectedMessage) {
		String response = null;
		try {
			String json = mapper.writeValueAsString(dto);
			response = mockMvc.perform(post(path).contentType(MediaType.APPLICATION_JSON).content(json))
					.andExpect(status().isBadRequest()).andReturn().getResponse().getContentAsString();	
			String[] responseMessages = response.split(";");
			Arrays.sort(responseMessages);
			response = String.join(";", responseMessages);
		} catch (Exception e) { }
		assertEquals(expectedMessage, response);
	}
	
	
	   

}
