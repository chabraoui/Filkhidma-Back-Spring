package com.sid.controllers;

import java.util.ArrayList;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sid.exceptions.VilleException;
import com.sid.responses.ErrorMessages;
import com.sid.responses.VilleResponse;
import com.sid.services.VilleService;
import com.sid.shared.Utils;
import com.sid.shared.VilleName;
import com.sid.shared.dto.VilleDto;

@RestController
@RequestMapping("/api/cities")
public class VilleController {

	@Autowired
	VilleService villeService;
	@Autowired
	Utils utils;

	@GetMapping
	public List<VilleResponse> listOfCities(@RequestParam(defaultValue = "1") int page,
			@RequestParam(defaultValue = "70") int limit) {

		List<VilleResponse> villeResponse = new ArrayList<>();

		List<VilleDto> villes = villeService.villes(page, limit);

		for (VilleDto villeDto : villes) {

			ModelMapper modelMapper = new ModelMapper();

			VilleResponse city = modelMapper.map(villeDto, VilleResponse.class);

			villeResponse.add(city);
		}

		return villeResponse;
	}

	@GetMapping(value = "/{villeName}")
	public VilleResponse category(@PathVariable String villeName) {

		VilleName city;
		try {
			city = VilleName.valueOf(villeName.toUpperCase());
		} catch (IllegalArgumentException e) {
			throw new VilleException(ErrorMessages.CITY_Not_FOUND.getErrorMessage(),HttpStatus.NOT_FOUND);
		}

		VilleDto cityDto = villeService.getCity(city);
		return utils.mapToDto(cityDto, VilleResponse.class);
	}

}
