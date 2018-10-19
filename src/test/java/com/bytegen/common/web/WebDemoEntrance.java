package com.bytegen.common.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;

@SpringBootApplication
@ComponentScan(value = {"com.bytegen.common.web"})
public class WebDemoEntrance {

    public static void main(String[] args) {
        SpringApplication.run(WebDemoEntrance.class, args);
    }
}
