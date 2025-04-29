package com.study.rpe;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.SignalType;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuple3;

import java.time.Duration;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ReactiveTutorial {

    private Mono<String> testMono() {
        return Mono.just("Reactive Java Programming").log();
    }

    private Flux<String> testFlux() {

        List<String> words = List.of("Java", "Reactive", "Programming", "WebFlux");

        return Flux.fromIterable(words);
    }

    private Flux<String> testMap() {

        Flux<String> flux = Flux.just("Java", "Reactive", "Programming", "WebFlux");

        return flux.map(s -> s.toUpperCase(Locale.ROOT));
    }

    // FlatMap é utilizado quando os dados de retorno necessitam de alguma operação,
    // por exemplo: cada dado faz uma consulta a um banco de dados
    private Flux<String> testFlatMap() {

        Flux<String> flux = Flux.just("Java", "Reactive", "Programming", "WebFlux");

        return flux.flatMap(s -> Mono.just(s.toUpperCase(Locale.ROOT)));
    }

    private Flux<String> testBasicSkip() {

        Flux<String> flux = Flux.just("Java", "Reactive", "Programming", "WebFlux")
                .delayElements(Duration.ofSeconds(1));

        return flux.skip(Duration.ofSeconds(2)).log();
    }

    private Flux<Integer> testComplexSkip() {

        Flux<Integer> flux = Flux.range(1, 20);

        return flux.skipUntil(integer -> integer == 10);
    }

    private Flux<Integer> testConcat() {

        Flux<Integer> flux1 = Flux.range(1, 20);
        Flux<Integer> flux2 = Flux.range(101, 20);
        Flux<Integer> flux3 = Flux.range(1001, 20);

        return Flux.concat(flux1, flux2, flux3);
    }

    private Flux<Integer> testMerge() {

        Flux<Integer> flux1 = Flux.range(1, 20).delayElements(Duration.ofMillis(500));
        Flux<Integer> flux2 = Flux.range(101, 20).delayElements(Duration.ofMillis(500));

        return Flux.merge(flux1, flux2);
    }

    private Flux<Tuple2<Integer, Integer>> testZip() {

        Flux<Integer> flux1 = Flux.range(1, 20).delayElements(Duration.ofMillis(500));
        Flux<Integer> flux2 = Flux.range(101, 20).delayElements(Duration.ofMillis(500));

        return Flux.zip(flux1, flux2);
    }

    private Flux<Tuple3<Integer, Integer, Integer>> testComplexZip() {

        Flux<Integer> flux1 = Flux.range(1, 20).delayElements(Duration.ofMillis(500));
        Flux<Integer> flux2 = Flux.range(101, 20).delayElements(Duration.ofMillis(500));
        Flux<Integer> flux3 = Flux.range(1001, 20).delayElements(Duration.ofMillis(500));

        return Flux.zip(flux1, flux2, flux3);
    }

    private Mono<List<Integer>> testCollect() {

        Flux<Integer> flux = Flux.range(1, 20).delayElements(Duration.ofMillis(1000));

        return flux.collectList();
    }

    private Flux<List<Integer>> testBuffer() {

        Flux<Integer> flux = Flux.range(1, 10).delayElements(Duration.ofMillis(1000));

        return flux.buffer(Duration.ofMillis(3_100));
    }

    private Mono<Map<Integer, Integer>> testMapCollection() {

        Flux<Integer> flux = Flux.range(1, 20);

        return flux.collectMap(integer -> integer, integer -> integer * integer);
    }

    private Flux<Integer> testDoFunctionOnEach() {

        Flux<Integer> flux = Flux.range(1, 10);

        return flux.doOnEach(signal -> {
            if (signal.getType() == SignalType.ON_COMPLETE) {
                System.out.println("Flux Done!");

            } else {
                System.out.println("Signal: " + signal.get());
            }
        });
    }

    private Flux<Integer> testDoFunctionOnComplete() {

        Flux<Integer> flux = Flux.range(1, 10);

        return flux.doOnComplete(() -> System.out.println("Flux Done!"));
    }

    private Flux<Integer> testDoFunctionOnNext() {

        Flux<Integer> flux = Flux.range(1, 10);

        return flux.doOnNext(integer -> System.out.println(integer));
    }

    private Flux<Integer> testDoFunctionOnSubscribe() {

        Flux<Integer> flux = Flux.range(1, 10);

        return flux.doOnSubscribe(subscription -> System.out.println("Subscribed!"));
    }

    private Flux<Integer> testDoFunctionOnCancel() {

        Flux<Integer> flux = Flux.range(1, 10).delayElements(Duration.ofSeconds(1));

        return flux.doOnCancel(() -> System.out.println("Cancelled!"));
    }

    private Flux<Integer> testDoFunctionOnErrorContinue() {

        Flux<Integer> flux = Flux.range(1, 10)
                .map(integer -> {
                    if (integer == 5) {
                        throw new RuntimeException("Unexpected Number");
                    }

                    return integer;
                });

        return flux.onErrorContinue((throwable, o) ->System.out.println("Error: " + o));
    }

    private Flux<Integer> testDoFunctionOnErrorReturn() {

        Flux<Integer> flux = Flux.range(1, 10)
                .map(integer -> {
                    if (integer == 5) {
                        throw new RuntimeException("Unexpected Number");
                    }

                    return integer;
                });

        return flux.onErrorReturn(-1);
    }

    private Flux<Integer> testDoFunctionOnErrorResume() {

        Flux<Integer> flux = Flux.range(1, 10)
                .map(integer -> {
                    if (integer == 5) {
                        throw new RuntimeException("Unexpected Number");
                    }

                    return integer;
                });

        Flux<Integer> specialErrorHandlerFlux = Flux.range(100, 5);

        return flux.onErrorResume(throwable -> specialErrorHandlerFlux);
    }

    private Flux<Integer> testDoFunctionOnErrorMap() {

        Flux<Integer> flux = Flux.range(1, 10)
                .map(integer -> {
                    if (integer == 5) {
                        throw new RuntimeException("Unexpected Number");
                    }

                    return integer;
                });

        Flux<Integer> specialErrorHandlerFlux = Flux.range(100, 5);

        return flux.onErrorMap(throwable -> new UnsupportedOperationException(throwable.getMessage()));
    }

    public static void main(String[] args) throws InterruptedException {

        ReactiveTutorial reactiveTutorial = new ReactiveTutorial();

        reactiveTutorial.testDoFunctionOnErrorMap().subscribe(System.out::println);

        // Disposable disposable = reactiveTutorial.testDoFunctionOnError().subscribe(System.out::println);

        // Thread.sleep(3_500);

        // disposable.dispose();;
    }
}
