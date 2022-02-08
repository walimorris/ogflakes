package com.morris.ogflakes.controller;

import com.morris.ogflakes.model.OgCereal;
import com.morris.ogflakes.repository.OgCerealRepository;
import com.morris.ogflakes.service.OgCerealService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/ogcereal")
public class OgCerealController {
    private static final Logger logger = LoggerFactory.getLogger(OgCerealController.class);

    private static final String RESULTS = "results";
    private static final String QUERY = "q";
    private static final String EMPTY = "empty";
    private static final String OG_FLAKES_LIST = "ogFlakesList";
    private static final String MESSAGE_1 = "message1";
    private static final String MESSAGE_2 = "message2";

    private static final String CONTRIBUTOR_MESSAGE_1 = "You've contributed to OGFlakes, but there's no limit.";
    private static final String CONTRIBUTOR_MESSAGE_2 = "We appreciate the love!";

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
    public String getOgCereal(@RequestParam(value = "q", required = false) String query,
                              @RequestParam(value = "page") Optional<Integer> page, Model model, HttpServletRequest request) {
        Map<String, Object> results;
        List<OgCereal> maxQueryResultsList;

        // 5 items per page
        Pageable pageable = PageRequest.of(page.orElse(0), 5);
        if (StringUtils.isNotEmpty(query)) {
            results = ogCerealService.getQueryResults(query, pageable);
            maxQueryResultsList = (List<OgCereal>) results.get(OgCerealService.CEREALS);
            if (ogCerealService.isEmptyOrAllNonValidatedResults(maxQueryResultsList)) {
                model.addAttribute(RESULTS, EMPTY);
            }
        } else {
            Pageable allPageable = PageRequest.of(page.orElse(0), 5);
            results = ogCerealService.getAllResults(allPageable);
            maxQueryResultsList = (List<OgCereal>) results.get(OgCerealService.CEREALS);
        }
        model.addAttribute(QUERY, query);
        model.addAttribute(OgCerealService.TOTAL_PAGES, results.get(OgCerealService.TOTAL_PAGES));
        model.addAttribute(OG_FLAKES_LIST, maxQueryResultsList);

        // add contributor message if contributor cookie exists
        if (ogCerealService.isContributor(request)) {
            StringBuilder message1 = new StringBuilder(CONTRIBUTOR_MESSAGE_1);
            StringBuilder message2 = new StringBuilder(CONTRIBUTOR_MESSAGE_2);
            model.addAttribute(MESSAGE_1, message1);
            model.addAttribute(MESSAGE_2, message2);
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
}