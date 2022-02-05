package com.morris.ogflakes.repository;

import com.morris.ogflakes.model.OgCereal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface OgCerealRepository extends MongoRepository<OgCereal, String> {

    @Query("{'name' : {'$regex' : ?0 }}")
    Page<OgCereal> findByNameRegex(String name, Pageable pageable);

    @Query("{ 'name' : ?0 }")
    Page<OgCereal> findByName(String name, Pageable pageable);

    @Override
    Page<OgCereal> findAll(Pageable pageable);
}
