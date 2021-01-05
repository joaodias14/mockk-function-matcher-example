package jdias.mockk

import io.mockk.every
import io.mockk.impl.annotations.MockK
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.util.UriBuilder
import java.net.URI
import java.util.function.Function
import kotlin.reflect.KClass

abstract class WebClientTest {

    @MockK
    protected lateinit var webClientBuilderWrapper: WebClientBuilderWrapper

    @MockK
    protected lateinit var webClientBuilder: WebClient.Builder

    @MockK
    protected lateinit var webClient: WebClient

    @MockK
    protected lateinit var webClientRequestBodyUriSpec: WebClient.RequestBodyUriSpec

    @MockK
    protected lateinit var webClientRequestHeadersUriSpec: WebClient.RequestHeadersUriSpec<*>

    @MockK
    protected lateinit var webClientRequestHeadersSpec: WebClient.RequestHeadersSpec<*>

    @MockK
    protected lateinit var webClientResponseSpec: WebClient.ResponseSpec

    protected fun setUpWebClientRelatedMocks() {
        // Mock WebClientBuilderWrapper behaviour
        every { webClientBuilderWrapper.withTimeout(ofType()) } returns webClientBuilder

        // Mock WebClient.Builder behaviour
        every { webClientBuilder.baseUrl(ofType()) } returns webClientBuilder
        every { webClientBuilder.build() } returns webClient

        // Mock WebClient behaviour
        every { webClient.get() } returns webClientRequestHeadersUriSpec
        every { webClient.post() } returns webClientRequestBodyUriSpec

        // Mock WebClient.RequestBodyUriSpec behaviour
        every { webClientRequestBodyUriSpec.body(ofType()) } returns webClientRequestHeadersSpec
        every { webClientRequestBodyUriSpec.header(ofType(), ofType()) } returns webClientRequestBodyUriSpec
        every { webClientRequestBodyUriSpec.retrieve() } returns webClientResponseSpec

        // Mock WebClient.RequestHeadersUriSpec behaviour
        every { webClientRequestHeadersUriSpec.uri(ofType(Function::class as KClass<Function<UriBuilder, URI>>)) } returns webClientRequestHeadersSpec
        every { webClientRequestHeadersUriSpec.retrieve() } returns webClientResponseSpec

        // Mock WebClient.RequestHeadersSpec behaviour
        every { webClientRequestHeadersSpec.header(ofType(), ofType()) } returns webClientRequestHeadersSpec
        every { webClientRequestHeadersSpec.retrieve() } returns webClientResponseSpec
    }

}