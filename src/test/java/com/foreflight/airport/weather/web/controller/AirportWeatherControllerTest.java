package com.foreflight.airport.weather.web.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.RestTemplate;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = AirportWeatherController.class)
public class AirportWeatherControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private RestTemplate client;

    @Test
    public void getOneAirportThroughQuery() throws Exception {

        Mockito.when(
            client.getForEntity()
        ).thenReturn(new ResponseEntity<>(airport, HttpStatus.OK));

        mvc.perform(get("api/airports/weather").param("id", "arpt"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("[0].identifier").value("arpt"))
            .andExpect(jsonPath("[0].name").value("Airport Name"))
            .andExpect(jsonPath("[0].available_runways[0]").value("run-way-1"))
            .andExpect(jsonPath("[0].available_runways[1]").value("run-way-2"))
            .andExpect(jsonPath("[0].latitude").value(23.001))
            .andExpect(jsonPath("[0].longitude").value(24.001))
            .andExpect(jsonPath("[0].weather.current.temperature").value("kaus"))
            .andExpect(jsonPath("[0].weather.current.relative_humidity").value("kaus"))
            .andExpect(jsonPath("[0].weather.current.cloud_coverage_summary").value("kaus"))
            .andExpect(jsonPath("[0].weather.current.visibility").value("kaus"))
            .andExpect(jsonPath("[0].weather.current.wind.speed").value(12))
            .andExpect(jsonPath("[0].weather.current.wind.direction").value(45.1234))
            .andExpect(jsonPath("[0].weather.forecast[0].period_offset").value("17:34"))
            .andExpect(jsonPath("[0].weather.forecast[0].temperature").value(76))
            .andExpect(jsonPath("[0].weather.forecast[0].wind.speed").value(8))
            .andExpect(jsonPath("[0].weather.forecast[0].wind.direction").value(13.28))
            .andExpect(jsonPath("[0].weather.forecast[1].period_offset").value("12:05"))
            .andExpect(jsonPath("[0].weather.forecast[1].temperature").value(34))
            .andExpect(jsonPath("[0].weather.forecast[1].wind.speed").value(10))
            .andExpect(jsonPath("[0].weather.forecast[1].wind.direction").value(12.90))

    }

    @Test
    public void getOneAirportThroughPathIdentifier() throws Exception {

        /*
        mvc.perform(get("api/airports/kaus/weather"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("[0].").value());

         */

    }

}
