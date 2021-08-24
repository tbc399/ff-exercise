package com.foreflight.airport.weather.service;

import com.foreflight.airport.weather.util.Conversions;
import com.foreflight.airport.weather.web.dto.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AirportWeatherService {

    private static final Logger logger = LoggerFactory.getLogger(AirportWeatherService.class);

    private final ForeFlightApiService foreFlightApiService;

    public AirportWeatherService(ForeFlightApiService foreFlightApiService) {
        this.foreFlightApiService = foreFlightApiService;
    }

    public List<AirportWeather> getAirportWeather(List<String> airportIds) {

        List<AirportWeather> airportWeather = new ArrayList<>();

        for (String id : airportIds) {

            Airport airport = foreFlightApiService.getAirport(id);
            WeatherReport weatherReport = foreFlightApiService.getWeatherReport(id);

            if (airport != null && weatherReport != null) {
                logger.info(String.format("building airport weather for \"%s\"", id));
                airportWeather.add(combine(airport, weatherReport));
            }

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

        AirportWeather.Current airportWeatherCurrent = current == null ? null : new AirportWeather.Current(
            current.getTempC() != null ? Conversions.celsiusToFahrenheit(current.getTempC()) : null,
            current.getRelativeHumidity(),
            maxCloudCoverage(current.getCloudLayers()),
            current.getVisibility().getDistanceSm(),
            new AirportWeather.CurrentWind(
                Conversions.knotsToMph(current.getWind().getSpeedKts()),
                Conversions.degreesToCardinal(current.getWind().getDirection())
            )
        );

        WeatherReport.Forecast forecast = weatherReport.getReport().getForecast();
        List<AirportWeather.Forecast> forecastWeather = null;

        if (forecast != null) {

            List<WeatherReport.ForecastConditions> forecastConditions;
            if (forecast.getConditions().size() > 1) {
                forecastConditions = forecast.getConditions().subList(
                    1,
                    Math.min(3, forecast.getConditions().size())
                );
            } else {
                forecastConditions = Collections.emptyList();
            }

            forecastWeather = forecastConditions.stream()
                .map(weather -> new AirportWeather.Forecast(
                    Duration.between(
                        forecast.getPeriod().getDateStart(),
                        weather.getPeriod().getDateStart()
                    ),
                    weather.getTempC() != null ? Conversions.celsiusToFahrenheit(weather.getTempC()) : null,
                    new AirportWeather.ForecastWind(
                        Conversions.knotsToMph(weather.getWind().getSpeedKts()),
                        weather.getWind().getDirection()
                    )
                ))
                .collect(Collectors.toList());

        }

        return new AirportWeather(
            airport.getIcao(),
            airport.getName(),
            runways,
            airport.getLatitude(),
            airport.getLongitude(),
            new AirportWeather.Weather(
                airportWeatherCurrent,
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
