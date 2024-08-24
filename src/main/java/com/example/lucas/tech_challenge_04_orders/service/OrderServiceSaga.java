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
import java.time.LocalDateTime;
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
        Order order =new Order();
        order.setStatus("Pedido iniciado");
        LocalDateTime now = LocalDateTime.now();
        order.setTimestamp(now.toString());

        orderRepository.save(order);

        getCustomerRequest(order.getId(), createOrderDto);

        return order;
    }

    public void getCustomerRequest(String orderId, CreateOrderDto createOrderDto) {

        CustomerRequestDto requestDto = new CustomerRequestDto(orderId, createOrderDto.cpf(), createOrderDto.products());

        System.out.println("CPF: " + requestDto.getCpf());

        String json = gson.toJson(requestDto);

        rabbitTemplate.convertAndSend(RabbitMQConfig.QUEUE_CUSTOMER_REQUEST, json);
        System.out.println("Enviado queue QUEUE_CUSTOMER_REQUEST");
    }

    @RabbitListener(queues = RabbitMQConfig.QUEUE_CUSTOMER_RESPONSE)
    public void getCustomerResponse(String json) {
        System.out.println("Recebido queue QUEUE_CUSTOMER_RESPONSE");
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

    public void getProductsRequest(String orderId, List<String> products) {

        ProductRequestDto requestDto = new ProductRequestDto(orderId, products);

        String json = gson.toJson(requestDto);

        rabbitTemplate.convertAndSend(RabbitMQConfig.QUEUE_PRODUCT_REQUEST, json);
        System.out.println("Enviado queue QUEUE_PRODUCT_REQUEST");
    }

    @RabbitListener(queues = RabbitMQConfig.QUEUE_PRODUCT_RESPONSE)
    public void getProductResponse(String json) {
        System.out.println("Recebido queue QUEUE_PRODUCT_RESPONSE");
        Gson gson = new Gson();
        ProductResponseDto productResponseDto = gson.fromJson(json, ProductResponseDto.class);

        Optional<Order> orderFound = orderRepository.findById(productResponseDto.getOrderId());

        if (orderFound.isPresent()) {
            Order order = new Order();
            order.setId(orderFound.get().getId());
            order.setCustomer(orderFound.get().getCustomer());
            order.setTimestamp(orderFound.get().getTimestamp());
            List<Product> productList = new ArrayList<>();
            List<String> productNames = new ArrayList<>();

            productResponseDto.getProducts().forEach(product -> {
                productList.add(product);
                productNames.add(product.getName());
            });

            order.setProducts(productNames);
            order.setTotalPrice(calculateTotalPrice(productList));

            order.setStatus("Enviado para pagamento");

            orderRepository.save(order);

            sendToPayment(order.getId());
        } else {
            new Exception("Order Id does not exist");
        }
    }

    public void sendToPayment(String orderId) {
        rabbitTemplate.convertAndSend(RabbitMQConfig.QUEUE_PAYMENT_REQUEST, orderId);
        System.out.println("Enviado queue QUEUE_PAYMENT_REQUEST");
    }

    @RabbitListener(queues = RabbitMQConfig.QUEUE_PAYMENT_RESPONSE)
    public void getPaymentResponse(String json) {
        System.out.println("Recebido queue QUEUE_PAYMENT_RESPONSE");
        Gson gson = new Gson();
        PaymentResponseDto paymentResponseDto = gson.fromJson(json, PaymentResponseDto.class);

        Optional<Order> orderFound = orderRepository.findById(paymentResponseDto.getOrderId());

        if (orderFound.isPresent()) {
            Order order = new Order();
            if (paymentResponseDto.isPaymentOk()) {
                order.setStatus("Pagamento Aprovado");
                order.setId(orderFound.get().getId());
                order.setCustomer(orderFound.get().getCustomer());
                order.setStatus(orderFound.get().getStatus());
                order.setTimestamp(orderFound.get().getTimestamp());
                order.setTotalPrice(orderFound.get().getTotalPrice());
                order.setProducts(orderFound.get().getProducts());

                orderRepository.save(order);

                sendToKitchen(order);
            } else {
                order.setStatus("Pagamento Recusado");
                order.setId(orderFound.get().getId());
                order.setCustomer(orderFound.get().getCustomer());
                order.setTimestamp(orderFound.get().getTimestamp());
                order.setTotalPrice(orderFound.get().getTotalPrice());
                order.setProducts(orderFound.get().getProducts());
                orderRepository.save(order);
            }
        } else {
            new Exception("Order Id does not exist");
        }
    }

    private void sendToKitchen(Order order) {
        String json = gson.toJson(order);

        rabbitTemplate.convertAndSend(RabbitMQConfig.QUEUE_KITCHEN_REQUEST, json);
        System.out.println("Enviado queue QUEUE_KITCHEN_REQUEST");
    }

    @RabbitListener(queues = RabbitMQConfig.QUEUE_KITCHEN_RESPONSE)
    public void getKitchenResponse(String json) {
        System.out.println("Recebido queue QUEUE_KITCHEN_RESPONSE");
        Gson gson = new Gson();
        Order orderResponse = gson.fromJson(json, Order.class);

        Optional<Order> orderFound = orderRepository.findById(orderResponse.getId());

        if (orderFound.isPresent()) {
            Order order = new Order();

            order.setId(orderFound.get().getId());
            order.setCustomer(orderResponse.getCustomer());
            order.setStatus("Em preparo");
            order.setTimestamp(orderFound.get().getTimestamp());
            order.setProducts(orderResponse.getProducts());
            order.setTotalPrice(orderFound.get().getTotalPrice());

            orderRepository.save(order);

            if (!orderResponse.getStatus().contains("Pedido Recebido")) {
                new Exception("Erro ao enviar o pedido para a cozinha");
            }
        } else {
            new Exception("Order Id does not exist");
        }
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
