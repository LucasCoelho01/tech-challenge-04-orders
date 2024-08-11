package com.example.lucas.tech_challenge_04_orders.service;

import com.example.lucas.tech_challenge_04_orders.config.RabbitMQConfig;
import com.example.lucas.tech_challenge_04_orders.entity.Order;
import com.example.lucas.tech_challenge_04_orders.entity.dtos.*;
import com.example.lucas.tech_challenge_04_orders.repository.OrderRepository;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class OrderServiceSaga {
    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    Gson gson = new GsonBuilder().setPrettyPrinting().create();

    @Transactional
    public Order createOrder(CreateOrderDto createOrderDto) throws Exception {
        Order order = orderRepository.save(new Order());
        order.setStatus("Pedido iniciado");

        getCustomerRequest(order.getId(), createOrderDto);

        return order;
    }

    public void getCustomerRequest(String orderId, CreateOrderDto createOrderDto) {

        CustomerRequestDto requestDto = new CustomerRequestDto(orderId, createOrderDto.cpf(), createOrderDto.products());

        System.out.println("CPF: " + requestDto.getCpf());

        String json = gson.toJson(requestDto);

        rabbitTemplate.convertAndSend(RabbitMQConfig.QUEUE_CUSTOMER_REQUEST, json);
    }

    public void getProductsRequest(String orderId, List<String> products) {

        ProductRequestDto requestDto = new ProductRequestDto(orderId, products);

        String json = gson.toJson(requestDto);

        rabbitTemplate.convertAndSend(RabbitMQConfig.QUEUE_PRODUCT_REQUEST, json);
    }

    @RabbitListener(queues = RabbitMQConfig.QUEUE_CUSTOMER_RESPONSE)
    public void getCustomerResponse(String json) {
        Gson gson = new Gson();
        CustomerResponseDto customerResponse = gson.fromJson(json, CustomerResponseDto.class);

        Optional<Order> orderFound = orderRepository.findById(customerResponse.getOrderId());

        if (orderFound.isPresent()) {
            Order order = new Order();

            order.setId(orderFound.get().getId());
            order.setCustomer(customerResponse.getUsername());
            order.setStatus(orderFound.get().getStatus());
            order.setTimestamp(orderFound.get().getTimestamp());

            orderRepository.save(order);
            getProductsRequest(order.getId(), customerResponse.getProductNames());
        } else {
            new Exception("Order Id does not exist");
        }
    }

    @RabbitListener(queues = RabbitMQConfig.QUEUE_PRODUCT_RESPONSE)
    public void getProductResponse(String json) {
        Gson gson = new Gson();
        ProductResponseDto productResponseDto = gson.fromJson(json, ProductResponseDto.class);

        Optional<Order> orderFound = orderRepository.findById(productResponseDto.getOrderId());

        if (orderFound.isPresent()) {
            Order order = new Order();
            order.setId(orderFound.get().getId());
            System.out.println("Customer" + orderFound.get().getCustomer());
            order.setCustomer(orderFound.get().getCustomer());
            List<Product> productList = new ArrayList<>();
            List<String> productNames = new ArrayList<>();

            productResponseDto.getProducts().forEach(product -> {
                productList.add(product);
                productNames.add(product.getName());
            });

            order.setProducts(productNames);
            order.setTotalPrice(calculateTotalPrice(productList));

            order.setStatus("Enviado para cozinha");

            orderRepository.save(order);
        } else {
            new Exception("Order Id does not exist");
        }
    }

    private void sendToKitchen() {

    }

    private BigDecimal calculateTotalPrice(List<Product> productResponseDtoList) {
        List<BigDecimal> prices = new ArrayList<>();

        productResponseDtoList.forEach(product -> {
            prices.add(product.getPrice());
        });

        BigDecimal totalPrice;

        totalPrice = prices.stream().reduce(BigDecimal.ZERO, BigDecimal::add);

        return totalPrice;
    }
}
