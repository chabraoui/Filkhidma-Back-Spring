package com.sid.shared.dto;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;


@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class UserDto implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4607962803987034451L;
	private long id;
	private String userId;
	private String firstName;
	private String lastName;
	private String email; 
	private String password;
	private String tel;
	private String userImage;
	private String encryptePassword;
    @ToString.Exclude
	private List<RoleDto> role;
    @ToString.Exclude 
	private List<AnnonceDto> annonces;
	private String secretKey;
	
}
