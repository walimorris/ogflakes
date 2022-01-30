package com.morris.ogflakes.repository;

import com.morris.ogflakes.model.OgCereal;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface OgCerealRepository extends MongoRepository<OgCereal, String> {

    @Query("{'name' : {'$regex' : ?0 }}")
    List<OgCereal> findByNameRegex(String name);

    @Query("{ 'name' : ?0 }")
    List<OgCereal> findByName(String name);
}
