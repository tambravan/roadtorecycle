package com.app.roadtorecycle.models;

public class PlacesModel {

    private String centerName;
    private String centerAddress;
    private String centerHours;
    private String centerUrl;
    private String centerLatitude;
    private String centerLongitude;
    private String currentLatitude;
    private String currentLongitude;
    private String centerDistanceMiles;
    private String centerPhone;
    private String centerOpeningDays;
    private boolean flagRecycle;

    public String getCenterName() {
        return centerName;
    }

    public void setCenterName(String centerName) {
        this.centerName = centerName;
    }

    public String getCenterAddress() {
        return centerAddress;
    }

    public void setCenterAddress(String centerAddress) {
        this.centerAddress = centerAddress;
    }

    public String getCenterHours() {
        return centerHours;
    }

    public void setCenterHours(String centerHours) {
        this.centerHours = centerHours;
    }

    public String getCenterUrl() {
        return centerUrl;
    }

    public void setCenterUrl(String centerUrl) {
        this.centerUrl = centerUrl;
    }

    public String getCenterPhone() {
        return centerPhone;
    }

    public void setCenterPhone(String centerPhone) {
        this.centerPhone = centerPhone;
    }

    public boolean isFlagRecycle() {
        return flagRecycle;
    }

    public void setFlagRecycle(boolean flagRecycle) {
        this.flagRecycle = flagRecycle;
    }

    public String getCenterLatitude() {
        return centerLatitude;
    }

    public void setCenterLatitude(String centerLatitude) {
        this.centerLatitude = centerLatitude;
    }

    public String getCenterLongitude() {
        return centerLongitude;
    }

    public void setCenterLongitude(String centerLongitude) {
        this.centerLongitude = centerLongitude;
    }

    public String getCenterDistanceMiles() {
        return centerDistanceMiles;
    }

    public void setCenterDistanceMiles(String centerDistanceMiles) {
        this.centerDistanceMiles = centerDistanceMiles;
    }

    public String getCenterOpeningDays() {
        return centerOpeningDays;
    }

    public void setCenterOpeningDays(String centerOpeningDays) {
        this.centerOpeningDays = centerOpeningDays;
    }

    public String getCurrentLatitude() {
        return currentLatitude;
    }

    public void setCurrentLatitude(String currentLatitude) {
        this.currentLatitude = currentLatitude;
    }

    public String getCurrentLongitude() {
        return currentLongitude;
    }

    public void setCurrentLongitude(String currentLongitude) {
        this.currentLongitude = currentLongitude;
    }
}
