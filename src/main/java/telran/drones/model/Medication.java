package telran.drones.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name="medications")
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class Medication {
	@Column(name="medication_name")
	String medicationName;
	
	int weight;
	
	@Id
	@Column(name="medication_code")
	String medicationCode;
	
	
	

}
