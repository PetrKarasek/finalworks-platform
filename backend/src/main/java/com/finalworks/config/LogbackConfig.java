package com.finalworks.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

@Component
public class LogbackConfig implements ApplicationListener<ContextRefreshedEvent> {

    @Autowired
    private ApplicationContext applicationContext;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        // Nastavit aplikační kontext pro DatabaseAppender
        DatabaseAppender.setApplicationContext(applicationContext);
    }
}

