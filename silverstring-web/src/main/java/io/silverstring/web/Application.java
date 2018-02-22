package io.silverstring.web;

import org.springframework.amqp.core.Queue;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.filter.CharacterEncodingFilter;

import java.nio.charset.Charset;

@SpringBootApplication(scanBasePackages = {"io.silverstring.web", "io.silverstring.core"})
@EnableJpaRepositories(basePackages = "io.silverstring.core.repository.hibernate")
@EnableRedisRepositories(basePackages = {"io.silverstring.core.repository.cache"})
@EntityScan("io.silverstring.domain.hibernate")
@EnableTransactionManagement
@EnableScheduling
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    @Profile("docker")
    Queue queueDeposit() {
        return new Queue("deposit_transactions", false);
    }

    @Bean
    @Profile("docker")
    Queue queueWithdrawal() {
        return new Queue("withdrawal_transactions", false);
    }

    @Bean
    @Profile("docker")
    Queue queueWebsocket() {
        return new Queue("websock_message", false);
    }

    @Bean
    @Profile("docker")
    Queue queueEmail() {
        return new Queue("email_confirm", false);
    }

    @Bean
    public HttpMessageConverter<String> responseBodyConverter() {
        return new StringHttpMessageConverter(Charset.forName("UTF-8"));
    }

    @Bean
    public CharacterEncodingFilter characterEncodingFilter() {
        CharacterEncodingFilter characterEncodingFilter = new CharacterEncodingFilter();
        characterEncodingFilter.setEncoding("UTF-8");
        characterEncodingFilter.setForceEncoding(true);
        return characterEncodingFilter;
    }
}
