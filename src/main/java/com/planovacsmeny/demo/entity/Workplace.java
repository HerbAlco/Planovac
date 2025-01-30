package com.planovacsmeny.demo.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
