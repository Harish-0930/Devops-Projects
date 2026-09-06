package com.nexusbank.admin.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
public class RootController {

    @GetMapping("/")
    public ResponseEntity<String> root() {
        return ResponseEntity.ok("Admin service is running");
    }

    @GetMapping("/error")
    public ResponseEntity<String> error() {
        return ResponseEntity.status(404).body("Admin service endpoint not found");
    }
}
