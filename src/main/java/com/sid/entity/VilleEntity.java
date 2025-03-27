package com.sid.entity; 

import java.util.ArrayList;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import com.sid.shared.VilleName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity(name = "ville")
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString

public class VilleEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	

	@Enumerated(EnumType.STRING)
	private VilleName villeName;
	
	@OneToMany(mappedBy="ville",fetch = FetchType.LAZY)
	private List<AnnonceEntity> annonces = new ArrayList<>();

}
