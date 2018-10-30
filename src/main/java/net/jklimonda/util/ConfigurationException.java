package net.jklimonda.util;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
public class ConfigurationException extends RuntimeException{
    public ConfigurationException() {}
    public ConfigurationException(String message) {
        super(message);
    }
}
