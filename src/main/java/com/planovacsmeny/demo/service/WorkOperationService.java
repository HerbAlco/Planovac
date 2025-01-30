package com.planovacsmeny.demo.service;

import com.planovacsmeny.demo.dto.WorkOperationDTO;
import com.planovacsmeny.demo.dto.WorkerDTO;
import com.planovacsmeny.demo.dto.WorkplaceDTO;
import com.planovacsmeny.demo.entity.WorkOperation;
import com.planovacsmeny.demo.entity.repository.WorkOperationRepository;
import com.planovacsmeny.demo.mapper.WorkOperationMapper;
import com.planovacsmeny.demo.mapper.WorkerMapper;
import com.planovacsmeny.demo.mapper.WorkplaceMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WorkOperationService
{
	@Autowired
	private WorkOperationRepository workOperationRepository;
	@Autowired
	private WorkOperationMapper workOperationMapper;
	@Autowired
	private WorkerMapper workerMapper;
	@Autowired
	private WorkplaceMapper workplaceMapper;


	public WorkOperationDTO createWorkOperation(WorkOperationDTO workOperationDTO) {
		return workOperationMapper.toDTO(workOperationRepository.save(workOperationMapper.toEntity(workOperationDTO)));
	}

	public List<WorkOperationDTO> findAll()
	{
		return workOperationRepository.findAll().stream().map(workOperation -> workOperationMapper.toDTO(workOperation)).toList();
	}

	public WorkOperationDTO findById(Integer id)
	{
		WorkOperation workOperation = workOperationRepository.findById(id).orElseThrow();
		return workOperationMapper.toDTO(workOperation);
	}

	public void addWorkerToWorkOperation(WorkerDTO savedWorkerDTO, Integer id)
	{

	}

	public List<WorkerDTO> getWorkers(Integer id)
	{
		WorkOperationDTO workOperationDTO = findById(id);
		return workOperationDTO.getWorkers().stream().map(worker -> workerMapper.toDTO(worker)).toList();
	}

	public List<WorkplaceDTO> getWorkplaces(Integer id)
	{
		return workOperationRepository.findById(id).orElseThrow().getWorkplaces().stream().map(workplace -> workplaceMapper.toDTO(workplace)).toList();
	}
}