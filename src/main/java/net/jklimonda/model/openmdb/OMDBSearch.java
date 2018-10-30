package net.jklimonda.model.openmdb;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
@JsonIgnoreProperties(ignoreUnknown = true)
public class OMDBSearch extends OMDBResponse {

    @JsonProperty("Search")
    private List<OMDBItem> search;

    private int totalResults = 0;

    public List<OMDBItem> getSearch() {
        return search;
    }

    public void setSearch(List<OMDBItem> search) {
        this.search = search;
    }

    public int getTotalResults() {
        return totalResults;
    }

    public void setTotalResults(int totalResults) {
        this.totalResults = totalResults;
    }
}
