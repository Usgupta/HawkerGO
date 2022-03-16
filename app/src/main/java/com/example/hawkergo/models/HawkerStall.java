package com.example.hawkergo.models;

import java.util.HashMap;
import java.util.List;

public class HawkerStall {
    String id, address, hawkerCentre, name;
    HashMap<String, String> openingHours;
    List<Review> reviews;
    List<String> reviewsIds;

    public HawkerStall(){};

    public HawkerStall(String id, String address, String name, HashMap<String,String> openingHours, String hawkerCentre, List<String> reviewsIds){
        this.id = id;
        this.address = address;
        this.name = name;
        this.openingHours = openingHours;
        this.hawkerCentre = hawkerCentre;
        this.reviewsIds = reviewsIds;
    }

    public void attachReviews(List<Review> reviews){
        this.reviews = reviews;
    }

    public HashMap<String, Object> toMap(){
        HashMap<String, Object> map = new HashMap<>();
        map.put("address", this.address);
        map.put("openingHours", this.openingHours);
        map.put("name", this.name);
        map.put("reviews", this.reviews);
        map.put("hawkerCentre", this.hawkerCentre);
        return map;
    }


}
