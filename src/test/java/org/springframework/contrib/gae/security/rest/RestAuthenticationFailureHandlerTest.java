package org.springframework.contrib.gae.security.rest;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
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
    public void setUp()  {
        handler = new RestAuthenticationFailureHandler();
    }

    @Test
    public void onAuthenticationFailure() throws Exception {
        handler.onAuthenticationFailure(request, response, exception);

        verify(response).sendError(HttpServletResponse.SC_UNAUTHORIZED, "Not authorised");
    }
}