package com.sid.entity; 

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.sid.shared.VilleName;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
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
