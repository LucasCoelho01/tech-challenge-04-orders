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
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    public Order createOrder(CreateOrderDto createOrderDto) {
        Order order = new Order();

        CustomerResponse customerResponse = getCustomer(createOrderDto.cpf());
        order.setCustomer(customerResponse.getUsername());

        List<String> productNames = new ArrayList<>();

        createOrderDto.products().forEach(product -> {
            ProductResponse productResponse = getProduct(product);
            productNames.add(productResponse.getName());
        });

        order.setProducts(productNames);

        orderRepository.save(order);

        return order;
    }

    public CustomerResponse getCustomer(String cpf) {
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

    public ProductResponse getProduct(String productName) {
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
}
