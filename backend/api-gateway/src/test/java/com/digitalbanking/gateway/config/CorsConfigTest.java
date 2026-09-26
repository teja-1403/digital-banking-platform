package com.digitalbanking.gateway.config;

import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.filter.CorsFilter;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CorsConfigTest {

    @Test
    void corsFilter_shouldAllowConfiguredOrigin()
            throws Exception {

        CorsConfig config = new CorsConfig();

        ReflectionTestUtils.setField(
                config,
                "allowedOrigin",
                "http://localhost:5173"
        );

        CorsFilter filter = config.corsFilter();

        MockHttpServletRequest request =
                new MockHttpServletRequest();

        request.setMethod("GET");
        request.setRequestURI("/api/accounts");
        request.addHeader(
                "Origin",
                "http://localhost:5173"
        );

        MockHttpServletResponse response =
                new MockHttpServletResponse();

        FilterChain filterChain =
                mock(FilterChain.class);

        filter.doFilter(
                request,
                response,
                filterChain
        );

        assertEquals(
                "http://localhost:5173",
                response.getHeader(
                        "Access-Control-Allow-Origin"
                )
        );

        assertEquals(
                "true",
                response.getHeader(
                        "Access-Control-Allow-Credentials"
                )
        );

        verify(filterChain)
                .doFilter(request, response);
    }

    @Test
    void corsFilter_shouldRejectDisallowedOrigin()
            throws Exception {

        CorsConfig config = new CorsConfig();

        ReflectionTestUtils.setField(
                config,
                "allowedOrigin",
                "http://localhost:5173"
        );

        CorsFilter filter = config.corsFilter();

        MockHttpServletRequest request =
                new MockHttpServletRequest();

        request.setMethod("GET");
        request.setRequestURI("/api/accounts");
        request.addHeader(
                "Origin",
                "http://malicious.example.com"
        );

        MockHttpServletResponse response =
                new MockHttpServletResponse();

        FilterChain filterChain =
                mock(FilterChain.class);

        filter.doFilter(
                request,
                response,
                filterChain
        );

        assertNull(
                response.getHeader(
                        "Access-Control-Allow-Origin"
                )
        );

        verifyNoInteractions(filterChain);
    }

    @Test
    void corsFilter_shouldSupportConfiguredMethods()
            throws Exception {

        CorsConfig config = new CorsConfig();

        ReflectionTestUtils.setField(
                config,
                "allowedOrigin",
                "http://localhost:5173"
        );

        CorsFilter filter = config.corsFilter();

        MockHttpServletRequest request =
                new MockHttpServletRequest();

        request.setMethod("OPTIONS");
        request.setRequestURI("/api/transactions/transfers");
        request.addHeader(
                "Origin",
                "http://localhost:5173"
        );
        request.addHeader(
                "Access-Control-Request-Method",
                "POST"
        );

        MockHttpServletResponse response =
                new MockHttpServletResponse();

        FilterChain filterChain =
                mock(FilterChain.class);

        filter.doFilter(
                request,
                response,
                filterChain
        );

        String allowedMethods =
                response.getHeader(
                        "Access-Control-Allow-Methods"
                );

        assertNotNull(allowedMethods);
        assertTrue(allowedMethods.contains("GET"));
        assertTrue(allowedMethods.contains("POST"));
        assertTrue(allowedMethods.contains("PUT"));
        assertTrue(allowedMethods.contains("PATCH"));
        assertTrue(allowedMethods.contains("DELETE"));
        assertTrue(allowedMethods.contains("OPTIONS"));
    }

    @Test
    void corsFilter_shouldAllowRequiredHeaders()
            throws Exception {

        CorsConfig config = new CorsConfig();

        ReflectionTestUtils.setField(
                config,
                "allowedOrigin",
                "http://localhost:5173"
        );

        CorsFilter filter = config.corsFilter();

        MockHttpServletRequest request =
                new MockHttpServletRequest();

        request.setMethod("OPTIONS");
        request.setRequestURI("/api/transactions/transfers");
        request.addHeader(
                "Origin",
                "http://localhost:5173"
        );
        request.addHeader(
                "Access-Control-Request-Method",
                "POST"
        );
        request.addHeader(
                "Access-Control-Request-Headers",
                "Authorization, Content-Type, Idempotency-Key"
        );

        MockHttpServletResponse response =
                new MockHttpServletResponse();

        FilterChain filterChain =
                mock(FilterChain.class);

        filter.doFilter(
                request,
                response,
                filterChain
        );

        String allowedHeaders =
                response.getHeader(
                        "Access-Control-Allow-Headers"
                );

        assertNotNull(allowedHeaders);
        assertTrue(
                allowedHeaders.contains("Authorization")
        );
        assertTrue(
                allowedHeaders.contains("Content-Type")
        );
        assertTrue(
                allowedHeaders.contains("Idempotency-Key")
        );
    }

    @Test
    void corsFilter_shouldSupportMultipleConfiguredOrigins()
            throws Exception {

        CorsConfig config = new CorsConfig();

        ReflectionTestUtils.setField(
                config,
                "allowedOrigin",
                "http://localhost:5173, http://localhost:3000"
        );

        CorsFilter filter = config.corsFilter();

        MockHttpServletRequest request =
                new MockHttpServletRequest();

        request.setMethod("GET");
        request.setRequestURI("/api/accounts");
        request.addHeader(
                "Origin",
                "http://localhost:3000"
        );

        MockHttpServletResponse response =
                new MockHttpServletResponse();

        FilterChain filterChain =
                mock(FilterChain.class);

        filter.doFilter(
                request,
                response,
                filterChain
        );

        assertEquals(
                "http://localhost:3000",
                response.getHeader(
                        "Access-Control-Allow-Origin"
                )
        );
    }
}