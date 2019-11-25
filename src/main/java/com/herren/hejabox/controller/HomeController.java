package com.herren.hejabox.controller;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@Slf4j
public class HomeController {

    //slf4j 찾아야함
//    Logger log = LoggerFactory.getLogger(getClass()); 이런식으로 안하고, 어노테이션으로 SLF4J

    @RequestMapping("/")
    public String home() {
        log.info("home controller");
        return "home"; // home.html thymeleaf 화면으로 찾아감
    }
}
