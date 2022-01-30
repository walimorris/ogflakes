package com.morris.ogflakes.model;

import org.bson.internal.Base64;
import org.bson.types.Binary;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

@Document("cereal")
public class OgCereal {

    @MongoId
    private String id;

    private String name;
    private Binary image;
    private boolean isValidated;

    public OgCereal(String name) {
        this.name = name;
    }

    public OgCereal() {}

    public String getId() {
        return this.id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public void setImage(Binary image) {
        this.image = image;
    }

    public String getImage() {
        return Base64.encode(this.image.getData());
    }

    public boolean getIsValidated() {
        return this.isValidated;
    }
}
