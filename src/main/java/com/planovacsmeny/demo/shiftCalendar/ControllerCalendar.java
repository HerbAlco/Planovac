package com.planovacsmeny.demo.shiftCalendar;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;


@RestController
@RequestMapping("/calendar")
public class ControllerCalendar
{

	@Autowired
	private ShiftCalendarService shiftCalendarService;

	@GetMapping("/getShift")
	public Map<String, Map<String, String>> getCalendarByShift() {
		return shiftCalendarService.vytvoritKalendar();
	}
}
