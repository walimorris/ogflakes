package com.morris.ogflakes.service;

import com.amazonaws.services.sns.AmazonSNSClient;
import com.amazonaws.services.sns.model.PublishRequest;
import com.amazonaws.services.sns.model.ResourceNotFoundException;
import com.morris.ogflakes.model.OgCereal;
import com.morris.ogflakes.model.SnsEmail;
import com.morris.ogflakes.repository.OgCerealRepository;
import org.bson.BsonBinarySubType;
import org.bson.types.Binary;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class OgCerealService  {
    private static final Logger logger = LoggerFactory.getLogger(OgCerealService.class);

    public static final String CONTRIBUTOR = "contributor";
    public static final String TRUE = "true";
    public static final String ROOT = "/";
    public static final String CEREALS = "cereals";
    public static final String TOTAL_ITEMS = "totalItems";
    public static final String TOTAL_PAGES = "totalPages";

    @Value("${admin-topic-arn}")
    private String topicArn;

    @Autowired
    private OgCerealRepository ogCerealRepository;

    @Autowired
    private AmazonSNSClient amazonSNSClient;

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
        publishAdminSns(ogCereal);
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
    public Map<String, Object> getQueryResults(String query, Pageable pageable) {
        Map<String, Object> results = new HashMap<>();

        Page<OgCereal> pageResults = ogCerealRepository.findByNameRegex(query, pageable);
        List<OgCereal> queryResultsList = pageResults.getContent();

        results.put(CEREALS, queryResultsList);
        results.put(TOTAL_ITEMS, pageResults.getTotalElements());
        results.put(TOTAL_PAGES, pageResults.getTotalPages());

        return results;
    }

    /**
     * Get all (validated) results from repo.
     *
     * @return {@link List<OgCereal>} validated object results
     */
    public Map<String, Object> getAllResults(Pageable pageable) {
        Map<String, Object> results = new HashMap<>();
        Page<OgCereal> pageResults = ogCerealRepository.findAll(pageable);
        results.put(CEREALS, pageResults.getContent());
        results.put(TOTAL_PAGES, pageResults.getTotalPages());
        results.put(TOTAL_ITEMS, pageResults.getTotalElements());
        return results;
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
    public boolean isEmptyOrAllNonValidatedResults(List<OgCereal> results) {
        return (results.isEmpty() || allResultsAreNotValidated(results));
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
    public OgCereal getOgCereal(String id) {
        if (ogCerealRepository.findById(String.valueOf(new ObjectId(id))).isPresent()) {
            return ogCerealRepository.findById(String.valueOf(new ObjectId(id))).get();
        }
        throw new ResourceNotFoundException("Unable to find OgCereal with id: " + id);
    }

    /**
     * Determines if all results from query are validated. Helps determine which
     * results are rendered to showcase. Showcase will not render non-validated
     * results from repo.
     *
     * @param results {@link List<OgCereal>} results from repository search.
     * @return boolean
     */
    private boolean allResultsAreNotValidated(List<OgCereal> results) {
        for (OgCereal cereal : results) {
            if (cereal.getIsValidated()) {
                return false;
            }
        }
        return true;
    }

    private void publishAdminSns(OgCereal ogCereal) {
        String subject = String.format("New '%s' cereal uploaded OG!", ogCereal.getName());
        String message = String.format("Here's the description: '%s'\n\nUpload at your leisure.\n\n" +
                "Stay OG", ogCereal.getDescription());
        SnsEmail snsEmail = new SnsEmail(subject, message);
        final PublishRequest publishRequest = new PublishRequest(topicArn, snsEmail.getMessage(), snsEmail.getSubject());
        amazonSNSClient.publish(publishRequest);
    }
}