package com.foreflight.airport.weather.web.dto;

import java.util.List;

public class Airport {

    private String icao;

    private String name;

    public static class Runway {

        private String ident;

        private String name;

        public String getIdent() {
            return ident;
        }

        public String getName() {
            return name;
        }

    }

    private List<Runway> runways;

    private Double latitude;

    private Double longitude;

    public String getIcao() {
        return icao;
    }

    public String getName() {
        return name;
    }

    public List<Runway> getRunways() {
        return runways;
    }

    public Double getLongitude() {
        return longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

}