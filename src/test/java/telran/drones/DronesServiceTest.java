package telran.drones;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

import telran.drones.dto.*;
import telran.drones.exceptions.*;
import telran.drones.model.*;
import telran.drones.repo.*;
import telran.drones.service.DronesService;

@SpringBootTest
@Sql(scripts = {"classpath:test_data.sql"})
class DronesServiceTest {
	@Autowired
	DronesService dronesService;
	@Autowired
	DroneModelRepo droneModelRepo;
	@Autowired
	DroneRepo droneRepo;
	@Autowired
	MedicationRepo medicationRepo;
	@Autowired 
	EventLogRepo eventLogRepo;
	
	private static final String DRONE_NUMBER_NOT_EXISTS = "1111";
	private static final String DRONE_ALL_SET = "111";
	private static final String DRONE_LOADED = "222";
	//battery 10
	private static final String DRONE_LOW_BATTERY = "333";

	private static final String MEDICATION_CODE_NORMAL_WEIGHT = "EXISTS1";
	//weight 1000
	private static final String MEDICATION_CODE_HEAVY_WEIGHT = "EXISTS2";
	private static final String MEDICATION_CODE_NOT_EXISTS = "EXISTS3";
	

	private static final DroneDto droneNotExists = new DroneDto(DRONE_NUMBER_NOT_EXISTS, ModelType.Lightweight);
	private static final DroneDto droneAllSet = new DroneDto(DRONE_ALL_SET, ModelType.Lightweight);
	//ModelType.Heavyweight - not to add to testbase
	private static final DroneDto droneModelNotExists = new DroneDto(DRONE_NUMBER_NOT_EXISTS, ModelType.Heavyweight);
	
	private static final DroneMedication droneMedicationNormalWeight = new DroneMedication(DRONE_ALL_SET, MEDICATION_CODE_NORMAL_WEIGHT);
	private static final DroneMedication droneMedicationHeavyWeight = new DroneMedication(DRONE_ALL_SET, MEDICATION_CODE_HEAVY_WEIGHT);
	private static final DroneMedication droneMedicationNotExistsMedication = new DroneMedication(DRONE_ALL_SET, MEDICATION_CODE_NOT_EXISTS);
	private static final DroneMedication droneMedicationNotExistsDrone = new DroneMedication(DRONE_NUMBER_NOT_EXISTS, MEDICATION_CODE_NORMAL_WEIGHT);
	private static final DroneMedication droneMedicationLoaded = new DroneMedication(DRONE_LOADED, MEDICATION_CODE_NORMAL_WEIGHT);
	private static final DroneMedication droneMedicationLowBattery = new DroneMedication(DRONE_LOW_BATTERY, MEDICATION_CODE_NORMAL_WEIGHT);
	
	private static final EventLog logLoadedDrone = new EventLog(LocalDateTime.now(), droneNotExists.number(), State.LOADED, 100);
	
	

	@Test
	void registerDrone_correctFlow_success() {
		int eventLogCount = eventLogRepo.countByDroneNumber(droneNotExists.number());
		assertEquals(droneNotExists, dronesService.registerDrone(droneNotExists));
		Drone drone = droneRepo.findById(droneNotExists.number()).orElse(null);
		assertEquals(droneNotExists, drone.build());
		List<EventLog> eventLogs = eventLogRepo.findByDroneNumberOrderByTimestampAsc(droneNotExists.number());
		assertEquals(eventLogCount + 1, eventLogs.size());
		EventLog expectedLog = new EventLog(LocalDateTime.now(), droneNotExists.number(), State.IDLE, 100);
		assertEquals(expectedLog, eventLogs.get(eventLogCount));	
	}
	
	@Test
	void registerDrone_alreadyExists_throwsException() {
		assertThrowsExactly(DroneIllegalStateException.class, () -> dronesService.registerDrone(droneAllSet));
	}
	
	@Test
	void registerDrone_modelNotExists_throwsException() {
		assertThrowsExactly(DroneModelNotFoundException.class, () -> dronesService.registerDrone(droneModelNotExists));
	}
	
	@Test
	void loadDrone_correctFlow_success() {
		int eventLogCount = eventLogRepo.countByDroneNumber(droneMedicationNormalWeight.droneNumber());
		assertEquals(droneMedicationNormalWeight, dronesService.loadDrone(droneMedicationNormalWeight));
		Drone drone = droneRepo.findById(droneMedicationNormalWeight.droneNumber()).orElse(null);
		assertEquals(drone.getState(), State.LOADED);
		EventLog expectedLog = new EventLog(LocalDateTime.now(), drone.getNumber(), State.LOADED, drone.getBatteryCapacity());
		List<EventLog> eventLogs = eventLogRepo.findByDroneNumberOrderByTimestampAsc(drone.getNumber());
		assertNotEquals(eventLogCount, eventLogs.size());
		assertEquals(expectedLog, eventLogs.get(eventLogs.size() - 1));	
	}
	
	@Test 
	void loadDrone_droneLoaded_throwsException() {
		assertThrowsExactly(DroneStateIllegalStateException.class, () -> dronesService.loadDrone(droneMedicationLoaded));
	}
	
	@Test
	void loadDrone_droneLowBattery_throwsException() {
		assertThrowsExactly(DroneBatteryCapacityException.class, () -> dronesService.loadDrone(droneMedicationLowBattery));
	}
	
	@Test
	void loadDrone_wrongWeight_throwsException() {
		assertThrowsExactly(DroneWeightIllegalStateException.class, () -> dronesService.loadDrone(droneMedicationHeavyWeight));
	}
	
	@Test
	void loadDrone_droneNotExists_throwsException() {
		assertThrowsExactly(DroneNotFoundException.class, () -> dronesService.loadDrone(droneMedicationNotExistsDrone));
	}
	
	@Test
	void loadDrone_medicationNotExists_throwsException() {
		assertThrowsExactly(MedicationNotFoundException.class, () -> dronesService.loadDrone(droneMedicationNotExistsMedication));
	}

}
