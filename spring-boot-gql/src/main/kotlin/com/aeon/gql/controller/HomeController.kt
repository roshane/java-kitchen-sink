package com.aeon.gql.controller

import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping

@Controller
class HomeController {

    @GetMapping("/")
    fun index(model: Model): String {
        model.addAttribute("message", "Hello There how are you")
        return "index"
    }
}