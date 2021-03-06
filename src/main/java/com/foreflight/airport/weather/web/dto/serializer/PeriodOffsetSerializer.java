package com.foreflight.airport.weather.web.dto.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.time.Duration;

public class PeriodOffsetSerializer extends JsonSerializer<Duration> {

    @Override
    public void serialize(Duration duration, JsonGenerator generator, SerializerProvider provider) throws IOException {
        long durationInMinutes = duration.toMinutes();
        generator.writeString(
            String.format("%02d:%02d", durationInMinutes / 60, durationInMinutes % 60)
        );
    }

}
