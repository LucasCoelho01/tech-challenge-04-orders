package com.example.lucas.tech_challenge_04_orders.config;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String QUEUE_NAME = "testQueue";
    public static final String QUEUE_CUSTOMER_REQUEST = "customerRequest";
    public static final String QUEUE_CUSTOMER_RESPONSE = "customerResponse";
    public static final String QUEUE_PRODUCT_REQUEST = "productRequest";
    public static final String QUEUE_PRODUCT_RESPONSE = "productResponse";

    @Bean
    public Queue queue() {
        return new Queue(QUEUE_NAME, true);
    }

    @Bean
    public Queue queue_customerRequest() {
        return new Queue(QUEUE_CUSTOMER_REQUEST, true);
    }


    @Bean
    public Queue queue_customerResponse() {
        return new Queue(QUEUE_CUSTOMER_RESPONSE, true);
    }

    @Bean
    public Queue queue_productsRequest() { return new Queue(QUEUE_PRODUCT_REQUEST, true); }

    @Bean
    public Queue queue_productsResponse() { return new Queue(QUEUE_PRODUCT_RESPONSE, true); }
}

