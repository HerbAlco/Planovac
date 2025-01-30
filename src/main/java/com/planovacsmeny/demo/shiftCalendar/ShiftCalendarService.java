package com.planovacsmeny.demo.shiftCalendar;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
public class ShiftCalendarService
{

	// Stejná logika jako předtím, kde se vypočítají směny
	private static final LocalDate START_DATE = LocalDate.of(2024, 10, 11);
	private static final String[] SMENY = {"Ranní", "Noční", "Odpolední", "Volno"};


	// Funkce vrátí HashMap s kalendářními daty
	public  Map<String, Map<String, String>> vytvoritKalendar() {
		LocalDate currentDate = LocalDate.now();
		Map<String, Map<String, String>> kalendarMap = new LinkedHashMap<>();

		// Počet dnů, které chceme zobrazit, například 7 dní
		int pocetDnu = 2;

		for (int i = 0; i < pocetDnu; i++) {
			LocalDate den = currentDate.plusDays(i);
			Map<String, String> smenyProDen = vytiskniSmeny(den);
			kalendarMap.put(den.toString(), smenyProDen);
		}

		return kalendarMap;
	}

	// Funkce, která vypočítá směny pro daný den
	private static Map<String, String> vytiskniSmeny(LocalDate den) {
		long daysElapsed = ChronoUnit.DAYS.between(START_DATE, den);
		int dayOffset = (int) (daysElapsed / 2 % 4);

		// Mapu s přiřazenými směnami
		Map<String, String> smenyMap = new HashMap<>();
		smenyMap.put("A", SMENY[dayOffset]);
		smenyMap.put("B", SMENY[(dayOffset + 1) % 4]);
		smenyMap.put("C", SMENY[(dayOffset + 2) % 4]);
		smenyMap.put("D", SMENY[(dayOffset + 3) % 4]);

		return smenyMap;
	}

}
