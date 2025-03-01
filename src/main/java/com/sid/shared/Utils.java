package com.sid.shared;

import java.io.File;
import java.security.SecureRandom;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sid.responses.AnnonceResponse;
import com.sid.responses.RoleResponse;
import com.sid.responses.UserResponse;
import com.sid.shared.dto.UserDto;

@Component
public class Utils {
	private final Random RANDOM = new SecureRandom();
	private final String ALPHABET = "0123456789ABCDEFGHIJKLMNOPQERSTUVWXYZabcdefjhijklmnopqrstuvwxyz";
	private static String IMAGE_DIR = "/annonceImage/";
	private final ModelMapper modelMapper = new ModelMapper();

	public void setImageDir(String newImageDir) {
		IMAGE_DIR = newImageDir;
	}

	public String getImageDir() {
		return IMAGE_DIR;
	}

	public String genereteStringId(int lenght) {
		StringBuilder returnValue = new StringBuilder(lenght);
		for (int i = 0; i < lenght; i++) {
			returnValue.append(ALPHABET.charAt(RANDOM.nextInt(ALPHABET.length())));
		}
		return new String(returnValue);
	}

	public static void deleteImage(String imageName) {
		String imageDirectory = System.getProperty("user.dir") + IMAGE_DIR;
		File imageFile = new File(imageDirectory + File.separator + imageName);
		if (imageFile.exists()) {
			imageFile.delete();
		}
	}

	public String saveImage(MultipartFile image) throws Exception {
		if (image == null) {
			return null;
		}

		String filename = image.getOriginalFilename();
		String modifiedFileName = FilenameUtils.getBaseName(filename) + "_" + System.currentTimeMillis() + "."
				+ FilenameUtils.getExtension(filename);
		String imageDirectory = System.getProperty("user.dir") + IMAGE_DIR;
		File directory = new File(imageDirectory);
		if (!directory.exists()) {
			directory.mkdirs();
		}
		File serverFile = new File(imageDirectory + File.separator + modifiedFileName);

		try {
			FileUtils.writeByteArrayToFile(serverFile, image.getBytes());
		} catch (Exception e) {
			throw new RuntimeException("Failed to save image", e);
		}

		return modifiedFileName;
	}

	public <T> T parseToRequest(String json, Class<T> clazz) throws JsonProcessingException {
		return new ObjectMapper().readValue(json, clazz);
	}

	public Pageable createPageable(int page, int limit) {
		return PageRequest.of(page > 0 ? page - 1 : 0, limit);
	}

	public <T> T mapToDto(Object entity, Class<T> dtoClass) {
		return modelMapper.map(entity, dtoClass); // Conversion vers DTO
	}

	public <E, D> List<D> mapToLists(List<E> entities, Class<D> dtoClass) {
		return entities.stream().map(entity -> modelMapper.map(entity, dtoClass)) // Mapper chaque entité vers le DTO
				.collect(Collectors.toList()); // Collecter les résultats dans une liste
	}

//	public UserResponse mapUserDtoToUserResponse(UserDto userDto) {
//		UserResponse userResponse = modelMapper.map(userDto, UserResponse.class);
//
//		if (userDto.getAnnonces() != null) {
//			userResponse.setAnnonces(mapToLists(userDto.getAnnonces(), AnnonceResponse.class));
//		}
//
//		if (userDto.getRole() != null) {
//			userResponse.setRole(mapToLists(userDto.getRole(), RoleResponse.class));
//		}
//		System.out.println(userResponse);
//		return userResponse;
//	}

}

//public List<AnnonceDto> mapToList(List<AnnonceEntity> entities) {
//return modelMapper.map(entities, new TypeToken<List<AnnonceDto>>() {
//}.getType());
//}
//public AnnonceRequest parseToRequest(String annonce) throws JsonParseException, JsonProcessingException {
//return new ObjectMapper().readValue(annonce, AnnonceRequest.class);
//}
