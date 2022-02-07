package com.morris.ogflakes.model;

import org.bson.internal.Base64;
import org.bson.types.Binary;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("cereal")
public class OgCereal {

    @Id
    private String id;

    private String name;
    private Binary image;
    private boolean isValidated;
    private String description;
    private int count;

    public OgCereal(String name, String description) {
        this.count = 1;
        this.name = name;
        this.description = description;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
