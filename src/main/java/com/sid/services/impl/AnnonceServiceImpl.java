package com.sid.services.impl;

import java.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.sid.ByteArrayMultipartFile;
import com.sid.entity.*;
import com.sid.exceptions.*;
import com.sid.repository.*;
import com.sid.responses.ErrorMessages;
import com.sid.services.*;
import com.sid.shared.*;
import com.sid.shared.dto.*;

@Service
public class AnnonceServiceImpl implements AnnonceService {

	@Autowired
	AnnonceRepository annonceRepository;
	@Autowired
	UserRepository userRepository;
	@Autowired
	CategoryRepository categoryRepository;
	@Autowired
	VilleRepository villeRepository;
	@Autowired
	Utils utils;
	@Autowired
	private NotificationService notificationService;
	@Autowired
	private EmailService emailService;
	@Autowired
	private RedisTemplate<String, byte[]> redisTemplate;
	@Autowired
	private RedisTemplate<String, AnnonceDto> redisTemplateAnnonce;

	@Override
	public AnnonceDto createAnnonce(AnnonceDto annonce, String email, MultipartFile images) throws Exception {
		UserEntity userEntity = userRepository.findByEmail(email);
		Optional.ofNullable(userEntity).orElseThrow(
				() -> new UserException(ErrorMessages.NO_AUTH_FOUND.getErrorMessage(), HttpStatus.NOT_FOUND));
		boolean isUser = userEntity.getRole().stream().anyMatch(role -> role.getRoleName().equals(RoleName.USER))
				&& userEntity.getRole().size() == 1;
		if (!isUser) {
			throw new UserException(ErrorMessages.NO_AUTORIZ_REQUEST.getErrorMessage(), HttpStatus.FORBIDDEN);
		}

		VilleEntity villeEntity = villeRepository.findByVilleName(annonce.getVille().getVilleName());
		Optional.ofNullable(villeEntity).orElseThrow(
				() -> new AnnonceException(ErrorMessages.NO_CITY_FOUND.getErrorMessage(), HttpStatus.NOT_FOUND));

		CategoryEntity categoryEntity = categoryRepository.findByCategoryName(annonce.getCategory().getCategoryName());
		Optional.ofNullable(categoryEntity).orElseThrow(
				() -> new AnnonceException(ErrorMessages.NO_CAT_FOUND.getErrorMessage(), HttpStatus.NOT_FOUND));
		annonce.setAnnonceId(utils.genereteStringId(30));
		annonce.setUser(utils.mapToDto(userEntity, UserDto.class));
		annonce.setDateAnnonce(new Date());
		annonce.setCategory(utils.mapToDto(categoryEntity, CategoryDto.class));
		annonce.setVille(utils.mapToDto(villeEntity, VilleDto.class));
		if (images != null && !images.isEmpty()) {
			redisTemplate.opsForValue().set("image:" + annonce.getAnnonceId(), images.getBytes());
		}
		redisTemplateAnnonce.opsForValue().set("annonce:" + annonce.getAnnonceId(), annonce);
		notificationService.notifySuperAdmins(annonce);
		return annonce;
	}

	@Override
	public AnnonceDto approveAnnonce(String annonceId, String email) throws Exception {
		UserEntity userEntity = userRepository.findByEmail(email);
		Optional.ofNullable(userEntity).orElseThrow(
				() -> new UserException(ErrorMessages.NO_AUTH_FOUND.getErrorMessage(), HttpStatus.NOT_FOUND));

		boolean isSuperAdmin = userEntity.getRole().stream()
				.anyMatch(role -> role.getRoleName().equals(RoleName.SUPER_ADMIN));

		if (!isSuperAdmin) {
			throw new UserException(ErrorMessages.NO_AUTORIZ_REQUEST.getErrorMessage(), HttpStatus.FORBIDDEN);
		}

		AnnonceDto annonceToApprove = redisTemplateAnnonce.opsForValue().get("annonce:" + annonceId);
		if (annonceToApprove == null) {
			throw new AnnonceException(ErrorMessages.NO_RECORD_FOUND.getErrorMessage(), HttpStatus.NOT_FOUND);
		}

		byte[] img = redisTemplate.opsForValue().get("image:" + annonceId);
		MultipartFile imageFile = new ByteArrayMultipartFile(img, "image_" + ".jpg");
		annonceToApprove.setImages(utils.saveImage(imageFile));

		AnnonceEntity annonceEntity = utils.mapToDto(annonceToApprove, AnnonceEntity.class);
		AnnonceEntity annonceEntitySave = annonceRepository.save(annonceEntity);

		emailService.sendEmailToUser(annonceToApprove);

		if (redisTemplate.hasKey("image:" + annonceId)) {
			redisTemplate.delete("image:" + annonceId);
		}
		if (redisTemplateAnnonce.hasKey("annonce:" + annonceId)) {
			redisTemplateAnnonce.delete("annonce:" + annonceId);
		}

		return utils.mapToDto(annonceEntitySave, AnnonceDto.class);
	}

