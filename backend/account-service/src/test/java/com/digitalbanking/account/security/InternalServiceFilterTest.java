package com.digitalbanking.account.security;

import com.digitalbanking.account.config.InternalServiceFilter;
import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class InternalServiceFilterTest {

    @Test
    void shouldReturn401WhenSecretIsMissing()
            throws Exception {

        InternalServiceFilter filter =
                new InternalServiceFilter(
                        "test-internal-secret"
                );

        MockHttpServletRequest request =
                new MockHttpServletRequest();

        request.setRequestURI(
                "/internal/accounts/transfer"
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
                401,
                response.getStatus()
        );

        verifyNoInteractions(filterChain);
    }

    @Test
    void shouldReturn401WhenSecretIsInvalid()
            throws Exception {

        InternalServiceFilter filter =
                new InternalServiceFilter(
                        "test-internal-secret"
                );

        MockHttpServletRequest request =
                new MockHttpServletRequest();

        request.setRequestURI(
                "/internal/accounts/transfer"
        );

        request.addHeader(
                "X-Internal-Service-Secret",
                "wrong-secret"
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
                401,
                response.getStatus()
        );

        verifyNoInteractions(filterChain);
    }

    @Test
    void shouldAllowRequestWhenSecretIsValid()
            throws Exception {

        InternalServiceFilter filter =
                new InternalServiceFilter(
                        "test-internal-secret"
                );

        MockHttpServletRequest request =
                new MockHttpServletRequest();

        request.setRequestURI(
                "/internal/accounts/transfer"
        );

        request.addHeader(
                "X-Internal-Service-Secret",
                "test-internal-secret"
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
                200,
                response.getStatus()
        );

        verify(filterChain)
                .doFilter(request, response);
    }

    @Test
    void shouldAllowPublicRequestWithoutSecret()
            throws Exception {

        InternalServiceFilter filter =
                new InternalServiceFilter(
                        "test-internal-secret"
                );

        MockHttpServletRequest request =
                new MockHttpServletRequest();

        request.setRequestURI(
                "/api/accounts"
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
                200,
                response.getStatus()
        );

        verify(filterChain)
                .doFilter(request, response);
    }
}