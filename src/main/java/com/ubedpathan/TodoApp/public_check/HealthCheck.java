package com.ubedpathan.TodoApp.public_check;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/health-check")
public class HealthCheck {

    @GetMapping
    private ResponseEntity<String> healthCheck(){
        return new ResponseEntity<>("Hey there all ok", HttpStatus.OK);
    }

}
