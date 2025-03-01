package com.sid.services;

import java.util.List;

import com.sid.shared.dto.VilleDto;

public interface VilleService {

	List<VilleDto> villes(int page,int limit);
	
}
