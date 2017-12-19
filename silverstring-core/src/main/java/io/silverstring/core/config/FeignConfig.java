package io.silverstring.core.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import feign.Client;
import feign.Feign;
import feign.codec.Decoder;
import feign.codec.Encoder;
import feign.gson.GsonDecoder;
import feign.gson.GsonEncoder;
import feign.httpclient.ApacheHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
@EnableFeignClients(basePackages = {"io.silverstring.core.provider.feign"})
public class FeignConfig {
    @Bean
    public Client client() {

        final HttpClientBuilder builder = HttpClientBuilder.create()
                .setMaxConnPerRoute(300)
                .setMaxConnTotal(300);
        return new ApacheHttpClient(builder.build());
    }

    @Bean
    public Feign.Builder feignBuilder() {
        return Feign.builder()
                .client(client());
    }

    @Bean
    public Decoder decoder() {
        return new GsonDecoder(createGson());
    }

    @Bean
    public Encoder encoder() {
        return new GsonEncoder(createGson());
    }


    private Gson createGson() {
        return new GsonBuilder()
                .setDateFormat("yyyy'-'MM'-'dd'T'HH':'mm':'ss'.'SSS'Z'")
                .create();
    }
}
