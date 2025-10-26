package nsu.library;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.stream.Collectors;

@SpringBootApplication
public class LibraryApplication{
    @Value("${SPRING_DATASOURCE_URL:NOT_FOUND}")
    private static String url;

    public static void main(String[] args) {
        String classpath = System.getProperty("java.class.path");
        System.out.println(classpath);

        SpringApplication.run(LibraryApplication.class, args);
    }

}
