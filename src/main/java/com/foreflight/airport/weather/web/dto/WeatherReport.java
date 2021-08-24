package com.foreflight.airport.weather.web.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;

import java.time.LocalDateTime;
import java.util.List;

public class WeatherReport {

    public static class Wind {

        private Double speedKts;

        private Integer direction;

        public Double getSpeedKts() {
            return speedKts;
        }

        public Integer getDirection() {
            return direction;
        }

    }

    public static class Conditions {

        private Double tempC;
        private Wind wind;

        public Double getTempC() {
            return tempC;
        }

        public Wind getWind() {
            return wind;
        }

    }

    public static class Visibility {

        private Double distanceSm;

        public Double getDistanceSm() {
            return distanceSm;
        }

    }

    public static class CloudCoverage {

        private String coverage;

        public String getCoverage() {
            return coverage;
        }

    }

    public static class CurrentConditions extends Conditions {

        private Integer relativeHumidity;
        private Visibility visibility;
        private List<CloudCoverage> cloudLayers;

        public Integer getRelativeHumidity() {
            return relativeHumidity;
        }

        public Visibility getVisibility() {
            return visibility;
        }

        public List<CloudCoverage> getCloudLayers() {
            return cloudLayers;
        }

    }

    public static class ForecastPeriod {

        @JsonDeserialize(using = LocalDateTimeDeserializer.class)
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssX")
        private LocalDateTime dateStart;

        public LocalDateTime getDateStart() {
            return dateStart;
        }

    }

    public static class ForecastConditions extends Conditions {

        private ForecastPeriod period;

        public ForecastPeriod getPeriod() {
            return period;
        }

    }

    public static class Forecast {

        private ForecastPeriod period;
        private List<ForecastConditions> conditions;

        public ForecastPeriod getPeriod() {
            return period;
        }

        public List<ForecastConditions> getConditions() {
            return conditions;
        }

    }

    public static class Report {

        private CurrentConditions conditions;
        private Forecast forecast;

        public CurrentConditions getConditions() {
            return conditions;
        }

        public Forecast getForecast() {
            return forecast;
        }

    }

    private Report report;

    public Report getReport() {
        return report;
    }

}