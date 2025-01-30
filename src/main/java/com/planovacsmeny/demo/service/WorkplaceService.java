package com.planovacsmeny.demo.service;

import com.planovacsmeny.demo.dto.WorkplaceDTO;
import com.planovacsmeny.demo.entity.WorkOperation;
import com.planovacsmeny.demo.entity.Workplace;
import com.planovacsmeny.demo.entity.repository.WorkOperationRepository;
import com.planovacsmeny.demo.entity.repository.WorkplaceRepository;
import com.planovacsmeny.demo.mapper.WorkplaceMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WorkplaceService
{

	@Autowired
	private WorkplaceRepository workplaceRepository;
	@Autowired
	private WorkOperationRepository workOperationRepository;
	@Autowired
	private WorkplaceMapper workplaceMapper;

	public WorkplaceDTO createWorkplace(WorkplaceDTO workplaceDTO, Integer workOperationId)
	{
		WorkOperation workOperation = workOperationRepository.findById(workOperationId).orElseThrow();
		Workplace workplace = workplaceMapper.toEntity(workplaceDTO);
		workplace.setWorkOperation(workOperation);
		return workplaceMapper.toDTO(workplaceRepository.save(workplace));
	}

	public List<WorkplaceDTO> findAllByWorkOperationId(Integer workOperationId)
	{
		return workplaceRepository.findAllByWorkOperation_Id(workOperationId).stream().map(workplace -> workplaceMapper.toDTO(workplace)).toList();
	}

	public void update(WorkplaceDTO newWorkplaceDTO, Integer workOperationId)
	{
		Workplace workplace = workplaceRepository.findById(newWorkplaceDTO.getId()).orElseThrow();
		workplaceMapper.updateWorkplaceEntity(newWorkplaceDTO, workplace);
		workplaceRepository.save(workplace);
	}

	public void deleteById(Integer id)
	{
		workplaceRepository.deleteById(id);
	}
}
