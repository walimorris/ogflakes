package com.morris.ogflakes.controller;

import com.morris.ogflakes.model.OgCereal;
import com.morris.ogflakes.repository.OgCerealRepository;
import com.morris.ogflakes.service.OgCerealService;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

@Controller
@RequestMapping("/ogcereal")
public class OgCerealController {
    private static final Logger logger = LoggerFactory.getLogger(OgCerealController.class);

    private final OgCerealRepository ogCerealRepository;

    @Autowired
    OgCerealService ogCerealService;

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

            // search both upper and lowercase query with regex and return greatest results list
            if (StringUtils.isAllLowerCase(query.substring(0,1))) {
                query = StringUtils.join(query.substring(0,1).toUpperCase(), query.substring(1));
            }
            List<OgCereal> upperCaseQueryResultsList = ogCerealRepository.findByNameRegex(query);
            List<OgCereal> lowerCaseQueryResultsList = ogCerealRepository.findByNameRegex(StringUtils.lowerCase(query));

            // What if both results lists are not empty? Even with upper and lowercase regex of the
            // same search term we may have a case that each lists contains necessary results that
            // the other list does not contain
            if (!upperCaseQueryResultsList.isEmpty() && !lowerCaseQueryResultsList.isEmpty()) {
                maxQueryResultsList = joinShowcaseQueryResultsLists(upperCaseQueryResultsList, lowerCaseQueryResultsList);
            } else {
                maxQueryResultsList = upperCaseQueryResultsList.size() > lowerCaseQueryResultsList.size() ?
                        upperCaseQueryResultsList : lowerCaseQueryResultsList;
            }
            model.addAttribute("ogFlakesList", maxQueryResultsList);
        } else {
            // return all cereal options, even when user submits empty query
            model.addAttribute("ogFlakesList", ogCerealRepository.findAll());
        }

        // add contributor message if contributor cookie exists
        boolean isContributor = false;
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("contributor") && cookie.getValue().equals("true")) {
                    isContributor = true;
                    break;
                }
            }
        }
        if (isContributor) {
            // add dynamic text on successful upload on showcase redirect
            StringBuilder message1 = new StringBuilder();
            StringBuilder message2 = new StringBuilder();
            message1.append("You've contributed to OGFlakes, no worries there's no limit.");
            message2.append("Either way, we appreciate the love!");
            model.addAttribute("message1", message1);
            model.addAttribute("message2", message2);
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
                                 @RequestParam("description") String description, HttpServletResponse response,
                                 Model model) {

        ogCerealService.addOgCereal(name, description, image);

        // add contributor cookie on successful cereal contribution
        Cookie contributorCookie = new Cookie("contributor", "true");
        contributorCookie.setMaxAge(7 * 24 * 60 * 60); // expire 7 days or until manual deletion
        contributorCookie.setPath("/");
        response.addCookie(contributorCookie);
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

    private List<OgCereal> joinShowcaseQueryResultsLists(List<OgCereal> resultsList1, List<OgCereal>resultsList2) {
        // Do not add duplicates!
        Set<OgCereal> queryResultsSet = new HashSet<>(ListUtils.union(resultsList1, resultsList2));
        return new ArrayList<>(queryResultsSet);
    }
}