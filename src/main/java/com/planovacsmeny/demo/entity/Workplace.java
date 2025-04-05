package com.planovacsmeny.demo.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "_workplace")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Workplace {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	private String name;
	private Integer maxWorkers;

	@ManyToOne
	@JoinColumn(name = "work_operation_id")
	@JsonBackReference
	private WorkOperation workOperation;

	@OneToMany(mappedBy = "workplace")
	@JsonManagedReference
	private List<ScheduleAssignment> scheduleAssignments;

	@Override
	public String toString() {
		return "Workplace{" +
			"id=" + id +
			", name='" + name + '\'' +
			", maxWorkers=" + maxWorkers +
			", workOperationId=" + (workOperation != null ? workOperation.getId() : "null") +
			'}';
	}

}
