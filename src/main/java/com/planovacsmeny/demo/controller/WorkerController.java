package com.planovacsmeny.demo.controller;

import com.planovacsmeny.demo.dto.WorkerDTO;
import com.planovacsmeny.demo.entity.WorkOperation;
import com.planovacsmeny.demo.entity.Worker;
import com.planovacsmeny.demo.service.WorkOperationService;
import com.planovacsmeny.demo.service.WorkerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

	@GetMapping("/getall/{workOperationId}")
	public ResponseEntity<List<Worker>> getAllByOperationId(@PathVariable Integer workOperationId) {
		return ResponseEntity.ok(workerService.getWorkers(workOperationId));
	}

	@GetMapping("/{id}")
	public ResponseEntity<WorkerDTO> findWorker(@PathVariable Integer id) {
		return ResponseEntity.ok(workerService.findById(id));
	}

	@PutMapping("/{workerId}")
	public ResponseEntity<WorkerDTO> updateWorker(@PathVariable Integer workerId, @RequestBody WorkerDTO workerDTO) {
		return ResponseEntity.ok(workerService.updateWorker(workerId, workerDTO));
	}

	@DeleteMapping("/{workerId}")
	public ResponseEntity<Boolean> deleteWorker(@PathVariable Integer workerId){
		return ResponseEntity.ok(workerService.deleteById(workerId));
	}

	//TODO: refactor Worker.Priority on WorkerDTO.PriorityDTO?
	@PostMapping("/add-priority/{workerId}")
	public ResponseEntity<WorkerDTO> addPriorityToWorker(@PathVariable Integer workerId, @RequestBody Worker.Priority priority) {
		return ResponseEntity.ok(workerService.addPriority(workerId, priority));
	}

	@DeleteMapping("/delete-priority/{workerId}")
	public ResponseEntity<WorkerDTO> deletePriorityOfWorker(@PathVariable Integer workerId, @RequestBody Worker.Priority priority){
		return ResponseEntity.ok(workerService.deletePriority(workerId, priority));
	}


}
