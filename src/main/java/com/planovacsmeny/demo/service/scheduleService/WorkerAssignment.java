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
		Schedule assignedSchedule, List<Worker> excludedWorkers, Workplace replacementWorkplace)
	{

		if (assignedSchedule.getScheduleAssignments().size() == workplaces.size())
		{
			printSchedule(assignedSchedule);
			printUnassignedWorkers(workers, assignedSchedule);
			return assignedSchedule;
		}

		Workplace currentWorkplace = findNextWorkplace(assignedSchedule, workplaces, replacementWorkplace);

		if (currentWorkplace == null)
		{
			System.out.println("Žádné dostupné pracoviště nebylo nalezeno.");
			return assignedSchedule;
		}

		List<Worker> availableWorkers = getAvailableWorkers(priorityList, currentWorkplace, assignedSchedule,
			excludedWorkers);

		if (availableWorkers.isEmpty() || availableWorkers.size() < currentWorkplace.getMaxWorkers())
		{
			handleUnavailableWorkers(priorityList, currentWorkplace, excludedWorkers, assignedSchedule, workers,
				workplaces);
			return assignedSchedule;
		}

		ScheduleAssignment assignment = new ScheduleAssignment();
		assignment.setSchedule(assignedSchedule);
		assignment.setWorkplace(currentWorkplace);

		for (int i = 0; i < currentWorkplace.getMaxWorkers() && i < availableWorkers.size(); i++)
		{
			Worker worker = availableWorkers.get(i);
			System.err.println(currentWorkplace.getName() + " -> " + worker.getName());
			assignment.getWorkers().add(worker);
		}

		assignedSchedule.getScheduleAssignments().add(assignment);
		search(workers, workplaces, priorityList, assignedSchedule, new ArrayList<>(), null);

		return assignedSchedule;
	}

	private void printSchedule(Schedule assignedSchedule)
	{
		assignedSchedule.getScheduleAssignments().stream().sorted(Comparator.comparing(sa -> sa.getWorkplace().getId()))
			.forEach(sa -> {
				scheduleAssignmentRepository.save(sa);
				System.err.print(sa.getWorkplace() + " -> ");
				sa.getWorkers().forEach(w -> System.err.print(w == null ? "Neobsazeno" : w.getName() + ", "));
				System.err.println();
			});
	}

	private void printUnassignedWorkers(List<Worker> allWorkers, Schedule assignedSchedule)
	{
		List<Worker> assigned = assignedSchedule.getScheduleAssignments().stream().flatMap(sa -> sa.getWorkers().stream())
			.distinct().toList();

		List<Worker> unassigned = allWorkers.stream().filter(w -> !assigned.contains(w)).toList();

		if (unassigned.isEmpty())
		{
			System.out.println("Všichni pracovníci byli přiřazeni.");
		}
		else
		{
			unassigned.forEach(worker -> System.out.println("- " + worker));
		}
	}

	private Workplace findNextWorkplace(Schedule schedule, List<Workplace> workplaces, Workplace replacement)
	{
		if (replacement != null)
			return replacement;
		return workplaces.stream()
			.filter(w -> schedule.getScheduleAssignments().stream().noneMatch(sa -> sa.getWorkplace().equals(w)))
			.findFirst().orElse(null);
	}

	private List<Worker> getAvailableWorkers(List<Priority> priorityList, Workplace workplace, Schedule schedule,
		List<Worker> excluded)
	{
		return priorityList.stream().filter(p -> p.workplace.equals(workplace))
			.filter(p -> schedule.getScheduleAssignments().stream().noneMatch(sa -> sa.getWorkers().contains(p.worker)))
			.filter(p -> !excluded.contains(p.worker)).sorted(Comparator.comparingInt(p -> p.priority)).map(p -> p.worker)
			.toList();
	}

	private void handleUnavailableWorkers(List<Priority> priorityList, Workplace currentWorkplace,
		List<Worker> excludedWorkers, Schedule assignedSchedule, List<Worker> workers, List<Workplace> workplaces)
	{

		List<Priority> candidates = priorityList.stream().filter(p -> p.workplace.equals(currentWorkplace))
			.filter(p -> !excludedWorkers.contains(p.worker)).sorted(Comparator.comparingInt(p -> p.priority)).toList();

		List<Worker> reassignedWorkers = candidates.stream().map(p -> p.worker)
			.filter(w -> assignedSchedule.getScheduleAssignments().stream().anyMatch(sa -> sa.getWorkers().contains(w)))
			.limit(currentWorkplace.getMaxWorkers()).toList();

		ScheduleAssignment newAssignment = new ScheduleAssignment();
		newAssignment.setSchedule(assignedSchedule);
		newAssignment.setWorkplace(currentWorkplace);

		Workplace fromWorkplace = null;
		for (Worker reassigned : reassignedWorkers)
		{
			Optional<ScheduleAssignment> originalAssignment = assignedSchedule.getScheduleAssignments().stream()
				.filter(sa -> sa.getWorkers().contains(reassigned)).findFirst();

			if (originalAssignment.isPresent())
			{
				if (!originalAssignment.get().getWorkplace().equals(currentWorkplace))
				{
					fromWorkplace = originalAssignment.get().getWorkplace();
				}
				assignedSchedule.getScheduleAssignments().remove(originalAssignment.get());
				newAssignment.getWorkers().add(reassigned);
				excludedWorkers.add(reassigned);
			}
		}

		assignedSchedule.getScheduleAssignments().add(newAssignment);
		if (!newAssignment.getWorkers().isEmpty())
		{
			search(workers, workplaces, priorityList, assignedSchedule, excludedWorkers, fromWorkplace);
		}

		search(workers, workplaces, priorityList, assignedSchedule, new ArrayList<>(), null);
	}

}