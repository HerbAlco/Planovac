package com.planovacsmeny.demo.service.scheduleService;

import com.planovacsmeny.demo.entity.Schedule;
import com.planovacsmeny.demo.entity.WorkOperation;
import com.planovacsmeny.demo.entity.Worker;
import com.planovacsmeny.demo.entity.Workplace;
import com.planovacsmeny.demo.entity.repository.ScheduleRepository;
import com.planovacsmeny.demo.entity.repository.WorkOperationRepository;
import com.planovacsmeny.demo.service.ScheduleService;
import com.planovacsmeny.demo.service.WorkerAbsenceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

@Slf4j
@Service
public class ScheduleServices
{
	@Autowired
	private WorkOperationRepository workOperationRepository;
	@Autowired
	private WorkerAbsenceService workerAbsenceService;
	@Autowired
	private WorkerAssignment workerAssignment;
	@Autowired
	private ScheduleRepository scheduleRepository;

	public Schedule createSchedule(Integer workOperationId, LocalDate onDate)
	{

		WorkOperation workOperation = workOperationRepository.findById(workOperationId).orElseThrow();

		//Kontrola zda je vytvořené nějaké pracovní místo a pracovník jinak vratí null
		if (!hasValidWorkplacesAndWorkers(workOperation))
		{
			log.atInfo().log("Waiting for enough workplaces and workers to create Schedule.");
			return null;
		}

		List<Worker> workers = workerAbsenceService.filterWorkersWithoutAbsence(workOperation.getWorkers(), onDate);

		List<Workplace> workplaces = workOperation.getWorkplaces();

		List<Map<Workplace, List<Worker>>> priorityData = new ArrayList<>();

		// Iteruj přes všechna pracoviště
		for (Workplace workplace : workplaces)
		{
			int workplaceId = workplace.getId();

			// Pro každý pracovníka zjisti, zda má prioritu pro toto pracoviště
			Map<Workplace, List<Worker>> workplaceWorkersMap = new HashMap<>();

			List<Worker> matchedWorkers = workers.stream().filter(worker -> worker.getPriorities().stream()
				.anyMatch(priority -> priority.getWorkplaceId().equals(workplaceId))).sorted(Comparator.comparingInt(
				worker -> worker.getPriorities().stream().filter(priority -> priority.getWorkplaceId().equals(workplaceId))
					.findFirst().map(Worker.Priority::getPriority).orElse(Integer.MAX_VALUE)
				// fallback pokud by náhodou chyběla priorita
			)).toList();


			workplaceWorkersMap.put(workplace, matchedWorkers); // Přidej mapu s pracovištěm a pracovníky
			priorityData.add(workplaceWorkersMap); // Přidej mapu do seznamu
		}


		// Inicializace matice priorit
		PriorityMatrix matrix = new PriorityMatrix(workers, workplaces, priorityData);
		List<Priority> priorityList = matrix.constructSequence();
		Schedule scheduleOnDate = scheduleRepository.findByScheduleDateAndWorkOperation(onDate, workOperation);

		//TODO: dodelat...  Pokud se nezmění absence u pracovniku vrati uz vytvorenou scheduli
		//TODO: upravit metodu u pridavani absence aby se upravila schedule a tim padem podminka za else nemusi byt jen vrati scheduli
		if(scheduleOnDate == null){
			scheduleOnDate = new Schedule();
			scheduleOnDate.setScheduleDate(onDate);
			scheduleOnDate.setWorkOperation(workOperation);
			scheduleRepository.save(scheduleOnDate);
		} else {
			//TODO: vytváří rozdělení pokaždé, když se zavolá controller i když nedošlo ke změně (optimalizovat)
			scheduleOnDate.getScheduleAssignments().clear();
			scheduleRepository.save(scheduleOnDate);
		}


		return workerAssignment.search(workers, workplaces, priorityList, scheduleOnDate, new ArrayList<>(), null);


	}

	private boolean hasValidWorkplacesAndWorkers(WorkOperation workOperation)
	{
		return !workOperation.getWorkers().isEmpty() || !workOperation.getWorkplaces().isEmpty();
	}

}

