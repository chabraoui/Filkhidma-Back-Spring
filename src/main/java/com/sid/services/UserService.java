package com.sid.services;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.multipart.MultipartFile;

import com.sid.requests.UpdatePasswordRequest;
import com.sid.shared.RoleName;
import com.sid.shared.dto.UserDto;

@Transactional
public interface UserService extends UserDetailsService {

	UserDto createUser(UserDto userDto, MultipartFile images) throws Exception;

	UserDto getUser(String email);

	UserDto getUserByUserId(String userId);

	UserDto updateUser(String userId, UserDto userDto, String email);

	void deleteUser(String userId, String email);

	List<UserDto> allUsers(int page, int limit, String email);

	List<UserDto> getUserByRole(RoleName role, int page, int limit, String email);

	UserDto updatePassword(String id, UpdatePasswordRequest updatePasswordRequest, String email);

	UserDto saveNewPassword(String email, String newPasswords);

	void modifyUserImage(String id, String email, MultipartFile images)throws Exception;

	void deleteUserImage(String id, String email);

}
