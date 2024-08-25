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

    public static final String QUEUE_PAYMENT_REQUEST = "paymentRequest";
    public static final String QUEUE_PAYMENT_RESPONSE = "paymentResponse";

    public static final String QUEUE_KITCHEN_REQUEST = "kitchenRequest";
    public static final String QUEUE_KITCHEN_RESPONSE = "kitchenResponse";
    public static final String QUEUE_KITCHEN_UPDATE = "kitchenUpdate";

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

    @Bean
    public Queue queue_paymentRequest() { return new Queue(QUEUE_PAYMENT_REQUEST, true); }

    @Bean
    public Queue queue_paymentResponse() { return new Queue(QUEUE_PAYMENT_RESPONSE, true); }

    @Bean
    public Queue queue_kitchenRequest() { return new Queue(QUEUE_KITCHEN_REQUEST, true); }

    @Bean
    public Queue queue_KitchenResponse() { return new Queue(QUEUE_KITCHEN_RESPONSE, true); }

    @Bean
    public Queue queue_KitchenUpdate() { return new Queue(QUEUE_KITCHEN_UPDATE, true); }
}

