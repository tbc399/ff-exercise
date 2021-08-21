package com.foreflight.airport.weather.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class Airport {

    private String icao;

    private String name;

    static class Runway {
        private String ident;
    }

    private List<Runway> runways;

    private Double latitude;

    private Double longitude;

}