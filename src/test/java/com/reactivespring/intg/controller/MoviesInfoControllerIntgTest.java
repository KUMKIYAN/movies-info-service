package com.reactivespring.intg.controller;

import com.reactivespring.domain.MovieInfo;
import com.reactivespring.repository.MovieInfoRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureWebTestClient
public class MoviesInfoControllerIntgTest {

    @Autowired
    private MovieInfoRepository movieInfoRepository;

    @Autowired
    private WebTestClient webTestClient;

    public static String MOVIE_INFO_URL = "/v1/movieinfos";

    public static String MOVIE_NAME_INFO_URL = "/v1/movieinfos/movieName";

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
    void addMovieInfo(){
        var movieInfo = new MovieInfo(null, "nijam", 2005 , List.of("nikil", "sadha"), LocalDate.parse("2005-10-22"));
        webTestClient.post().uri(MOVIE_INFO_URL).bodyValue(movieInfo).exchange().expectStatus()
                .isCreated().expectBody(MovieInfo.class).consumeWith(movieInfoEntityExchangeResult -> {
                    var saveModeInfo = movieInfoEntityExchangeResult.getResponseBody();
                    assert saveModeInfo != null;
                    assert saveModeInfo.getMovieInfoId() != null;
                    assert saveModeInfo.getName().equalsIgnoreCase("nijam");
        });
    }

    @Test
    void getAllMovieInfos(){
        webTestClient.get().uri(MOVIE_INFO_URL).exchange()
                .expectStatus().is2xxSuccessful()
                .expectBodyList(MovieInfo.class).hasSize(4);
    }

    @Test
    void getMovieInfoById(){
        var id = "xyz";
        webTestClient.get().uri(MOVIE_INFO_URL+"/{id}", id).exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody().jsonPath("$.name").isEqualTo("shiva mani");
    }

    @Test
    void updateMovieInfo(){
        var id = "xyz";
        var updatedMovieInfo =
                new MovieInfo("null", "shiva mani", 2006, List.of("nag", "amala", "tabu"), LocalDate.parse("2006-07-18"));

        webTestClient.put().uri(MOVIE_INFO_URL+"/{id}", id).bodyValue(updatedMovieInfo).exchange().expectStatus()
                .is2xxSuccessful().expectBody(MovieInfo.class).consumeWith(movieInfoEntityExchangeResult -> {
                    var updatedModeInfo = movieInfoEntityExchangeResult.getResponseBody();
                    assert updatedModeInfo != null;
                    assert updatedModeInfo.getMovieInfoId() != null;
                    assert updatedModeInfo.getMovieInfoId().equalsIgnoreCase("xyz");
                    assertEquals(2006, updatedModeInfo.getYear());
                });
    }

    @Test
    void deleteMovieInfo(){
        var id = "xyz";
        webTestClient.delete().uri(MOVIE_INFO_URL+"/{id}", id).exchange().expectStatus().is2xxSuccessful();
        webTestClient.get().uri(MOVIE_INFO_URL).exchange()
                .expectStatus().is2xxSuccessful()
                .expectBodyList(MovieInfo.class).hasSize(3);
    }

    @Test
    void getAllMovieInfoByYear(){
        var year = "2003";

        var uri = UriComponentsBuilder.fromUriString(MOVIE_INFO_URL)
                        .queryParam("year", year)
                                .buildAndExpand().toUri();

       webTestClient.get()
               .uri(uri)
               .exchange()
               .expectStatus()
               .is2xxSuccessful()
               .expectBodyList(MovieInfo.class)
               .hasSize(1);
    }

    @Test
    void getMovieInfoByNamw(){
        var name = "okkadu";

        var uri = UriComponentsBuilder.fromUriString(MOVIE_NAME_INFO_URL)
                .queryParam("name", name)
                .buildAndExpand().toUri();

        webTestClient.get()
                .uri(uri)
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBodyList(MovieInfo.class)
                .hasSize(1);
    }
}
