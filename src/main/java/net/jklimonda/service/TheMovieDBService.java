package net.jklimonda.service;

import net.jklimonda.model.Movie;
import net.jklimonda.model.tmdb.TMDBCredit;
import net.jklimonda.model.tmdb.TMDBCredits;
import net.jklimonda.model.tmdb.TMDBMovie;
import net.jklimonda.model.tmdb.TMDBMovies;
import net.jklimonda.util.ConfigurationException;
import net.jklimonda.util.NotFoundException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class TheMovieDBService implements IService {

    @Value("${theMovieDB.apiKey}")
    private String apiKey;

    @Value ("${theMovieDB.search.url}")
    private String searchUrl;

    @Value("${theMovieDB.credits.url}")
    private String creditsUrl;

    @Value("${theMovieDB.credit.director.department}")
    private String dirDepartment;

    @Value("${theMovieDB.credit.director.job}")
    private String dirJob;

    @Autowired
    RestTemplate restTemplate;

    public List<Movie> findMovies(String query) throws ConfigurationException, NotFoundException {
        if(StringUtils.isBlank(apiKey)){
            throw new ConfigurationException("API Key not configured.");
        }

        TMDBMovies result = restTemplate.getForObject(searchUrl, TMDBMovies.class, apiKey, query, 1);

        if(result != null && result.getResults() != null && result.getResults().size() > 0 && result.getTotalResults() > 0) {
           List <Movie> movies = new ArrayList<>();

            //!!!!!!!! Because of free api usage I can't make more than 40 requests at a time,
            // this is resulting in exception from the service, thus only first page of search is returned.
           /*if(result.getTotalPages() > 1) {
              //TODO: add threads
                //because of connections limit
               for(int i = 2; i <= result.getTotalPages(); i++) {
                   TMDBMovies tempResult = restTemplate.getForObject(searchUrl, TMDBMovies.class, apiKey, query, 1);

                   if(tempResult != null && result.getResults() != null && result.getResults().size() > 0) {
                       result.getResults().addAll(tempResult.getResults());
                   }
               }
           }*/

           for(TMDBMovie item: result.getResults()) {
               Movie movie = new Movie();
               movie.setTitle(item.getTitle());
               if(item.getReleaseDate() != null) {
                   String year = Integer.toString(item.getReleaseDate().getYear());
                   movie.setYear(year);
               }
               //TODO: add threads
               TMDBCredits credits = restTemplate.getForObject(creditsUrl, TMDBCredits.class, item.getId(), apiKey);
               if(credits != null && credits.getCrew() != null && credits.getCrew().size() > 0) {
                   Optional<TMDBCredit> directorCredit = credits.getCrew().stream()
                           .filter(e -> e.getDepartment().equalsIgnoreCase(dirDepartment) && e.getJob().equalsIgnoreCase(dirJob))
                           .findAny();
                   if(directorCredit.isPresent()) {
                       movie.setDirector(directorCredit.get().getName());
                   }
               }
               movies.add(movie);
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

    private TMDBMovies getMovies(String query) {
        return null;
    }

    private TMDBCredit getDirector(int movieId) {
        return null;
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

    public String getCreditsUrl() {
        return creditsUrl;
    }

    public void setCreditsUrl(String creditsUrl) {
        this.creditsUrl = creditsUrl;
    }

    public String getDirDepartment() {
        return dirDepartment;
    }

    public void setDirDepartment(String dirDepartment) {
        this.dirDepartment = dirDepartment;
    }

    public String getDirJob() {
        return dirJob;
    }

    public void setDirJob(String dirJob) {
        this.dirJob = dirJob;
    }

    public RestTemplate getRestTemplate() {
        return restTemplate;
    }

    public void setRestTemplate(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }
}
