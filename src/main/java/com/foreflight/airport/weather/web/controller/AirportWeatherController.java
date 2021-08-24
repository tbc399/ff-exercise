package com.foreflight.airport.weather.web.controller;

import com.foreflight.airport.weather.service.AirportWeatherService;
import com.foreflight.airport.weather.web.dto.AirportWeather;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/airports")
public class AirportWeatherController {

    private final AirportWeatherService airportWeatherService;

    public AirportWeatherController(AirportWeatherService airportWeatherService) {
        this.airportWeatherService = airportWeatherService;
    }

    @GetMapping("weather")
    public List<AirportWeather> getAirportWeather(@RequestParam("id") List<String> airportIds) {
        List<AirportWeather> airportWeather = this.airportWeatherService.getAirportWeather(airportIds);
        if (airportWeather.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "invalid airport identifier");
        }
        return airportWeather;
    }

    @GetMapping("{id}/weather")
    public AirportWeather getAirportWeather(@PathVariable("id") String airportId) {
        AirportWeather airportWeather = this.airportWeatherService.getAirportWeather(airportId);
        if (airportWeather == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "invalid airport identifier");
        }
        return airportWeather;
    }

}
