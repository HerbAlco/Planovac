package com.planovacsmeny.demo.mapper;

import com.planovacsmeny.demo.dto.WorkOperationDTO;
import com.planovacsmeny.demo.entity.WorkOperation;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.springframework.stereotype.Component;

@Mapper(componentModel = "spring")
@Component
public interface WorkOperationMapper
{
	WorkOperationDTO toDTO(WorkOperation workOperation);
	WorkOperation toEntity(WorkOperationDTO workOperationDTO);
	void updateWorkOperationDTO(WorkOperationDTO source, @MappingTarget WorkOperationDTO target);
	void updateWorkOperationEntity(WorkOperationDTO source, @MappingTarget WorkOperation target);
}
