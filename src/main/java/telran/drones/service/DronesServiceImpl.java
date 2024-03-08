package telran.drones.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import telran.drones.dto.*;
import telran.drones.exceptions.*;
import telran.drones.model.*;
import telran.drones.repo.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class DronesServiceImpl implements DronesService {
	final DroneModelRepo droneModelRepo;
	final DroneRepo droneRepo;
	final MedicationRepo medicationRepo;
	final EventLogRepo eventLogRepo;

	@Override
	@Transactional
	public DroneDto registerDrone(DroneDto droneDto) {
		log.debug("DronesServiceImpl: registerDrone {} received", droneDto);
		if (droneRepo.existsById(droneDto.number())) {
			log.error("registerDrone: drone with number {} already exists", droneDto.number());
			throw new DroneIllegalStateException();
		}
		DroneModel model = droneModelRepo.findById(droneDto.modelType()).orElseThrow(() -> {
			log.error("registerDrone: drone type {} not exists", droneDto.modelType());
			return new DroneModelNotFoundException();
		});
		Drone drone = Drone.of(droneDto);
		drone.setModel(model);
		droneRepo.save(drone);
		log.debug("registerDrone: drone {} registered", droneDto);
		EventLog eventLog = new EventLog(LocalDateTime.now(), drone.getNumber(), drone.getState(), drone.getBatteryCapacity());
		eventLogRepo.save(eventLog);
		log.debug("registerDrone: event log created {}", eventLog);
		return droneDto;
	}
	

	@Override
	@Transactional
	public DroneMedication loadDrone(DroneMedication droneMedication) {
		log.debug("DronesServiceImpl: loadDrone {} received", droneMedication);
		Drone drone = droneRepo.findById(droneMedication.droneNumber()).orElseThrow(() -> {
			log.error("loadDrone: drone with number {} not found", droneMedication.droneNumber());
			return new DroneNotFoundException();
		});
		Medication medication = medicationRepo.findById(droneMedication.medicationCode()).orElseThrow(() -> {
			log.error("loadDrone: medication with code {} not found", droneMedication.medicationCode());
			return new MedicationNotFoundException();
		});
		log.debug("loadDrone: check drone availability {}", drone);
		checkDroneAvailability(drone);
		
		if (medication.getWeight() > drone.getModel().getWeight()) {
			log.error("loadDrone: weight of medication: {} more than drone {}", medication, drone);
			throw new DroneWeightIllegalStateException();
		}
		
		drone.setState(State.LOADING);
		log.debug("loadDrone: drone {} changed state to LOADING", drone);
		EventLog eventLog = new EventLog(LocalDateTime.now(), drone.getNumber(), drone.getState(), drone.getBatteryCapacity());
		eventLogRepo.save(eventLog);
		log.debug("loadDrone: event log created {}", eventLog);
		//FIXME some loading operations
		drone.setState(State.LOADED);
		log.debug("loadDrone: drone {} changed state to LOADED", drone);
		eventLog = new EventLog(LocalDateTime.now(), drone.getNumber(), drone.getState(), drone.getBatteryCapacity());
		eventLogRepo.save(eventLog);
		log.debug("loadDrone: event log created {}", eventLog);
		return droneMedication;
	}
	
	private void checkDroneAvailability(Drone drone) {
		if (drone.getBatteryCapacity() < 25) {
			log.error("checkDroneAvailability: battery capacity of drone {} < 25%", drone);
			throw new DroneBatteryCapacityException();
		}
		if(drone.getState() != State.IDLE) {
			log.error("checkDroneAvailability: state of drone {} is not IDLE", drone);
			throw new DroneStateIllegalStateException();
		}
	}

}
