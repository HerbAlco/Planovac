package com.planovacsmeny.demo.controller;

import com.planovacsmeny.demo.service.AssignmentService;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api")
public class PlanovacController {

	private final AssignmentService assignmentService;

	public PlanovacController(AssignmentService assignmentService) {
		this.assignmentService = assignmentService;
	}

//	@GetMapping("/workplaces")
//	public List<Workplace> getWorkplaces() {
//		List<Workplace> workplaces = new ArrayList<>();
//		workplaces.add(new Workplace(1, "Mair1", 1, new ArrayList<>()));
//		workplaces.add(new Workplace(2, "Mair2", 1, new ArrayList<>()));
//		workplaces.add(new Workplace(3, "Leštička č.1", 1, new ArrayList<>()));
//		workplaces.add(new Workplace(4, "Leštička č.2", 1, new ArrayList<>()));
//		workplaces.add(new Workplace(5, "CF", 1, new ArrayList<>()));
//		workplaces.add(new Workplace(6, "Pila/Hrot/Vazák", 1, new ArrayList<>()));
//		workplaces.add(new Workplace(7, "Expedient", 1, new ArrayList<>()));
//		workplaces.add(new Workplace(8, "Jiskra", 1, new ArrayList<>()));
//		workplaces.add(new Workplace(9, "Rovnačka", 1, new ArrayList<>()));
//		workplaces.add(new Workplace(10, "UZ", 1, new ArrayList<>()));
//		workplaces.add(new Workplace(11, "IZL1", 2, new ArrayList<>()));
//		workplaces.add(new Workplace(12, "IZL2", 2, new ArrayList<>()));
//		workplaces.add(new Workplace(13, "Jeřáb č.13", 1, new ArrayList<>()));
//		workplaces.add(new Workplace(14, "Jeřáb č.14", 1, new ArrayList<>()));
//		workplaces.add(new Workplace(15, "Jeřáb č.15", 1, new ArrayList<>()));
//		workplaces.add(new Workplace(16, "Jeřáb č.16", 1, new ArrayList<>()));
//		workplaces.add(new Workplace(17, "K1", 1, new ArrayList<>()));
//
//		List<List<Integer>> workerPriorities = List.of(
//			List.of(5, 5, 5, 5, 5, 4, 5, 5, 1, 5, 5, 5, 5, 5, 5, 5, 5), // Bartosz
//			List.of(2, 2, 5, 5, 5, 1, 3, 3, 5, 5, 5, 5, 5, 5, 5, 5, 3), // Beneš
//			List.of(5, 5, 5, 5, 1, 5, 4, 5, 5, 3, 5, 5, 3, 3, 3, 3, 2), // Bernkopf
//			List.of(5, 5, 5, 5, 5, 5, 5, 5, 3, 5, 2, 1, 2, 2, 2, 2, 5), // Brel
//			List.of(5, 5, 5, 5, 5, 4, 5, 5, 5, 5, 5, 5, 1, 1, 1, 1, 5), // Foltyn
//			List.of(1, 1, 2, 2, 5, 5, 5, 5, 5, 5, 5, 5, 3, 3, 3, 3, 5), // Kubosch
//			List.of(2, 2, 5, 5, 3, 5, 5, 5, 5, 1, 5, 5, 5, 5, 5, 5, 5), // Kufa
//			List.of(5, 5, 5, 5, 5, 2, 3, 5, 5, 5, 2, 1, 4, 4, 4, 4, 5), // Maj
//			List.of(5, 5, 5, 5, 5, 5, 1, 2, 5, 5, 4, 3, 2, 2, 2, 2, 5), // Niedoba
//			List.of(5, 5, 5, 5, 5, 4, 5, 3, 3, 5, 1, 2, 3, 3, 3, 3, 5), // Sikora
//			List.of(5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 2, 4, 1, 1, 1, 1, 5), // Skupien
//			List.of(1, 1, 5, 5, 5, 5, 3, 3, 5, 2, 5, 5, 5, 5, 5, 5, 5), // Slowik
//			List.of(2, 2, 1, 1, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5), // Stryja
//			List.of(5, 5, 1, 1, 5, 2, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5), // Timan
//			List.of(5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 1, 2, 3, 3, 3, 3, 3), // Valek
//			List.of(5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 1, 1, 1, 1, 5), // ValkovaD
//			List.of(5, 5, 5, 5, 5, 5, 3, 3, 5, 5, 5, 5, 1, 1, 1, 1, 5), // ValkovaK
//			List.of(5, 5, 5, 5, 3, 5, 5, 5, 5, 2, 2, 3, 3, 3, 3, 3, 2), // Walica
//			List.of(5, 5, 5, 5, 2, 5, 4, 5, 5, 4, 5, 5, 5, 5, 5, 5, 1), // Zapalc
//			List.of(5, 5, 5, 5, 5, 5, 5, 1, 5, 5, 5, 5, 5, 5, 5, 5, 5), // Subrt
//			List.of(5, 5, 3, 3, 5, 2, 5, 3, 1, 5, 5, 5, 5, 5, 5, 5, 5)  // Cmiel
//		);
//
//		List<String> nameOfWorkers = List.of("Bartosz", "Beneš", "Bernkopf", "Brél", "Foltyn", "Kubosch", "Kufa", "Máj",
//			"Niedoba", "Sikora", "Skupień", "Słowik", "Stryja", "Timan", "Válek", "Válková D.", "Válková K.", "Walica",
//			"Zapalač", "Šubert", "Čmiel");
//
//		// Vytvoření seznamu pracovníků
//		List<Worker> workers = new ArrayList<>();
//		for (int i = 0; i < workerPriorities.size(); i++) {
//			List<Worker.Priority> priorities = new ArrayList<>();
//			// Přiřazení priorit k jednotlivým pracovním místům
//			for (int j = 0; j < workplaces.size(); j++) {
//				priorities.add(new Worker.Priority(j + 1, workerPriorities.get(i).get(j)));
//			}
//			// Přiřazení jména pracovníka z nameOfWorkers
//			workers.add(new Worker(i + 1, nameOfWorkers.get(i), true, priorities));
//		}
//
//		// Přiřazení pracovníků k pracovním místům
//		return assignmentService.assignWorkersToWorkplace(workplaces, workers);
//	}
}
