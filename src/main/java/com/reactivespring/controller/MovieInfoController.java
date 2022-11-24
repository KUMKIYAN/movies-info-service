package com.reactivespring.controller;

import com.reactivespring.domain.MovieInfo;
import com.reactivespring.exceptions.MovieInfoNotFoundException;
import com.reactivespring.service.MovieInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

import javax.validation.Valid;

@RestController
@RequestMapping("/v1")
@Slf4j
public class MovieInfoController {
    private MovieInfoService movieInfoService;

    Sinks.Many<MovieInfo> movieInfoSink = Sinks.many().replay().all();

    public MovieInfoController(MovieInfoService movieInfoService) {
        this.movieInfoService = movieInfoService;
    }

    @PostMapping("/movieinfos")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<MovieInfo> addMovieInfo(@RequestBody @Valid MovieInfo movieInfo){
        return movieInfoService.addMovieInfo(movieInfo)
                .doOnNext(saveInfo -> movieInfoSink.tryEmitNext(saveInfo));
    }

    //    @GetMapping("/movieinfos")
    //    public Flux<MovieInfo> getAllMovieInfos(){
    //        return movieInfoService.getAllMovieInfos().log();
    //    }

    // This will work as streaming. Server sent event work as real time live data.
    // Like uber car location or cricket score.

    @GetMapping(value = "/movieinfos/stream", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<MovieInfo> streamMovieInfos(){
        return movieInfoSink.asFlux();
    }

    @GetMapping("/movieinfos")
    public Flux<MovieInfo> getAllMovieInfos(@RequestParam(value = "year", required = false) Integer year){
        log.info( "year is {} ", year);
        if(year != null){
            return movieInfoService.getAllMovieInfosByYear(year);
        }
            return movieInfoService.getAllMovieInfos().log();
    }

    @GetMapping("/movieinfos/movieName")
    public Mono<MovieInfo> getMovieInfosByName(@RequestParam(value = "name", required = false) String name){
        log.info( "Name is {} ", name);
        return movieInfoService.getMovieInfoByName(name).log();
    }

    @GetMapping("/movieinfos/{id}")
    public Mono<ResponseEntity<MovieInfo>> getMovieById(@PathVariable String id){
        return movieInfoService.getMovieInfoById(id)
                .map(movieInfo -> ResponseEntity.ok().body(movieInfo))
                .switchIfEmpty(Mono.just(ResponseEntity.notFound().build()))
                .log();
    }

    @PutMapping("/movieinfos/{id}")
    public Mono<ResponseEntity<MovieInfo>> updateMovieById(@RequestBody MovieInfo updatedMovieInfo, @PathVariable String id){
        return movieInfoService.updateMovieInfoById(updatedMovieInfo, id)
                .map(ResponseEntity.ok()::body)
                .switchIfEmpty(Mono.error(new MovieInfoNotFoundException("MovieInfo Not Found")));
                //.switchIfEmpty(Mono.just(ResponseEntity.notFound().build())).log();
    }

    @DeleteMapping("/movieinfos/{id}")
    public Mono<Void> deleteMovieById(@PathVariable String id){
        return movieInfoService.deleteMovieById(id);
    }

}
