package com.sid.requests;

import javax.validation.constraints.*;
import lombok.Data;

@Data
public class UserLoginRequest {
	@NotBlank(message="Ce champ ne doit pas etre null !")
	@Email
	private String email;
	@NotBlank(message="Ce champ ne doit pas etre null !")
	@Size(min=8, message="mot de passe doit avoir au moins 8 caracteres !")
	@Size(max=30, message="mot de passe doit avoir au max 30 caracteres !")
	private String password;

	

}
