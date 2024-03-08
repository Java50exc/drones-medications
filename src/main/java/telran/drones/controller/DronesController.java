package telran.drones.controller;

import static telran.drones.api.DronesValidationErrorMessages.DRONE_NUMBER_WRONG_LENGTH;
import static telran.drones.api.DronesValidationErrorMessages.MAX_DRONE_NUMBER_LENGTH;
import static telran.drones.api.DronesValidationErrorMessages.MISSING_DRONE_NUMBER;

import java.util.List;

import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import telran.drones.api.UrlConstants;
import telran.drones.dto.*;
import telran.drones.service.DronesService;

@RestController
@RequiredArgsConstructor
@Slf4j
public class DronesController {
	final DronesService dronesService;
	 @PostMapping(UrlConstants.DRONES)
	    DroneDto registerDrone(@RequestBody @Valid DroneDto droneDto) {
	    	log.debug("received: {}", droneDto);
	    	return dronesService.registerDrone(droneDto);
	    }
	    @PostMapping(UrlConstants.LOAD_DRONE)
	    DroneMedication loadDrone(@RequestBody @Valid DroneMedication droneMedication) {
	    	log.debug("received: {}", droneMedication);
	    	return dronesService.loadDrone(droneMedication);
	    }
	    
	    @GetMapping(UrlConstants.CHECK_AVAILABLE_DRONES)
	    List<String> checkAvailableDrones()  {
	    	log.debug("Controller: checkAvailableDrones");
	    	return dronesService.checkAvailableDrones();
	    }
	    
	    @GetMapping(UrlConstants.CHECK_MEDICATION_ITEMS + "{droneNumber}")
	    List<String> checkMedicationItems(@PathVariable(name="droneNumber") @Size(max=MAX_DRONE_NUMBER_LENGTH , message=DRONE_NUMBER_WRONG_LENGTH)
	    @NotEmpty(message=MISSING_DRONE_NUMBER) String droneNumber) {
	    	log.debug("Controller: checkMedicationItems, received {}", droneNumber);
	    	return dronesService.checkMedicationItems(droneNumber);
	    }
	    
	    @GetMapping(UrlConstants.CHECK_BATTERY_CAPACITY + "{droneNumber}")
	    int checkBatteryCapacity(@PathVariable(name="droneNumber") @Size(max=MAX_DRONE_NUMBER_LENGTH , message=DRONE_NUMBER_WRONG_LENGTH)
	    @NotEmpty(message=MISSING_DRONE_NUMBER) String droneNumber) {
	    	log.debug("Controller: checkBatteryCapacity, received {}", droneNumber);
	    	return dronesService.checkBatteryCapacity(droneNumber);
	    }
	    
	    @GetMapping(UrlConstants.CHECK_DRONE_LOADED_ITEM_AMOUNTS)
	    List<DroneItemsAmount> checkDroneLoadedItemAmounts() {
	    	log.debug("Controller: checkDroneLoadedItemAmounts");
	    	return dronesService.checkDroneLoadedItemAmounts();
	    }
	    

}
