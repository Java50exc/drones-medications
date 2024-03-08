package telran.drones.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import telran.drones.dto.DroneItemsAmount;
import telran.drones.dto.State;
import telran.drones.model.*;

public interface DronesRepo extends JpaRepository<Drone, String>{
	
	@Query("select number from Drone where state=:state")
	List<String> findDronesByState(State state);
	
	@Query("select drone.number as number, count(el.droneNumber) as amount from Drone drone left join EventLog el on drone.number = el.droneNumber group by number order by amount desc")
	List<DroneItemsAmount> findDroneItemsAmount();
	
	

}
