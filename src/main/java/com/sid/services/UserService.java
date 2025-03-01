package com.sid.services;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.security.core.userdetails.UserDetailsService;
import com.sid.shared.RoleName;
import com.sid.shared.dto.UserDto;
@Transactional

public interface UserService extends UserDetailsService  {
	
	UserDto createUser(UserDto userDto);
	
	UserDto getUser(String email);
	
	UserDto getUserByUserId(String userId);
	
	UserDto updateUser(String userId, UserDto userDto,String email);
	
	void deleteUser(String userId, String email);
	
	List<UserDto> allUsers(int page,int limit,String email);	
	
	List<UserDto> getUserByRole(RoleName role, int page,int limit, String email);
	
	//String saveImage(MultipartFile images) throws Exception;

	//UserRequest parseUserRequest(String user) throws JsonParseException, JsonProcessingException;

	
}
