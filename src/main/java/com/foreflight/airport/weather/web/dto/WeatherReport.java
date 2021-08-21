package com.foreflight.airport.weather.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class WeatherReport {

    @JsonProperty("report.conditions.relativeHumidity")
    private Integer relativeHumidity;

    public Integer getRelativeHumidity() {
        return relativeHumidity;
    }

}
