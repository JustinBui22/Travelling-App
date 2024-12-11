package com.example.travelingapp.controller;

import com.example.travelingapp.util.ResponseBody;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RequestMapping("/private/auth/")
public interface TokenController {
    @GetMapping("/token")
    ResponseEntity<ResponseBody<Object>> generateToken(@RequestParam(value = "phoneNumber") String phoneNumber);
}
