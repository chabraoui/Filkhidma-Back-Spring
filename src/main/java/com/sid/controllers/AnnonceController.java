package com.sid.controllers;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.sid.exceptions.AnnonceException;
import com.sid.requests.AnnonceRequest;
import com.sid.requests.UpdateAnnonceRequest;
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
	private RedisTemplate<String, byte[]> redisTemplate;
	@Autowired
	Utils utils;

	@PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = { MediaType.APPLICATION_JSON_VALUE,
			MediaType.APPLICATION_XML_VALUE })
	public ResponseEntity<AnnonceResponse> createAnnonce(Principal principal, @RequestPart("annonce") String annonce,
			@RequestPart(value = "file", required = false) MultipartFile images) throws Exception {
		AnnonceRequest annonceRequest = utils.parseToRequest(annonce, AnnonceRequest.class);
		AnnonceDto annonceDto = utils.mapToDto(annonceRequest, AnnonceDto.class);
		AnnonceDto savedAnnonce = annonceService.createAnnonce(annonceDto, principal.getName(), images);
		AnnonceResponse response = utils.mapToDto(savedAnnonce, AnnonceResponse.class);
		return new ResponseEntity<>(response, HttpStatus.CREATED);
	}

	@PostMapping("/approve/{annonceId}")
	public ResponseEntity<AnnonceResponse> approveAnnonce(Principal principal, @PathVariable String annonceId)
			throws Exception {
		AnnonceDto approvedAnnonce = annonceService.approveAnnonce(annonceId, principal.getName());
		AnnonceResponse response = utils.mapToDto(approvedAnnonce, AnnonceResponse.class);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@DeleteMapping("/approve/{annonceId}")
	public ResponseEntity<Void> annonceNotApprouved(Principal principal, @PathVariable String annonceId) {
		annonceService.annonceNotApprouved(annonceId, principal.getName());
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

	@GetMapping("/pending")
	public ResponseEntity<List<AnnonceResponse>> getPendingAnnonces(Principal principal) {
		List<AnnonceDto> pendingAnnonces = annonceService.getPendingAnnonces(principal.getName());
		List<AnnonceResponse> response = pendingAnnonces.stream()
				.map(annonce -> utils.mapToDto(annonce, AnnonceResponse.class)).collect(Collectors.toList());
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@PutMapping(path = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = {
			MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
	public ResponseEntity<AnnonceResponse> updateAnnonce(@PathVariable String id, Principal principal,
			@RequestBody @Valid UpdateAnnonceRequest annonce) throws Exception {
		AnnonceDto annonceDto = utils.mapToDto(annonce, AnnonceDto.class);
		AnnonceDto savedAnnonce = annonceService.updateAnnonce(annonceDto, id, principal.getName());
		AnnonceResponse response = utils.mapToDto(savedAnnonce, AnnonceResponse.class);
		return new ResponseEntity<>(response, HttpStatus.ACCEPTED);
	}

	@PutMapping(path = "/annonceImage/{id}")
	public void modifyAnnonceImage(@PathVariable String id, Principal principal,
			@RequestPart("file") MultipartFile images) throws Exception {
		annonceService.modifyAnnonceImage(id, principal.getName(), images);
	}

	@DeleteMapping(path = "/annonceImage/{id}")
	public void deleteAnnonceImage(@PathVariable String id, Principal principal) throws Exception {
		annonceService.deleteAnnonceImage(id, principal.getName());
	}

	@DeleteMapping(path = "/{id}")
	public ResponseEntity<Void> deleteAnnonce(@PathVariable String id, Principal principal) {
		annonceService.deleteAnnonce(id, principal.getName());
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

	@GetMapping(path = "/annonceImage/{id}")
	public ResponseEntity<byte[]> getPhoto(@PathVariable String id) throws Exception {
		AnnonceDto annonceDto = annonceService.getAnnonceByAnnonceId(id);
		String imageDirectory = System.getProperty("user.dir") + utils.getImageDir();
		Path imagePath = Paths.get(imageDirectory + File.separator + annonceDto.getImages());
		if (!Files.exists(imagePath)) {
			return ResponseEntity.notFound().build();
		}
		byte[] imageBytes = Files.readAllBytes(imagePath);
		String type = Files.probeContentType(imagePath);
		type = type != null ? type : "application/octet-stream";
		return ResponseEntity.ok().contentType(MediaType.valueOf(type)).body(imageBytes);
	}

	@GetMapping("/api/images/{annonceId}")
	public ResponseEntity<?> getImageFromRedisToApprouve(@PathVariable String annonceId) {
		byte[] imageData = redisTemplate.opsForValue().get("image:" + annonceId);

		if (imageData == null) {
			return ResponseEntity.notFound().build();
		}

		ByteArrayResource resource = new ByteArrayResource(imageData);
		return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "inline;filename=" + annonceId + ".jpg")
				.contentType(MediaType.IMAGE_JPEG).body(resource);
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

	@GetMapping(path = "/myAnnonce", produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
	public List<AnnonceResponse> getMyAnnonce(Principal principal, @RequestParam(defaultValue = "1") int page,
			@RequestParam(defaultValue = "1000") int limit) {
		List<AnnonceDto> listAnnonce = annonceService.getMyAllAnnonce(principal.getName(), page, limit);
		List<AnnonceResponse> annonceResp = utils.mapToLists(listAnnonce, AnnonceResponse.class);
		return annonceResp;
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
				throw new AnnonceException(ErrorMessages.CITY_Not_FOUND.getErrorMessage(), HttpStatus.NOT_FOUND);
			}
		}
		if (category != null) {
			try {
				cat = CategoryName.valueOf(category.toUpperCase());
			} catch (IllegalArgumentException e) {
				throw new AnnonceException(ErrorMessages.CAT_Not_FOUND.getErrorMessage(), HttpStatus.NOT_FOUND);
			}
		}
		List<AnnonceDto> listAnnonce = annonceService.search(cat, city, keyword, page, limit);
		List<AnnonceResponse> annonceResp = utils.mapToLists(listAnnonce, AnnonceResponse.class);
		return new ResponseEntity<>(annonceResp, HttpStatus.OK);
	}
}
