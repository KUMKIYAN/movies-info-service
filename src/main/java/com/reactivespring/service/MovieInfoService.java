package com.reactivespring.service;

import com.reactivespring.domain.MovieInfo;
import com.reactivespring.repository.MovieInfoRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class MovieInfoService {

    private MovieInfoRepository movieInfoRepository;

    public MovieInfoService(MovieInfoRepository movieInfoRepository) {
        this.movieInfoRepository = movieInfoRepository;
    }

    public Mono<MovieInfo> addMovieInfo(MovieInfo movieInfo){
        return movieInfoRepository.save(movieInfo);
    }

    public Flux<MovieInfo> getAllMovieInfos() {
        return movieInfoRepository.findAll();
    }
    public Mono<MovieInfo> getMovieInfoById(String id) {
        return movieInfoRepository.findById(id).log();
    }

    public Mono<MovieInfo> updateMovieInfoById(MovieInfo updatedMovieInfo, String id) {
        return movieInfoRepository.findById(id).flatMap( movieInfo -> {
                movieInfo.setYear(updatedMovieInfo.getYear());
                movieInfo.setCast(updatedMovieInfo.getCast());
                movieInfo.setName(updatedMovieInfo.getName());
                movieInfo.setRelease_date(updatedMovieInfo.getRelease_date());
                movieInfo.setMovieInfoId(id);
                return movieInfoRepository.save(movieInfo).log();
        });


    }
    public Mono<Void> deleteMovieById(String id) {
        return movieInfoRepository.deleteById(id);
    }

    public Flux<MovieInfo> getAllMovieInfosByYear(Integer year) {
        return movieInfoRepository.findByYear(year);
    }

    public Mono<MovieInfo> getMovieInfoByName(String name) {
        return movieInfoRepository.findByName(name);
    }
}
