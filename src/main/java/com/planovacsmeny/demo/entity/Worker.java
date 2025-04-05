package com.planovacsmeny.demo.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "_worker")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Worker {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	private String name;
	@Setter
	private Boolean isAvailable;

	@ManyToOne
	@JoinColumn(name = "schedule_assignment_id")
	@JsonBackReference
	private ScheduleAssignment scheduleAssignment;

	@OneToMany(mappedBy = "worker", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<WorkerAbsence> absences;

	@ElementCollection
	@CollectionTable(name = "worker_priorities",
		joinColumns = @JoinColumn(name = "worker_id"))
	private List<Priority> priorities;

	@ManyToOne
	@JoinColumn(name = "work_operation_id")
	@JsonBackReference
	private WorkOperation workOperation;

	public Boolean isAvailable()
	{
		return isAvailable;
	}

	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	@Embeddable
	public static class Priority {
		private Integer workplaceId;
		private Integer priority;
	}

	@Override
	public String toString() {
		return "Worker{" +
			"id=" + id +
			", name='" + name + '\'' +
			", available=" + isAvailable +
			", workOperation=" + (workOperation != null ? workOperation.getId() : "null") +
			'}';
	}

}
