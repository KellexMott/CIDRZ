package com.techart.cidrz.model;

/**
 * Object for a Facility
 * Created by Kelvin on 05/06/2017.
 */

public class Facility {
    private String facilityMode;
    private String facilityName;
    private String imageUrl;
    private Long timeCreated;


    public Facility()
    {

    }

    public Long getTimeCreated() {
        return timeCreated;
    }

    public void setTimeCreated(Long timeCreated) {
        this.timeCreated = timeCreated;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getFacilityMode() {
        return facilityMode;
    }

    public void setFacilityMode(String facilityMode) {
        this.facilityMode = facilityMode;
    }

    public String getFacilityName() {
        return facilityName;
    }

    public void setFacilityName(String facilityName) {
        this.facilityName = facilityName;
    }
}
