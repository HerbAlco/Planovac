package com.planovacsmeny.demo.service;

import com.planovacsmeny.demo.entity.AbsenceTypeEnum;
import com.planovacsmeny.demo.entity.Worker;
import com.planovacsmeny.demo.entity.WorkerAbsence;
import com.planovacsmeny.demo.entity.repository.WorkerAbsenceRepository;
import com.planovacsmeny.demo.entity.repository.WorkerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class WorkerAbsenceService
{
	@Autowired
	private WorkerAbsenceRepository workerAbsenceRepository;
	@Autowired
	private WorkerRepository workerRepository;

	public Boolean hasAbsenceOnDate(Integer workerId, LocalDate date)
	{
		List<WorkerAbsence> workerAbsences = workerAbsenceRepository.findByWorkerId(workerId);
		return workerAbsences.stream().anyMatch(
			workerAbsence -> !date.isBefore(workerAbsence.getStartDate()) && !date.isAfter(workerAbsence.getEndDate()));

	}

	public WorkerAbsence createWorkerAbsence(Integer workerId, AbsenceTypeEnum absenceTypeEnum, LocalDate from,
		LocalDate to)
	{
		WorkerAbsence newWorkerAbsence = new WorkerAbsence();
		newWorkerAbsence.setWorker(workerRepository.findById(workerId).orElseThrow());
		newWorkerAbsence.setAbsenceType(absenceTypeEnum);
		newWorkerAbsence.setStartDate(from);
		newWorkerAbsence.setEndDate(to != null ? to : from);

		return workerAbsenceRepository.save(newWorkerAbsence);
	}

	public List<Worker> filterWorkersWithoutAbsence(List<Worker> workers, LocalDate date)
	{
		workers.removeIf(worker -> worker.getName().equals("Máj") || worker.getName().equals("Walica") ||  worker.getName().equals("Brel"));
		return workers.stream().filter(worker -> !hasAbsenceOnDate(worker.getId(), date)).toList();
	}

}
