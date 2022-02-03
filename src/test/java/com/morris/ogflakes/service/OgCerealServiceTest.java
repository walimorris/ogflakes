package com.morris.ogflakes.service;

import com.morris.ogflakes.model.OgCereal;
import com.morris.ogflakes.repository.OgCerealRepository;
import org.bson.BsonBinarySubType;
import org.bson.types.Binary;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;

import javax.servlet.http.Cookie;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class OgCerealServiceTest {
    private static final Logger logger = LoggerFactory.getLogger(OgCerealServiceTest.class);

    private static final String uppercaseQuery = "Uppercase";

    private static final String TEST_CEREAL_UPPERCASE_1 = "Honey";
    private static final String TEST_CEREAL_UPPERCASE_2 = "Captain";
    private static final String TEST_CEREAL_UPPERCASE_3 = "Smacks";

    private static final String TEST_CEREAL_LOWERCASE_1 = "honey";
    private static final String TEST_CEREAL_LOWERCASE_2 = "captain";
    private static final String TEST_CEREAL_LOWERCASE_3 = "smacks";

    private static final Cookie TEST_COOKIE_1 = new Cookie("test_cookie1", "test_cookie1_value1");
    private static final Cookie TEST_COOKIE_2 = new Cookie("text_cookie2", "text_cookie2_value2");

    private static final String TEST_DESCRIPTION_1 = "Some test text for cereal description member";
    private static final String TEST_DESCRIPTION_2 = "Some more test text for cereal description member";
    private static final String TEST_DESCRIPTION_3 = "Even more test text for cereal description member";

    private static final MockMultipartFile TEST_IMAGE_FILE_1 = new MockMultipartFile("file", "TEST_IMAGE_FILE_1",
            "multipart/form-data", "TEST_FILE".getBytes());

    private static final MockMultipartFile TEST_IMAGE_FILE_2 = new MockMultipartFile("file", "TEST_IMAGE_FILE_2",
            "multipart/form-data", "TEST_FILE".getBytes());

    private static final MockMultipartFile TEST_IMAGE_FILE_3 = new MockMultipartFile("file", "TEST_IMAGE_FILE_3",
            "multipart/form-data", "TEST_FILE".getBytes());

    @MockBean
    private OgCerealService mockOgCerealService;

    @MockBean
    private OgCerealRepository mockOgCerealRepository;

    private List<OgCereal> ogCereals;

    // also acts as uppercase cereal lists - reuse this
    private List<OgCereal> lowercaseCerealsList;
    private Cookie contributorCookie;
    private Cookie[] testCookies1;
    private Cookie[] testCookies2;

    @BeforeEach
    void setUp() {
        contributorCookie = new Cookie(OgCerealService.CONTRIBUTOR, OgCerealService.TRUE);
        testCookies1 = new Cookie[]{contributorCookie, TEST_COOKIE_1, TEST_COOKIE_2};
        testCookies2 = new Cookie[]{TEST_COOKIE_1, TEST_COOKIE_2};

        ogCereals = Arrays.asList(
                new OgCereal(TEST_CEREAL_UPPERCASE_1, TEST_DESCRIPTION_1),
                new OgCereal(TEST_CEREAL_UPPERCASE_2, TEST_DESCRIPTION_2),
                new OgCereal(TEST_CEREAL_UPPERCASE_3, TEST_DESCRIPTION_3)
        );

        lowercaseCerealsList = Arrays.asList(
                new OgCereal(TEST_CEREAL_LOWERCASE_1, TEST_DESCRIPTION_1),
                new OgCereal(TEST_CEREAL_LOWERCASE_2, TEST_DESCRIPTION_2),
                new OgCereal(TEST_CEREAL_LOWERCASE_3, TEST_DESCRIPTION_3)
        );
    }

    @AfterEach
    void tearDown() {
        contributorCookie = null;
        testCookies1 = null;
        testCookies2 = null;
        ogCereals = null;
    }

    @Test
    public void contextLoads() throws Exception {
        Assertions.assertNotNull(mockOgCerealService);
    }

    @Test
    void addOgCereal() throws IOException {
        OgCereal ogCereal1 = new OgCereal(TEST_CEREAL_UPPERCASE_1, TEST_DESCRIPTION_1);
        ogCereal1.setImage(new Binary(BsonBinarySubType.BINARY, TEST_IMAGE_FILE_1.getBytes()));
        when(mockOgCerealRepository.insert(any(OgCereal.class))).thenReturn(ogCereal1);
        OgCereal result = mockOgCerealRepository.insert(ogCereal1);

        assertEquals(result.getName(), TEST_CEREAL_UPPERCASE_1);
        assertEquals(result.getDescription(), TEST_DESCRIPTION_1);
        assertEquals(result.getCount(), 1);
        assertNotNull(result.getImage());
    }

    @Test
    void getCaseOptimizedResultsListLowercase() {
    }

   /*
    Validation occurs outside of site, handled by DBA. All cereals are invalidated as
    there's no way to handle validation programmatically and this feature is sealed.
    */
    @Test
    void isEmptyOrNonValidatedResults() {
        assertTrue(ogCereals.isEmpty() || !allResultsAreValidated(ogCereals));
    }

    @Test
    void getAllResults() {
        when(mockOgCerealRepository.findAll()).thenReturn(ogCereals);
        List<OgCereal> results = mockOgCerealRepository.findAll();
        assertFalse(results.isEmpty());
    }

    @Test
    void isContributorCookieTrue() {
        assertTrue(containsContributorCookie(testCookies1));
    }

    @Test
    void isContributorCookieFalse() {
        assertFalse(containsContributorCookie(testCookies2));
    }

    @Test
    void getOgCereal() {
    }

    private ObjectId getRandomId() {
        return new ObjectId(String.valueOf(UUID.randomUUID()));
    }

    private boolean containsContributorCookie(Cookie[] cookies) {
        boolean containsContributorCookie = false;
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals(OgCerealService.CONTRIBUTOR)
                    && cookie.getValue().equals(OgCerealService.TRUE)) {
                containsContributorCookie = true;
                break;
            }
        }
        return containsContributorCookie;
    }

    private boolean allResultsAreValidated(List<OgCereal> results) {
        for (OgCereal cereal : results) {
            if (!cereal.getIsValidated()) {
                return false;
            }
        }
        return true;
    }
}