package com.sid.shared.dto;

import java.io.Serializable;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class AnnonceDto implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5241778935097029204L;
	private long id;
	private String annonceId;
	private String name;
	private String description;
	private String prix;
	private String images;
	private Date dateAnnonce;
	@JsonIgnoreProperties(value = "annonces")
	private CategoryDto category;
	private VilleDto ville;
	@JsonIgnoreProperties(value = "annonces")
	private UserDto user;

}
