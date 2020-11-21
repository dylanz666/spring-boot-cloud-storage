package com.github.dylanz666.controller;

import org.springframework.web.bind.annotation.*;

/**
 * @author : dylanz
 * @since : 10/04/2020
 */
@RestController
@RequestMapping("/api")
public class PingController {
    /**
     * 验证服务是否正常
     * */
    @GetMapping("/ping")
    public String ping() {
        return "success";
    }
}
