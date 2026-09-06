package com.vinsguru.netflux.movie.service;

import com.vinsguru.netflux.movie.dto.MovieDetails;
import com.vinsguru.netflux.movie.dto.MovieSearchRequest;
import com.vinsguru.netflux.movie.dto.MovieSummary;
import com.vinsguru.netflux.movie.exceptions.MovieNotFoundException;
import com.vinsguru.netflux.movie.mapper.MovieMapper;
import com.vinsguru.netflux.movie.repository.MovieRepository;
import com.vinsguru.netflux.movie.util.LimitResolver;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MovieService {

    private final MovieRepository movieRepository;

    public MovieService(MovieRepository movieRepository) {
        this.movieRepository = movieRepository;
    }

    public MovieDetails findById(Long id) {
        return movieRepository.findById(id)
                .map(MovieMapper::toDetails)
                .orElseThrow(() -> new MovieNotFoundException(id));
    }

    public List<MovieSummary> findByIds(List<Long> ids) {
        return movieRepository.findAllById(ids).stream()
                .map(MovieMapper::toSummary)
                .toList();
    }

    public List<MovieSummary> findPopular(Integer limit) {
        var pageable = PageRequest.of(0, LimitResolver.resolve(limit), Sort.by(Sort.Direction.DESC, "voteCount"));
        return movieRepository.findAll(pageable).stream()
                .map(MovieMapper::toSummary)
                .toList();
    }

    public List<MovieSummary> findLatest(Integer limit) {
        var pageable = PageRequest.of(0, LimitResolver.resolve(limit), Sort.by(Sort.Direction.DESC, "releaseDate"));
        return movieRepository.findAll(pageable).stream()
                .map(MovieMapper::toSummary)
                .toList();
    }

    public List<MovieSummary> search(MovieSearchRequest request) {
        var movies = movieRepository.search(
                request.title(),
                request.director(),
                request.genre(),
                LimitResolver.resolve(request.limit())
        );
        return movies.stream()
                .map(MovieMapper::toSummary)
                .toList();
    }

}
