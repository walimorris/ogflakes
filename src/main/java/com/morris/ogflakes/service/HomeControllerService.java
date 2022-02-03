package com.morris.ogflakes.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class HomeControllerService {
    private static final Logger logger = LoggerFactory.getLogger(HomeControllerService.class);

    public String getHomePage() {
        return "index";
    }
}
