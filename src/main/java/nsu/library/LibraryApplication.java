package nsu.library;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.Base64;
@SpringBootApplication
public class LibraryApplication{


    public static void main(String[] args) {
        SpringApplication.run(LibraryApplication.class, args);
    }

}
