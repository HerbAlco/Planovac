package com.planovacsmeny.demo.entity.repository;

import com.planovacsmeny.demo.entity.ScheduleAssignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ScheduleAssignmentRepository extends JpaRepository<ScheduleAssignment, Long>
{
}
