package com.morris.ogflakes.controller;

import com.morris.ogflakes.model.OgCereal;
import com.morris.ogflakes.repository.OgCerealRepository;
import com.morris.ogflakes.service.OgCerealService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

@Controller
public class OgCerealController {
    private static final Logger logger = LoggerFactory.getLogger(OgCerealController.class);

    private final OgCerealRepository ogCerealRepository;

    @Autowired
    OgCerealService ogCerealService;

    public OgCerealController(OgCerealRepository cerealRepository) {
        this.ogCerealRepository = cerealRepository;
    }

    @GetMapping("/ogcereal")
    public String getOgCerealPage() {
        return "ogcerealLandingPage";
    }


    @GetMapping("/ogcereal/showcase")
    public String getOgCereal(Model model) {
        model.addAttribute("ogFlakesList", ogCerealRepository.findAll());
        return "showcase";
    }

    @GetMapping("/ogcereal/showcase/{id}")
    public String getOgCerealById(@PathVariable String id, Model model) {
        Optional<OgCereal> ogCereal = ogCerealService.getOgCereal(id);
        model.addAttribute("name", ogCereal.get().getName());
        model.addAttribute("image", ogCereal.get().getImage());

        return "showcase";
    }

    @PostMapping("ogcereal/add")
    public String postOgCereal(@RequestParam("name") String name, @RequestParam("image")MultipartFile image,
                                 Model model) {
        ogCerealService.addOgCereal(name, image);
        // add dynamic text on successful upload on showcase redirect
        return "redirect:/ogcereal/showcase/";
    }
}