package com.sid.controllers;

import java.security.Principal;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import com.sid.exceptions.UserException;
import com.sid.requests.UserRequest;
import com.sid.responses.ErrorMessages;
import com.sid.responses.UserResponse;
import com.sid.services.UserService;
import com.sid.shared.RoleName;
import com.sid.shared.Utils;
import com.sid.shared.dto.UserDto;

@RestController
@RequestMapping("/users")
@Validated
public class UserController {

	@Autowired
	UserService userService;
	@Autowired
	Utils utils;
	@Autowired
	private Validator validator;

	@PostMapping(consumes = { MediaType.MULTIPART_FORM_DATA_VALUE }, produces = { MediaType.APPLICATION_XML_VALUE,
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<?> createUser(@RequestPart("user") String user,
			@RequestPart(value = "file", required = false) MultipartFile images) throws Exception {
		UserRequest userRequest = utils.parseToRequest(user, UserRequest.class);
		Set<ConstraintViolation<UserRequest>> violations = validator.validate(userRequest);
		if (!violations.isEmpty()) {
			List<String> errors = violations.stream().map(v -> v.getPropertyPath() + " : " + v.getMessage()).collect(Collectors.toList());
			return ResponseEntity.badRequest().body(errors);
		}
		String initialImageDir = utils.getImageDir();
		if (userRequest.getFirstName().isEmpty() || userRequest.getLastName().isEmpty()
				|| userRequest.getEmail().isEmpty() || userRequest.getPassword().isEmpty())
			throw new UserException(ErrorMessages.MISSING_REQUIRED_FIELD.getErrorMessage());

		utils.setImageDir("/userImage/");
		String imageName = utils.saveImage(images);
		userRequest.setUserImage(imageName);
		UserDto userDto = utils.mapToDto(userRequest, UserDto.class);
		UserDto createUserDto = userService.createUser(userDto);
		utils.setImageDir(initialImageDir);
		return ResponseEntity.ok(utils.mapToDto(createUserDto, UserResponse.class));
	}

	@PutMapping(path = "/{id}", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE }, produces = {
			MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE })
	public UserResponse modifyUser(@RequestPart("user") String user,
			@RequestPart(value = "file", required = false) MultipartFile images, @PathVariable String id,
			Principal principal) throws Exception {
		UserRequest userRequest = utils.parseToRequest(user, UserRequest.class);
		String initialImageDir = utils.getImageDir();
		if (images != null) {
			utils.setImageDir("/userImage/");
			String imageName = utils.saveImage(images);
			userRequest.setUserImage(imageName);
		} else {
			userRequest.setUserImage(null);
		}
		UserDto userDto = utils.mapToDto(userRequest, UserDto.class);
		UserDto createUserDto = userService.updateUser(id, userDto, principal.getName());
		utils.setImageDir(initialImageDir);
		return utils.mapToDto(createUserDto, UserResponse.class);
	}

	@GetMapping(path = "/role/{roleName}", produces = { MediaType.APPLICATION_XML_VALUE,
			MediaType.APPLICATION_JSON_VALUE })
	public List<UserResponse> getUserByRole(@PathVariable String roleName, @RequestParam(defaultValue = "1") int page,
			@RequestParam(defaultValue = "10") int limit, Principal principal) {

		RoleName role;
		try {
			role = RoleName.valueOf(roleName.toUpperCase());
		} catch (IllegalArgumentException e) {
			throw new UserException(ErrorMessages.NO_SCOPE_FOUND.getErrorMessage());
		}
		List<UserDto> userDto = userService.getUserByRole(role, page, limit, principal.getName());
		return utils.mapToLists(userDto, UserResponse.class);
	}

	@GetMapping(path = "/{id}", produces = { MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE })
	public UserResponse getUser(@PathVariable String id) {
		UserDto userDto = userService.getUserByUserId(id);
		return utils.mapToDto(userDto, UserResponse.class);
	}

	@DeleteMapping(path = "/{id}")
	public void deleteUser(@PathVariable String id, Principal principal) {
		userService.deleteUser(id, principal.getName());
	}

	@GetMapping
	public List<UserResponse> listOfUsers(@RequestParam(defaultValue = "1") int page,
			@RequestParam(defaultValue = "10") int limit, Principal principal) {
		List<UserDto> users = userService.allUsers(page, limit, principal.getName());
		return utils.mapToLists(users, UserResponse.class);
//		   return users.stream()
//	                .map(utils::mapUserDtoToUserResponse)
//	                .collect(Collectors.toList());
	}

}

//@GetMapping
//public List<UserResponse> listOfUsers(@RequestParam(defaultValue = "1") int page,
//		@RequestParam(defaultValue = "10") int limit, Principal principal) {
//	List<UserResponse> usersResponse = new ArrayList<>();
//	List<UserDto> users = userService.allUsers(page, limit, principal.getName());
//	for (UserDto userDto : users) {
//		ModelMapper modelMapper = new ModelMapper();
//		UserResponse user = modelMapper.map(userDto, UserResponse.class);
//		usersResponse.add(user);
//	}
//	return usersResponse;
//}
