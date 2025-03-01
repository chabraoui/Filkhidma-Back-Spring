package com.sid.services;

import java.util.List;
import javax.transaction.Transactional;
import com.sid.shared.CategoryName;
import com.sid.shared.VilleName;
import com.sid.shared.dto.AnnonceDto;

@Transactional
public interface AnnonceService {

	AnnonceDto createAnnonce(AnnonceDto annonce, String email);

	void deleteAnnonce(String annonceId, String email);

	AnnonceDto updateAnnonce(AnnonceDto annonce, String annonceId, String email);

	List<AnnonceDto> getAllAnnonce(int page, int limit);

	AnnonceDto getAnnonceByAnnonceId(String annonceId);

	List<AnnonceDto> getMyAllAnnonce(String email, int page, int limit);

	List<AnnonceDto> getAnnonceByCategory(CategoryName category, int page, int limit);

	List<AnnonceDto> getAnnonceByVilles(VilleName ville, int page, int limit);

	List<AnnonceDto> search(CategoryName category, VilleName ville, String keyword, int page, int limit);

	// String saveImage(MultipartFile images) throws Exception;

	// AnnonceRequest parseAnnonceRequest(String annonce) throws JsonParseException,
	// JsonProcessingException;

}
