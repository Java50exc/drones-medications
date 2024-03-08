package telran.drones.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import telran.drones.model.EventLog;

public interface EventLogRepo extends JpaRepository<EventLog, Long> {
	EventLog findFirstByDroneNumberOrderByTimestampDesc(String droneNumber);
	int countByDroneNumber(String droneNumber);
	List<EventLog> findByDroneNumberOrderByTimestampAsc(String droneNumber);

}
