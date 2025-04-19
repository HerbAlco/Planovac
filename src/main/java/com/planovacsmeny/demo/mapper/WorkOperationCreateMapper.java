package com.planovacsmeny.demo.mapper;

import com.planovacsmeny.demo.dto.WorkOperationCreateDTO;
import com.planovacsmeny.demo.entity.WorkOperation;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;

@Mapper(componentModel = "spring")
@Component
public interface WorkOperationCreateMapper
{
	WorkOperation toEntity(WorkOperationCreateDTO workOperationCreateDTO);
}
