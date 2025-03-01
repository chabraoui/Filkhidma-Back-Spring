package com.sid.controllers;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.ServletContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.sid.exceptions.AnnonceException;
import com.sid.requests.AnnonceRequest;
import com.sid.responses.AnnonceResponse;
import com.sid.responses.ErrorMessages;
import com.sid.services.AnnonceService;
import com.sid.shared.*;
import com.sid.shared.dto.AnnonceDto;

@RestController
@RequestMapping("/annonces")
public class AnnonceController {

	@Autowired
	private AnnonceService annonceService;
	@Autowired
	private ServletContext context;
	@Autowired
	Utils utils;

	@PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = { MediaType.APPLICATION_JSON_VALUE,
			MediaType.APPLICATION_XML_VALUE })
	public ResponseEntity<AnnonceResponse> createAnnonce(Principal principal, @RequestPart("annonce") String annonce,
			@RequestPart(value = "file", required = false) MultipartFile images) throws Exception {

		AnnonceRequest annonceRequest = utils.parseToRequest(annonce, AnnonceRequest.class);
		String imageName = utils.saveImage(images);
		annonceRequest.setImages(imageName);
		AnnonceDto annonceDto = utils.mapToDto(annonceRequest, AnnonceDto.class);
		AnnonceDto savedAnnonce = annonceService.createAnnonce(annonceDto, principal.getName());
		AnnonceResponse response = utils.mapToDto(savedAnnonce, AnnonceResponse.class);

		return new ResponseEntity<>(response, HttpStatus.CREATED);
	}

	@PutMapping(path = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = {
			MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
	public ResponseEntity<AnnonceResponse> updateAnnonce(@PathVariable String id, Principal principal,
			@RequestPart("annonce") String annonce, @RequestPart(value = "file", required = false) MultipartFile images)
			throws Exception {

		AnnonceRequest annonceRequest = utils.parseToRequest(annonce, AnnonceRequest.class);

		if (images != null) {
			annonceRequest.setImages(utils.saveImage(images));
		} else {
			annonceRequest.setImages(null);
		}

//		if (images == null) {
//			AnnonceDto existingAnnonce = annonceService.getAnnonceByAnnonceId(id);
//			String existingImage = existingAnnonce.getImages();
//
//			if (existingImage != null) {
//				String imageDirectory = System.getProperty("user.dir") + IMAGE_DIR;
//				File oldImageFile = new File(imageDirectory + File.separator + existingImage);
//				if (oldImageFile.exists()) {
//					oldImageFile.delete();
//				}
//			}
//			annonceRequest.setImages(null);
//		} else {
//			String imageName = annonceService.saveImage(images);
//			annonceRequest.setImages(imageName);
//		}

		AnnonceDto annonceDto = utils.mapToDto(annonceRequest, AnnonceDto.class);

		AnnonceDto savedAnnonce = annonceService.updateAnnonce(annonceDto, id, principal.getName());

		AnnonceResponse response = utils.mapToDto(savedAnnonce, AnnonceResponse.class);

		return new ResponseEntity<>(response, HttpStatus.ACCEPTED);
	}

	@DeleteMapping(path = "/{id}")
	public ResponseEntity<Void> deleteAnnonce(@PathVariable String id, Principal principal) {
		annonceService.deleteAnnonce(id, principal.getName());
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

	@GetMapping(path = "/annonceImage/{id}")
	public ResponseEntity<byte[]> getPhoto(@PathVariable String id) throws Exception {
		AnnonceDto annonceDto = annonceService.getAnnonceByAnnonceId(id);
		Path imagePath = Paths.get(context.getRealPath(utils.getImageDir()) + annonceDto.getImages());

		if (!Files.exists(imagePath)) {
			return ResponseEntity.notFound().build();
		}

		byte[] imageBytes = Files.readAllBytes(imagePath);
		String type = Files.probeContentType(imagePath);
		type = type != null ? type : "application/octet-stream";

		return ResponseEntity.ok().contentType(MediaType.valueOf(type)).body(imageBytes);
	}

	@GetMapping(produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
	public List<AnnonceResponse> getAllAnnonce(@RequestParam(defaultValue = "1") int page,
			@RequestParam(defaultValue = "1000") int limit) {
		List<AnnonceDto> annonceList = annonceService.getAllAnnonce(page, limit);
		List<AnnonceResponse> annonceResponses = new ArrayList<>();

		for (AnnonceDto annonceDto : annonceList) {
			annonceResponses.add(utils.mapToDto(annonceDto, AnnonceResponse.class));
		}

		return annonceResponses;
	}

	@GetMapping(path = "/{id}", produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
	public ResponseEntity<AnnonceResponse> getOneAnnonce(@PathVariable("id") String annonceId) {
		AnnonceDto annonceDto = annonceService.getAnnonceByAnnonceId(annonceId);
		AnnonceResponse response = utils.mapToDto(annonceDto, AnnonceResponse.class);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@GetMapping(path = "/searchAnnonce", produces = { MediaType.APPLICATION_JSON_VALUE,
			MediaType.APPLICATION_XML_VALUE })
	public ResponseEntity<List<AnnonceResponse>> search(@RequestParam(required = false) String category,
			@RequestParam(required = false) String ville, @RequestParam(required = false) String keyword,
			@RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "1000") int limit) {
		VilleName city = null;
		CategoryName cat = null;

		if (ville != null) {
			try {
				city = VilleName.valueOf(ville.toUpperCase());
			} catch (IllegalArgumentException e) {
				throw new AnnonceException(ErrorMessages.CITY_Not_FOUND.getErrorMessage());
			}
		}

		if (category != null) {
			try {
				cat = CategoryName.valueOf(category.toUpperCase());
			} catch (IllegalArgumentException e) {
				throw new AnnonceException(ErrorMessages.CAT_Not_FOUND.getErrorMessage());
			}
		}

		List<AnnonceDto> listAnnonce = annonceService.search(cat, city, keyword, page, limit);
		List<AnnonceResponse> annonceResp = utils.mapToLists(listAnnonce, AnnonceResponse.class);

		// List<AnnonceResponse> annonceResp = modelMapper.map(listAnnonce, new
		// TypeToken<List<AnnonceResponse>>() {
//		}.getType());

		return new ResponseEntity<>(annonceResp, HttpStatus.OK);
	}
}
