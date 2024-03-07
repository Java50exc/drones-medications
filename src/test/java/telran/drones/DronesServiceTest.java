package telran.drones;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import telran.drones.dto.DroneDto;
import telran.drones.dto.DroneMedication;
import telran.drones.dto.ModelType;
import telran.drones.dto.State;
import telran.drones.exceptions.DroneBatteryCapacityException;
import telran.drones.exceptions.DroneIllegalStateException;
import telran.drones.exceptions.DroneModelNotFoundException;
import telran.drones.exceptions.DroneNotFoundException;
import telran.drones.exceptions.DroneStateIllegalStateException;
import telran.drones.exceptions.DroneWeightIllegalStateException;
import telran.drones.exceptions.MedicationNotFoundException;
import telran.drones.model.Drone;
import telran.drones.repo.DroneModelRepo;
import telran.drones.repo.DroneRepo;
import telran.drones.repo.MedicationRepo;
import telran.drones.service.DronesService;

@SpringBootTest
class DronesServiceTest {
	@Autowired
	DronesService dronesService;
	@Autowired
	DroneModelRepo droneModelRepo;
	@Autowired
	DroneRepo droneRepo;
	@Autowired
	MedicationRepo medicationRepo;
	
	private static final String DRONE_NUMBER_NOT_EXISTS = "1111";
	private static final String DRONE_ALL_SET = "111";
	private static final String DRONE_LOADED = "222";
	
	//battery 10
	private static final String DRONE_LOW_BATTERY = "333";

	//weight 50
	private static final String MEDICATION_CODE_NORMAL_WEIGHT = "EXISTS1";
	
	//weight 1000
	private static final String MEDICATION_CODE_HEAVY_WEIGHT = "EXISTS2";
	private static final String MEDICATION_CODE_NOT_EXISTS = "EXISTS3";
	

	
	private static final DroneDto droneNotExists = new DroneDto(DRONE_NUMBER_NOT_EXISTS, ModelType.Lightweight);
	private static final DroneDto droneAllSet = new DroneDto(DRONE_ALL_SET, ModelType.Lightweight);
	private static final DroneDto droneLoaded = new DroneDto(DRONE_LOADED, ModelType.Lightweight);
	private static final DroneDto droneLowBattery = new DroneDto(DRONE_LOW_BATTERY, ModelType.Lightweight);
	//ModelType.Heavyweight - not to add to dummybase
	private static final DroneDto droneModelNotExists = new DroneDto(DRONE_NUMBER_NOT_EXISTS, ModelType.Heavyweight);
	
	
	private static final DroneMedication droneMedicationNormalWeight = new DroneMedication(DRONE_ALL_SET, MEDICATION_CODE_NORMAL_WEIGHT);
	private static final DroneMedication droneMedicationHeavyWeight = new DroneMedication(DRONE_ALL_SET, MEDICATION_CODE_HEAVY_WEIGHT);
	private static final DroneMedication droneMedicationNotExistsMedication = new DroneMedication(DRONE_ALL_SET, MEDICATION_CODE_NOT_EXISTS);
	private static final DroneMedication droneMedicationNotExistsDrone = new DroneMedication(DRONE_NUMBER_NOT_EXISTS, MEDICATION_CODE_NORMAL_WEIGHT);
	private static final DroneMedication droneMedicationLoaded = new DroneMedication(DRONE_LOADED, MEDICATION_CODE_NORMAL_WEIGHT);
	private static final DroneMedication droneMedicationLowBattery = new DroneMedication(DRONE_LOW_BATTERY, MEDICATION_CODE_NORMAL_WEIGHT);
	
	

	@Test
	void registerDrone_correctFlow_success() {
		assertEquals(droneNotExists, dronesService.registerDrone(droneNotExists));
		Drone drone = droneRepo.findById(droneNotExists.number()).orElse(null);
		assertEquals(droneNotExists, drone.build());	
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
		assertEquals(droneMedicationNormalWeight, dronesService.loadDrone(droneMedicationNormalWeight));
		Drone drone = droneRepo.findById(droneMedicationNormalWeight.droneNumber()).orElse(null);
		assertEquals(drone.getState(), State.LOADED);
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
