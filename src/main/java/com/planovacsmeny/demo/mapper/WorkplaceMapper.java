package com.planovacsmeny.demo.mapper;

import com.planovacsmeny.demo.dto.WorkplaceDTO;
import com.planovacsmeny.demo.entity.Workplace;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.springframework.stereotype.Component;

@Mapper(componentModel = "spring")
@Component
public interface WorkplaceMapper
{
	WorkplaceDTO toDTO(Workplace workplace);
	Workplace toEntity(WorkplaceDTO workplaceDTO);
	void updateWorkplaceDTO(WorkplaceDTO source, @MappingTarget WorkplaceDTO target);
	void updateWorkplaceEntity(WorkplaceDTO source, @MappingTarget Workplace target);
}
