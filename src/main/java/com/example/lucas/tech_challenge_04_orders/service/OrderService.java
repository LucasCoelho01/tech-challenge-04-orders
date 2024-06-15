package com.example.lucas.tech_challenge_04_orders.service;

import com.example.lucas.tech_challenge_04_orders.entity.CustomerResponse;
import com.example.lucas.tech_challenge_04_orders.entity.ProductResponse;
import com.example.lucas.tech_challenge_04_orders.entity.dtos.CreateOrderDto;
import com.example.lucas.tech_challenge_04_orders.entity.Order;
import com.example.lucas.tech_challenge_04_orders.repository.OrderRepository;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    public Order createOrder(CreateOrderDto createOrderDto) {
        Order order = new Order();

        CustomerResponse customerResponse = getCustomer(createOrderDto.cpf());
        order.setCustomer(customerResponse.getUsername());

        List<String> productNames = new ArrayList<>();
        List<ProductResponse> productResponseList = new ArrayList<>();

        createOrderDto.products().forEach(product -> {
            ProductResponse productResponse = getProduct(product);
            productResponseList.add(productResponse);
            productNames.add(productResponse.getName());
        });

        order.setProducts(productNames);
        order.setStatus("Aguardando pagamento");
        order.setTimestamp(LocalDateTime.now().toString());
        order.setTotalPrice(calculateTotalPrice(productResponseList));

        orderRepository.save(order);

        return order;
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public Optional<Order> getOrderById(String id) {
        return orderRepository.findById(id);
    }

    private CustomerResponse getCustomer(String cpf) {
        CustomerResponse customerResponse = new CustomerResponse();
        try {
            // Creating a URL object
            URL url = new URL("http://localhost:8081/api/customers/" + cpf);

            // Opening a connection
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            // Setting the request method to GET
            connection.setRequestMethod("GET");

            // Retrieving the response code
            int responseCode = connection.getResponseCode();

            // Processing the response
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();

                Gson gson = new Gson();

                while ((inputLine = in.readLine()) != null) {
                    customerResponse = gson.fromJson(inputLine, CustomerResponse.class);

                    response.append(inputLine);
                }
                in.close();

                return customerResponse;
            } else {
                System.out.println("API Call Failed. Response Code: " + responseCode);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return customerResponse;
    }

    private ProductResponse getProduct(String productName) {
        ProductResponse productResponse = new ProductResponse();
        try {
            // Creating a URL object
            URL url = new URL("http://localhost:8082/api/products/" + productName);

            // Opening a connection
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            // Setting the request method to GET
            connection.setRequestMethod("GET");

            // Retrieving the response code
            int responseCode = connection.getResponseCode();

            // Processing the response
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();

                Gson gson = new Gson();

                while ((inputLine = in.readLine()) != null) {
                    productResponse = gson.fromJson(inputLine, ProductResponse.class);

                    response.append(inputLine);
                }
                in.close();

                return productResponse;
            } else {
                System.out.println("API Call Failed. Response Code: " + responseCode);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return productResponse;
    }

    private BigDecimal calculateTotalPrice(List<ProductResponse> productResponseList) {
        List<BigDecimal> prices = new ArrayList<>();

        productResponseList.forEach(product -> {
            prices.add(product.getPrice());
        });

        BigDecimal totalPrice;

        totalPrice = prices.stream().reduce(BigDecimal.ZERO, BigDecimal::add);

        return totalPrice;
    }
}
