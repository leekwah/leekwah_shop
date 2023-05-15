package com.shop.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(value = "/thymeleaf") // 클라이언트 요청에 대해서 어떤 컨트롤러가 처리할지 매핑하는 어노테이션이다. url에 '/thymeleaf' 경로로 오는 요청을 ThymeleafExController가 처리하도록 한다.
public class ThymeleafController {
    @GetMapping(value = "/test")
    public String thymeleafTest() {
        return "thymeleaf/test";
    }
}
