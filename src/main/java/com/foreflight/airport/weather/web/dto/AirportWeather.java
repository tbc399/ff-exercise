package com.foreflight.airport.weather.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.foreflight.airport.weather.web.dto.serializer.PeriodOffsetSerializer;
import com.foreflight.airport.weather.web.dto.serializer.SingleDecimalPlaceSerializer;

import java.time.Duration;
import java.util.List;

public class AirportWeather {

    public static class Runway {

        private String id;

        private String name;

        public Runway(String id, String name) {
            this.id = id;
            this.name = name;
        }

        public String getId() {
            return id;
        }

        public String getName() {
            return name;
        }

    }

    public static class Weather {

        private Current current;

        private List<Forecast> forecast;

        public Weather(Current current, List<Forecast> forecast) {
            this.current = current;
            this.forecast = forecast;
        }

        public Current getCurrent() {
            return current;
        }

        public List<Forecast> getForecast() {
            return forecast;
        }

    }

    public static class Current {

        private Double temperature;

        @JsonProperty("relative_humidity")
        private Integer relativeHumidity;

        @JsonProperty("cloud_coverage")
        private String cloudCoverage;

        private Double visibility;

        private CurrentWind wind;

        public Current(
                Double temperature,
                Integer relativeHumidity,
                String cloudCoverage,
                Double visibility,
                CurrentWind wind) {
            this.temperature = temperature;
            this.relativeHumidity = relativeHumidity;
            this.cloudCoverage = cloudCoverage;
            this.visibility = visibility;
            this.wind = wind;
        }

        @JsonSerialize(using = SingleDecimalPlaceSerializer.class)
        public Double getTemperature() {
            return temperature;
        }

        public Integer getRelativeHumidity() {
            return relativeHumidity;
        }

        public String getCloudCoverage() {
            return cloudCoverage;
        }

        public Double getVisibility() {
            return visibility;
        }

        public CurrentWind getWind() {
            return wind;
        }

    }

    public static class Forecast {

        @JsonProperty("period_offset")
        private Duration periodOffset;

        private Double temperature;

        private ForecastWind wind;

        public Forecast(Duration periodOffset, Double temperature, ForecastWind wind) {
            this.periodOffset = periodOffset;
            this.temperature = temperature;
            this.wind = wind;
        }

        @JsonSerialize(using = PeriodOffsetSerializer.class)
        public Duration getPeriodOffset() {
            return periodOffset;
        }

        @JsonSerialize(using = SingleDecimalPlaceSerializer.class)
        public Double getTemperature() {
            return temperature;
        }

        public ForecastWind getWind() {
            return wind;
        }

    }

    static class Wind {

        private Double speed;

        protected Wind(Double speed) {
            this.speed = speed;
        }

        @JsonSerialize(using = SingleDecimalPlaceSerializer.class)
        public Double getSpeed() {
            return speed;
        }

    }

    public static class CurrentWind extends Wind {

        private String direction;

        public CurrentWind(Double speed, String direction) {
            super(speed);
            this.direction = direction;
        }

        public String getDirection() {
            return direction;
        }

    }

    public static class ForecastWind extends Wind {

        private Integer direction;

        public ForecastWind(Double speed, Integer direction) {
            super(speed);
            this.direction = direction;
        }

        public Integer getDirection() {
            return direction;
        }

    }

    private String id;

    private String name;

    private List<Runway> runways;

    private Double latitude;

    private Double longitude;

    private Weather weather;

    public AirportWeather(
            String id,
            String name,
            List<Runway> runways,
            Double latitude,
            Double longitude,
            Weather weather) {
        this.id = id;
        this.name = name;
        this.runways = runways;
        this.latitude = latitude;
        this.longitude = longitude;
        this.weather = weather;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<Runway> getRunways() {
        return runways;
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public Weather getWeather() {
        return weather;
    }

}
