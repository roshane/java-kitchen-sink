package com.aeon.restrictionpoc.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLException;

@RestController
public class HomeController {

    @GetMapping("/")
    public String health() throws SQLException {
        return "service is healthy";
    }
}
