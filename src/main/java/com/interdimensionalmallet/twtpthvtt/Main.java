package com.interdimensionalmallet.twtpthvtt;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import reactor.core.publisher.Hooks;

@SpringBootApplication
public class Main {
    public static void main(String[] args) {
        Hooks.onOperatorDebug();
        SpringApplication.run(Main.class, args);
    }
}