package com.example.hawkergo.models;

import java.util.HashMap;
import java.util.List;

public class HawkerStall extends BaseDbFields {
    String id, address, hawkerCentre, name;
    OpeningHours openingHours;
    List<Review> reviews;
    List<String> reviewsIds;
    List<String> popularitems;
    List<String> tags;

    public HawkerStall(){};

    public HawkerStall(String address, String name, OpeningHours openingHours, String hawkerCentre, List<String> reviewsIds, List<String> popularitems, List<String> tags){

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
