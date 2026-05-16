package com.example.company_employee_service;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.core.AuthorizationGrantType;

@SpringBootTest
class CompanyEmployeeServiceApplicationTests {

	@TestConfiguration
	static class TestConfig {
		@Bean
		public ClientRegistrationRepository clientRegistrationRepository() {
			return new InMemoryClientRegistrationRepository(
					ClientRegistration.withRegistrationId("keycloak")
							.clientId("dummy")
							.authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
							.redirectUri("http://localhost")
							.authorizationUri("http://localhost")
							.tokenUri("http://localhost")
							.build()
			);
		}
	}

	@Test
	void contextLoads() {
	}
}