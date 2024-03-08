package telran.drones.repo;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import telran.drones.dto.DroneItemsAmount;
import telran.drones.model.*;

public interface EventLogRepo extends JpaRepository<EventLog, Long>{
	@Query("select medicationCode from EventLog where droneNumber=:number")
	List<String> findMedicationsByDroneNumber(String number);
	

	

	
	

}
