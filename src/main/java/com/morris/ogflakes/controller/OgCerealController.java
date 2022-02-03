package com.morris.ogflakes.controller;

import com.morris.ogflakes.model.OgCereal;
import com.morris.ogflakes.repository.OgCerealRepository;
import com.morris.ogflakes.service.OgCerealService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/ogcereal")
public class OgCerealController {
    private static final Logger logger = LoggerFactory.getLogger(OgCerealController.class);

    private static final String RESULTS = "results";
    private static final String EMPTY = "empty";
    private static final String OG_FLAKES_LIST = "ogFlakesList";
    private static final String MESSAGE_1 = "message1";
    private static final String MESSAGE_2 = "message2";

    private final OgCerealRepository ogCerealRepository;

    @Autowired
    private OgCerealService ogCerealService;

    public OgCerealController(OgCerealRepository cerealRepository) {
        this.ogCerealRepository = cerealRepository;
    }

    @GetMapping("/contribute")
    public String getOgCerealPage() {
        return "contribute";
    }

    @GetMapping("/showcase")
    public String getOgCereal(@RequestParam(value = "q", required = false) String query, Model model,
                              HttpServletRequest request) {
        List<OgCereal> maxQueryResultsList;
        if (StringUtils.isNotEmpty(query)) {
            maxQueryResultsList = ogCerealService.getCaseOptimizedResultsList(query);
            if (ogCerealService.isEmptyOrAllNonValidatedResults(maxQueryResultsList)) {
                model.addAttribute(RESULTS, EMPTY);
            }
            model.addAttribute(OG_FLAKES_LIST, maxQueryResultsList);
        } else {
            // return all cereal options, when user submits empty query or page load
            maxQueryResultsList = ogCerealService.getAllResults();
            model.addAttribute(OG_FLAKES_LIST, maxQueryResultsList);
        }

        // add contributor message if contributor cookie exists
        if (ogCerealService.isContributor(request)) {
            // add dynamic text on successful upload on showcase redirect
            StringBuilder message1 = new StringBuilder("You've contributed to OGFlakes, no worries there's no limit.");
            StringBuilder message2 = new StringBuilder("Either way, we appreciate the love!");
            model.addAttribute(MESSAGE_1, message1);
            model.addAttribute(MESSAGE_2, message2);
        }
        return "showcase";
    }

    @GetMapping("/showcase/{id}")
    public String getOgCerealById(@PathVariable String id, Model model) {
        logger.info("Searching db for {}", id);
        Optional<OgCereal> ogCereal = ogCerealService.getOgCereal(id);
        if (ogCereal.isPresent()) {
            logger.info("ogCereal is present: {}", ogCereal);
        }
        return "showcase";
    }

    @PostMapping("/add")
    public String postOgCereal(@RequestParam("name") String name, @RequestParam("image") MultipartFile image,
                                 @RequestParam("description") String description, HttpServletResponse response) {

        ogCerealService.addOgCereal(name, description, image);
        ogCerealService.addContributorCookie(response);
        return "redirect:/ogcereal/showcase";
    }

    @PostMapping(value = "/ogcereal/showcase/updateticker")
    public String updateOgCerealTickerCount(@RequestParam("id") String id, @RequestParam("count") String count) throws ResourceNotFoundException {
        Optional<OgCereal> cereal = ogCerealRepository.findById(id);
        if (cereal.isPresent()) {
            OgCereal ogCereal = cereal.get();
            ogCereal.setCount(Integer.parseInt(count));
            ogCerealRepository.save(ogCereal);
        }
        return "redirect:/ogcereal/showcase";
    }
}