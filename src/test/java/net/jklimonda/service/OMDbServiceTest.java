package net.jklimonda.service;

import net.jklimonda.model.Movie;
import net.jklimonda.model.openmdb.OMDBItem;
import net.jklimonda.model.openmdb.OMDBMovie;
import net.jklimonda.model.openmdb.OMDBSearch;
import net.jklimonda.util.ConfigurationException;
import net.jklimonda.util.NotFoundException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class OMDbServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    OMDbService service;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void testApiNotConfiguredException() throws Exception{
        service.setApiKey(null);
        thrown.expect(ConfigurationException.class);
        thrown.expectMessage("API Key not configured.");
        service.findMovies("a movie");
    }

    @Test
    public void testResultsFound() throws ConfigurationException, NotFoundException {
        String detailsUrl = "/test/?apikey={apiKey}&i={imdbId}";
        String searchUrl = "/test/?apikey={apiKey}?s={query}&page={page}";

        String apiKey = "akey";
        String query = "beetle";
        service.setApiKey(apiKey);

        service.setDetailsUrl(detailsUrl);
        service.setSearchUrl(searchUrl);

        OMDBSearch noResults = new OMDBSearch();
        noResults.setResponse("False");
        noResults.setError("Movie not found!");

        OMDBSearch search = new OMDBSearch();

        search.setTotalResults(2);

        search.setSearch(new ArrayList<>());

        OMDBItem beetlejuice = new OMDBItem();

        String bjImdbId = "tt123456";
        beetlejuice.setTitle("Beetlejuice");
        beetlejuice.setImdbId(bjImdbId);

        OMDBItem theBeetle = new OMDBItem();
        String tbImdbId = "ff242323e";
        theBeetle.setImdbId(tbImdbId);
        theBeetle.setTitle("The Beetle");

        search.getSearch().add(beetlejuice);
        search.getSearch().add(theBeetle);

        OMDBMovie bjMovie = new OMDBMovie();

        bjMovie.setTitle("Beetlejuice");
        bjMovie.setDirector("Tim Burton");
        bjMovie.setYear("1988");

        OMDBMovie tbMovie = new OMDBMovie();

        tbMovie.setYear("2008");
        tbMovie.setDirector("Yishai Orian");
        tbMovie.setTitle("The Beetle");

        Mockito.when(restTemplate.getForObject(searchUrl, OMDBSearch.class, apiKey, query, 1)).thenReturn(search);
        Mockito.when(restTemplate.getForObject(searchUrl, OMDBSearch.class, apiKey, query, 2)).thenReturn(noResults);
        Mockito.when(restTemplate.getForObject(detailsUrl, OMDBMovie.class, apiKey, beetlejuice.getImdbId())).thenReturn(bjMovie);
        Mockito.when(restTemplate.getForObject(detailsUrl, OMDBMovie.class, apiKey, theBeetle.getImdbId())).thenReturn(tbMovie);

        List<Movie> movies = movies = service.findMovies(query);


        assertNotNull(movies);
        assertEquals(2, movies.size());
        Optional<Movie> bj = movies.stream().filter(e -> e.getTitle().equals(beetlejuice.getTitle())).findAny();
        assertTrue(bj.isPresent());
        Optional<Movie> tb = movies.stream().filter(e -> e.getTitle().equals(theBeetle.getTitle())).findAny();
        assertTrue(tb.isPresent());

        assertEquals(bjMovie.getTitle(), bj.get().getTitle());
        assertEquals(bjMovie.getDirector(), bj.get().getDirector());
        assertEquals(bjMovie.getYear(), bj.get().getYear());

        assertEquals(tbMovie.getTitle(), tb.get().getTitle());
        assertEquals(tbMovie.getDirector(), tb.get().getDirector());
        assertEquals(tbMovie.getYear(), tb.get().getYear());
    }

    @Test
    public void testResultsNotFoundException() throws ConfigurationException, NotFoundException {
        String detailsUrl = "/test/?apikey={apiKey}&i={imdbId}";
        String searchUrl = "/test/?apikey={apiKey}?s={query}&page={page}";

        String apiKey = "akey";
        String query = "ev343243wsa";
        service.setApiKey(apiKey);

        service.setDetailsUrl(detailsUrl);
        service.setSearchUrl(searchUrl);

        OMDBSearch noResults = new OMDBSearch();
        noResults.setResponse("False");
        noResults.setError("Movie not found!");
        Mockito.when(restTemplate.getForObject(searchUrl, OMDBSearch.class, apiKey, query, 1)).thenReturn(noResults);
        thrown.expect(NotFoundException.class);
        thrown.expectMessage("No movies found with matching title.");
        service.findMovies(query);

    }

}
