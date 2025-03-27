package com.sid.services.impl;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.sid.entity.VilleEntity;
import com.sid.exceptions.VilleException;
import com.sid.repository.VilleRepository;
import com.sid.responses.ErrorMessages;
import com.sid.services.VilleService;
import com.sid.shared.Utils;
import com.sid.shared.VilleName;
import com.sid.shared.dto.VilleDto;

@Service
public class VilleServiceImpl implements VilleService {

	@Autowired
	VilleRepository villeRepository;
	@Autowired
	Utils utils;

	@Override
	public List<VilleDto> villes(int page, int limit) {
		if (page > 0)
			page = page - 1;

		Pageable pageableRequest = PageRequest.of(page, limit);

		Page<VilleEntity> cities = villeRepository.findAll(pageableRequest);
		List<VilleEntity> listOfCities = cities.getContent();

		Type listType = new TypeToken<List<VilleDto>>() {
		}.getType();
		List<VilleDto> cityDto = new ModelMapper().map(listOfCities, listType);
		return cityDto;
	}

	@Override
	public VilleDto getCity(VilleName villeName) {
		VilleEntity city = villeRepository.findByVilleName(villeName);
		Optional.ofNullable(city)
				.orElseThrow(() -> new VilleException(ErrorMessages.NO_RECORD_FOUND.getErrorMessage(),HttpStatus.NOT_FOUND));
		return utils.mapToDto(city, VilleDto.class);
	}

}
