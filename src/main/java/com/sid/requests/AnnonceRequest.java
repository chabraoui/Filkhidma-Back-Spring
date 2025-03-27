package com.sid.requests;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import com.sid.shared.dto.CategoryDto;
import com.sid.shared.dto.VilleDto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class AnnonceRequest {

	@NotBlank(message = "Ce champ ne doitpas etre null !")
	@Size(min = 3, message = "Ce champ doit avoir au moins 3 Caracteres !")
	@Size(max = 30, message = "Ce champ doit avoir au max 30 Caracteres !")
	private String name;

	@NotBlank(message = "Ce champ ne doitpas etre null !")
	@Size(min = 3, message = "Ce champ doit avoir au moins 3 Caracteres !")
	@Size(max = 150, message = "Ce champ doit avoir au max 150 Caracteres !")
	private String description;

	@NotBlank(message = "Ce champ ne doit pas être null !")
	@Pattern(regexp = "^(?:[1-9][0-9]{0,5}|100000)(\\.[0-9]{1,2})?$", message = "Le prix doit être un nombre supérieur à 0 et inférieur ou égal à 100000 !")
	private String prix;

	
	@Pattern(regexp="^.*\\.(jpg|jpeg|png)$", message="Le fichier d'image doit avoir une extension valide (jpg, jpeg, png.")
	private String images;
	
	@NotBlank(message = "Ce champ ne doitpas etre null !")
	private CategoryDto category;
	
	@NotBlank(message = "Ce champ ne doitpas etre null !")
	private VilleDto ville;

}
