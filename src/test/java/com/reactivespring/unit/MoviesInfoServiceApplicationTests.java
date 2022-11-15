package com.reactivespring.unit;

import com.reactivespring.controller.FluxAndMonoController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.List;
import java.util.Objects;

@WebFluxTest(controllers = FluxAndMonoController.class)
@AutoConfigureWebTestClient
class MoviesInfoServiceApplicationTests {

	@Autowired
	WebTestClient webTestClient;


	@Test
	void flux(){
		webTestClient.get()
				.uri("/flux").exchange()
				.expectStatus().is2xxSuccessful()
				.expectBodyList(Integer.class).hasSize(3);
	}

	@Test
	void flux_approach2(){
		Flux<Integer> flux = webTestClient.get()
				.uri("/flux").exchange()
				.expectStatus().is2xxSuccessful()
				.returnResult(Integer.class)
				.getResponseBody();

		StepVerifier.create(flux).expectNext(1,2,3).verifyComplete();
	}

	@Test
	void flux_approach3(){
		webTestClient.get()
				.uri("/flux").exchange()
				.expectStatus().is2xxSuccessful()
				.expectBodyList(Integer.class)
						.consumeWith( listEntityExchangeResult -> {
						var responseBody = listEntityExchangeResult.getResponseBody();
						assert (Objects.requireNonNull(responseBody).size() == 3);
		});
	}

	@Test
	void stream(){
		var flux = webTestClient.get()
				.uri("/stream").exchange()
				.expectStatus().is2xxSuccessful()
				.returnResult(Long.class)
				.getResponseBody();

		StepVerifier
				.create(flux)
				.expectNext(0L,1L,2L,3L)
				.thenCancel().verify();
	}

	@Test
	void mono(){
		var mono = webTestClient.get()
				.uri("/mono").exchange()
				.expectStatus().is2xxSuccessful()
				.returnResult(String.class)
				.getResponseBody();

		StepVerifier
				.create(mono)
				.expectNext("hello... World..!")
				.verifyComplete();
	}

	@Test
	void contextLoads() {
	}

}
