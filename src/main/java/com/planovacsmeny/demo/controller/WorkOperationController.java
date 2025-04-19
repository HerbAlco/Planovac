package com.planovacsmeny.demo.controller;

import com.planovacsmeny.demo.dto.WorkOperationCreateDTO;
import com.planovacsmeny.demo.dto.WorkOperationDTO;
import com.planovacsmeny.demo.dto.WorkerDTO;
import com.planovacsmeny.demo.dto.WorkplaceDTO;
import com.planovacsmeny.demo.service.WorkOperationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/workOperation")
@CrossOrigin(origins = "http://localhost:5173")
public class WorkOperationController {

	@Autowired
	private WorkOperationService workOperationService;

	@PostMapping("/create")
	public ResponseEntity<WorkOperationDTO> createWorkOperation(@RequestBody WorkOperationCreateDTO workOperationCreateDTO) {
		WorkOperationDTO createdWorkOperationDTO = workOperationService.createWorkOperation(workOperationCreateDTO);
		return ResponseEntity.ok(createdWorkOperationDTO);
	}

	@GetMapping("/getall")
	public ResponseEntity<List<WorkOperationDTO>> getAllWorkOperations() {
		List<WorkOperationDTO> workOperations = workOperationService.findAll();
		return ResponseEntity.ok(workOperations);
	}

	@GetMapping("/{id}")
	public ResponseEntity<WorkOperationDTO> getWorkOperationById(@PathVariable Integer id) {
		WorkOperationDTO workOperationDTO = workOperationService.findById(id);
		return ResponseEntity.ok(workOperationDTO);
	}

	@GetMapping("/workersfromworkoperation/{id}")
	public ResponseEntity<List<WorkerDTO>> getWorkersFromWorkOperationById(@PathVariable Integer id) {
		List<WorkerDTO> workerDTOS = workOperationService.getWorkers(id);
		return ResponseEntity.ok(workerDTOS);
	}

	@GetMapping("/workplacesfromworkoperation/{id}")
	public ResponseEntity<List<WorkplaceDTO>> getWorkplacesFromWorkOperationById(@PathVariable Integer id) {
		List<WorkplaceDTO> workplaceDTOS = workOperationService.getWorkplaces(id);
		return ResponseEntity.ok(workplaceDTOS);
	}



}