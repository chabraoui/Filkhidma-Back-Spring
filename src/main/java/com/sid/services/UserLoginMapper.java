package com.sid.services;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import com.sid.entity.UserEntity;
import com.sid.shared.dto.UserDto;

@Mapper
public interface UserLoginMapper {
	UserLoginMapper INSTANCE = Mappers.getMapper(UserLoginMapper.class);

    @Mapping(target = "annonces", ignore = true) // Ignorer la propriété "annonces" pour éviter les boucles infinies
    UserDto toDto(UserEntity userEntity);

    //@Mapping(target = "user", ignore = true) // Ignorer la propriété "user" pour éviter les boucles infinies
    //AnnonceDto toAnnonceDto(AnnonceEntity annonceEntity);
}
