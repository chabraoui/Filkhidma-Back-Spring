package com.sid.requests;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class UserRequest {

	@NotBlank(message = "Ce champ ne doit pas etre null !")
	@Size(min = 3, message = "Ce champ doit avoir au moins 3 Caracteres !")
	@Size(max = 20, message = "Ce champ doit avoir au max 20 Caracteres !")
	private String firstName;

	@NotBlank(message = "Ce champ ne doit pas etre null !")
	@Size(min = 3, message = "Ce champ doit avoir au moins 3 Caracteres !")
	@Size(max = 20, message = "Ce champ doit avoir au max 20 Caracteres !")
	private String lastName;

	@NotBlank(message = "Ce champ ne doit pas etre null !")
	@Email
	private String email;

	@NotBlank(message = "Ce champ ne doit pas etre null !")
	@Size(min = 8, message = "mot de passe doit avoir au moins 8 caracteres !")
	@Size(max = 30, message = "mot de passe doit avoir au max 30 caracteres !")
	@Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{8,}$", message = "Le mot de passe doit contenir au moins une lettre majuscule, une lettre minuscule, un chiffre et un caractère spécial.")
	private String password;

	@Pattern(regexp = "^.*\\.(jpg|jpeg|png)$", message = "Le fichier d'image doit avoir une extension valide (jpg, jpeg, png.")
	private String userImage;

	@Pattern(regexp = "^(\\+212|0)(6|7)\\d{8}$", message = "Le numéro de téléphone doit être valide (ex: +212 601234567 ou 0612345678).")
	private String tel;

	@Size(min = 5, message = "mot de passe doit avoir au moins 5 caracteres !")
	@Size(max = 20, message = "mot de passe doit avoir au max 20 caracteres !")
	private String secretKey;

}
