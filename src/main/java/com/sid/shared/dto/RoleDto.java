package com.sid.shared.dto;

import com.sid.shared.RoleName;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class RoleDto {

	private long id;
	private RoleName roleName;	
	//private List<UserDto> users;
}
