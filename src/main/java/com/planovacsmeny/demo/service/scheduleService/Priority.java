package com.planovacsmeny.demo.service.scheduleService;

import com.planovacsmeny.demo.entity.Worker;
import com.planovacsmeny.demo.entity.Workplace;

class Priority {
	Worker worker;
	Workplace workplace;
	int priority;

	Priority(Worker worker, Workplace workplace, int priority) {
		this.worker = worker;
		this.workplace = workplace;
		this.priority = priority;
	}
}
