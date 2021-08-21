package com.foreflight.airport.weather.web.dto;

public class AirportWeather {

    private String identifier;
    private String name;

    public AirportWeather(String identifier, String name) {
        this.identifier = identifier;
        this.name = name;
    }

    // TODO: maybe don't need accessor methods
    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
