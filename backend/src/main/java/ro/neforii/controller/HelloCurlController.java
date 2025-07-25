package ro.neforii.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class HelloCurlController {
    @GetMapping
    public String hello() {
        return "Hello, suntem echipa 2!";
    }
}
