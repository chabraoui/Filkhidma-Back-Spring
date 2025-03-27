package com.sid.controllers;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.validation.ConstraintViolation;
import javax.validation.Valid;
import javax.validation.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import com.sid.exceptions.UserException;
import com.sid.requests.ForgetPasswordRequest;
import com.sid.requests.UpdatePasswordRequest;
import com.sid.requests.UpdateUserRequest;
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
			List<String> errors = violations.stream().map(v -> v.getPropertyPath() + " : " + v.getMessage())
					.collect(Collectors.toList());
			return ResponseEntity.badRequest().body(errors);
		}
		String initialImageDir = utils.getImageDir();
		if (userRequest.getFirstName().isEmpty() || userRequest.getLastName().isEmpty()
				|| userRequest.getEmail().isEmpty() || userRequest.getPassword().isEmpty())
			throw new UserException(ErrorMessages.MISSING_REQUIRED_FIELD.getErrorMessage(), HttpStatus.BAD_REQUEST);

		utils.setImageDir("/userImage/");
		UserDto userDto = utils.mapToDto(userRequest, UserDto.class);
		UserDto createUserDto = userService.createUser(userDto, images);
		utils.setImageDir(initialImageDir);
		return ResponseEntity.ok(utils.mapToDto(createUserDto, UserResponse.class));
	}

	@PutMapping(path = "/{id}", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE }, produces = {
			MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<?> modifyUser(@RequestPart(value = "user", required = false) String user,
			@PathVariable String id, Principal principal) throws Exception {

		UpdateUserRequest userRequest = null;
		UserDto userDto = null;
		if (user != null) {
			userRequest = utils.parseToRequest(user, UpdateUserRequest.class);
			Set<ConstraintViolation<UpdateUserRequest>> violations = validator.validate(userRequest);
			if (!violations.isEmpty()) {
				List<String> errors = violations.stream()
						// .filter(v -> v.getInvalidValue() != null)
						.map(v -> v.getPropertyPath() + ": " + v.getMessage()).collect(Collectors.toList());
				return ResponseEntity.badRequest().body(errors);
			}
			userDto = utils.mapToDto(userRequest, UserDto.class);
		}

		UserDto createUserDto = userService.updateUser(id, userDto, principal.getName());
		return ResponseEntity.ok(utils.mapToDto(createUserDto, UserResponse.class));
	}

	@GetMapping(path = "/role/{roleName}", produces = { MediaType.APPLICATION_XML_VALUE,
			MediaType.APPLICATION_JSON_VALUE })
	public List<UserResponse> getUserByRole(@PathVariable String roleName, @RequestParam(defaultValue = "1") int page,
			@RequestParam(defaultValue = "10") int limit, Principal principal) {

		RoleName role;
		try {
			role = RoleName.valueOf(roleName.toUpperCase());
		} catch (IllegalArgumentException e) {
			throw new UserException(ErrorMessages.NO_SCOPE_FOUND.getErrorMessage(), HttpStatus.NOT_FOUND);
		}
		List<UserDto> userDto = userService.getUserByRole(role, page, limit, principal.getName());
		return utils.mapToLists(userDto, UserResponse.class);
	}

	@GetMapping(path = "/user/{id}", produces = { MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE })
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
	}

	@PostMapping(path = "/forgetpassword")
	public UserResponse saveNewPassword(@RequestBody ForgetPasswordRequest forgetPasswordRequest) {
		String email = forgetPasswordRequest.getEmail();
		String pass = forgetPasswordRequest.getPassword();
		UserDto userdto = userService.saveNewPassword(email, pass);
		return utils.mapToDto(userdto, UserResponse.class);
	}

	@PutMapping(path = "/password/{id}", consumes = { MediaType.APPLICATION_JSON_VALUE,
			MediaType.APPLICATION_XML_VALUE }, produces = { MediaType.APPLICATION_XML_VALUE,
					MediaType.APPLICATION_JSON_VALUE })
	public UserResponse modifyPassword(@PathVariable String id, @RequestBody @Valid UpdatePasswordRequest Password,
			Principal principal) {
		UserDto userdto = userService.updatePassword(id, Password, principal.getName());
		return utils.mapToDto(userdto, UserResponse.class);
	}

	@PutMapping(path = "/userImage/{id}")
	public void modifyUserImage(@PathVariable String id, Principal principal, @RequestPart("file") MultipartFile images)
			throws Exception {
		String initialImageDir = utils.getImageDir();
		utils.setImageDir("/userImage/");
		userService.modifyUserImage(id, principal.getName(), images);
		utils.setImageDir(initialImageDir);

	}

	@DeleteMapping(path = "/userImage/{id}")
	public void deleteUserImage(@PathVariable String id, Principal principal) throws Exception {
		String initialImageDir = utils.getImageDir();
		utils.setImageDir("/userImage/");
		userService.deleteUserImage(id, principal.getName());
		utils.setImageDir(initialImageDir);

	}

	@GetMapping(path = "/user/userImage/{id}")
	public ResponseEntity<byte[]> getPhoto(@PathVariable String id) throws Exception {
		String initialImageDir = utils.getImageDir();
		UserDto userDto = userService.getUserByUserId(id);
		utils.setImageDir("/userImage/");
		String imageDirectory = System.getProperty("user.dir") + utils.getImageDir();
		Path imagePath = Paths.get(imageDirectory + File.separator + userDto.getUserImage());
		if (!Files.exists(imagePath)) {
			return ResponseEntity.notFound().build();
		}
		byte[] imageBytes = Files.readAllBytes(imagePath);
		String type = Files.probeContentType(imagePath);
		type = type != null ? type : "application/octet-stream";
		utils.setImageDir(initialImageDir);
		return ResponseEntity.ok().contentType(MediaType.valueOf(type)).body(imageBytes);
	}
}
