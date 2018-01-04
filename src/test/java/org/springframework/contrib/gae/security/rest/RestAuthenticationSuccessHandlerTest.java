package org.springframework.contrib.gae.security.rest;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.security.core.Authentication;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class RestAuthenticationSuccessHandlerTest {
    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private Authentication authentication;

    private RestAuthenticationSuccessHandler handler;

    @Before
    public void setUp() throws Exception {
        handler = new RestAuthenticationSuccessHandler();
    }

    @Test
    public void onAuthenticationSuccess() throws Exception {
        handler.onAuthenticationSuccess(request, response, authentication);

        verify(response).setStatus(HttpServletResponse.SC_OK);
    }
}