package com.planovacsmeny.demo.entity.repository;

import com.planovacsmeny.demo.entity.Worker;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WorkerRepository extends JpaRepository<Worker, Integer>
{
}
