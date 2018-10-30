package net.jklimonda.service;

import net.jklimonda.model.Movie;
import net.jklimonda.model.openmdb.OMDBItem;
import net.jklimonda.model.openmdb.OMDBMovie;
import net.jklimonda.model.openmdb.OMDBSearch;
import net.jklimonda.util.ConfigurationException;
import net.jklimonda.util.NotFoundException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Service
public class OMDbService implements IService{

    @Value("${OMDb.apiKey}")
    private String apiKey;

    @Value("${OMDb.search.url}")
    private String searchUrl;

    @Value("${OMDb.details.url}")
    private String detailsUrl;

    @Autowired
    RestTemplate restTemplate;

    @Override
    public List<Movie> findMovies(String query) throws ConfigurationException, NotFoundException {
        if(StringUtils.isBlank(apiKey)){
            throw new ConfigurationException("API Key not configured.");
        }

        OMDBSearch result = restTemplate.getForObject(searchUrl, OMDBSearch.class, apiKey, query, 1);

        if(result != null && result.getResponse().equals("True") && result.getTotalResults() > 0) {
            int page = 2;

            OMDBSearch tempSearch = restTemplate.getForObject(searchUrl, OMDBSearch.class, apiKey, query, page);
            while (result.getSearch().size() < result.getTotalResults()) {
                result.getSearch().addAll(tempSearch.getSearch());
                tempSearch = restTemplate.getForObject(searchUrl, OMDBSearch.class, apiKey, query, ++page);
            }

            List<Movie> movies = new ArrayList<>();

            //TODO: thread it
            for (OMDBItem item : result.getSearch()) {
                OMDBMovie oMovie = restTemplate.getForObject(detailsUrl, OMDBMovie.class, apiKey, item.getImdbId());
                if (oMovie != null && oMovie.getResponse().equals("True") && oMovie.getError() == null) {
                    Movie movie = new Movie();
                    movie.setDirector(oMovie.getDirector());
                    movie.setYear(oMovie.getYear());
                    movie.setTitle(oMovie.getTitle());
                    movies.add(movie);
                }
            }
            if(movies.size() > 0) {
                return movies;
            } else {
                throw new NotFoundException("No movies found with matching title.");
            }
        } else {
            throw new NotFoundException("No movies found with matching title.");
        }
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getSearchUrl() {
        return searchUrl;
    }

    public void setSearchUrl(String searchUrl) {
        this.searchUrl = searchUrl;
    }

    public String getDetailsUrl() {
        return detailsUrl;
    }

    public void setDetailsUrl(String detailsUrl) {
        this.detailsUrl = detailsUrl;
    }

    public RestTemplate getRestTemplate() {
        return restTemplate;
    }

    public void setRestTemplate(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }
}