	@Override
	public void annonceNotApprouved(String annonceId, String email) {
		UserEntity userEntity = userRepository.findByEmail(email);
		Optional.ofNullable(userEntity).orElseThrow(
				() -> new UserException(ErrorMessages.NO_AUTH_FOUND.getErrorMessage(), HttpStatus.NOT_FOUND));

		boolean isSuperAdmin = userEntity.getRole().stream()
				.anyMatch(role -> role.getRoleName().equals(RoleName.SUPER_ADMIN));

		if (!isSuperAdmin) {
			throw new UserException(ErrorMessages.NO_AUTORIZ_REQUEST.getErrorMessage(), HttpStatus.FORBIDDEN);
		}

		if (redisTemplate.hasKey("image:" + annonceId)) {
			redisTemplate.delete("image:" + annonceId);
		}
		if (redisTemplateAnnonce.hasKey("annonce:" + annonceId)) {
			redisTemplateAnnonce.delete("annonce:" + annonceId);
		}
	}

	@Override
	public List<AnnonceDto> getPendingAnnonces(String email) {
		UserEntity userEntity = userRepository.findByEmail(email);
		Optional.ofNullable(userEntity).orElseThrow(
				() -> new UserException(ErrorMessages.NO_AUTH_FOUND.getErrorMessage(), HttpStatus.NOT_FOUND));
		boolean isSuperAdmin = userEntity.getRole().stream()
				.anyMatch(role -> role.getRoleName().equals(RoleName.SUPER_ADMIN));

		if (!isSuperAdmin) {
			throw new UserException(ErrorMessages.NO_AUTORIZ_REQUEST.getErrorMessage(), HttpStatus.FORBIDDEN);
		}

		Set<String> annonceKeys = redisTemplateAnnonce.keys("annonce:*");
		List<AnnonceDto> pendingAnnonces = new ArrayList<>();
		if (annonceKeys != null && !annonceKeys.isEmpty()) {
			for (String annonceKey : annonceKeys) {
				AnnonceDto annonce = redisTemplateAnnonce.opsForValue().get(annonceKey);
				if (annonce != null) {
					pendingAnnonces.add(annonce);
				}
			}
		}
		if (pendingAnnonces.isEmpty()) {
			throw new AnnonceException(ErrorMessages.LIST_EMPTY.getErrorMessage(), HttpStatus.NOT_FOUND);
		}

		return pendingAnnonces;
	}

	@Override
	public AnnonceDto updateAnnonce(AnnonceDto annonce, String annonceId, String email) {
		AnnonceEntity annonceEntity = annonceRepository.findByAnnonceId(annonceId);

		Optional.ofNullable(annonceEntity).orElseThrow(
				() -> new AnnonceException(ErrorMessages.NO_RECORD_FOUND.getErrorMessage(), HttpStatus.NOT_FOUND));

		boolean isOwner = annonceEntity.getUser().getEmail().equals(email);
		if (!isOwner) {
			throw new UserException(ErrorMessages.NO_AUTORIZ_REQUEST.getErrorMessage(), HttpStatus.FORBIDDEN);
		}

		if (annonce.getDescription() != null)
			annonceEntity.setDescription(annonce.getDescription());
		if (annonce.getPrix() != null)
			annonceEntity.setPrix(annonce.getPrix());
		if (annonce.getName() != null)
			annonceEntity.setName(annonce.getName());
		annonceRepository.save(annonceEntity);
		return utils.mapToDto(annonceEntity, AnnonceDto.class);
	}

	@Override
	public void deleteAnnonce(String annonceId, String email) {
		AnnonceEntity annonce = annonceRepository.findByAnnonceId(annonceId);
		Optional.ofNullable(annonce).orElseThrow(
				() -> new AnnonceException(ErrorMessages.NO_RECORD_FOUND.getErrorMessage(), HttpStatus.NOT_FOUND));
		UserEntity userRequester = userRepository.findByEmail(email);
		Optional.ofNullable(userRequester).orElseThrow(
				() -> new UserException(ErrorMessages.NO_AUTH_FOUND.getErrorMessage(), HttpStatus.NOT_FOUND));
		boolean isOwner = annonce.getUser().getEmail().equals(email);
		boolean isAdminOrSuperAdmin = userRequester.getRole().stream().anyMatch(
				role -> role.getRoleName().equals(RoleName.ADMIN) || role.getRoleName().equals(RoleName.SUPER_ADMIN));
		if (!isAdminOrSuperAdmin && !isOwner)
			throw new UserException(ErrorMessages.NO_AUTORIZ_REQUEST.getErrorMessage(), HttpStatus.FORBIDDEN);
		annonceRepository.delete(annonce);
	}

