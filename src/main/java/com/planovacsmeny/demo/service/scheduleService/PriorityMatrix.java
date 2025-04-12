package com.planovacsmeny.demo.service.scheduleService;

import com.planovacsmeny.demo.entity.Worker;
import com.planovacsmeny.demo.entity.Workplace;

import java.util.*;

class PriorityMatrix {
	private final Map<Worker, Map<Workplace, Integer>> matrix = new HashMap<>();
	private final List<Worker> workers;
	private final List<Workplace> workplaces;

	public PriorityMatrix(List<Worker> workers, List<Workplace> workplaces, List<Map<Workplace, List<Worker>>> priorityData) {
		this.workers = workers;
		this.workplaces = workplaces;

		// Inicializace matice priorit
		for (Worker worker : workers) {
			matrix.put(worker, new HashMap<>());
			for (Workplace workplace : workplaces) {
				matrix.get(worker).put(workplace, 0);
			}
		}

		// Naplnění matice priorit
		for (Map<Workplace, List<Worker>> priority : priorityData) {
			for (Map.Entry<Workplace, List<Worker>> entry : priority.entrySet()) {
				Workplace workplace = entry.getKey();
				List<Worker> workerList = entry.getValue();

				int p = 1;
				for (Worker worker : workerList) {
					if (matrix.containsKey(worker)) {
						matrix.get(worker).put(workplace, p++);
					}
				}
			}
		}
	}

	public List<Priority> constructSequence() {
		List<Priority> priorityList = new ArrayList<>();

		for (Worker worker : workers) {
			for (Workplace workplace : workplaces) {
				int prio = matrix.getOrDefault(worker, Collections.emptyMap()).getOrDefault(workplace, 0);
				if (prio > 0) {
					priorityList.add(new Priority(worker, workplace, prio));
				}
			}
		}

		priorityList.sort(Comparator.comparingInt(o -> o.priority));
		return priorityList;
	}
}

