package com.planovacsmeny.demo.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

	@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry.addMapping("/**") // Umožňuje CORS pro všechny endpointy
			.allowedOrigins("http://localhost:5173") // Povolený původ
			.allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // Povolené HTTP metody
			.allowedHeaders("*") // Povolené hlavičky
			.allowCredentials(true); // Povolit sdílení přihlašovacích údajů (např. cookies)
	}
}
