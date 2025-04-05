package com.planovacsmeny.demo.controller;

import com.planovacsmeny.demo.entity.Schedule;
import com.planovacsmeny.demo.service.ScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/schedule")
@CrossOrigin(origins = "http://localhost:5173")
public class ScheduleController
{

	@Autowired
	private ScheduleService scheduleService;

	@GetMapping("/{id}")
	public ResponseEntity<Schedule> getSchedule (@PathVariable Integer id){
		//Schedule for today
		return  ResponseEntity.ok(scheduleService.createOrUpdateSchedule(id, LocalDate.now()));
	}

}
