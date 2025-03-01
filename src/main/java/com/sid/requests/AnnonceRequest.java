package com.sid.requests;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import com.sid.shared.dto.CategoryDto;
import com.sid.shared.dto.VilleDto;

import lombok.*;


@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class AnnonceRequest {
	
	@NotBlank(message="Ce champ ne doitpas etre null !")
	@Size(min=3, message="Ce champ doit avoir au moins 3 Caracteres !")
	private String name;
	
	@NotBlank(message="Ce champ ne doitpas etre null !")
	@Size(min=3, message="Ce champ doit avoir au moins 3 Caracteres !")
	private String description;
	
	@NotBlank(message="Ce champ ne doitpas etre null !")
	private String prix;
	
	private String images;

	private CategoryDto category;

	private VilleDto ville;
	
	
}
