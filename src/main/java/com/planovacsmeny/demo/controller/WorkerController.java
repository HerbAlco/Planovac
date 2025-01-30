package com.planovacsmeny.demo.controller;

import com.planovacsmeny.demo.dto.WorkerDTO;
import com.planovacsmeny.demo.entity.Worker;
import com.planovacsmeny.demo.service.WorkerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/workers")
@CrossOrigin(origins = "http://localhost:5173")
public class WorkerController {

	@Autowired
	private WorkerService workerService;

	@PostMapping("/create/{workOperationId}")
	public ResponseEntity<WorkerDTO> createWorker(@RequestBody WorkerDTO workerDTO, @PathVariable Integer workOperationId) {

		WorkerDTO savedWorkerDTO = workerService.createWorker(workerDTO, workOperationId);
		return ResponseEntity.status(HttpStatus.CREATED).body(savedWorkerDTO);
	}

	@GetMapping("/{id}")
	public ResponseEntity<WorkerDTO> findWorker(@PathVariable Integer id) {
		return ResponseEntity.ok(workerService.findById(id));
	}

	@PutMapping("/{id}")
	public ResponseEntity<WorkerDTO> updateWorker(@PathVariable Integer id, @RequestBody WorkerDTO workerDTO) {
		return ResponseEntity.ok(workerService.updateWorker(id, workerDTO));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Boolean> deleteWorker(@PathVariable Integer id){
		return ResponseEntity.ok(workerService.deleteById(id));
	}

	//TODO: refactor Worker.Priority on WorkerDTO.PriorityDTO?
	@PostMapping("/add-priority/{id}")
	public ResponseEntity<WorkerDTO> addPriorityToWorker(@PathVariable Integer id, @RequestBody Worker.Priority priority) {
		return ResponseEntity.ok(workerService.addPriority(id, priority));
	}


}
