package com.planovacsmeny.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WorkOperationCreateDTO {
	private String name;
	private List<Long> workplaceIds;
	private List<Long> workerIds;
}

