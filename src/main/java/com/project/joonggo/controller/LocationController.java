package com.project.joonggo.controller;

import com.project.joonggo.service.LocationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;

@Slf4j
@RequestMapping("/login/*")
@RequiredArgsConstructor
@Controller
public class LocationController {
    private final LocationService locationService;

    @GetMapping("/")
    public String mainPage(Principal principal, Model model){
        if(principal != null){
            String userId = principal.getName();
            String streetAddress = locationService.getStreetAddress(userId);
            model.addAttribute("streetAddress", streetAddress);
        }
        return "index";
    }

}
