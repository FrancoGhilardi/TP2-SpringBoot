package com.tp2springboot.tp2_spring_boot.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.tags.Tag;
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
						.description(
								"API REST para el sistema de gestion de pedidos (categorias, productos, usuarios y pedidos). "
										+ "Trabajo practico de Programacion IV - UTN.")
						.contact(new Contact()
								.name("Franco Ghilardi")
								.email("franco.ghilardi1@gmail.com"))
						.license(new License().name("Uso academico - UTN")))
				.servers(List.of(new Server().url("http://localhost:8080")))
				.tags(List.of(
						new Tag().name("Categorías").description("ABM de categorías de productos"),
						new Tag().name("Productos").description("ABM de productos"),
						new Tag().name("Usuarios").description("ABM de usuarios"),
						new Tag().name("Pedidos").description("ABM de pedidos")));
	}

}
