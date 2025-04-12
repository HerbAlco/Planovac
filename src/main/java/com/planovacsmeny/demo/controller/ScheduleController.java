package com.planovacsmeny.demo.controller;

import com.planovacsmeny.demo.entity.Schedule;
import com.planovacsmeny.demo.service.scheduleService.ScheduleServices;
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
	private ScheduleServices scheduleServices;

	@GetMapping("/{id}")
	public ResponseEntity<Schedule> getSchedule(@PathVariable Integer id)
	{
		//Schedule for today
		return ResponseEntity.ok(scheduleServices.createSchedule(id, LocalDate.now()));
	}

}
