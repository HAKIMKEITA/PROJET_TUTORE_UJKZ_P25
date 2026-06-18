package com.ujkz.memoire.GestionMemoiresBackend.controller;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/test")
@CrossOrigin(origins = "*")
public class TestController {

    @GetMapping
    public String test() {
        return "API REST fonctionne correctement !";
    }
}