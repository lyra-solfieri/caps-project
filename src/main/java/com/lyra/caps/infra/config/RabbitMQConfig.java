package com.lyra.caps.infra.config;

import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Value("${ocr.queue.request}")
    private String requestQueue;

    @Value("${ocr.queue.response}")
    private String responseQueue;

    @Bean
    public Queue ocrRequestQueue() {
        return new Queue(requestQueue, true);
    }

    @Bean
    public Queue ocrResponseQueue() {
        return new Queue(responseQueue, true);
    }
}
