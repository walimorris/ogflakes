package com.morris.ogflakes.repository;

import com.morris.ogflakes.model.OgCereal;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface OgCerealRepository extends MongoRepository<OgCereal, String> {
}
