package telran.drones.controller;

import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import telran.drones.dto.DroneDto;
import telran.drones.dto.DroneMedication;
import telran.drones.service.DronesService;


@RestController
@RequestMapping("drones")
@RequiredArgsConstructor
@Slf4j
public class DronesController {
	final DronesService dronesService;
	
	@PostMapping
	DroneDto registerDrone(@RequestBody @Valid DroneDto droneDto) {
		log.debug("Controller: registerDrone {} validation passed", droneDto);
		return dronesService.registerDrone(droneDto);
	}
	
	@PostMapping("load")
	DroneMedication loadDrone(@RequestBody @Valid DroneMedication droneMedication) {
		log.debug("Controller: loadDrone {} validation passed", droneMedication);
		return dronesService.loadDrone(droneMedication);
	}

}
