package com.planovacsmeny.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WorkerDTO
{

	private Integer id;
	private String name;
	private Boolean available;
	private List<PriorityDTO> priorities;

	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	public static class PriorityDTO
	{
		private Integer workplaceId;
		private Integer priority;
	}


}


