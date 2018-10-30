package net.jklimonda.model.tmdb;


import java.util.List;

public class TMDBCredits extends TMDBResponse {

    private int id;

    List<TMDBCredit> crew;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<TMDBCredit> getCrew() {
        return crew;
    }

    public void setCrew(List<TMDBCredit> crew) {
        this.crew = crew;
    }
}
