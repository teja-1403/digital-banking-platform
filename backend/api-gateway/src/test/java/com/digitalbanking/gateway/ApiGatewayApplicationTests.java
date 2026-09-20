package com.digitalbanking.gateway;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.cloud.client.circuitbreaker.CircuitBreaker;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ApiGatewayApplicationTests {

	@Autowired
	private CircuitBreakerFactory<?, ?> circuitBreakerFactory;

	@Autowired
	private MockMvc mockMvc;

	@Test
	void contextLoads() {
	}

	@Test
	void circuitBreakerFactoryIsConfigured() {
		assertThat(circuitBreakerFactory).isNotNull();

		CircuitBreaker accountServiceCircuitBreaker =
				circuitBreakerFactory.create("accountServiceCircuitBreaker");

		assertThat(accountServiceCircuitBreaker).isNotNull();
	}

	@Test
	void serviceUnavailableFallbackReturns503() throws Exception {
		mockMvc.perform(get("/fallback/service-unavailable"))
				.andExpect(status().isServiceUnavailable())
				.andExpect(content().contentTypeCompatibleWith("application/json"))
				.andExpect(jsonPath("$.code").value("SERVICE_UNAVAILABLE"))
				.andExpect(jsonPath("$.message")
						.value("The requested service is temporarily unavailable. Please try again later."))
				.andExpect(jsonPath("$.timestamp").exists());
	}
}