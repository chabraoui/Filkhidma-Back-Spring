package com.sid.services.impl;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import com.sid.entity.AnnonceEntity;
import com.sid.entity.CategoryEntity;
import com.sid.entity.UserEntity;
import com.sid.entity.VilleEntity;
import com.sid.exceptions.AnnonceException;
import com.sid.exceptions.UserException;
import com.sid.repository.AnnonceRepository;
import com.sid.repository.CategoryRepository;
import com.sid.repository.UserRepository;
import com.sid.repository.VilleRepository;
import com.sid.responses.ErrorMessages;
import com.sid.services.AnnonceService;
import com.sid.shared.CategoryName;
import com.sid.shared.RoleName;
import com.sid.shared.Search;
import com.sid.shared.Utils;
import com.sid.shared.VilleName;
import com.sid.shared.dto.AnnonceDto;
import com.sid.shared.dto.CategoryDto;
import com.sid.shared.dto.UserDto;
import com.sid.shared.dto.VilleDto;

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

	@Override
	public AnnonceDto createAnnonce(AnnonceDto annonce, String email) {
		UserEntity userEntity = userRepository.findByEmail(email);
		Optional.ofNullable(annonce)
				.orElseThrow(() -> new UserException(ErrorMessages.NO_AUTH_FOUND.getErrorMessage()));
		VilleEntity villeEntity = villeRepository.findByVilleName(annonce.getVille().getVilleName());
		Optional.ofNullable(annonce)
				.orElseThrow(() -> new AnnonceException(ErrorMessages.NO_CITY_FOUND.getErrorMessage()));
		CategoryEntity categoryEntity = categoryRepository.findByCategoryName(annonce.getCategory().getCategoryName());
		Optional.ofNullable(annonce)
				.orElseThrow(() -> new AnnonceException(ErrorMessages.NO_CAT_FOUND.getErrorMessage()));
		annonce.setAnnonceId(utils.genereteStringId(30));
		annonce.setUser(utils.mapToDto(userEntity, UserDto.class));
		annonce.setDateAnnonce(new Date());
		annonce.setCategory(utils.mapToDto(categoryEntity, CategoryDto.class));
		annonce.setVille(utils.mapToDto(villeEntity, VilleDto.class));
		AnnonceEntity annonceEntity = utils.mapToDto(annonce, AnnonceEntity.class);
		AnnonceEntity annonceEntitySave = annonceRepository.save(annonceEntity);
		return utils.mapToDto(annonceEntitySave, AnnonceDto.class);
	}

	@Override
	public AnnonceDto getAnnonceByAnnonceId(String annonceId) {
		AnnonceEntity annonce = annonceRepository.findByAnnonceId(annonceId);
		Optional.ofNullable(annonce)
				.orElseThrow(() -> new AnnonceException(ErrorMessages.NO_RECORD_FOUND.getErrorMessage()));
		return utils.mapToDto(annonce, AnnonceDto.class);
	}

	@Override
	public void deleteAnnonce(String annonceId, String email) {

		AnnonceEntity annonce = annonceRepository.findByAnnonceId(annonceId);

		Optional.ofNullable(annonce)
				.orElseThrow(() -> new AnnonceException(ErrorMessages.NO_RECORD_FOUND.getErrorMessage()));

		UserEntity userRequester = userRepository.findByEmail(email);

		Optional.ofNullable(userRequester)
				.orElseThrow(() -> new UserException(ErrorMessages.NO_AUTH_FOUND.getErrorMessage()));

		boolean isOwner = annonce.getUser().getEmail().equals(email);

		boolean isAdminOrSuperAdmin = userRequester.getRole().stream().anyMatch(
				role -> role.getRoleName().equals(RoleName.ADMIN) || role.getRoleName().equals(RoleName.SUPER_ADMIN));

		if (!isAdminOrSuperAdmin && !isOwner)
			throw new UserException(ErrorMessages.NO_AUTORIZ_REQUEST.getErrorMessage());

//		String imageName = annonce.getImages();
//		if (imageName != null) {	
//			utils.deleteImage(imageName);
//		}
		annonceRepository.delete(annonce);
	}

	@Override
	public AnnonceDto updateAnnonce(AnnonceDto annonce, String annonceId, String email) {
		AnnonceEntity annonceEntity = annonceRepository.findByAnnonceId(annonceId);

		Optional.ofNullable(annonceEntity)
				.orElseThrow(() -> new AnnonceException(ErrorMessages.NO_RECORD_FOUND.getErrorMessage()));

		boolean isOwner = annonceEntity.getUser().getEmail().equals(email);
		if (!isOwner) {
			throw new UserException(ErrorMessages.NO_AUTORIZ_REQUEST.getErrorMessage());
		}

		// si user entre une nvll image dans la modification, on supprime l'image existe
		// en server et on persist la nouvelle
		if (annonce.getImages() != null && !annonce.getImages().equals(annonceEntity.getImages())) {
			String oldImageName = annonceEntity.getImages();
			if (oldImageName != null) {
				Utils.deleteImage(oldImageName);
			}
		}
		annonceEntity.setImages(annonce.getImages());
		annonceEntity.setDescription(annonce.getDescription());
		annonceEntity.setPrix(annonce.getPrix());
		annonceEntity.setName(annonce.getName());
		annonceRepository.save(annonceEntity);

		return utils.mapToDto(annonceEntity, AnnonceDto.class);
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
	public List<AnnonceDto> getAnnonceByCategory(CategoryName category, int page, int limit) {
		Pageable pagerequest = utils.createPageable(page, limit);
		CategoryEntity categoryEntity = categoryRepository.findByCategoryName(category);
		Optional.ofNullable(categoryEntity)
				.orElseThrow(() -> new AnnonceException(ErrorMessages.CAT_Not_FOUND.getErrorMessage()));

		Page<AnnonceEntity> annonceEn = annonceRepository.findByCategory(categoryEntity, pagerequest);

		List<AnnonceEntity> annonceEntity = annonceEn.getContent();

		if (annonceEntity.isEmpty())
			throw new AnnonceException(ErrorMessages.CAT_EMPTY.getErrorMessage());
		return utils.mapToLists(annonceEntity, AnnonceDto.class);
	}

	@Override
	public List<AnnonceDto> getAnnonceByVilles(VilleName ville, int page, int limit) {
		VilleEntity villeEntity = villeRepository.findByVilleName(ville);
		Optional.ofNullable(villeEntity)
				.orElseThrow(() -> new AnnonceException(ErrorMessages.CITY_Not_FOUND.getErrorMessage()));
		Pageable pageable = utils.createPageable(page, limit);
		Page<AnnonceEntity> annonceEn = annonceRepository.findByVille(villeEntity, pageable);
		List<AnnonceEntity> annonceEntityList = annonceEn.getContent();

		if (annonceEntityList.isEmpty()) {
			throw new AnnonceException(ErrorMessages.CITY_EMPTY.getErrorMessage());
		}
		return utils.mapToLists(annonceEntityList, AnnonceDto.class);
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
			throw new AnnonceException(ErrorMessages.SEARCH_EMPTY.getErrorMessage());
		return utils.mapToLists(annonceEntity, AnnonceDto.class);
	}

}

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