	@Override
	public AnnonceDto getAnnonceByAnnonceId(String annonceId) {
		AnnonceEntity annonce = annonceRepository.findByAnnonceId(annonceId);
		Optional.ofNullable(annonce).orElseThrow(
				() -> new AnnonceException(ErrorMessages.NO_RECORD_FOUND.getErrorMessage(), HttpStatus.NOT_FOUND));
		return utils.mapToDto(annonce, AnnonceDto.class);
	}

	@Override
	public List<AnnonceDto> getAllAnnonce(int page, int limit) {
		Pageable pageAnnonce = utils.createPageable(page, limit);
		Page<AnnonceEntity> annonce = annonceRepository.findAll(pageAnnonce);
		return utils.mapToLists(annonce.getContent(), AnnonceDto.class);
	}

	@Override
	public List<AnnonceDto> getMyAllAnnonce(String email, int page, int limit) {
		UserEntity userCurent = userRepository.findByEmail(email);
		Pageable pageable = utils.createPageable(page, limit);
		Page<AnnonceEntity> listMyAnnonce = annonceRepository.findByUser(userCurent, pageable);
		return utils.mapToLists(listMyAnnonce.getContent(), AnnonceDto.class);

	}

	@Override
	public List<AnnonceDto> search(CategoryName category, VilleName ville, String keyword, int page, int limit) {
		Pageable pagerequest = utils.createPageable(page, limit);
		Long categoryId = category != null ? categoryRepository.findByCategoryName(category).getId() : null;
		Long villeId = ville != null ? villeRepository.findByVilleName(ville).getId() : null;
		Specification<AnnonceEntity> spec = new Search(categoryId, villeId, keyword);
		Page<AnnonceEntity> result = annonceRepository.findAll(spec, pagerequest);
		List<AnnonceEntity> annonceEntity = result.getContent();
		if (annonceEntity.isEmpty())
			throw new AnnonceException(ErrorMessages.SEARCH_EMPTY.getErrorMessage(), HttpStatus.NOT_FOUND);
		return utils.mapToLists(annonceEntity, AnnonceDto.class);
	}

	@Override
	public void modifyAnnonceImage(String id, String email, MultipartFile images) throws Exception {
		AnnonceEntity annonceEntity = annonceRepository.findByAnnonceId(id);

		Optional.ofNullable(annonceEntity).orElseThrow(
				() -> new AnnonceException(ErrorMessages.NO_RECORD_FOUND.getErrorMessage(), HttpStatus.NOT_FOUND));

		boolean isOwner = annonceEntity.getUser().getEmail().equals(email);
		if (!isOwner) {
			throw new UserException(ErrorMessages.NO_AUTORIZ_REQUEST.getErrorMessage(), HttpStatus.FORBIDDEN);
		}
		String imageName = utils.saveImage(images);
		updateAnnonceImage(imageName, annonceEntity);
	}

	@Override
	public void deleteAnnonceImage(String id, String email) {
		AnnonceEntity annonceEntity = annonceRepository.findByAnnonceId(id);

		Optional.ofNullable(annonceEntity).orElseThrow(
				() -> new AnnonceException(ErrorMessages.NO_RECORD_FOUND.getErrorMessage(), HttpStatus.NOT_FOUND));

		boolean isOwner = annonceEntity.getUser().getEmail().equals(email);
		if (!isOwner) {
			throw new UserException(ErrorMessages.NO_AUTORIZ_REQUEST.getErrorMessage(), HttpStatus.FORBIDDEN);
		}
		String oldImageName = annonceEntity.getImages();

		if (oldImageName != null) {
			Utils.deleteImage(oldImageName);
			annonceEntity.setImages(null);
			annonceRepository.save(annonceEntity);
		} else {
			throw new AnnonceException(ErrorMessages.NO_IMAGE_FOUND.getErrorMessage(), HttpStatus.NOT_FOUND);
		}
	}

	private void updateAnnonceImage(String images, AnnonceEntity annonceEntity) {
		if (images != null && !images.equals(annonceEntity.getImages())) {
			String oldImageName = annonceEntity.getImages();
			if (oldImageName != null) {
				Utils.deleteImage(oldImageName);
			}
			annonceEntity.setImages(images);
			annonceRepository.save(annonceEntity);
		}
	}

}

