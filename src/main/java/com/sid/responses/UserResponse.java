package com.sid.responses;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.sid.shared.dto.AnnonceDto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class UserResponse {
	private String userId;
	private String firstName;
	private String lastName;
	private String email;
	private String tel;
	private String userImage;
	private List<RoleResponse> role;
	@JsonIgnoreProperties(value = "user")
	private List<AnnonceDto> annonces;

}
