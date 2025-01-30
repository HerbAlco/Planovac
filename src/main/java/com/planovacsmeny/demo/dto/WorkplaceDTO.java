package com.planovacsmeny.demo.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WorkplaceDTO
{
	private Integer id;
	private String name;
	private Integer maxWorkers;
}
