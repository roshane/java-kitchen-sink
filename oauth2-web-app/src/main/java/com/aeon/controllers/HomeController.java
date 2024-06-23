package com.aeon.controllers;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(
        produces = MediaType.TEXT_PLAIN_VALUE
)
public class HomeController {

    @GetMapping("/ping")
    public String ping() {
        return "pong";
    }
}
