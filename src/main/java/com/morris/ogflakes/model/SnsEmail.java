package com.morris.ogflakes.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;

public class SnsEmail {
    private static final Logger logger = LoggerFactory.getLogger(SnsEmail.class);

    @JsonProperty
    @NonNull
    private final String subject;

    @JsonProperty
    private final String message;

    public SnsEmail(String subject, String message) {
        this.subject = subject;
        this.message = message;
    }

    @NonNull
    public String getSubject() {
        return subject;
    }

    public String getMessage() {
        return message;
    }
}
