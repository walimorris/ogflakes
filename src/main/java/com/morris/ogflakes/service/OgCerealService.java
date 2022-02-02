package com.morris.ogflakes.service;

import com.morris.ogflakes.model.OgCereal;
import com.morris.ogflakes.repository.OgCerealRepository;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.bson.BsonBinarySubType;
import org.bson.types.Binary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

@Service
public class OgCerealService  {
    private static final Logger logger = LoggerFactory.getLogger(OgCerealService.class);

    private static final String CONTRIBUTOR = "contributor";
    private static final String TRUE = "true";
    private static final String ROOT = "/";

    @Autowired
    private OgCerealRepository ogCerealRepository;

    /**
     * Add {@link OgCereal} object to repository with name, description, and image.
     *
     * @param name        name of {@link OgCereal}
     * @param description {@link OgCereal} description
     * @param file        {@link MultipartFile} image
     */
    public void addOgCereal(String name, String description, MultipartFile file) {
        OgCereal ogCereal = new OgCereal(name, description);
        try {
            ogCereal.setImage(new Binary(BsonBinarySubType.BINARY, file.getBytes()));
        } catch (IOException e) {
            logger.error("Error adding image to repository: {}", e.getMessage());
        }
        ogCerealRepository.insert(ogCereal);
    }

    /**
     * A case optimized query, queries the repo ignoring case. In such cases where a client searches using an uppercase
     * keyword, the query may not return optimized results and may skip over the lowercase keyword. To optimize for full
     * results, the case optimized query searches the repo for the keyword's upper and lowercase forms. The result is a
     * joined {@link List<OgCereal>} of unique objects.
     *
     * @param query {@link String} keyword/term for repo search
     * @return {@link List<OgCereal>}
     */
    public List<OgCereal> getCaseOptimizedResultsList(String query) {
        List<OgCereal> maxQueryResultsList;
        if (StringUtils.isAllLowerCase(query.substring(0,1))) {
            query = StringUtils.join(query.substring(0,1).toUpperCase(), query.substring(1));
        }
        List<OgCereal> upperCaseQueryResultsList = ogCerealRepository.findByNameRegex(query);
        List<OgCereal> lowerCaseQueryResultsList = ogCerealRepository.findByNameRegex(StringUtils.lowerCase(query));

        if (!upperCaseQueryResultsList.isEmpty() && !lowerCaseQueryResultsList.isEmpty()) {
            maxQueryResultsList = joinShowcaseQueryResultsLists(upperCaseQueryResultsList, lowerCaseQueryResultsList);
        } else {
            maxQueryResultsList = upperCaseQueryResultsList.size() > lowerCaseQueryResultsList.size() ?
                    upperCaseQueryResultsList : lowerCaseQueryResultsList;
        }
        return maxQueryResultsList;
    }

    /**
     * Adds contributor cookie to client response.
     *
     * @param response {@link HttpServletResponse}
     */
    public void addContributorCookie(HttpServletResponse response) {
        Cookie contributorCookie = new Cookie(CONTRIBUTOR, TRUE);
        contributorCookie.setMaxAge(7 * 24 * 60 * 60); // expire 7 days or until manual deletion
        contributorCookie.setPath(ROOT);
        response.addCookie(contributorCookie);
    }

    /**
     * Determines if {@link List<OgCereal>} objects is empty or contains all
     * non-validated {@link OgCereal} objects in list.
     *
     * @param results {@link List<OgCereal>} objects
     * @return boolean
     */
    public boolean isEmptyOrNonValidatedResults(List<OgCereal> results) {
        return (results.isEmpty() || !allResultsAreValidated(results));
    }

    /**
     * Get all (validated) results from repo.
     *
     * @return {@link List<OgCereal>} validated object results
     */
    public List<OgCereal> getAllResults() {
        return ogCerealRepository.findAll();
    }

    /**
     * Determines weather client who sent {@link HttpServletRequest}has
     * contributed to showcase by checking if contributor cookie exists.
     *
     * @param request {@link HttpServletRequest}
     * @return boolean
     */
    public boolean isContributor(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(CONTRIBUTOR) && cookie.getValue().equals(TRUE)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Get {@link Optional<OgCereal>} object from repo.
     *
     * @param id {@link OgCereal} id
     * @return {@link Optional<OgCereal>} object.
     */
    public Optional<OgCereal> getOgCereal(String id) {
        return ogCerealRepository.findById(id);
    }

    /**
     * Joins the results of two {@link List<OgCereal>}.
     *
     * @param resultsList1 {@link List<OgCereal>} list 1.
     * @param resultsList2 {@link List<OgCereal>} list 2.
     * @return joined {@link List<OgCereal>} list.
     */
    private List<OgCereal> joinShowcaseQueryResultsLists(List<OgCereal> resultsList1, List<OgCereal>resultsList2) {
        return new ArrayList<>(new HashSet<>(ListUtils.union(resultsList1, resultsList2)));
    }

    /**
     * Determines if all results from query are validated. Helps determine which
     * results are rendered to showcase. Showcase will not render non-validated
     * results from repo.
     *
     * @param results {@link List<OgCereal>} results from repository search.
     * @return boolean
     */
    private boolean allResultsAreValidated(List<OgCereal> results) {
        for (OgCereal cereal : results) {
            if (!cereal.getIsValidated()) {
                return false;
            }
        }
        return true;
    }
}