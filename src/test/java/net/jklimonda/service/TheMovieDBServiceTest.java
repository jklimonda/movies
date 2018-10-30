package net.jklimonda.service;

import net.jklimonda.model.*;
import net.jklimonda.model.tmdb.TMDBCredit;
import net.jklimonda.model.tmdb.TMDBCredits;
import net.jklimonda.model.tmdb.TMDBMovie;
import net.jklimonda.model.tmdb.TMDBMovies;
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
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(MockitoJUnitRunner.class)
public class TheMovieDBServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private TheMovieDBService service;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");


    @Test
    public void testApiNotConfiguredException() throws ConfigurationException, NotFoundException  {
        service.setApiKey(null);
        thrown.expect(ConfigurationException.class);
        thrown.expectMessage("API Key not configured.");
        service.findMovies("a movie");
    }

    @Test
    public void testResultsFound() throws ConfigurationException, NotFoundException  {
        String credits = "/test/{movieId}/credits?api_key={apiKey}";
        String movies = "/test/movies?api_key={apiKey}&query={query}&page={page}";
        String apiKey = "akey";
        String query = "beetle";
        service.setApiKey(apiKey);
        service.setCreditsUrl(credits);
        service.setSearchUrl(movies);
        service.setDirDepartment("Directing");
        service.setDirJob("Director");

        TMDBMovies tmdbMovies = new TMDBMovies();
        tmdbMovies.setTotalPages(1);
        tmdbMovies.setTotalResults(2);

        TMDBMovie beetleJuice = new TMDBMovie();
        TMDBMovie theBeetle = new TMDBMovie();
        beetleJuice.setId(1);
        beetleJuice.setReleaseDate(LocalDate.parse("1988-02-29", formatter));
        beetleJuice.setTitle("Beetlejuice");

        theBeetle.setId(2);
        theBeetle.setReleaseDate(LocalDate.parse("2008-04-24", formatter));
        theBeetle.setTitle("The Beetle");

        tmdbMovies.setResults(new ArrayList<>());
        tmdbMovies.getResults().add(beetleJuice);
        tmdbMovies.getResults().add(theBeetle);

        TMDBCredits bjCredits = new TMDBCredits();
        TMDBCredits tbCredits = new TMDBCredits();

        TMDBCredit bjDirector = new TMDBCredit();

        TMDBCredit tbDirector = new TMDBCredit();

        tbDirector.setName("Ally Mc Beal");
        tbDirector.setDepartment("Directing");
        tbDirector.setJob("Director");

        bjDirector.setName("Mr Bean");
        bjDirector.setDepartment("Directing");
        bjDirector.setJob("Director");

        tbCredits.setCrew(new ArrayList<>());
        tbCredits.getCrew().add(tbDirector);

        bjCredits.setCrew(new ArrayList<>());
        bjCredits.getCrew().add(bjDirector);


        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);
        HttpEntity<?> entity = new HttpEntity<>(headers);


        Mockito.when(restTemplate.getForObject(movies, TMDBMovies.class, apiKey, query, 1)).thenReturn(tmdbMovies);
        Mockito.when(restTemplate.getForObject(credits, TMDBCredits.class, beetleJuice.getId(), apiKey)).thenReturn(bjCredits);
        Mockito.when(restTemplate.getForObject(credits, TMDBCredits.class, theBeetle.getId(), apiKey)).thenReturn(tbCredits);

        List<Movie> movieList = service.findMovies(query);

        assertNotNull(movieList);
        assertEquals(2, movieList.size());
        Optional<Movie> bj = movieList.stream().filter(e -> e.getTitle().equals(beetleJuice.getTitle())).findAny();
        assertTrue(bj.isPresent());
        Optional<Movie> tb = movieList.stream().filter(e -> e.getTitle().equals(theBeetle.getTitle())).findAny();
        assertTrue(tb.isPresent());

        assertEquals(bjDirector.getName(), bj.get().getDirector());
        assertEquals(tbDirector.getName(), tb.get().getDirector());
        assertEquals(beetleJuice.getReleaseDate().getYear(), bj.get().getYear());
        assertEquals(theBeetle.getReleaseDate().getYear(), tb.get().getYear());
    }

    @Test
    public void testResultsNotFoundException() throws ConfigurationException, NotFoundException {
        String credits = "/test/{movieId}/credits?api_key={apiKey}";
        String movies = "/test/movies?api_key={apiKey}&query={query}&page={page}";
        String apiKey = "akey";
        String query = "dgegwvaqu";
        service.setApiKey(apiKey);
        service.setCreditsUrl(credits);
        service.setSearchUrl(movies);
        service.setDirDepartment("Directing");
        service.setDirJob("Director");
        TMDBMovies tmdbMovies = new TMDBMovies();
        tmdbMovies.setTotalPages(1);
        tmdbMovies.setTotalResults(0);
        Mockito.when(restTemplate.getForObject(movies, TMDBMovies.class, apiKey, query, 1)).thenReturn(tmdbMovies);
        thrown.expect(NotFoundException.class);
        thrown.expectMessage("No movies found with matching title.");
        service.findMovies(query);
    }
}