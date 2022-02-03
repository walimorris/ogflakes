package com.morris.ogflakes.controller;

import com.morris.ogflakes.service.HomeControllerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
    private static final Logger logger = LoggerFactory.getLogger(HomeController.class);

    @Autowired
    private HomeControllerService homeControllerService;

    public HomeController() {}

    @GetMapping("/")
    public String index() {
        return homeControllerService.getHomePage();
    }
}
