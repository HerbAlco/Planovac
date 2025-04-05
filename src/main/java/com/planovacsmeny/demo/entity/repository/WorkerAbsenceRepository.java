package com.planovacsmeny.demo.entity.repository;

import com.planovacsmeny.demo.entity.WorkerAbsence;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WorkerAbsenceRepository extends JpaRepository<WorkerAbsence, Long>
{
	List<WorkerAbsence> findByWorkerId(Integer id);
}
