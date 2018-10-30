package net.jklimonda.util;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;


public class LocalDateDeserializer extends StdDeserializer<LocalDate> {

    public LocalDateDeserializer(){
        super(LocalDate.class);
    }

    private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Override
    public LocalDate deserialize(JsonParser parser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
        try {
            return LocalDate.parse(parser.readValueAs(String.class), this.formatter);
        } catch (DateTimeParseException ex) {
            return null;
        }

    }
}
