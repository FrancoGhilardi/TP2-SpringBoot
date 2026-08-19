package com.tp2springboot.tp2_spring_boot.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

	@Bean
	public OpenAPI openApi() {
		return new OpenAPI()
				.info(new Info()
						.title("API REST - Sistema de Gestion de Pedidos")
						.version("1.0.0")
						.description("API REST para el sistema de gestion de pedidos (categorias, productos, usuarios y pedidos)."))
				.servers(List.of(new Server().url("http://localhost:8080")));
	}

}
