package com.api.usercontrol.mappers;

import com.api.usercontrol.dto.UserDto;
import com.api.usercontrol.models.UserModel;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.mapstruct.InheritConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(uses = JsonNullableMapper.class,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "cpf", ignore = true)
    UserModel map(UserDto entity);

    UserDto map(UserModel entity) throws JsonProcessingException;

    @InheritConfiguration
    void update(UserDto update, @MappingTarget UserModel destination);
}