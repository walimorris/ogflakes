package com.morris.ogflakes.controller;

import com.morris.ogflakes.service.HomeControllerService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.hamcrest.core.StringContains.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class HomeControllerTest {

    private static final String HOMEPAGE_FILENAME = "index";
    private static final String HOMEPAGE_URL = "/";
    private static final String HOMEPAGE_TITLE = "OG Home";

    @MockBean
    private HomeControllerService homeControllerService;

    @Autowired
    private MockMvc mockHomeController;

    @Test
    public void contextLoads() throws Exception {
        Assertions.assertNotNull(homeControllerService);
    }

    @Test
    public void getHomePage() throws Exception {
        when(homeControllerService.getHomePage()).thenReturn(HOMEPAGE_FILENAME);
        mockHomeController.perform(get(HOMEPAGE_URL)).andExpect(status().isOk())
                .andExpect(content().string(containsString(HOMEPAGE_TITLE)));
    }
}