package jdias.mockk

import io.netty.channel.ChannelOption
import io.netty.handler.timeout.ReadTimeoutHandler
import io.netty.handler.timeout.WriteTimeoutHandler
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.client.reactive.ReactorClientHttpConnector
import org.springframework.web.reactive.function.client.ExchangeStrategies
import org.springframework.web.reactive.function.client.WebClient
import reactor.netty.http.client.HttpClient
import java.util.concurrent.TimeUnit


@Configuration(proxyBeanMethods = false)
class WebConfiguration {

    @Bean
    fun webClientBuilderWrapper() = WebClientBuilderWrapper()
}

class WebClientBuilderWrapper {

    companion object {
        private const val GZIP_ENCODING = "gzip"
        private const val MAX_IN_MEMORY_SIZE_IN_BYTES = 2 * 1024 * 1024
    }

    fun withTimeout(timeoutInMilliseconds: Int = 10000, connectTimeoutInMilliseconds: Int = 2000): WebClient.Builder {
        val httpClient = HttpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, connectTimeoutInMilliseconds)
                .doOnConnected {
                    it.addHandlerLast(ReadTimeoutHandler(timeoutInMilliseconds.toLong(), TimeUnit.MILLISECONDS))
                            .addHandlerLast(WriteTimeoutHandler(timeoutInMilliseconds.toLong(), TimeUnit.MILLISECONDS))
                }

        return WebClient.builder()
                .clientConnector(ReactorClientHttpConnector(httpClient.compress(true).followRedirect(true)))
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.ACCEPT_ENCODING, GZIP_ENCODING)
                .exchangeStrategies(ExchangeStrategies.builder()
                        .codecs { it.defaultCodecs().maxInMemorySize(MAX_IN_MEMORY_SIZE_IN_BYTES) }
                        .build())
    }
}