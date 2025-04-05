package com.planovacsmeny.demo.entity.repository;

import com.planovacsmeny.demo.entity.Schedule;
import com.planovacsmeny.demo.entity.WorkOperation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Long>
{
	Schedule findByScheduleDateAndWorkOperation(LocalDate date, WorkOperation workOperation);
	void deleteAllByWorkOperationId(Integer id);
}
