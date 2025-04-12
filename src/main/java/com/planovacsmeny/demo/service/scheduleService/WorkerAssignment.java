package com.planovacsmeny.demo.service.scheduleService;

import com.planovacsmeny.demo.entity.Schedule;
import com.planovacsmeny.demo.entity.ScheduleAssignment;
import com.planovacsmeny.demo.entity.Worker;
import com.planovacsmeny.demo.entity.Workplace;
import com.planovacsmeny.demo.entity.repository.ScheduleAssignmentRepository;
import com.planovacsmeny.demo.entity.repository.ScheduleRepository;
import com.planovacsmeny.demo.entity.repository.WorkOperationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class WorkerAssignment
{

	@Autowired
	private WorkOperationRepository workOperationRepository;
	@Autowired
	private ScheduleRepository scheduleRepository;
	@Autowired
	private ScheduleAssignmentRepository scheduleAssignmentRepository;

	public Schedule search(List<Worker> workers, List<Workplace> workplaces, List<Priority> priorityList,
		Schedule assignedSchedule, Worker excludedWorker, Workplace replacementWorkplace)
	{


		// Pokud jsou všechna pracovní místa obsazena, vypiš řešení
		if (assignedSchedule.getScheduleAssignments().size() == workplaces.size())
		{
			assignedSchedule.getScheduleAssignments().forEach(sa -> {
				System.err.print(sa.getWorkplace() + " -> ");
				sa.getWorkers().forEach(w -> {
					if (w == null)
					{
						System.err.print("Neobsazeno");
					}
					else
					{
						System.err.print(w.getName() + ", ");
					}
				});
				System.err.println();
			});

			// Určení pracovníků, kteří nebyli přiřazeni
			List<Worker> assignedWorkersList = assignedSchedule.getScheduleAssignments().stream()
				.filter(scheduleAssignment -> !scheduleAssignment.getWorkers().isEmpty())
				.flatMap(scheduleAssignment -> scheduleAssignment.getWorkers().stream()).toList();

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
			return assignedSchedule;
		}

		// Najdi první neobsazené pracoviště
		Workplace currentWorkplace = replacementWorkplace != null
			? replacementWorkplace
			: workplaces.stream().filter(
					w -> assignedSchedule.getScheduleAssignments().stream().noneMatch(sa -> sa.getWorkplace().equals(w)))
				.findFirst().orElse(null);

		if (currentWorkplace == null)
		{
			System.out.println("Žádné dostupné pracoviště nebylo nalezeno.");
			return assignedSchedule;
		}


		// Získání dostupných pracovníků seřazených podle priority
		List<Worker> availableWorkers = priorityList.stream().filter(
				p -> p.workplace.equals(currentWorkplace) && assignedSchedule.getScheduleAssignments().stream()
					.noneMatch(sa -> sa.getWorkers().equals(p.worker)) && !p.worker.equals(excludedWorker))
			.sorted(Comparator.comparingInt(p -> p.priority)).map(p -> p.worker).toList();

		// Pokud nejsou žádní dostupní pracovníci, vrať se
		if (availableWorkers.isEmpty())
		{
			List<Worker> workersForCurrentWorkplace = priorityList.stream()
				.filter(p -> p.workplace.equals(currentWorkplace)).map(p -> p.worker).toList();

			for (Worker s : workersForCurrentWorkplace)
			{

				Optional<Workplace> workplace = assignedSchedule.getScheduleAssignments().stream()
					.filter(sa -> sa.getWorkers().equals(s)).map(ScheduleAssignment::getWorkplace).findFirst();


				if (workplace.isEmpty())
				{
					continue;
				}

				if (workerAvailableForWorkplace(s, workplace.get(), assignedSchedule, priorityList) != null)
				{
					ScheduleAssignment scheduleAssignment = assignedSchedule.getScheduleAssignments().stream()
						.filter(sa -> sa.getWorkplace().equals(workplace.get())).findFirst().orElseThrow();

					assignedSchedule.getScheduleAssignments().remove(scheduleAssignment);
					search(workers, workplaces, priorityList, assignedSchedule, s, workplace.get());
					return assignedSchedule;
				}

			}
		}

		Worker worker;
		// Rekurzivně přiřazuj pracovníky k pracovištím
		if (availableWorkers.isEmpty())
		{
			worker = null;

		}
		else
		{
			worker = availableWorkers.get(0);
		}

		ScheduleAssignment scheduleAssignment = new ScheduleAssignment();
		scheduleAssignment.setSchedule(assignedSchedule);
		scheduleAssignment.setWorkplace(currentWorkplace);
		scheduleAssignment.getWorkers().add(worker);
		scheduleAssignmentRepository.save(scheduleAssignment);
		assignedSchedule.getScheduleAssignments().add(scheduleAssignment);

		search(workers, workplaces, priorityList, assignedSchedule, null, null);

		return assignedSchedule;
	}


	private static Worker workerAvailableForWorkplace(Worker worker, Workplace workplace, Schedule assignedSchedule,
		List<Priority> priorityList)
	{

		List<Worker> availableWorkers = priorityList.stream().filter(
				p -> p.workplace.equals(workplace) && assignedSchedule.getScheduleAssignments().stream()
					.noneMatch(sa -> sa.getWorkers().equals(p.worker)) && !p.worker.equals(worker))
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

