package com.foreflight.airport.weather.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ConversionsTest {

    @Test
    public void basicKnotsToMph1() {
        Assertions.assertEquals(294.87012 ,Conversions.knotsToMph(256.24), 0.00001);
    }

    @Test
    public void basicKnotsToMph2() {
        Assertions.assertEquals(3.45227 ,Conversions.knotsToMph(3.0), 0.00001);
    }

    @Test
    public void basicCelsiusToFahrenheitFreezing() {
        Assertions.assertEquals(32.0 ,Conversions.celsiusToFahrenheit(0.0), 0.001);
    }

    @Test
    public void basicCelsiusToFahrenheitBoiling() {
        Assertions.assertEquals(212.0 ,Conversions.celsiusToFahrenheit(100.0), 0.001);
    }

    @Test
    public void basicCelsiusToFahrenheitNegative() {
        Assertions.assertEquals(-148.0 ,Conversions.celsiusToFahrenheit(-100.0), 0.001);
    }

    @Test
    public void degreesToCardinalNorth() {
        Assertions.assertEquals("N", Conversions.degreesToCardinal(0.0));
        Assertions.assertEquals("N", Conversions.degreesToCardinal(360.0));
    }

    @Test
    public void degreesToCardinalSSWTurningPoint() {
        Assertions.assertEquals("SSW", Conversions.degreesToCardinal(191.25));
        Assertions.assertEquals("S", Conversions.degreesToCardinal(191.24));
    }

    @Test
    public void degreesToCardinalEast() {
        Assertions.assertEquals("E", Conversions.degreesToCardinal(90));
    }

    @Test
    public void degreesToCardinalWest() {
        Assertions.assertEquals("W", Conversions.degreesToCardinal(270));
    }

}
