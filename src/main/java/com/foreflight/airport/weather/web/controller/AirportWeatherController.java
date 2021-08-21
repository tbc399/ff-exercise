package com.foreflight.airport.weather.web.controller;

import com.foreflight.airport.weather.web.dto.AirportWeather;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(name = "/airports")
public class AirportWeatherController {

    @GetMapping("/weather")
    public AirportWeather getAirportWeather(@RequestParam("id") String id) {

        return null;

    }

}
