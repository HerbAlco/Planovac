package com.planovacsmeny.demo.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "_workOperation")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class WorkOperation {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	private String name;

	@OneToMany(mappedBy = "workOperation")
	@JsonManagedReference
	private List<Worker> workers;

	@OneToMany(mappedBy = "workOperation")
	@JsonManagedReference
	private List<Workplace> workplaces;

	@Override
	public String toString() {
		return "WorkOperation{" +
			"id=" + id +
			", name='" + name + '\'' +
			", workersCount=" + (workers != null ? workers.size() : 0) +
			", workplacesCount=" + (workplaces != null ? workplaces.size() : 0) +
			'}';
	}


}
