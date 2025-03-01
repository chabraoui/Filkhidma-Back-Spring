package com.sid.controllers;

import java.util.ArrayList;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sid.responses.VilleResponse;
import com.sid.services.VilleService;
import com.sid.shared.dto.VilleDto;


@RestController
@RequestMapping("/api/cities")
public class VilleController {
	
	
	@Autowired
	VilleService villeService;

	
	@GetMapping
	public List<VilleResponse> listOfCities(
			@RequestParam(value="page", defaultValue = "1") int page,
			@RequestParam(value="limit", defaultValue = "10")  int limit){
		
		List<VilleResponse> villeResponse=new ArrayList<>();
		
		List<VilleDto> villes = villeService.villes(page, limit);
		
		for(VilleDto villeDto:villes) {
		    
			ModelMapper modelMapper = new ModelMapper();
			
			VilleResponse city = modelMapper.map(villeDto, VilleResponse.class);
		      
			villeResponse.add(city);
		}
		
		return villeResponse;
	}

}
