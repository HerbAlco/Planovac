package com.planovacsmeny.demo.mapper;

import com.planovacsmeny.demo.dto.WorkerDTO;
import com.planovacsmeny.demo.entity.Worker;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface WorkerMapper {
	WorkerDTO toDTO(Worker worker);
	Worker toEntity(WorkerDTO workerDTO);

	void updateWorkerDTO(WorkerDTO source, @MappingTarget WorkerDTO target);

	void updateWorkerEntity(WorkerDTO source, @MappingTarget Worker target);
}


