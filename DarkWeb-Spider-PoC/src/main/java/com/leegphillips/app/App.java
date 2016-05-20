package com.leegphillips.app;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.Arrays;

@SpringBootApplication
public class App
{
    private static final Logger LOG = LoggerFactory.getLogger(App.class);

    public static void main( String[] args ) {
        LOG.debug(Arrays.toString(args));
        ConfigurableApplicationContext context = SpringApplication.run(App.class, args);
        Engine engine = context.getBean(Engine.class);
        engine.add(args[0]);
        engine.consume();
    }
}
