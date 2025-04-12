package com.planovacsmeny.demo.service.scheduleService;

import com.planovacsmeny.demo.entity.Worker;
import com.planovacsmeny.demo.entity.Workplace;
import com.planovacsmeny.demo.entity.repository.ScheduleRepository;
import com.planovacsmeny.demo.entity.repository.WorkOperationRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;
import java.util.stream.Collectors;

public class WorkerAssignment
{

	@Autowired
	private WorkOperationRepository workOperationRepository;
	@Autowired
	private ScheduleRepository scheduleRepository;

	public static Map<Workplace, Worker> search(List<Worker> workers, List<Workplace> workplaces,
		List<Priority> priorityList, Map<Workplace, Worker> assignedWorkplaces, Worker excludedWorker,
		Workplace replacementWorkplace)
	{



		// Pokud jsou všechna pracovní místa obsazena, vypiš řešení
		if (assignedWorkplaces.size() == workplaces.size())
		{

			Map<Workplace, Worker> sortedMap = assignedWorkplaces.entrySet().stream()
				.sorted(Comparator.comparing(entry -> entry.getKey().getId()))
				.filter(e -> e.getValue() != null)

				.collect(Collectors.toMap(
					Map.Entry::getKey,
					Map.Entry::getValue,
					(oldValue, newValue) -> oldValue,
					LinkedHashMap::new
				));


			sortedMap.forEach((workplace, worker) -> System.out.println(workplace.getName() + " -> " + worker.getName()));

			// Určení pracovníků, kteří nebyli přiřazeni
			List<Worker> assignedWorkersList = new ArrayList<>(assignedWorkplaces.values());
			List<Worker> unassignedWorkers = workers.stream().filter(worker -> !assignedWorkersList.contains(worker))
				.toList();

			if (unassignedWorkers.isEmpty())
			{
				System.out.println("Všichni pracovníci byli přiřazeni.");
			}
			else
			{
				unassignedWorkers.forEach(worker -> System.out.println("- " + worker));
			}
			return assignedWorkplaces;
		}

		// Najdi první neobsazené pracoviště
		Workplace currentWorkplace = replacementWorkplace != null
			? replacementWorkplace
			: workplaces.stream().filter(w -> !assignedWorkplaces.containsKey(w)).findFirst().orElse(null);

		if (currentWorkplace == null)
		{
			System.out.println("Žádné dostupné pracoviště nebylo nalezeno.");
			return assignedWorkplaces;
		}


		// Získání dostupných pracovníků seřazených podle priority
		List<Worker> availableWorkers = priorityList.stream().filter(
			p -> p.workplace.equals(currentWorkplace) && !assignedWorkplaces.containsValue(p.worker) && !p.worker.equals(
				excludedWorker)).sorted(Comparator.comparingInt(p -> p.priority)).map(p -> p.worker).toList();


		// Pokud nejsou žádní dostupní pracovníci, vrať se
		if (availableWorkers.isEmpty())
		{
			List<Worker> workersForCurrentWorkplace = priorityList.stream()
				.filter(p -> p.workplace.equals(currentWorkplace)).map(p -> p.worker).toList();

			for (Worker s : workersForCurrentWorkplace)
			{
				Optional<Workplace> workplace = assignedWorkplaces.entrySet().stream()
					.filter(stringStringEntry -> stringStringEntry.getValue().equals(s)).map(Map.Entry::getKey).findFirst();


				if (workplace.isEmpty())
				{
					continue;
				}

				if (workerAvailableForWorkplace(s, workplace.get(), assignedWorkplaces, priorityList) != null)
				{
					assignedWorkplaces.remove(workplace.get());
					search(workers, workplaces, priorityList, assignedWorkplaces, s, workplace.get());
					return assignedWorkplaces;
				}

			}
		}

		Worker worker;
		// Rekurzivně přiřazuj pracovníky k pracovištím
		if (availableWorkers.isEmpty())
		{
			worker = null;

		} else {
			worker = availableWorkers.get(0);
		}

		assignedWorkplaces.put(currentWorkplace, worker);
		search(workers, workplaces, priorityList, assignedWorkplaces, null, null);

		return assignedWorkplaces;
	}


	private static Worker workerAvailableForWorkplace(Worker worker, Workplace workplace,
		Map<Workplace, Worker> assignedWorkplaces, List<Priority> priorityList)
	{

		List<Worker> availableWorkers = priorityList.stream().filter(
				p -> p.workplace.equals(workplace) && !assignedWorkplaces.containsValue(p.worker) && !p.worker.equals(worker))
			.sorted(Comparator.comparingInt(p -> p.priority)).map(p -> p.worker).toList();

		if (availableWorkers.isEmpty())
		{
			return null;
		}
		else
		{
			return availableWorkers.stream().findFirst().get();
		}
	}

}

