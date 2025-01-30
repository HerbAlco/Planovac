package com.planovacsmeny.demo.entity.repository;

import com.planovacsmeny.demo.entity.WorkOperation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WorkOperationRepository extends JpaRepository<WorkOperation, Integer> {
}
