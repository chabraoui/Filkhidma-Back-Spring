package com.sid.requests;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import lombok.Data;

@Data
public class UpdatePasswordRequest {
	@NotBlank(message = "Ce champ ne doit etre null !")
	@Size(min = 8, message = "mot de passe doit avoir au moins 8 caracteres !")
	@Size(max = 30, message = "mot de passe doit avoir au max 30 caracteres !")
	private String lastPassword;
	@NotBlank(message = "Ce champ ne doit etre null !")
	@Size(min = 8, message = "mot de passe doit avoir au moins 8 caracteres !")
	@Size(max = 30, message = "mot de passe doit avoir au max 30 caracteres !")
	@Pattern(regexp = "(?=^.{8,}$)((?=.*\\d)|(?=.*\\W+))(?![.\\n])(?=.*[A-Z])(?=.*[a-z]).*$", message = "ce mot de passe doit avoir des lettres en Maj et Minsc et numero et plus que 8 char")
	private String newPassword;

}
