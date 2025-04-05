package com.planovacsmeny.demo.service;

import com.planovacsmeny.demo.entity.AbsenceTypeEnum;
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

	public Boolean isAbsenceToday (Integer workerId, LocalDate date){
		List<WorkerAbsence> workerAbsences = workerAbsenceRepository.findByWorkerId(workerId);
		for (WorkerAbsence workerAbsence: workerAbsences){
			if(date.isAfter(workerAbsence.getStartDate()) && date.isBefore(workerAbsence.getEndDate())){
				return true;
			}
		}
		return false;
	}

	public WorkerAbsence createWorkerAbsence(Integer workerId, AbsenceTypeEnum absenceTypeEnum, LocalDate from, LocalDate to){
		WorkerAbsence newWorkerAbsence = new WorkerAbsence();
		newWorkerAbsence.setWorker(workerRepository.findById(workerId).orElseThrow());
		newWorkerAbsence.setAbsenceType(absenceTypeEnum);
		newWorkerAbsence.setStartDate(from);
		newWorkerAbsence.setEndDate(to != null ? to : from);

		return workerAbsenceRepository.save(newWorkerAbsence);
	}
}
