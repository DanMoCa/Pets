package com.example.android.pets.data;

/**
 * Created by Dan14z on 31/08/2017.
 */

public class Pet {
    private Integer mId;
    private String mName;
    private String mBreed;
    private Integer mGender;
    private Integer mWeight;

    public Pet(Integer id, String name, String breed, Integer gender, Integer weight) {
        mId = id;
        mName = name;
        mBreed = breed;
        mGender = gender;
        mWeight = weight;
    }

    public Integer getId() {
        return mId;
    }

    public String getName() {
        return mName;
    }

    public String getBreed() {
        return mBreed;
    }

    public Integer getGender() {
        return mGender;
    }

    public Integer getWeight() {
        return mWeight;
    }
}
