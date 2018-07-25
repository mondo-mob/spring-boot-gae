package org.springframework.contrib.gae.security.rest;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
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
    public void setUp()  {
        handler = new RestAuthenticationSuccessHandler();
    }

    @Test
    public void onAuthenticationSuccess()  {
        handler.onAuthenticationSuccess(request, response, authentication);

        verify(response).setStatus(HttpServletResponse.SC_OK);
    }
}
