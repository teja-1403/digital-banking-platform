package com.digitalbanking.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerResponse;

import static org.springframework.cloud.gateway.server.mvc.filter.BeforeFilterFunctions.uri;
import static org.springframework.cloud.gateway.server.mvc.handler.GatewayRouterFunctions.route;
import static org.springframework.cloud.gateway.server.mvc.handler.HandlerFunctions.http;

@Configuration
public class GatewayRoutesConfig {

    @Bean
    public RouterFunction<ServerResponse> authServiceRoute() {
        return route("auth-service")
                .GET("/api/auth/**", http())
                .POST("/api/auth/**", http())
                .PUT("/api/auth/**", http())
                .PATCH("/api/auth/**", http())
                .DELETE("/api/auth/**", http())
                .before(uri("http://localhost:8081"))
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> customerServiceRoute() {
        return route("customer-service")
                .GET("/api/customers/**", http())
                .POST("/api/customers/**", http())
                .PUT("/api/customers/**", http())
                .PATCH("/api/customers/**", http())
                .DELETE("/api/customers/**", http())
                .before(uri("http://localhost:8082"))
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> accountServiceRoute() {
        return route("account-service")
                .GET("/api/accounts/**", http())
                .POST("/api/accounts/**", http())
                .PUT("/api/accounts/**", http())
                .PATCH("/api/accounts/**", http())
                .DELETE("/api/accounts/**", http())
                .before(uri("http://localhost:8082"))
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> beneficiaryServiceRoute() {
        return route("beneficiary-service")
                .GET("/api/beneficiaries/**", http())
                .POST("/api/beneficiaries/**", http())
                .PUT("/api/beneficiaries/**", http())
                .PATCH("/api/beneficiaries/**", http())
                .DELETE("/api/beneficiaries/**", http())
                .before(uri("http://localhost:8082"))
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> transactionServiceRoute() {
        return route("transaction-service")
                .GET("/api/transactions/**", http())
                .POST("/api/transactions/**", http())
                .PUT("/api/transactions/**", http())
                .PATCH("/api/transactions/**", http())
                .DELETE("/api/transactions/**", http())
                .before(uri("http://localhost:8083"))
                .build();
    }
}