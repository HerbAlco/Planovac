package com.planovacsmeny.demo.entity.repository;

import com.planovacsmeny.demo.entity.Workplace;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WorkplaceRepository extends JpaRepository<Workplace, Integer> {

	List<Workplace> findAllByWorkOperation_Id(Integer workOperationId);
}

