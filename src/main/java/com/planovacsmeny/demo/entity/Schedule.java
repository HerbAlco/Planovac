package com.planovacsmeny.demo.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Schedule {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private LocalDate scheduleDate = LocalDate.now();

	@OneToMany(mappedBy = "schedule", cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonManagedReference
	private List<ScheduleAssignment> scheduleAssignments = new ArrayList<>();

	@ManyToOne
	@JoinColumn(name = "work_operation_id")
	private WorkOperation workOperation;

	@Override
	public String toString() {
		return "Schedule{" +
			"id=" + id +
			", workOperation=" + (workOperation != null ? workOperation.getId() : "null") +
			", scheduleAssignments=" + (scheduleAssignments != null ?
			scheduleAssignments.stream().map(ScheduleAssignment::toString).collect(Collectors.joining(", ")) : "null") +
			'}';
	}
}
