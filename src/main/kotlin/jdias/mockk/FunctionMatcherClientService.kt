package jdias.mockk

import org.springframework.cloud.circuitbreaker.resilience4j.ReactiveResilience4JCircuitBreakerFactory
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class FunctionMatcherClientService(webClientBuilderWrapper: WebClientBuilderWrapper,
                                   reactiveCircuitBreakerFactory: ReactiveResilience4JCircuitBreakerFactory) {

    private val webClient = webClientBuilderWrapper.withTimeout(10).baseUrl("url").build()

    private val reactiveCircuitBreaker by lazy { reactiveCircuitBreakerFactory.create("CIRCUIT_BREAKER_ID") }

    fun call() = webClient.post()
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .retrieve()
                .bodyToMono(Response::class.java)
                .transform {
                    reactiveCircuitBreaker.run(it) { Mono.empty() }
                }
}
