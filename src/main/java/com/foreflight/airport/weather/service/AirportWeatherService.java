package com.foreflight.airport.weather.service;

import com.foreflight.airport.weather.util.Conversions;
import com.foreflight.airport.weather.web.dto.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AirportWeatherService {

    private static final Logger logger = LoggerFactory.getLogger(AirportWeatherService.class);

    private final RestTemplate client;
    private final String airportEndpoint;
    private final String weatherEndpoint;

    public AirportWeatherService(
            @Qualifier("foreflightClient") RestTemplate client,
            @Value("${foreflight-api.airport.endpoint}") String airportEndpoint,
            @Value("${foreflight-api.weather.endpoint}") String weatherEndpoint) {

        this.client = client;
        this.airportEndpoint = airportEndpoint;
        this.weatherEndpoint = weatherEndpoint;

    }

    public List<AirportWeather> getAirportWeather(List<String> airportIds) {

        List<AirportWeather> airportWeather = new ArrayList<>();

        // TODO: maybe replace with stream
        for (String id : airportIds) {

            // TODO: move these rest calls out to their own ForeFlightApi service?
            WeatherReport weatherReport;
            try {
                ResponseEntity<WeatherReport> weatherReportResponse = this.client.getForEntity(
                    Paths.get(this.weatherEndpoint, id).toString(),
                    WeatherReport.class
                );
                weatherReport = weatherReportResponse.getBody();
            } catch (HttpClientErrorException.NotFound exception) {
                logger.info(String.format("could not find weather report for \"%s\"", id));
                continue;
            }

            Airport airport;
            try {
                ResponseEntity<Airport> airportResponse = this.client.getForEntity(
                    Paths.get(this.airportEndpoint, id).toString(),
                    Airport.class
                );
                airport = airportResponse.getBody();
            } catch (HttpClientErrorException.NotFound exception) {
                logger.info(String.format("could not find airport for \"%s\"", id));
                continue;
            }

            airportWeather.add(combine(airport, weatherReport));

        }

        return airportWeather;

    }

    public AirportWeather getAirportWeather(String airportId) {
        try {
            return getAirportWeather(Collections.singletonList(airportId)).get(0);
        } catch (IndexOutOfBoundsException exception) {
            return null;
        }
    }

    private AirportWeather combine(Airport airport, WeatherReport weatherReport) {

        List<AirportWeather.Runway> runways = airport.getRunways().stream()
            .map(x -> new AirportWeather.Runway(x.getIdent(), x.getName()))
            .collect(Collectors.toList());

        WeatherReport.CurrentConditions current = weatherReport.getReport().getConditions();
        WeatherReport.Forecast forecast = weatherReport.getReport().getForecast();

        LocalDateTime dateStart = forecast.getPeriod().getDateStart();

        List<AirportWeather.Forecast> forecastWeather = forecast.getConditions().subList(1, 3)
            .stream()
            .map(weather -> new AirportWeather.Forecast(
                Duration.between(dateStart, weather.getPeriod().getDateStart()),
                weather.getTempC() != null ? Conversions.celsiusToFahrenheit(weather.getTempC()) : null,
                new AirportWeather.ForecastWind(
                    Conversions.knotsToMph(weather.getWind().getSpeedKts()),
                    weather.getWind().getDirection()
                )
            ))
            .collect(Collectors.toList());

        return new AirportWeather(
            airport.getIcao(),
            airport.getName(),
            runways,
            airport.getLatitude(),
            airport.getLongitude(),
            new AirportWeather.Weather(
                new AirportWeather.Current(
                    Conversions.celsiusToFahrenheit(current.getTempC()),
                    current.getRelativeHumidity(),
                    maxCloudCoverage(current.getCloudLayers()),
                    current.getVisibility().getDistanceSm(),
                    new AirportWeather.CurrentWind(
                        Conversions.knotsToMph(current.getWind().getSpeedKts()),
                        Conversions.degreesToCardinal(current.getWind().getDirection())
                    )
                ),
                forecastWeather
            )
        );

    }

    private enum CloudCoverageCodes {

        vv(1),
        clr(2),
        skc(3),
        few(4),
        sct(5),
        bkn(6),
        ovc(7);

        private final int order;

        CloudCoverageCodes(int order) {
            this.order = order;
        }

        public int getOrder() {
            return order;
        }

    }

    private String maxCloudCoverage(List<WeatherReport.CloudCoverage> coverage) {

        return coverage.stream()
            .map(WeatherReport.CloudCoverage::getCoverage)
            .map(CloudCoverageCodes::valueOf)
            .sorted(Comparator.comparing(CloudCoverageCodes::getOrder))
            .collect(Collectors.toList())
            .get(coverage.size() - 1)
            .name();

    }

}
