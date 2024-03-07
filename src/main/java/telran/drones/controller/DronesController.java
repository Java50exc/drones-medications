package telran.drones.controller;

import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import telran.drones.dto.DroneDto;
import telran.drones.dto.DroneMedication;
import telran.drones.service.DronesService;


@RestController
@RequestMapping("drones")
@RequiredArgsConstructor
public class DronesController {
	final DronesService dronesService;
	
	@PostMapping
	DroneDto registerDrone(@RequestBody @Valid DroneDto droneDto) {
		//FIXME
		return dronesService.registerDrone(droneDto);
	}
	
	@PostMapping("load")
	DroneMedication loadDrone(@RequestBody @Valid DroneMedication droneMedication) {
		//FIXME
		return dronesService.loadDrone(droneMedication);
	}

}
