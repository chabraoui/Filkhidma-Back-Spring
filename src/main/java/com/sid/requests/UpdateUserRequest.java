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
public class UpdateUserRequest {
	@Size(min = 3, message = "Ce champ doit avoir au moins 3 Caracteres !")
	@Size(max = 20, message = "Ce champ doit avoir au max 20 Caracteres !")
	private String firstName;

	@Size(min = 3, message = "Ce champ doit avoir au moins 3 Caracteres !")
	@Size(max = 20, message = "Ce champ doit avoir au max 20 Caracteres !")
	private String lastName;

	@Pattern(regexp = "^(\\+212|0)(6|7)\\d{8}$", message = "Le numéro de téléphone doit être valide (ex: +212 601234567 ou 0612345678).")
	private String tel;
}
