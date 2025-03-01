package com.sid.services.impl;

import java.lang.reflect.Type;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import com.sid.entity.VilleEntity;
import com.sid.repository.VilleRepository;
import com.sid.services.VilleService;
import com.sid.shared.dto.VilleDto;

@Service
public class VilleServiceImpl implements VilleService {

	@Autowired
	VilleRepository villeRepository;

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

}
