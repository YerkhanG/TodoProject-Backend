package com.example.prac1.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationListener implements ApplicationListener<AuthenticationSuccessEvent> {

    private static final Logger logger = LoggerFactory.getLogger(AuthenticationListener.class);
    @Override
    public void onApplicationEvent(AuthenticationSuccessEvent event) {
        String username = event.getAuthentication().getName();
        logger.info("User {} successfully logged in." , username);
    }
}
