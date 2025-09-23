package com.example.marketplace.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home() {
        return "redirect:/index.html";
    }
    
    @GetMapping("/favicon.ico")
    @ResponseBody
    public ResponseEntity<String> favicon() {
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
