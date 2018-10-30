package net.jklimonda.model.tmdb;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import net.jklimonda.util.LocalDateDeserializer;
import java.time.LocalDate;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TMDBMovie {
    private int id;
    private String title;

    @JsonProperty("release_date")
    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonInclude(value= JsonInclude.Include.NON_EMPTY, content= JsonInclude.Include.NON_NULL)
    private LocalDate releaseDate;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public LocalDate getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(LocalDate releaseDate) {
        this.releaseDate = releaseDate;
    }
}
