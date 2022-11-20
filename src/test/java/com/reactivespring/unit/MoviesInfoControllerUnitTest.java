package com.reactivespring.unit;


import com.reactivespring.controller.MovieInfoController;
import com.reactivespring.domain.MovieInfo;
import com.reactivespring.service.MovieInfoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static com.reactivespring.intg.controller.MoviesInfoControllerIntgTest.MOVIE_INFO_URL;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.when;

@WebFluxTest(controllers = MovieInfoController.class)
@AutoConfigureWebTestClient
public class MoviesInfoControllerUnitTest {

    @MockBean
    private MovieInfoService movieInfoServiceMock;

    @Autowired
    private WebTestClient webTestClient;


    @Test
    void getAllMoviesInfo(){
        var movieInfos = List.of(
                new MovieInfo(null, "okkadu", 2003, List.of("mahesh", "boomika"), LocalDate.parse("2003-08-08")),
                new MovieInfo(null, "kushi", 2001, List.of("pavan", "boomika"), LocalDate.parse("2002-11-10")),
                new MovieInfo(null, "simhadhri", 2004, List.of("mahesh", "boomika"), LocalDate.parse("2004-12-01")),
                new MovieInfo("xyz", "shiva mani", 2002, List.of("nag", "amala"), LocalDate.parse("2005-06-01"))
        );

        when(movieInfoServiceMock.getAllMovieInfos()).thenReturn(Flux.fromIterable(movieInfos));

        webTestClient
                .get().uri(MOVIE_INFO_URL)
                .exchange()
                .expectStatus().is2xxSuccessful().expectBodyList(MovieInfo.class).hasSize(4);

    }

    @Test
    void getMovieInfoById() {
        var id = "xyz";

        when(movieInfoServiceMock.getMovieInfoById(isA(String.class)))
                .thenReturn(Mono.just(new MovieInfo("xyz", "Dark Knight Rises",
                        2012, List.of("Christian Bale", "Tom Hardy"), LocalDate.parse("2012-07-20"))));

        webTestClient
                .get()
                .uri(MOVIE_INFO_URL + "/{id}", id)
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBody(MovieInfo.class)
                .consumeWith(movieInfoEntityExchangeResult -> {
                    var movieInfo = movieInfoEntityExchangeResult.getResponseBody();
                    assert movieInfo != null;
                });
    }

    @Test
    void addNewMovieInfo() {

        var movieInfo = new MovieInfo(null, "Batman Begins",
                2005, List.of("Christian Bale", "Michael Cane"), LocalDate.parse("2005-06-15"));

        when(movieInfoServiceMock.addMovieInfo(isA(MovieInfo.class))).thenReturn(Mono.just(
                new MovieInfo("mockId", "Batman Begins",
                        2005, List.of("Christian Bale", "Michael Cane"), LocalDate.parse("2005-06-15"))));

        webTestClient
                .post()
                .uri(MOVIE_INFO_URL)
                .bodyValue(movieInfo)
                .exchange()
                .expectStatus()
                .isCreated()
                .expectBody(MovieInfo.class)
                .consumeWith(movieInfoEntityExchangeResult -> {
                    var savedMovieInfo = movieInfoEntityExchangeResult.getResponseBody();
                    assert Objects.requireNonNull(savedMovieInfo).getMovieInfoId() != null;

                });
    }

    @Test
    void updateMovieInfo() {

        var movieId = "xyz";

        var movieInfo = new MovieInfo(null, "Batman Begins",
                2006, List.of("Christian Bale", "Michael Cane"), LocalDate.parse("2005-06-15"));

        when(movieInfoServiceMock.updateMovieInfoById(isA(MovieInfo.class) , isA(String.class))).thenReturn(Mono.just(
                new MovieInfo(movieId, "Batman Begins",
                        2006, List.of("Christian Bale", "Michael Cane"), LocalDate.parse("2005-06-15"))));

        webTestClient
                .put()
                .uri(MOVIE_INFO_URL+"/{id}" , movieId)
                .bodyValue(movieInfo)
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBody(MovieInfo.class)
                .consumeWith(movieInfoEntityExchangeResult -> {
                    var updateMovieInfo = movieInfoEntityExchangeResult.getResponseBody();
                    assert updateMovieInfo != null;
                    assert updateMovieInfo.getYear().equals(2006);

                });
    }

    @Test
    void addNewMovieInfoNegativeYearNameTest() {

        var movieInfo = new MovieInfo(null, null,
                -2005, List.of("Christian Bale", "Michael Cane"), LocalDate.parse("2005-06-15"));

        webTestClient
                .post()
                .uri(MOVIE_INFO_URL)
                .bodyValue(movieInfo)
                .exchange()
                .expectStatus()
                .isBadRequest()
                .expectBody(String.class)
                .consumeWith(movieInfoEntityExchangeResult -> {
                    var response = movieInfoEntityExchangeResult.getResponseBody();
                    System.out.println("RESPONSE: " + response);
                    var expectedResponse = "MovieInfo.name must be present,MovieInfo.year must be Positive value";
                    assertEquals(expectedResponse, response);

                });
    }
        @Test
        void addNewMovieInfoNegativeYearNameAndCastNullTest() {

            var movieInfo = new MovieInfo(null, null,
                    -2005, Arrays.asList(""), LocalDate.parse("2005-06-15"));

            webTestClient
                    .post()
                    .uri(MOVIE_INFO_URL)
                    .bodyValue(movieInfo)
                    .exchange()
                    .expectStatus()
                    .isBadRequest()
                    .expectBody(String.class)
                    .consumeWith(movieInfoEntityExchangeResult -> {
                        var response = movieInfoEntityExchangeResult.getResponseBody();
                        System.out.println("RESPONSE: " + response);
                        var expectedResponse = "MovieInfo.cast must be present,MovieInfo.name must be present,MovieInfo.year must be Positive value";
                        assertEquals(expectedResponse, response);

                    });
    }

    @Test
    void updateMovieInfo_NotFound() {

        var movieId = "xyz1";

        var movieInfo = new MovieInfo(null, "Batman Begins",
                2006, List.of("Christian Bale", "Michael Cane"), LocalDate.parse("2005-06-15"));

        when(movieInfoServiceMock.updateMovieInfoById(isA(MovieInfo.class) , isA(String.class)))
                .thenReturn(Mono.empty());

        webTestClient
                .put()
                .uri(MOVIE_INFO_URL+"/{id}" , movieId)
                .bodyValue(movieInfo)
                .exchange()
                .expectStatus()
                .isNotFound();
    }

    @Test
    void getMovieInfoById_NotFound() {

        var movieId = "xyz1";

        when(movieInfoServiceMock.getMovieInfoById(isA(String.class)))
                .thenReturn(Mono.empty());
        webTestClient
                .get()
                .uri(MOVIE_INFO_URL+"/{id}" , movieId)
                .exchange()
                .expectStatus()
                .isNotFound();
    }

}
