package com.sid.services.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import com.sid.entity.RoleEntity;
import com.sid.entity.UserEntity;
import com.sid.exceptions.UserException;
import com.sid.repository.RoleRepository;
import com.sid.repository.UserRepository;
import com.sid.responses.ErrorMessages;
import com.sid.security.SecurityConstants;
import com.sid.services.UserService;
import com.sid.shared.RoleName;
import com.sid.shared.Utils;
import com.sid.shared.dto.RoleDto;
import com.sid.shared.dto.UserDto;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	UserRepository userRepository;
	@Autowired
	RoleRepository roleRepository;
	@Autowired
	BCryptPasswordEncoder bCryptPasswordEncoder;
	@Autowired
	Utils utils;

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		UserEntity userEntity = userRepository.findByEmail(email);

		if (userEntity == null)
			throw new UsernameNotFoundException(email);

		return new User(userEntity.getEmail(), userEntity.getEncryptePassword(), new ArrayList<>());
	}

	@Override
	public UserDto createUser(UserDto userDto) {
		UserEntity searchUserByEmail = userRepository.findByEmail(userDto.getEmail());

		if (searchUserByEmail != null)
			throw new UserException(ErrorMessages.RECORD_ALREADY_EXISTS.getErrorMessage());
		ModelMapper modelMapper = new ModelMapper();
		UserEntity userEntity = modelMapper.map(userDto, UserEntity.class);
		userEntity.setEncryptePassword(bCryptPasswordEncoder.encode(userDto.getPassword()));
		userEntity.setUserId(utils.genereteStringId(32));

		List<RoleEntity> role = new ArrayList<>();
		String secretKey = userDto.getSecretKey();

		if (secretKey != null && secretKey.equals(SecurityConstants.SUPER_ADMIN_SECRET_KEY)) {
			RoleEntity superAdminRole = roleRepository.findByRoleName(RoleName.SUPER_ADMIN);
			RoleEntity userRole = roleRepository.findByRoleName(RoleName.USER);
			role.add(superAdminRole);
			role.add(userRole);
		} else if (secretKey != null && secretKey.equals(SecurityConstants.ADMIN_SECRET_KEY)) {
			RoleEntity adminRole = roleRepository.findByRoleName(RoleName.ADMIN);
			RoleEntity userRole = roleRepository.findByRoleName(RoleName.USER);
			role.add(adminRole);
			role.add(userRole);
		} else {
			RoleEntity userRole = roleRepository.findByRoleName(RoleName.USER);
			role.add(userRole);
		}

		userEntity.setRole(role);
		UserEntity userSave = userRepository.save(userEntity);

		// si on utilise pas stream on doit ignoré dans RoleDto l'attribut list de users

		List<RoleDto> rolesDto = userSave.getRole().stream().map(roleEntity -> {
			RoleDto roleDto = new RoleDto();
			roleDto.setId(roleEntity.getId());
			roleDto.setRoleName(roleEntity.getRoleName());
			return roleDto;
		}).collect(Collectors.toList());

		UserDto savedto = utils.mapToDto(userSave, UserDto.class);

		savedto.setRole(rolesDto);

		return savedto;
	}

	@Override
	public UserDto getUser(String email) {
		UserEntity userEntity = userRepository.findByEmail(email);
		if (userEntity == null)
			throw new UsernameNotFoundException(email);
		return utils.mapToDto(userEntity, UserDto.class);
	}

	@Override
	public UserDto getUserByUserId(String userId) {
		UserEntity userEntity = userRepository.findByUserId(userId);
		if (userEntity == null)
			throw new UserException(ErrorMessages.NO_RECORD_FOUND.getErrorMessage());
		return utils.mapToDto(userEntity, UserDto.class);

	}

	@Override
	public UserDto updateUser(String userId, UserDto userDto, String email) {
		UserEntity userEntity = userRepository.findByUserId(userId);
		if (userEntity == null)
			throw new UserException(ErrorMessages.NO_RECORD_FOUND.getErrorMessage());

		// check le email de user authentifié
		UserEntity userRequester = userRepository.findByEmail(email);
		if (userRequester == null)
			throw new UserException(ErrorMessages.NO_AUTH_FOUND.getErrorMessage());

		// Vérification si c'est le créateur de l'annonce
		boolean isOwner = userEntity.getEmail().equals(email);

		// Si l'utilisateur n'est pas le créateur il ne peut maj l'utilisateur
		if (!isOwner) {
			throw new UserException(ErrorMessages.NO_AUTORIZ_REQUEST.getErrorMessage());
		}

		// Vérification des champs obligatoires
		if (userDto.getFirstName() == null || userDto.getFirstName().isEmpty())
			throw new UserException(ErrorMessages.MISSING_REQUIRED_FIELD.getErrorMessage());

		if (userDto.getLastName() == null || userDto.getLastName().isEmpty())
			throw new UserException(ErrorMessages.MISSING_REQUIRED_FIELD.getErrorMessage());

		// si user entre une nvll image dans la modification, on supprime l'image existe
		// en server et on persist la nouvelle
		if (userDto.getUserImage() != null && !userDto.getUserImage().equals(userEntity.getUserImage())) {
			String oldImageName = userEntity.getUserImage();

			if (oldImageName != null) {
				utils.setImageDir("/userImage/");
				Utils.deleteImage(oldImageName);
			}
		}
		userEntity.setUserImage(userDto.getUserImage());
		userEntity.setFirstName(userDto.getFirstName());
		userEntity.setLastName(userDto.getLastName());
		UserEntity userUpdated = userRepository.save(userEntity);
		return utils.mapToDto(userUpdated, UserDto.class);

	}

	@Override
	public void deleteUser(String userId, String email) {
		UserEntity userEntity = userRepository.findByUserId(userId);
		String initialImageDir = utils.getImageDir();

		if (userEntity == null)
			throw new UserException(ErrorMessages.NO_RECORD_FOUND.getErrorMessage());

		// Récupérer l'utilisateur qui fait la requête
		UserEntity userRequester = userRepository.findByEmail(email);
		if (userRequester == null)
			throw new UserException(ErrorMessages.NO_AUTH_FOUND.getErrorMessage());

		// Vérification si l'utilisateur authentifié est le même que celui qui tente de
		// supprimer son compte
		boolean isOwner = userEntity.getEmail().equals(email);

		// Vérification des rôles de l'utilisateur qui fait la requête
		boolean isAdmin = userRequester.getRole().stream().anyMatch(role -> role.getRoleName().equals(RoleName.ADMIN));

		boolean isSuperAdmin = userRequester.getRole().stream()
				.anyMatch(role -> role.getRoleName().equals(RoleName.SUPER_ADMIN));
		// 1. Si l'utilisateur veut supprimer son propre compte ou si il est un
		// SUPER_ADMIN
		if (isOwner || isSuperAdmin) {
			String imageName = userEntity.getUserImage();
			if (imageName != null) {
				utils.setImageDir("/userImage/");
				Utils.deleteImage(imageName);
			}
			utils.setImageDir(initialImageDir);
			userRepository.delete(userEntity);
			return;
		}

		// 2. Si l'utilisateur est un Admin
		if (isAdmin) {
			// Un Admin peut supprimer uniquement un utilisateur avec le rôle "USER"
			if (userEntity.getRole().stream().anyMatch(role -> role.getRoleName().equals(RoleName.ADMIN)
					|| role.getRoleName().equals(RoleName.SUPER_ADMIN))) {
				throw new UserException(ErrorMessages.NO_AUTORIZ_REQUEST.getErrorMessage());
			}
			String imageName = userEntity.getUserImage();
			if (imageName != null) {
				utils.setImageDir("/userImage/");
				Utils.deleteImage(imageName);
			}
			utils.setImageDir(initialImageDir);
			userRepository.delete(userEntity);
			return;
		}
		throw new UserException(ErrorMessages.NO_AUTORIZ_REQUEST.getErrorMessage());
	}

	@Override
	public List<UserDto> allUsers(int page, int limit, String email) {
		UserEntity userRequester = userRepository.findByEmail(email);
		Pageable pagerequest = utils.createPageable(page, limit);
		boolean isAdminOrSuperAdmin = userRequester.getRole().stream().anyMatch(
				role -> role.getRoleName().equals(RoleName.ADMIN) || role.getRoleName().equals(RoleName.SUPER_ADMIN));
		if (!isAdminOrSuperAdmin)
			throw new UserException(ErrorMessages.NO_AUTORIZ_REQUEST.getErrorMessage());
		Page<UserEntity> userPage = userRepository.findAll(pagerequest);
		List<UserEntity> users = userPage.getContent();
		return utils.mapToLists(users, UserDto.class);
//		  return users.stream()
//	                .map(user -> utils.mapToDto(user, UserDto.class))
//	                .collect(Collectors.toList());
	}

	@Override
	public List<UserDto> getUserByRole(RoleName role, int page, int limit, String email) {
		UserEntity userRequester = userRepository.findByEmail(email);
		Pageable pagerequest = utils.createPageable(page, limit);
		boolean isAdminOrSuperAdmin = userRequester.getRole().stream()
				.anyMatch(roleAuth -> roleAuth.getRoleName().equals(RoleName.ADMIN)
						|| roleAuth.getRoleName().equals(RoleName.SUPER_ADMIN));
		if (!isAdminOrSuperAdmin)
			throw new UserException(ErrorMessages.NO_AUTORIZ_REQUEST.getErrorMessage());
		RoleEntity roleEntity = roleRepository.findByRoleName(role);
		if (roleEntity == null)
			throw new UserException(ErrorMessages.NO_SCOPE_FOUND.getErrorMessage());
		Page<UserEntity> usersByRole = userRepository.findByRole(roleEntity, pagerequest);
		List<UserEntity> userEntity = usersByRole.getContent();
		if (userEntity.isEmpty())
			throw new UserException(ErrorMessages.NO_USER_WITH_SCOPE.getErrorMessage() + role);

		return utils.mapToLists(userEntity, UserDto.class);
	}

}
