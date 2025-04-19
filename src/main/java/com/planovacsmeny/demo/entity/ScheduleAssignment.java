package com.planovacsmeny.demo.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"workplace_id", "date"}))
public class ScheduleAssignment
{

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "schedule_id", nullable = false)
	@JsonBackReference
	private Schedule schedule;

	@ManyToOne
	@JoinColumn(name = "workplace_id", nullable = false)
	private Workplace workplace;

	@OneToMany(mappedBy = "scheduleAssignment", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Worker> workers = new ArrayList<>();

	@Override
	public String toString() {
		return "ScheduleAssignment{" +
			"id=" + id +
			", schedule=" + (schedule != null ? schedule.getId() : "null") +
			", workplace=" + (workplace != null ? workplace.getName() : "null") +
			", workers=" + (workers != null ? workers.stream().map(Worker::getName).collect(Collectors.joining(", ")) : "null") +
			'}';
	}

}
