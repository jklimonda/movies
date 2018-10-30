package net.jklimonda.service;

import net.jklimonda.model.Movie;
import net.jklimonda.util.NotFoundException;

import javax.naming.ConfigurationException;
import java.util.List;

public interface IService {
    public List<Movie> findMovies(String query) throws ConfigurationException, NotFoundException;
}
