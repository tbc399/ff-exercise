package com.foreflight.airport.weather.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.foreflight.airport.weather.service.AirportWeatherService;
import com.foreflight.airport.weather.service.ForeFlightApiService;
import com.foreflight.airport.weather.web.dto.Airport;
import com.foreflight.airport.weather.web.dto.WeatherReport;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.contains;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = AirportWeatherController.class)
@Import({
    AirportWeatherService.class,
    ForeFlightApiService.class
})
@ActiveProfiles("test")
public class AirportWeatherControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean(name = "foreflightClient")
    private RestTemplate client;

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    public void getOneAirportThroughQueryParam() throws Exception {

        WeatherReport weatherReport = mapper.readValue(
            "{" +
                "\"report\": {" +
                    "\"conditions\": {" +
                        "\"tempC\": 34.0," +
                        "\"relativeHumidity\": 84," +
                        "\"cloudLayers\": [" +
                            "{\"coverage\": \"few\"}," +
                            "{\"coverage\": \"bkn\"}," +
                            "{\"coverage\": \"bkn\"}," +
                            "{\"coverage\": \"sct\"}" +
                        "]," +
                        "\"visibility\": {" +
                            "\"distanceSm\": 2.0" +
                        "}," +
                        "\"wind\": {" +
                            "\"speedKts\": 21.0," +
                            "\"direction\": 47" +
                        "}" +
                    "}," +
                    "\"forecast\": {" +
                        "\"period\": {\"dateStart\": \"2021-08-22T14:14:00+0000\"}," +
                        "\"conditions\": [" +
                            "{}," +
                            "{" +
                                "\"wind\": {" +
                                    "\"speedKts\": 5.0," +
                                    "\"direction\": 14" +
                                "}," +
                                "\"period\": {\"dateStart\": \"2021-08-22T15:14:00+0000\"}" +
                            "}," +
                            "{" +
                                "\"wind\": {" +
                                    "\"speedKts\": 11.0," +
                                    "\"direction\": 12" +
                                "}," +
                                "\"period\": {\"dateStart\": \"2021-08-22T17:00:00+0000\"}" +
                            "}" +
                        "]" +
                    "}" +
                "}" +
            "}",
            WeatherReport.class
        );

        Mockito.when(
            client.getForEntity(
                contains("/weather/report/kbos"),
                eq(WeatherReport.class)
            )
        ).thenReturn(new ResponseEntity<>(weatherReport, HttpStatus.OK));

        Airport airport = mapper.readValue(
            "{" +
                "\"icao\": \"KBOS\"," +
                "\"name\": \"General Edward Lawrence Logan International\"," +
                "\"longitude\": -71.0012857346623," +
                "\"latitude\": 23.0012857346623," +
                "\"runways\": [" +
                    "{\"ident\": \"15L-33R\", \"name\": \"15L\"}," +
                    "{\"ident\": \"15R-33L\", \"name\": \"15R\"}" +
                "]" +
            "}",
            Airport.class
        );

        Mockito.when(
            client.getForEntity(
                contains("/airports/kbos"),
                eq(Airport.class)
            )
        ).thenReturn(new ResponseEntity<>(airport, HttpStatus.OK));

        mvc.perform(get("/airports/weather").param("id", "kbos"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("[0].id").value("KBOS"))
            .andExpect(jsonPath("[0].name").value("General Edward Lawrence Logan International"))
            .andExpect(jsonPath("[0].runways[0].id").value("15L-33R"))
            .andExpect(jsonPath("[0].runways[0].name").value("15L"))
            .andExpect(jsonPath("[0].runways[1].id").value("15R-33L"))
            .andExpect(jsonPath("[0].runways[1].name").value("15R"))
            .andExpect(jsonPath("[0].latitude").value(23.0012857346623))
            .andExpect(jsonPath("[0].longitude").value(-71.0012857346623))
            .andExpect(jsonPath("[0].weather.current.temperature").value(93.2))
            .andExpect(jsonPath("[0].weather.current.relative_humidity").value(84))
            .andExpect(jsonPath("[0].weather.current.cloud_coverage").value("bkn"))
            .andExpect(jsonPath("[0].weather.current.visibility").value(2.0))
            .andExpect(jsonPath("[0].weather.current.wind.speed").value(24.2))
            .andExpect(jsonPath("[0].weather.current.wind.direction").value("NE"))
            .andExpect(jsonPath("[0].weather.forecast[0].period_offset").value("01:00"))
            .andExpect(jsonPath("[0].weather.forecast[0].temperature").isEmpty())
            .andExpect(jsonPath("[0].weather.forecast[0].wind.speed").value(5.8))
            .andExpect(jsonPath("[0].weather.forecast[0].wind.direction").value(14))
            .andExpect(jsonPath("[0].weather.forecast[1].period_offset").value("02:46"))
            .andExpect(jsonPath("[0].weather.forecast[1].temperature").isEmpty())
            .andExpect(jsonPath("[0].weather.forecast[1].wind.speed").value(12.7))
            .andExpect(jsonPath("[0].weather.forecast[1].wind.direction").value(12));

    }

    @Test
    public void getMultipleAirportsThroughQueryParams() throws Exception {

        WeatherReport weatherReportKbos = mapper.readValue(
            "{" +
                "\"report\": {" +
                    "\"conditions\": {" +
                        "\"tempC\": 34.0," +
                        "\"relativeHumidity\": 84," +
                        "\"cloudLayers\": [" +
                            "{\"coverage\": \"few\"}," +
                            "{\"coverage\": \"bkn\"}," +
                            "{\"coverage\": \"bkn\"}," +
                            "{\"coverage\": \"sct\"}" +
                        "]," +
                        "\"visibility\": {" +
                            "\"distanceSm\": 2.0" +
                        "}," +
                        "\"wind\": {" +
                            "\"speedKts\": 21.0," +
                            "\"direction\": 47" +
                        "}" +
                    "}," +
                    "\"forecast\": {" +
                        "\"period\": {\"dateStart\": \"2021-08-22T14:14:00+0000\"}," +
                        "\"conditions\": [" +
                            "{}," +
                            "{" +
                                "\"wind\": {" +
                                    "\"speedKts\": 5.0," +
                                    "\"direction\": 14" +
                                "}," +
                                "\"period\": {\"dateStart\": \"2021-08-22T15:14:00+0000\"}" +
                            "}," +
                            "{" +
                                "\"wind\": {" +
                                    "\"speedKts\": 11.0," +
                                    "\"direction\": 12" +
                                "}," +
                                "\"period\": {\"dateStart\": \"2021-08-22T17:00:00+0000\"}" +
                            "}" +
                        "]" +
                    "}" +
                "}" +
            "}",
            WeatherReport.class
        );

        Mockito.when(
            client.getForEntity(
                contains("/weather/report/kbos"),
                eq(WeatherReport.class)
            )
        ).thenReturn(new ResponseEntity<>(weatherReportKbos, HttpStatus.OK));

        WeatherReport weatherReportKaus = mapper.readValue(
            "{" +
                "\"report\": {" +
                    "\"conditions\": {" +
                        "\"tempC\": 34.0," +
                        "\"relativeHumidity\": 84," +
                        "\"cloudLayers\": [" +
                            "{\"coverage\": \"few\"}," +
                            "{\"coverage\": \"bkn\"}," +
                            "{\"coverage\": \"bkn\"}," +
                            "{\"coverage\": \"sct\"}" +
                        "]," +
                        "\"visibility\": {" +
                            "\"distanceSm\": 2.0" +
                        "}," +
                        "\"wind\": {" +
                            "\"speedKts\": 21.0," +
                            "\"direction\": 47" +
                        "}" +
                    "}," +
                    "\"forecast\": {" +
                        "\"period\": {\"dateStart\": \"2021-08-22T14:14:00+0000\"}," +
                        "\"conditions\": [" +
                            "{}," +
                            "{" +
                                "\"wind\": {" +
                                    "\"speedKts\": 5.0," +
                                    "\"direction\": 14" +
                                "}," +
                                "\"period\": {\"dateStart\": \"2021-08-22T15:14:00+0000\"}" +
                            "}," +
                            "{" +
                                "\"wind\": {" +
                                    "\"speedKts\": 11.0," +
                                    "\"direction\": 12" +
                                "}," +
                                "\"period\": {\"dateStart\": \"2021-08-22T17:00:00+0000\"}" +
                            "}" +
                        "]" +
                    "}" +
                "}" +
            "}",
            WeatherReport.class
        );

        Mockito.when(
            client.getForEntity(
                contains("/weather/report/kaus"),
                eq(WeatherReport.class)
            )
        ).thenReturn(new ResponseEntity<>(weatherReportKaus, HttpStatus.OK));

        Airport airportKbos = mapper.readValue(
            "{" +
                "\"icao\": \"KBOS\"," +
                "\"name\": \"General Edward Lawrence Logan International\"," +
                "\"longitude\": -71.0012857346623," +
                "\"latitude\": 23.0012857346623," +
                "\"runways\": [" +
                    "{\"ident\": \"15L-33R\", \"name\": \"15L\"}," +
                    "{\"ident\": \"15R-33L\", \"name\": \"15R\"}" +
                "]" +
            "}",
            Airport.class
        );

        Mockito.when(
            client.getForEntity(
                contains("/airports/kbos"),
                eq(Airport.class)
            )
        ).thenReturn(new ResponseEntity<>(airportKbos, HttpStatus.OK));

        Airport airportKaus = mapper.readValue(
            "{" +
                "\"icao\": \"KAUS\"," +
                "\"name\": \"Austin-Bergstrom International\"," +
                "\"longitude\": -71.0012857346623," +
                "\"latitude\": 23.0012857346623," +
                "\"runways\": [" +
                    "{\"ident\": \"18L-33R\", \"name\": \"18L\"}," +
                    "{\"ident\": \"18R-33L\", \"name\": \"18R\"}" +
                "]" +
            "}",
            Airport.class
        );

        Mockito.when(
            client.getForEntity(
                contains("/airports/kaus"),
                eq(Airport.class)
            )
        ).thenReturn(new ResponseEntity<>(airportKaus, HttpStatus.OK));

        mvc.perform(get("/airports/weather").param("id", "kbos", "kaus"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("[0].id").value("KBOS"))
            .andExpect(jsonPath("[0].name").value("General Edward Lawrence Logan International"))
            .andExpect(jsonPath("[0].runways[0].id").value("15L-33R"))
            .andExpect(jsonPath("[0].runways[0].name").value("15L"))
            .andExpect(jsonPath("[0].runways[1].id").value("15R-33L"))
            .andExpect(jsonPath("[0].runways[1].name").value("15R"))
            .andExpect(jsonPath("[0].latitude").value(23.0012857346623))
            .andExpect(jsonPath("[0].longitude").value(-71.0012857346623))
            .andExpect(jsonPath("[0].weather.current.temperature").value(93.2))
            .andExpect(jsonPath("[0].weather.current.relative_humidity").value(84))
            .andExpect(jsonPath("[0].weather.current.cloud_coverage").value("bkn"))
            .andExpect(jsonPath("[0].weather.current.visibility").value(2.0))
            .andExpect(jsonPath("[0].weather.current.wind.speed").value(24.2))
            .andExpect(jsonPath("[0].weather.current.wind.direction").value("NE"))
            .andExpect(jsonPath("[0].weather.forecast[0].period_offset").value("01:00"))
            .andExpect(jsonPath("[0].weather.forecast[0].temperature").isEmpty())
            .andExpect(jsonPath("[0].weather.forecast[0].wind.speed").value(5.8))
            .andExpect(jsonPath("[0].weather.forecast[0].wind.direction").value(14))
            .andExpect(jsonPath("[0].weather.forecast[1].period_offset").value("02:46"))
            .andExpect(jsonPath("[0].weather.forecast[1].temperature").isEmpty())
            .andExpect(jsonPath("[0].weather.forecast[1].wind.speed").value(12.7))
            .andExpect(jsonPath("[0].weather.forecast[1].wind.direction").value(12))
            .andExpect(jsonPath("[1].id").value("KAUS"))
            .andExpect(jsonPath("[1].name").value("Austin-Bergstrom International"))
            .andExpect(jsonPath("[1].runways[0].id").value("18L-33R"))
            .andExpect(jsonPath("[1].runways[0].name").value("18L"))
            .andExpect(jsonPath("[1].runways[1].id").value("18R-33L"))
            .andExpect(jsonPath("[1].runways[1].name").value("18R"))
            .andExpect(jsonPath("[1].latitude").value(23.0012857346623))
            .andExpect(jsonPath("[1].longitude").value(-71.0012857346623))
            .andExpect(jsonPath("[1].weather.current.temperature").value(93.2))
            .andExpect(jsonPath("[1].weather.current.relative_humidity").value(84))
            .andExpect(jsonPath("[1].weather.current.cloud_coverage").value("bkn"))
            .andExpect(jsonPath("[1].weather.current.visibility").value(2.0))
            .andExpect(jsonPath("[1].weather.current.wind.speed").value(24.2))
            .andExpect(jsonPath("[1].weather.current.wind.direction").value("NE"))
            .andExpect(jsonPath("[1].weather.forecast[0].period_offset").value("01:00"))
            .andExpect(jsonPath("[1].weather.forecast[0].temperature").isEmpty())
            .andExpect(jsonPath("[1].weather.forecast[0].wind.speed").value(5.8))
            .andExpect(jsonPath("[1].weather.forecast[0].wind.direction").value(14))
            .andExpect(jsonPath("[1].weather.forecast[1].period_offset").value("02:46"))
            .andExpect(jsonPath("[1].weather.forecast[1].temperature").isEmpty())
            .andExpect(jsonPath("[1].weather.forecast[1].wind.speed").value(12.7))
            .andExpect(jsonPath("[1].weather.forecast[1].wind.direction").value(12));

    }

    @Test
    public void getOneAirportThroughPathIdentifier() throws Exception {

        WeatherReport weatherReport = mapper.readValue(
            "{" +
                "\"report\": {" +
                    "\"conditions\": {" +
                        "\"tempC\": 34.0," +
                        "\"relativeHumidity\": 84," +
                        "\"cloudLayers\": [" +
                            "{\"coverage\": \"few\"}," +
                            "{\"coverage\": \"bkn\"}," +
                            "{\"coverage\": \"bkn\"}," +
                            "{\"coverage\": \"sct\"}" +
                        "]," +
                        "\"visibility\": {" +
                            "\"distanceSm\": 2.0" +
                        "}," +
                        "\"wind\": {" +
                            "\"speedKts\": 21.0," +
                            "\"direction\": 47" +
                        "}" +
                    "}," +
                    "\"forecast\": {" +
                        "\"period\": {\"dateStart\": \"2021-08-22T14:14:00+0000\"}," +
                        "\"conditions\": [" +
                            "{}," +
                            "{" +
                                "\"wind\": {" +
                                    "\"speedKts\": 5.0," +
                                    "\"direction\": 14" +
                                "}," +
                                "\"period\": {\"dateStart\": \"2021-08-22T15:14:00+0000\"}" +
                            "}," +
                            "{" +
                                "\"wind\": {" +
                                    "\"speedKts\": 11.0," +
                                    "\"direction\": 12" +
                                "}," +
                                "\"period\": {\"dateStart\": \"2021-08-22T17:00:00+0000\"}" +
                            "}" +
                        "]" +
                    "}" +
                "}" +
            "}",
            WeatherReport.class
        );

        Mockito.when(
            client.getForEntity(
                contains("/weather/report/kbos"),
                eq(WeatherReport.class)
            )
        ).thenReturn(new ResponseEntity<>(weatherReport, HttpStatus.OK));

        Airport airport = mapper.readValue(
            "{" +
                "\"icao\": \"KBOS\"," +
                "\"name\": \"General Edward Lawrence Logan International\"," +
                "\"longitude\": -71.0012857346623," +
                "\"latitude\": 23.0012857346623," +
                "\"runways\": [" +
                    "{\"ident\": \"15L-33R\", \"name\": \"15L\"}," +
                    "{\"ident\": \"15R-33L\", \"name\": \"15R\"}" +
                "]" +
            "}",
            Airport.class
        );

        Mockito.when(
            client.getForEntity(
                contains("/airports/kbos"),
                eq(Airport.class)
            )
        ).thenReturn(new ResponseEntity<>(airport, HttpStatus.OK));

        mvc.perform(get("/airports/kbos/weather"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("id").value("KBOS"))
            .andExpect(jsonPath("name").value("General Edward Lawrence Logan International"))
            .andExpect(jsonPath("runways[0].id").value("15L-33R"))
            .andExpect(jsonPath("runways[0].name").value("15L"))
            .andExpect(jsonPath("runways[1].id").value("15R-33L"))
            .andExpect(jsonPath("runways[1].name").value("15R"))
            .andExpect(jsonPath("latitude").value(23.0012857346623))
            .andExpect(jsonPath("longitude").value(-71.0012857346623))
            .andExpect(jsonPath("weather.current.temperature").value(93.2))
            .andExpect(jsonPath("weather.current.relative_humidity").value(84))
            .andExpect(jsonPath("weather.current.cloud_coverage").value("bkn"))
            .andExpect(jsonPath("weather.current.visibility").value(2.0))
            .andExpect(jsonPath("weather.current.wind.speed").value(24.2))
            .andExpect(jsonPath("weather.current.wind.direction").value("NE"))
            .andExpect(jsonPath("weather.forecast[0].period_offset").value("01:00"))
            .andExpect(jsonPath("weather.forecast[0].temperature").isEmpty())
            .andExpect(jsonPath("weather.forecast[0].wind.speed").value(5.8))
            .andExpect(jsonPath("weather.forecast[0].wind.direction").value(14))
            .andExpect(jsonPath("weather.forecast[1].period_offset").value("02:46"))
            .andExpect(jsonPath("weather.forecast[1].temperature").isEmpty())
            .andExpect(jsonPath("weather.forecast[1].wind.speed").value(12.7))
            .andExpect(jsonPath("weather.forecast[1].wind.direction").value(12));

    }

    @Test
    public void invalidIdentifierInputThroughQuery() throws Exception {

        Mockito.when(
            client.getForEntity(
                contains("/weather/report"),
                eq(WeatherReport.class)
            )
        ).thenThrow(
            HttpClientErrorException.create(
                HttpStatus.NOT_FOUND, null, null, null, null
            )
        );

        Mockito.when(
            client.getForEntity(
                contains("/airports"),
                eq(Airport.class)
            )
        ).thenThrow(
            HttpClientErrorException.create(
                HttpStatus.NOT_FOUND, null, null, null, null
            )
        );

        mvc.perform(get("/airports/weather").param("id", "nonsense"))
            .andExpect(status().isNotFound());

    }

    @Test
    public void invalidIdentifierInputThroughPath() throws Exception {

        Mockito.when(
            client.getForEntity(
                contains("/airports"),
                eq(Airport.class)
            )
        ).thenThrow(
            HttpClientErrorException.create(
                HttpStatus.NOT_FOUND, null, null, null, null
            )
        );

        Mockito.when(
            client.getForEntity(
                contains("/weather/report"),
                eq(WeatherReport.class)
            )
        ).thenThrow(
            HttpClientErrorException.create(
                HttpStatus.NOT_FOUND, "", null, null, null
            )
        );

        mvc.perform(get("/airports/nonsense/weather"))
            .andExpect(status().isNotFound());

    }

}
