package com.morris.ogflakes.service;

import com.morris.ogflakes.model.OgCereal;
import com.morris.ogflakes.repository.OgCerealRepository;
import org.bson.BsonBinarySubType;
import org.bson.types.Binary;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

@Service
public class OgCerealService  {
    private static final Logger logger = LoggerFactory.getLogger(OgCerealService.class);

    @Autowired
    private OgCerealRepository ogCerealRepository;

    public void addOgCereal(String name, String description, MultipartFile file) {
        OgCereal ogCereal = new OgCereal(name, description);
        try {
            ogCereal.setImage(new Binary(BsonBinarySubType.BINARY, file.getBytes()));
        } catch (IOException e) {
            logger.error("Error adding image to repository: {}", e.getMessage());
        }
        ogCerealRepository.insert(ogCereal);
    }

    public Optional<OgCereal> getOgCereal(String id) {
        return ogCerealRepository.findById(id);
    }
}