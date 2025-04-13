package com.planovacsmeny.demo.controller;

import com.planovacsmeny.demo.dto.WorkplaceDTO;
import com.planovacsmeny.demo.service.WorkplaceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/workplaces")
@CrossOrigin(origins = "http://localhost:5173")
public class WorkplaceController {

	@Autowired
	private WorkplaceService workplaceService;

	@PostMapping("/create/{workOperationId}")
	public ResponseEntity<WorkplaceDTO> createWorkplace(@RequestBody WorkplaceDTO workplaceDTO, @PathVariable Integer workOperationId) {
		try {
			WorkplaceDTO createdWorkplaceDTO = workplaceService.createWorkplace(workplaceDTO, workOperationId);
			return ResponseEntity.ok(createdWorkplaceDTO);
		} catch (Exception e) {
			return ResponseEntity.status(500).body(null);  // Handle error case
		}
	}

	@GetMapping("/getall/{workOperationId}")
	public ResponseEntity<List<WorkplaceDTO>> getAllWorkplaces(@PathVariable Integer workOperationId){
		List<WorkplaceDTO> workplaceDTOS = workplaceService.findAllByWorkOperationId(workOperationId);
		return ResponseEntity.ok(workplaceDTOS);
	}

	@PutMapping("/update")
	public void updateWorkplace(@RequestBody WorkplaceDTO newWorkplaceDTO){
		workplaceService.update(newWorkplaceDTO);
	}

	@DeleteMapping("/delete/{id}")
	public void deleteWorkplace(@PathVariable Integer id){
		workplaceService.deleteById(id);
	}

}
