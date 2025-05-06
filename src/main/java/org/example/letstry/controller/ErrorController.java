package org.example.letstry.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ErrorController {

    @GetMapping("/error1")
    public String error1() {
        return "/error/error1";
    }
}
