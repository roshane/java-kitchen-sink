package com.commons.togglz;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class FeatureController {

    @GetMapping("/")
    public String home() {
        return "redirect:/togglz-console";
    }
}
