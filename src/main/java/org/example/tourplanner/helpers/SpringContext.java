package org.example.tourplanner.helpers;

import org.springframework.context.ConfigurableApplicationContext;

public class SpringContext {
    private static ConfigurableApplicationContext context;

    public static void setApplicationContext(ConfigurableApplicationContext context) {
        SpringContext.context = context;
    }

    public static ConfigurableApplicationContext getApplicationContext() {
        return context;
    }
}