package jdias.mockk

import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.cloud.circuitbreaker.resilience4j.ReactiveResilience4JCircuitBreakerFactory
import org.springframework.cloud.client.circuitbreaker.ReactiveCircuitBreaker
import reactor.core.publisher.Mono
import reactor.test.StepVerifier

class FunctionMatcherClientServiceTest : WebClientTest() {

    @MockK
    private lateinit var reactiveCircuitBreakerFactory: ReactiveResilience4JCircuitBreakerFactory

    @MockK
    private lateinit var reactiveCircuitBreaker: ReactiveCircuitBreaker

    private lateinit var functionMatcherClientService: FunctionMatcherClientService

    private val response = Response("test")

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
        setUpWebClientRelatedMocks()

        // Mock WebClient.ResponseSpec behaviour
        every { webClientResponseSpec.bodyToMono(Response::class.java) } returns Mono.just(response)

        // Mock ReactiveResilience4JCircuitBreakerFactory behaviour
        every { reactiveCircuitBreakerFactory.create(ofType()) } returns reactiveCircuitBreaker

        // Mock ReactiveCircuitBreaker behaviour
        every { reactiveCircuitBreaker.run(ofType(Mono::class), ofType()) } answers { firstArg() }

        functionMatcherClientService = FunctionMatcherClientService(webClientBuilderWrapper, reactiveCircuitBreakerFactory)
    }

    @Test
    fun `Should return VMDS class models by calling VMDS service`() {
        // Act
        val verifier = StepVerifier.create(functionMatcherClientService.call())

        // Assert
        verifier.expectNext(response)
                .verifyComplete()
    }
}