package com.foreflight.airport.weather.service;

import com.foreflight.airport.weather.web.dto.Airport;
import com.foreflight.airport.weather.web.dto.WeatherReport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.nio.file.Paths;

@Service
public class ForeFlightApiService {

    private static final Logger logger = LoggerFactory.getLogger(ForeFlightApiService.class);

    private final RestTemplate client;
    private final String airportEndpoint;
    private final String weatherEndpoint;

    public ForeFlightApiService(
            @Qualifier("foreflightClient") RestTemplate client,
            @Value("${foreflight-api.airport.endpoint}") String airportEndpoint,
            @Value("${foreflight-api.weather.endpoint}") String weatherEndpoint) {

        this.client = client;
        this.airportEndpoint = airportEndpoint;
        this.weatherEndpoint = weatherEndpoint;

    }

    public Airport getAirport(String id) {

        try {
            ResponseEntity<Airport> airportResponse = this.client.getForEntity(
                Paths.get(this.airportEndpoint, id).toString(),
                Airport.class
            );
            return airportResponse.getBody();
        } catch (HttpClientErrorException.NotFound exception) {
            logger.info(String.format("could not find airport for \"%s\"", id));
            return null;
        }

    }

    public WeatherReport getWeatherReport(String id) {

        try {
            ResponseEntity<WeatherReport> weatherReportResponse = this.client.getForEntity(
                Paths.get(this.weatherEndpoint, id).toString(),
                WeatherReport.class
            );
            return weatherReportResponse.getBody();
        } catch (HttpClientErrorException.NotFound exception) {
            logger.info(String.format("could not find weather report for \"%s\"", id));
            return null;
        }

    }

}
