package com.planovacsmeny.demo.service;

import com.planovacsmeny.demo.entity.WorkOperation;
import com.planovacsmeny.demo.entity.Worker;
import com.planovacsmeny.demo.entity.Workplace;
import com.planovacsmeny.demo.entity.repository.WorkOperationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AssignmentService {

	@Autowired
	private WorkOperationRepository workOperationRepository;

	public List<Workplace> assignWorkersToWorkplace(Integer workOperationId) {

		WorkOperation workOperation = workOperationRepository.findById(workOperationId).orElseThrow();

		// Iterovat přes všechna pracovní místa
		for (Workplace workplace : workOperation.getWorkplaces()) {
			List<Worker> availableWorkers = workOperation.getWorkers().stream()
				.filter(Worker::getAvailable)
				.filter(worker -> worker.getPriorities().stream()
					.anyMatch(priority -> priority.getWorkplaceId().equals(workplace.getId())))
				.sorted((w1, w2) -> {
					int priority1 = w1.getPriorities().stream()
						.filter(p -> p.getWorkplaceId().equals(workplace.getId()))
						.findFirst().get().getPriority();
					int priority2 = w2.getPriorities().stream()
						.filter(p -> p.getWorkplaceId().equals(workplace.getId()))
						.findFirst().get().getPriority();
					return Integer.compare(priority1, priority2);
				})
				.collect(Collectors.toList());

			// Přiřadit pracovníky, dokud není naplněna kapacita
			List<String> assignedWorkers = new ArrayList<>();
			while (assignedWorkers.size() < workplace.getMaxWorkers() && !availableWorkers.isEmpty()) {
				Worker worker = availableWorkers.remove(0);
				assignedWorkers.add(worker.getName());
				worker.setAvailable(false);
			}


		}

		return null;
	}

}
