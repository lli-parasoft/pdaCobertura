package com.parasoft.demoapp.config.security;

import com.parasoft.demoapp.exception.CannotLogoutFromKeycloakException;
import com.parasoft.demoapp.service.KeycloakService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@Component
public class CustomLogoutSuccessHandler implements LogoutSuccessHandler{

    @Autowired
    KeycloakService keycloakService;

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        if (authentication instanceof OAuth2AuthenticationToken) {
            CustomOidcUser user = (CustomOidcUser) authentication.getPrincipal();
            String idTokenValue = user.getIdToken().getTokenValue();
            try {
                keycloakService.oauth2Logout(idTokenValue);
            } catch (CannotLogoutFromKeycloakException e) {
                log.error(e.getMessage(), e);
            }
        }
        response.sendRedirect("/loginPage");
    }
}
