package com.sid.requests;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class UpdateAnnonceRequest {

	@Size(min = 3, message = "Ce champ doit avoir au moins 3 Caracteres !")
	@Size(max = 30, message = "Ce champ doit avoir au max 30 Caracteres !")
	private String name;

	@Size(min = 3, message = "Ce champ doit avoir au moins 3 Caracteres !")
	@Size(max = 150, message = "Ce champ doit avoir au max 150 Caracteres !")
	private String description;

	@Pattern(regexp = "^(?:[1-9][0-9]{0,5}|100000)(\\.[0-9]{1,2})?$", message = "Le prix doit être un nombre supérieur à 0 et inférieur ou égal à 100000 !")
	private String prix;
}
