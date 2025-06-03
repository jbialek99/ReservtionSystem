package org.example.letstry.controller;

import org.example.letstry.model.Reservation;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
public class MenuController {

    @GetMapping("/welcome")
    public String welcome() {
        return "/menu/welcome";
    }

    @GetMapping("/home")
    public String home(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof OAuth2User user) {
            model.addAttribute("email", user.getAttribute("email"));
            model.addAttribute("name", user.getAttribute("name"));
        }
        return "/menu/home";
    }
    @GetMapping("/test")
    public String test() {
        Reservation reservation = new Reservation();
        System.out.println(reservation.getDate());
        return "/test";
    }
}