//@Override
//public List<AnnonceDto> getAnnonceByCategory(CategoryName category, int page, int limit) {
//	Pageable pagerequest = utils.createPageable(page, limit);
//	CategoryEntity categoryEntity = categoryRepository.findByCategoryName(category);
//	Optional.ofNullable(categoryEntity)
//			.orElseThrow(() -> new AnnonceException(ErrorMessages.CAT_Not_FOUND.getErrorMessage()));
//
//	Page<AnnonceEntity> annonceEn = annonceRepository.findByCategory(categoryEntity, pagerequest);
//
//	List<AnnonceEntity> annonceEntity = annonceEn.getContent();
//
//	if (annonceEntity.isEmpty())
//		throw new AnnonceException(ErrorMessages.CAT_EMPTY.getErrorMessage());
//	return utils.mapToLists(annonceEntity, AnnonceDto.class);
//}
//
//@Override
//public List<AnnonceDto> getAnnonceByVilles(VilleName ville, int page, int limit) {
//	VilleEntity villeEntity = villeRepository.findByVilleName(ville);
//	Optional.ofNullable(villeEntity)
//			.orElseThrow(() -> new AnnonceException(ErrorMessages.CITY_Not_FOUND.getErrorMessage()));
//	Pageable pageable = utils.createPageable(page, limit);
//	Page<AnnonceEntity> annonceEn = annonceRepository.findByVille(villeEntity, pageable);
//	List<AnnonceEntity> annonceEntityList = annonceEn.getContent();
//
//	if (annonceEntityList.isEmpty()) {
//		throw new AnnonceException(ErrorMessages.CITY_EMPTY.getErrorMessage());
//	}
//	return utils.mapToLists(annonceEntityList, AnnonceDto.class);
//}

//@Override
//public List<AnnonceDto> search(CategoryName category, VilleName ville, String keyword, int page, int limit) {
//	if (page > 0)
//		page = page - 1;
//	Pageable pagerequest = PageRequest.of(page, limit);
//	
//	CategoryEntity categoryEntity = categoryRepository.findByCategoryName(category);
//	VilleEntity villeEntity = villeRepository.findByVilleName(ville);
//	
//	if (categoryEntity == null)
//		throw new AnnonceException(ErrorMessages.CAT_Not_FOUND.getErrorMessage());
//	
//	if (villeEntity == null)
//		throw new AnnonceException(ErrorMessages.CITY_Not_FOUND.getErrorMessage());
//	
//	Page<AnnonceEntity> annonceEn = annonceRepository.search(categoryEntity, villeEntity, keyword, pagerequest);
//	List<AnnonceEntity> annonceEntity = annonceEn.getContent();
//	if (annonceEntity.isEmpty())
//		throw new RuntimeException("annonce cherché introuvable ");
//	Type listType = new TypeToken<List<AnnonceDto>>() {
//	}.getType();
//	List<AnnonceDto> annonceDto = new ModelMapper().map(annonceEntity, listType);
//	return annonceDto;
//}

//@Override
//public List<AnnonceDto> getAnnonceByVilles(VilleName ville, int page, int limit) {
//	if (page > 0)
//		page = page - 1;
//	Pageable pagerequest = PageRequest.of(page, limit);
//
//	VilleEntity villeEntity = villeRepository.findByVilleName(ville);
//
//	if (villeEntity == null)
//		throw new AnnonceException(ErrorMessages.CITY_Not_FOUND.getErrorMessage());
//
//	Page<AnnonceEntity> annonceEn = annonceRepository.findByVille(villeEntity, pagerequest);
//
//	List<AnnonceEntity> annonceEntity = annonceEn.getContent();
//
//	if (annonceEntity.isEmpty())
//		throw new AnnonceException(ErrorMessages.CITY_EMPTY.getErrorMessage());
//
//	Type listType = new TypeToken<List<AnnonceDto>>() {
//	}.getType();
//	List<AnnonceDto> annonceDto = new ModelMapper().map(annonceEntity, listType);
//	return annonceDto;
//}

//@Override
//public List<AnnonceDto> getAllAnnonce(int page, int limit) {
//	Pageable pageAnnonce = createPageable(page, limit);
//	List<AnnonceDto> listannonce = new ArrayList<>();
//	Page<AnnonceEntity> annonce = annonceRepository.findAll(pageAnnonce);
//	List<AnnonceEntity> list = annonce.getContent();
//	for (AnnonceEntity annonceEntity : list) {
//		ModelMapper modelMapper = new ModelMapper();
//		AnnonceDto annonceDto = modelMapper.map(annonceEntity, AnnonceDto.class);
//		listannonce.add(annonceDto);
//	}
//	return listannonce;
//}

//AnnonceDto annonceToApprove = pendingAnnonces.stream().filter(c -> c.getAnnonceId().equals(annonceId))
//.findFirst().orElseThrow(() -> new AnnonceException(ErrorMessages.NO_RECORD_FOUND.getErrorMessage(),
//		HttpStatus.NOT_FOUND));
