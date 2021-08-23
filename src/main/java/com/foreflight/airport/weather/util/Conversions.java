package com.foreflight.airport.weather.util;

public class Conversions {

    private static final String[] directions = {
        "N","NNE","NE","ENE","E","ESE", "SE", "SSE","S","SSW","SW","WSW","W","WNW","NW","NNW"
    };

    public static Double knotsToMph(Double knots) {
        return (knots * 6076.0) / 5280.0;
    }

    public static String degreesToCardinal(Integer degrees) {
        int value = (int) Math.floor((degrees / 22.5) + .5);
        return directions[Math.floorMod(value, 16)];
    }

    public static Double celsiusToFahrenheit(Double celsius) {
        return (celsius * 1.8) + 32;
    }

}
