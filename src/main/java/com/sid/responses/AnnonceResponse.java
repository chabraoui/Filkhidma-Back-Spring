package com.sid.responses;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class AnnonceResponse {
	private String annonceId;
	private String name;
	private String description;
	private String prix;
	private String images;
	@JsonIgnoreProperties(value = "annonces")
	private CategoryResponse category;
	private VilleResponse ville;
	private Date dateAnnonce;
	@JsonIgnoreProperties(value = "annonces")
	private UserResponse user;
}
