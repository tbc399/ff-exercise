package com.foreflight.airport.weather.web.dto.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class SingleDecimalPlaceSerializer extends JsonSerializer<Double> {

    @Override
    public void serialize(Double value, JsonGenerator generator, SerializerProvider provider) throws IOException {
        generator.writeNumber(BigDecimal.valueOf(value).setScale(1, RoundingMode.HALF_UP));
    }

}
