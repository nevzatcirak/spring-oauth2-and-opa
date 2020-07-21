package com.nevzatcirak.example.oauth2.opa.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

/**
 * @author Nevzat ÇIRAK
 * @mail nevzatcirak17@gmail.com
 * Created by nevzatcirak at 02/07/2020
 */
@RestController
@RequestMapping("/rest")
public class TestController {
    private static final Logger logger = LogManager.getLogger();

    @Autowired
    private RestTemplate restTemplate;

    @GetMapping("/test")
    public String getTestInfo() {
        return "OK. You have authenticated! :)";
    }

    @GetMapping("/rest/test")
    public String getRestTest() {
        return restTemplate.getForObject("http://localhost:8089/testinfo", String.class);
    }

    @GetMapping("/rest/test2")
    public String getRestTest2() {
        return restTemplate.getForObject("http://localhost:8089/testinfo", String.class);
    }

}
