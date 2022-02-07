package com.morris.ogflakes.controller;

import com.morris.ogflakes.model.OgCereal;
import com.morris.ogflakes.repository.OgCerealRepository;
import com.morris.ogflakes.service.OgCerealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
public class OgCerealRestController {

    private final OgCerealRepository ogCerealRepository;

    @Autowired
    private OgCerealService ogCerealService;

    public OgCerealRestController(OgCerealRepository ogCerealRepository) {
        this.ogCerealRepository = ogCerealRepository;
    }

    @PatchMapping(path = "/ogcereal/showcase/{id}", consumes = "application/json")
    public ResponseEntity<OgCereal> updateOgCerealTickerCount(@PathVariable("id") String id,
                                                              @RequestBody OgCereal ogCereal) throws ResourceNotFoundException {
        Optional<OgCereal> cereal = ogCerealRepository.findById(id);
        if (cereal.isPresent()) {
            cereal.get().setCount(ogCereal.getCount());
            return new ResponseEntity<>(ogCerealRepository.save(cereal.get()), HttpStatus.OK);
        }
        throw new ResourceNotFoundException("Could not find ogcereal with id: " + id);
    }

    @GetMapping("ogcereal/showcase/{id}")
    public OgCereal getOgCerealById(@PathVariable String id) {
        return ogCerealService.getOgCereal(id);
    }
}
