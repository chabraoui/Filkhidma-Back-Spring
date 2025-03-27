package com.sid.services;

import java.util.List;
import javax.transaction.Transactional;

import org.springframework.web.multipart.MultipartFile;

import com.sid.shared.CategoryName;
import com.sid.shared.VilleName;
import com.sid.shared.dto.AnnonceDto;

@Transactional
public interface AnnonceService {

	AnnonceDto createAnnonce(AnnonceDto annonce, String email, MultipartFile images) throws Exception;

	void deleteAnnonce(String annonceId, String email);

	AnnonceDto updateAnnonce(AnnonceDto annonce, String annonceId, String email);

	List<AnnonceDto> getAllAnnonce(int page, int limit);

	AnnonceDto getAnnonceByAnnonceId(String annonceId);

	List<AnnonceDto> getMyAllAnnonce(String email, int page, int limit);

//	List<AnnonceDto> getAnnonceByCategory(CategoryName category, int page, int limit);

	// List<AnnonceDto> getAnnonceByVilles(VilleName ville, int page, int limit);

	List<AnnonceDto> search(CategoryName category, VilleName ville, String keyword, int page, int limit);

	List<AnnonceDto> getPendingAnnonces(String email);

	AnnonceDto approveAnnonce(String annonceId, String email) throws Exception;

	void annonceNotApprouved(String annonceId, String email);

	void modifyAnnonceImage(String id, String email, MultipartFile images) throws Exception;

	void deleteAnnonceImage(String id, String email);

}
