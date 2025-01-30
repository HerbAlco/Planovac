package com.planovacsmeny.demo.dto;

import com.planovacsmeny.demo.entity.Worker;
import com.planovacsmeny.demo.entity.Workplace;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WorkOperationDTO
{

	private Integer id;
	private String name;
	private List<Workplace> workplaces;
	private List<Worker> workers;

}
