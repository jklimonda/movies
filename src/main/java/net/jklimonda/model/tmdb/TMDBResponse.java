package net.jklimonda.model.tmdb;

import com.fasterxml.jackson.annotation.JsonProperty;

public abstract class TMDBResponse {

    @JsonProperty("status_message")
    private String statusMessage;

    @JsonProperty("status_code")
    private int statusCode;

    public String getStatusMessage() {
        return statusMessage;
    }

    public void setStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }
}
