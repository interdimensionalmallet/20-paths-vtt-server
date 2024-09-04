package com.interdimensionalmallet.twtpthvtt;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import reactor.core.publisher.Hooks;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.stream.Stream;

import java.nio.file.Path;

@SpringBootApplication
public class Main {


    public static void deleteDirectory(Path directory) throws IOException {
        if (Files.exists(directory) && Files.isDirectory(directory)) {
            try (Stream<Path> paths = Files.walk(directory)) {
                paths.sorted(Comparator.reverseOrder())
                     .forEach(path -> {
                         try {
                             Files.delete(path);
                         } catch (IOException e) {
                             throw new RuntimeException("Failed to delete " + path, e);
                         }
                     });
            }
        }
    }

    public static void main(String[] args) throws IOException {
        Hooks.onOperatorDebug();
        //deleteDirectory(Path.of("dataStore"));

        SpringApplication.run(Main.class, args);
    }
}