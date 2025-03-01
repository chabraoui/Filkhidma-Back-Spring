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
	
	@NotBlank(message="Ce champ ne doit pas etre null !")
	@Size(min=3, message="Ce champ doit avoir au moins 3 Caracteres !")
	private String firstName;
	
	@NotBlank(message="Ce champ ne doit pas etre null !")
	@Size(min=3, message="Ce champ doit avoir au moins 3 Caracteres !")
	private String lastName;
	
	@NotBlank(message="Ce champ ne doit pas etre null !")
	@Email
	private String email;
	
	@NotBlank(message="Ce champ ne doit pas etre null !")
	@Size(min=6, message="mot de passe doit avoir au moins 6 caracteres !")
	@Size(max=19, message="mot de passe doit avoir au max 19 caracteres !")
	private String password;
	
	@Pattern(regexp="^.*\\.(jpg|jpeg|png)$", message="Le fichier d'image doit avoir une extension valide (jpg, jpeg, png.")
	private String userImage;
	
	@Pattern(regexp="^(\\+212|0)(6|7)\\d{8}$", message="Le numéro de téléphone doit être valide (ex: +212 601234567 ou 0612345678).")
	private String tel;
	
	
    private String secretKey;
	


}
