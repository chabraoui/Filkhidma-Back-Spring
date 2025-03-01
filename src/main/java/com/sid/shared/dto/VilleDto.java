package com.sid.shared.dto;


import com.sid.shared.VilleName;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class VilleDto {

	
	private long id;
	private VilleName villeName;
	//private List<AnnonceDto> annonce;
}
