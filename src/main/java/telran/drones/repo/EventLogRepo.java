package telran.drones.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import telran.drones.model.EventLog;

public interface EventLogRepo extends JpaRepository<EventLog, Long> {
	
	@Query(value="select log from EventLog log where log.droneNumber = :droneNumber order by log.timestamp desc limit 1", nativeQuery=false)
	EventLog findLastEventLogByDroneNumber(String droneNumber);
	
	EventLog findFirstByDroneNumberOrderByTimestampDesc(String droneNumber);
	
	int countByDroneNumber(String droneNumber);
	
	List<EventLog> findByDroneNumberOrderByTimestampAsc(String droneNumber);
	


}
