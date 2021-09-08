package com.bandmates.application;

import com.bandmates.application.domain.AppUser;
import com.bandmates.application.domain.Profile;
import com.bandmates.application.domain.Role;
import com.bandmates.application.service.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.ArrayList;

@SpringBootApplication
public class BandmatesApplication {

	public static void main(String[] args) {
		SpringApplication.run(BandmatesApplication.class, args);
	}

	@Bean
	CommandLineRunner run(UserService userService) {
		return args -> {
			userService.saveRole(new Role(null, "ROLE_USER"));
			userService.saveRole(new Role(null, "ROLE_ADMIN"));

			userService.saveUser(new AppUser(null, "Tim", "Tyler", "tt", "tt@gmail.com", "password", new ArrayList<>(), null));
			userService.saveUser(new AppUser(null, "Jordan", "Scott", "js", "js@gmail.com", "password", new ArrayList<>(), null));

			userService.addRoleToUser("tt", "ROLE_USER");
			userService.addRoleToUser("js", "ROLE_ADMIN");
		};
	}

	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
}
