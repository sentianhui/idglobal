package com.example.idglobal.controller;

import com.example.idglobal.utils.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/test")
public class TestController {

    @Autowired
    private RedisUtil redisUtil;

    @GetMapping("/getRedis")
    public Object getRedis(){
        redisUtil.addKey("r","aaaaaa",3600, TimeUnit.SECONDS);
        return redisUtil.getValue("r");
    }

}