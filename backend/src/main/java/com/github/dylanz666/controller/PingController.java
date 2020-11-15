package com.github.dylanz666.controller;

import org.springframework.web.bind.annotation.*;

/**
 * @author : dylanz
 * @since : 10/04/2020
 */
@RestController
public class PingController {
    @GetMapping("/ping")
    public String ping() {
        return "success";
    }
}
