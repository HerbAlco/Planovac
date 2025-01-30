package com.planovacsmeny.demo.service;

import com.planovacsmeny.demo.dto.WorkerDTO;
import com.planovacsmeny.demo.entity.WorkOperation;
import com.planovacsmeny.demo.entity.Worker;
import com.planovacsmeny.demo.entity.repository.WorkOperationRepository;
import com.planovacsmeny.demo.entity.repository.WorkerRepository;
import com.planovacsmeny.demo.mapper.WorkerMapper;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WorkerService
{

	@Autowired
	private WorkerRepository workerRepository;
	@Autowired
	private WorkOperationRepository workOperationRepository;
	@Autowired
	private WorkerMapper workerMapper;

	public WorkerDTO createWorker(WorkerDTO workerDTO, Integer workOperationId){
		WorkOperation workOperation = workOperationRepository.findById(workOperationId).orElseThrow();
		Worker newWorker = workerMapper.toEntity(workerDTO);
		newWorker.setWorkOperation(workOperation);
		return workerMapper.toDTO(workerRepository.save(newWorker));
	}

	public List<WorkerDTO> findAll()
	{
		return workerRepository.findAll().stream().map(worker -> workerMapper.toDTO(worker)).toList();
	}

	public WorkerDTO updateWorker(Integer id, WorkerDTO workerDTO)
	{
		Worker existingWorker = workerRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Worker not found"));

		if (workerDTO.getName() != null)
		{
			existingWorker.setName(workerDTO.getName());
		}

		return workerMapper.toDTO(workerRepository.save(existingWorker));
	}

	public boolean deleteById(Integer id)
	{
		if (workerRepository.existsById(id))
		{
			workerRepository.deleteById(id);
			return true;
		}
		else
		{
			return false;
		}
	}

	public WorkerDTO findById(Integer id)
	{
		return workerMapper.toDTO(workerRepository.findById(id).orElseThrow());
	}

	public WorkerDTO addPriority(Integer workerId, Worker.Priority priority) {
		Worker worker = workerRepository.findById(workerId)
			.orElseThrow(() -> new RuntimeException("Worker not found"));
		worker.getPriorities().add(priority);
		workerRepository.save(worker);

		return workerMapper.toDTO(workerRepository.save(worker));
	}
}
