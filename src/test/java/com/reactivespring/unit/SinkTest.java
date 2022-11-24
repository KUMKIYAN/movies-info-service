package com.reactivespring.unit;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import java.util.concurrent.Flow;

public class SinkTest {

    @Test
    void sink(){

        // All old and new subscriber will get data.

        Sinks.Many<Integer> replaySink = Sinks.many().replay().all();

        replaySink.emitNext(1, Sinks.EmitFailureHandler.FAIL_FAST);
        replaySink.emitNext(2, Sinks.EmitFailureHandler.FAIL_FAST);
        replaySink.emitNext(3, Sinks.EmitFailureHandler.FAIL_FAST);

        Flux<Integer> integerFlux = replaySink.asFlux();
        integerFlux.subscribe((i) -> {
            System.out.println("Subscriber 1 :" + i);
        });
        integerFlux.subscribe((i) -> {
            System.out.println("Subscriber 2 :" + i);
        });
        integerFlux.subscribe((i) -> {
            System.out.println("Subscriber 3 :" + i);
        });

        replaySink.emitNext(4, Sinks.EmitFailureHandler.FAIL_FAST);

        integerFlux.subscribe((i) -> {
            System.out.println("Subscriber 4 :" + i);
        });

    }

    @Test
    void sinks_multicast(){

        // old subscriber will get all the data
        // new subscriber will get only new data that is published afterwards
        // Subscriber 1 :1
        // Subscriber 1 :2
        // Subscriber 1 :3
        // Subscriber 2 :3

        Sinks.Many<Integer> multiCast = Sinks.many().multicast().onBackpressureBuffer();

        multiCast.emitNext(1, Sinks.EmitFailureHandler.FAIL_FAST);
        multiCast.emitNext(2, Sinks.EmitFailureHandler.FAIL_FAST);

        Flux<Integer> integerFlux1 = multiCast.asFlux();

        integerFlux1.subscribe((i) -> {
            System.out.println("Subscriber 1 :" + i);
        });

        Flux<Integer> integerFlux2 = multiCast.asFlux();

        integerFlux2.subscribe((i) -> {
            System.out.println("Subscriber 2 :" + i);
        });

        multiCast.emitNext(3, Sinks.EmitFailureHandler.FAIL_FAST);
    }

    @Test
    void sinks_unicast(){

        // Caused by: java.lang.IllegalStateException:
        // UnicastProcessor allows only a single Subscriber
        // the fist subscriber will keep running even after that error.

        Sinks.Many<Integer> uniCast = Sinks.many().unicast().onBackpressureBuffer();

        uniCast.emitNext(1, Sinks.EmitFailureHandler.FAIL_FAST);
        uniCast.emitNext(2, Sinks.EmitFailureHandler.FAIL_FAST);

        Flux<Integer> integerFlux1 = uniCast.asFlux();

        integerFlux1.subscribe((i) -> {
            System.out.println("Subscriber 1 :" + i);
        });

        Flux<Integer> integerFlux2 = uniCast.asFlux();

        integerFlux2.subscribe((i) -> {
            System.out.println("Subscriber 2 :" + i);
        });

        uniCast.emitNext(3, Sinks.EmitFailureHandler.FAIL_FAST);
        uniCast.emitNext(4, Sinks.EmitFailureHandler.FAIL_FAST);
    }
}
