package com.digitalbanking.gateway.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerResponse;

import static org.junit.jupiter.api.Assertions.*;

class GatewayRoutesConfigTest {

    private GatewayRoutesConfig config;

    @BeforeEach
    void setUp() {
        config = new GatewayRoutesConfig();

        ReflectionTestUtils.setField(
                config,
                "authServiceUrl",
                "http://localhost:8081"
        );

        ReflectionTestUtils.setField(
                config,
                "accountServiceUrl",
                "http://localhost:8082"
        );

        ReflectionTestUtils.setField(
                config,
                "transactionServiceUrl",
                "http://localhost:8083"
        );
    }

    @Test
    void authServiceRoute_shouldBeCreated() {

        RouterFunction<ServerResponse> route =
                config.authServiceRoute();

        assertNotNull(route);
    }

    @Test
    void customerServiceRoute_shouldBeCreated() {

        RouterFunction<ServerResponse> route =
                config.customerServiceRoute();

        assertNotNull(route);
    }

    @Test
    void accountServiceRoute_shouldBeCreated() {

        RouterFunction<ServerResponse> route =
                config.accountServiceRoute();

        assertNotNull(route);
    }

    @Test
    void beneficiaryServiceRoute_shouldBeCreated() {

        RouterFunction<ServerResponse> route =
                config.beneficiaryServiceRoute();

        assertNotNull(route);
    }

    @Test
    void transactionServiceRoute_shouldBeCreated() {

        RouterFunction<ServerResponse> route =
                config.transactionServiceRoute();

        assertNotNull(route);
    }

    @Test
    void adminUserRoute_shouldBeCreated() {

        RouterFunction<ServerResponse> route =
                config.adminUserRoute();

        assertNotNull(route);
    }

    @Test
    void adminAccountRoute_shouldBeCreated() {

        RouterFunction<ServerResponse> route =
                config.adminAccountRoute();

        assertNotNull(route);
    }

    @Test
    void adminTransactionRoute_shouldBeCreated() {

        RouterFunction<ServerResponse> route =
                config.adminTransactionRoute();

        assertNotNull(route);
    }

    @Test
    void allRoutes_shouldBeCreatedIndependently() {

        assertAll(
                () -> assertNotNull(
                        config.authServiceRoute()
                ),
                () -> assertNotNull(
                        config.customerServiceRoute()
                ),
                () -> assertNotNull(
                        config.accountServiceRoute()
                ),
                () -> assertNotNull(
                        config.beneficiaryServiceRoute()
                ),
                () -> assertNotNull(
                        config.transactionServiceRoute()
                ),
                () -> assertNotNull(
                        config.adminUserRoute()
                ),
                () -> assertNotNull(
                        config.adminAccountRoute()
                ),
                () -> assertNotNull(
                        config.adminTransactionRoute()
                )
        );
    }
}