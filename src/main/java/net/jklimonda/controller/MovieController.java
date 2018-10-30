package net.jklimonda.controller;

import net.jklimonda.model.Movie;
import net.jklimonda.service.IService;
import net.jklimonda.service.OMDbService;
import net.jklimonda.service.TheMovieDBService;
import net.jklimonda.util.NoSuchServiceException;
import net.jklimonda.util.NotFoundException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.naming.ConfigurationException;
import java.util.List;

@RestController
@RequestMapping("/")
public class MovieController {

    @Autowired
    OMDbService omdbService;

    @Autowired
    TheMovieDBService tmdbService;

    @RequestMapping("/movies/{title}")
    @ResponseBody
    public List<Movie> getMovies(@PathVariable("title") String title, @RequestParam(value="apiName") String apiName)
            throws NoSuchServiceException, NotFoundException, ConfigurationException {
       if(StringUtils.isBlank(apiName)) {
            throw new NoSuchServiceException("apiName request parameter should not be empty.");
        }

        IService service;

        switch(apiName) {
            case "theMovieDB":
                service = tmdbService;
                break;
            case "openMovieDB":
                service = omdbService;
                break;
            default:
                throw new NoSuchServiceException("apiName request parameter must be one of values [theMovieDB, openMovieDB].");
        }
        return service.findMovies(title);
    }
}
