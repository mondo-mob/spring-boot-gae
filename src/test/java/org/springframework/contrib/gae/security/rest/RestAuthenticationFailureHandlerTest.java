package org.springframework.contrib.gae.security.rest;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.core.AuthenticationException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class RestAuthenticationFailureHandlerTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private AuthenticationException exception;

    private RestAuthenticationFailureHandler handler;

    @Before
    public void setUp() {
        handler = new RestAuthenticationFailureHandler();
    }

    @Test
    public void onAuthenticationFailure() {
        handler.onAuthenticationFailure(request, response, exception);

        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    }
}
