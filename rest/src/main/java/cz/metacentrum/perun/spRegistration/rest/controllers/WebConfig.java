package cz.metacentrum.perun.spRegistration.rest.controllers;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

/**
 * @author Vojtech Sassmann <vojtech.sassmann@gmail.com>
 */
@Configuration
public class WebConfig extends WebMvcConfigurationSupport {
	@Override
	protected void addCorsMappings(CorsRegistry registry) {
		registry.addMapping("/**")
				.allowedOrigins("http://localhost:4200")
				.allowedMethods(
						"GET",
						"POST",
						"PUT",
						"DELETE",
						"OPTIONS",
						"HEAD")
				.allowedHeaders("Origin", "X-Requested-With", "Content-Type", "Accept")
				.allowCredentials(true);
	}
}
