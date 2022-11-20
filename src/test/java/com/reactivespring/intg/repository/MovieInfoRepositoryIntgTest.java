package com.reactivespring.intg.repository;

import com.reactivespring.domain.MovieInfo;
import com.reactivespring.repository.MovieInfoRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.ActiveProfiles;
import reactor.test.StepVerifier;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataMongoTest
@ActiveProfiles("test")
public class MovieInfoRepositoryIntgTest {
    @Autowired
    MovieInfoRepository movieInfoRepository;

    @BeforeEach
    void setUp(){

        var movieInfos = List.of(
                new MovieInfo(null, "okkadu", 2003, List.of("mahesh", "boomika"), LocalDate.parse("2003-08-08")),
                new MovieInfo(null, "kushi", 2001, List.of("pavan", "boomika"), LocalDate.parse("2002-11-10")),
                new MovieInfo(null, "simhadhri", 2004, List.of("mahesh", "boomika"), LocalDate.parse("2004-12-01")),
                new MovieInfo("xyz", "shiva mani", 2002, List.of("nag", "amala"), LocalDate.parse("2005-06-01"))
                );

        movieInfoRepository.saveAll(movieInfos).blockLast();
    }

    @AfterEach
    void tearDown(){
        movieInfoRepository.deleteAll().block();
    }


    @Test
    void findAll(){
        var moviesInfoFlux = movieInfoRepository.findAll().log();
        StepVerifier.create(moviesInfoFlux).expectNextCount(4).verifyComplete();
    }

    @Test
    void findById(){
        var moviesInfoMono = movieInfoRepository.findById("xyz").log();
        StepVerifier.create(moviesInfoMono).assertNext(movieInfo -> assertEquals ("shiva mani", movieInfo.getName())).verifyComplete();
    }

    @Test
    void saveMovieInfo(){
        var movieInfo = new MovieInfo(null, "nijam", 2005 , List.of("nikil", "sadha"), LocalDate.parse("2005-10-22"));
        var movieInfoMono = movieInfoRepository.save(movieInfo).log();
        StepVerifier.create(movieInfoMono).assertNext(movieInfo1 -> {
            assertEquals("nijam", movieInfo1.getName());
            assertNotNull(movieInfo1.getMovieInfoId());
        }).verifyComplete();
    }

    @Test
    void updateMovieInfo(){
        var movieInfo = movieInfoRepository.findById("xyz").block();
        movieInfo.setYear(2004);
        var movieInfoMono = movieInfoRepository.save(movieInfo).log();
        StepVerifier.create(movieInfoMono).assertNext(movieInfo1 -> {
            assertEquals("shiva mani", movieInfo1.getName());
            assertEquals("2004", movieInfo1.getYear().toString());
            assertNotNull(movieInfo1.getMovieInfoId());
        }).verifyComplete();
    }

    @Test
    void deleteMovieInfo(){
        movieInfoRepository.deleteById("xyz").block();
        var movieInfo =  movieInfoRepository.findAll().log();
        StepVerifier.create(movieInfo).expectNextCount(3).verifyComplete();
    }

    @Test
    void findByYear(){
        var movieInfoFlux = movieInfoRepository.findByYear(2002);
        StepVerifier.create(movieInfoFlux).expectNextCount(1).verifyComplete();
    }

    @Test
    void findByMovieName(){
        var movieInfoFlux = movieInfoRepository.findByName("simhadhri");
        StepVerifier.create(movieInfoFlux).expectNextCount(1).verifyComplete();
    }






}
