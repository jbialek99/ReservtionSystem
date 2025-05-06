package org.example.letstry.controller;

import org.example.letstry.model.Reservation;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.ZoneId;
import java.util.TimeZone;

@Controller
public class MenuController {

    @GetMapping("/welcome")
    public String welcome() {
        return "/menu/welcome";
    }
/*
    @GetMapping("/home")
    public String home() {
        return "/menu/home";
    }
*/
    @GetMapping("/test")
    public String test() {
        Reservation reservation = new Reservation();
        System.out.println(reservation.getDate());
        return "/test";
    }
}
