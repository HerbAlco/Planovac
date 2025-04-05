package com.planovacsmeny.demo.service;

import com.planovacsmeny.demo.entity.ScheduleAssignment;
import com.planovacsmeny.demo.entity.Schedule;
import com.planovacsmeny.demo.entity.WorkOperation;
import com.planovacsmeny.demo.entity.Worker;
import com.planovacsmeny.demo.entity.repository.ScheduleRepository;
import com.planovacsmeny.demo.entity.repository.WorkOperationRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ScheduleService
{

	@Autowired
	private WorkOperationRepository workOperationRepository;
	@Autowired
	private ScheduleRepository scheduleRepository;
	@Autowired
	private WorkerAbsenceService workerAbsenceService;


	//TODO: vymyslet kdy se bude/budou vytvářet rozvrhy pro daný den/dny
	//TODO: pokud nebude ani jeden list empty workOperation.workers && workOperation.workplaces + pri kazdé úprave pridanim odebranim worker/workplace
	public Schedule createOrUpdateSchedule(Integer workOperationId, LocalDate date)
	{

		WorkOperation workOperation = workOperationRepository.findById(workOperationId)
			.orElseThrow(() -> new IllegalArgumentException("WorkOperation not found"));

		//Kontrola zda je vytvořené nějaké pracovní místo a pracovník jinak vratí null
		if (!hasValidWorkplacesAndWorkers(workOperation))
		{
			log.atInfo().log("Waiting for enough workplaces and workers to create Schedule.");
			return null;
		}

		//nastaví pritomnost pracovnika na dany den
		updateWorkerAvailability(workOperation.getWorkers(), date);

		//Kontrola zda už existuje rozvrh poté ho upravi a vráti
		Schedule existingSchedule = scheduleRepository.findByScheduleDateAndWorkOperation(date, workOperation);

		if (existingSchedule != null)
		{
			return updateSchedule(workOperation, existingSchedule);
		}

		//Vytvoří rozvrh pokud není vytvořený a vrátí ho
		Schedule schedule = initializeSchedule(workOperation);
		assignWorkersToWorkplaces(workOperation, schedule);

		schedule = scheduleRepository.save(schedule);
		log.atInfo().log("Created Schedule {}", schedule);
		return schedule;
	}

	private Schedule updateSchedule(WorkOperation workOperation, Schedule schedule)
	{
		updateWorkerAvailability(workOperation.getWorkers(), schedule.getScheduleDate());

		assignWorkersToWorkplaces(workOperation, schedule);

		schedule = scheduleRepository.save(schedule);
		log.atInfo().log("Updated Schedule {}", schedule);
		return schedule;
	}

	/*
	Obnoví pracovníkovi available na true a následně kontroluje zda-li nemá absenci v daný den
	následně nastavi hodnotu available
	*/
	private void updateWorkerAvailability(List<Worker> workers, LocalDate date)
	{
		for (Worker worker : workers)
		{
			//TODO: refactor -> on the end of the code are all worker.isAvailable set on false
			worker.setIsAvailable(true);

			worker.setIsAvailable(!workerAbsenceService.isAbsenceToday(worker.getId(), date));

			if (worker.getName().equals("Kubosch") || worker.getName().equals("Kukuczka") || worker.getName()
				.equals("Walica"))
			{
				worker.setIsAvailable(false);
			}

		}
	}

	private void assignWorkersToWorkplaces(WorkOperation workOperation, Schedule schedule)
	{
		List<Worker> availableWorkers = workOperation.getWorkers();
		List<ScheduleAssignment> assignments = schedule.getScheduleAssignments();
		for (ScheduleAssignment assignment : assignments)
		{
			assignment.getWorkers().clear();
			assignWorkersToWorkplace(availableWorkers, assignment);
		}

		boolean cyclus = false;
		updateWorkerAvailability(workOperation.getWorkers(), schedule.getScheduleDate());

		Set<String> previousStates = new HashSet<>();
		while (reassignWorkersToWorkplace(workOperation.getWorkers(), schedule, previousStates));


		do
		{
			for (ScheduleAssignment assignment : assignments)
			{
				if (assignment.getWorkers().isEmpty())
				{
					cyclus = assignWorkersToWorkplacePriority2(workOperation.getWorkers(), assignment, assignments);
					if (cyclus)
						break;
				}
			}
		}
		while (cyclus);

	}

	//prirazeni pracovniku k pracovisti podle priority 1
	private void assignWorkersToWorkplace(List<Worker> availableWorkers, ScheduleAssignment assignment)
	{
		Integer workplaceId = assignment.getWorkplace().getId();

		availableWorkers = availableWorkers.stream().filter(Worker::isAvailable)  // Zkontroluj dostupnost
			.filter(worker -> worker.getPriorities().stream().anyMatch(p -> p.getWorkplaceId()
				.equals(workplaceId) && p.getPriority() == 1))  // Filtruj pracovníky s prioritou 1 pro dané pracoviště
			.sorted(Comparator.comparingInt(worker -> worker.getPriorities().stream()
				.filter(p -> p.getWorkplaceId().equals(workplaceId) && p.getPriority() == 1)
				.map(Worker.Priority::getPriority).findFirst().orElse(
					1)))  // Seřaď pracovníky podle priority (i když mají stejnou prioritu 1, můžeš přidat další logiku pro další kritéria)
			.collect(Collectors.toList());

		// Přiřazování pracovníků s prioritou 1 k pracovišti, dokud není dosáhnuto maximálního počtu pracovníků
		while (assignment.getWorkers().size() < assignment.getWorkplace().getMaxWorkers() && !availableWorkers.isEmpty())
		{
			Worker worker = availableWorkers.remove(0);
			assignment.getWorkers().add(worker);
			worker.setIsAvailable(false);
		}
	}

	/*
    Najde nepřiřazeného pracovníka, jestli má prioritu 1 k již obsazenému pracovišti,
    a následně zkontroluje, jestli se může vyměnit s přiřazeným pracovníkem,
    který musí mít prioritu 1 k nějakému jinému neobsazenému pracovišti.
	*/
	private boolean reassignWorkersToWorkplace(List<Worker> workers, Schedule schedule, Set<String> previousStates)
	{

		List<Worker> unassignedWorkers = workers.stream().filter(Worker::isAvailable).filter(
				worker -> schedule.getScheduleAssignments().stream()
					.noneMatch(sa -> sa.getWorkers().contains(worker))) // Najdeme pracovníky, kteří nejsou v žádném schedule
			.toList();


		for (Worker worker : unassignedWorkers)
		{
			for (Worker.Priority priority : worker.getPriorities())
			{
				if (priority.getPriority() == 1)
				{

					// Získání obsazeného pracoviště pro pracovníka bez pracoviště s prioritou 1
					ScheduleAssignment occupiedSchedule = schedule.getScheduleAssignments().stream()
						.filter(sa -> sa.getWorkplace().getId().equals(priority.getWorkplaceId())).findFirst().orElse(null);

					if (occupiedSchedule == null)
						continue; // Pokud neexistuje, přeskoč iteraci

					// Získání pracovníka/ů z obsazeného pracoviště
					// Seznam přiřazených pracovníků na aktuálním obsazeném pracovišti
					List<Worker> assignedWorkers = occupiedSchedule.getWorkers();

					// Najdeme všechna neobsazená pracoviště (ScheduleAssignments, kde nejsou žádní pracovníci)
					List<ScheduleAssignment> unassignedSchedules = schedule.getScheduleAssignments().stream()
						.filter(sa -> sa.getWorkers().isEmpty()) // Filtrujeme na prázdná pracoviště
						.toList(); // Uložíme všechny neobsazené pracoviště do seznamu

					if (!unassignedSchedules.isEmpty())
					{
						// Procházení seznamu přiřazených pracovníků
						for (Worker assignedWorker : assignedWorkers)
						{
							// Pro každý pracovník procházíme jeho priority
							for (Worker.Priority assignedWorkerPriority : assignedWorker.getPriorities())
							{
								// Pro každé neobsazené pracoviště
								for (ScheduleAssignment unassignedSchedule : unassignedSchedules)
								{
									// Kontrola, zda má pracovník prioritu 1 k aktuálnímu neobsazenému pracovišti
									if (assignedWorkerPriority.getPriority() == 1 && assignedWorkerPriority.getWorkplaceId()
										.equals(unassignedSchedule.getWorkplace().getId()))
									{
										// Tento pracovník má prioritu 1 k neobsazenému pracovišti
										System.out.println(
											"Pracovník " + assignedWorker.getId() + " má prioritu 1 k neobsazenému pracovišti " + unassignedSchedule.getWorkplace()
												.getId());


										// Zkontrolujeme, zda se výměna neopakuje
										String stateKey = worker.getId() + "-" + occupiedSchedule.getWorkplace()
											.getId() + "_" + assignedWorker.getId() + "-" + unassignedSchedule.getWorkplace()
											.getId();

										if (previousStates.contains(stateKey))
										{
											return false; // Cyklus detekován, zastavíme výměny
										}
										previousStates.add(stateKey);

										// Provedení výměny:
										// - Pracovník bez pracoviště jde na obsazené místo
										// - Pracovník z obsazeného místa jde na neobsazené místo
										occupiedSchedule.getWorkers().remove(assignedWorker);
										occupiedSchedule.getWorkers().add(worker);
										unassignedSchedule.getWorkers().add(assignedWorker);

										return true; // Úspěšně přiděleno, končíme

									}
								}
							}
						}
					}
				}
			}
		}

		return false;
	}


	private Boolean assignWorkersToWorkplacePriority2(List<Worker> workers, ScheduleAssignment assignment,
		List<ScheduleAssignment> scheduleAssignments)
	{
		Integer workplaceId = assignment.getWorkplace().getId();

		List<Worker> availableWorkers = workers.stream().filter(Worker::isAvailable)  // Zkontroluj dostupnost
			.filter(worker -> worker.getPriorities().stream().anyMatch(p -> p.getWorkplaceId()
				.equals(workplaceId) && p.getPriority() == 2))  // Filtruj pracovníky s prioritou 2 pro dané pracoviště
			.sorted(Comparator.comparingInt(worker -> worker.getPriorities().stream()
				.filter(p -> p.getWorkplaceId().equals(workplaceId) && p.getPriority() == 2)
				.map(Worker.Priority::getPriority).findFirst().orElse(
					Integer.MAX_VALUE)))  // Seřaď pracovníky podle priority (i když mají stejnou prioritu 2, můžeš přidat další logiku pro další kritéria)
			.collect(Collectors.toList());

		// Přiřazování pracovníků s prioritou 2 k pracovišti, dokud není dosáhnuto maximálního počtu pracovníků na pracovišti
		while (assignment.getWorkers().size() < assignment.getWorkplace().getMaxWorkers() && !availableWorkers.isEmpty())
		{
			Worker worker = availableWorkers.remove(0);
			for (ScheduleAssignment scheduleAssignment : scheduleAssignments)
			{
				scheduleAssignment.getWorkers().removeIf(worker1 -> worker1.equals(worker));
			}
			assignment.getWorkers().add(worker);
			worker.setIsAvailable(false);
			return true;
		}
		return false;
	}

	private boolean hasValidWorkplacesAndWorkers(WorkOperation workOperation)
	{
		return !workOperation.getWorkers().isEmpty() || !workOperation.getWorkplaces().isEmpty();
	}

	//vytvoření prázdného rozvrhu
	private Schedule initializeSchedule(WorkOperation workOperation)
	{
		Schedule schedule = new Schedule();
		schedule.setWorkOperation(workOperation);

		List<ScheduleAssignment> scheduleAssignments = workOperation.getWorkplaces().stream().map(workplace -> {
			ScheduleAssignment assignment = new ScheduleAssignment();
			assignment.setWorkplace(workplace);
			assignment.setWorkers(new ArrayList<>());
			assignment.setSchedule(schedule);
			return assignment;
		}).toList();

		schedule.setScheduleAssignments(scheduleAssignments);
		return schedule;
	}

}
