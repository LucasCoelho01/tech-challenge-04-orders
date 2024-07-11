package com.example.lucas.tech_challenge_04_orders.service;

import com.example.lucas.tech_challenge_04_orders.entity.dtos.*;
import com.example.lucas.tech_challenge_04_orders.entity.Order;
import com.example.lucas.tech_challenge_04_orders.repository.OrderRepository;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
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

    public Order createOrder(CreateOrderDto createOrderDto) throws Exception {
        Order order = new Order();

        CustomerResponseDto customerResponseDto = getCustomer(createOrderDto.cpf());
        order.setCustomer(customerResponseDto.getUsername());

        List<String> productNames = new ArrayList<>();
        List<ProductResponseDto> productResponseDtoList = new ArrayList<>();

        createOrderDto.products().forEach(product -> {
            ProductResponseDto productResponseDto = getProduct(product);
            productResponseDtoList.add(productResponseDto);
            productNames.add(productResponseDto.getName());
        });

        order.setProducts(productNames);
        order.setStatus("Aguardando pagamento");
        order.setTimestamp(LocalDateTime.now().toString());
        order.setTotalPrice(calculateTotalPrice(productResponseDtoList));

        orderRepository.save(order);

        if (sendOrderToPayment(order)) {
            order.setStatus("Pagamento Aprovado");
            sendOrderToKitchen(order);
        }

        return order;
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public Optional<Order> getOrderById(String id) {
        return orderRepository.findById(id);
    }

    public Optional<Order> updateOrderStatus(String id, boolean isPaymentOk) throws Exception {
        var order = getOrderById(id);
        Order orderFound = order.get();

        if (order.isPresent()) {
            if (isPaymentOk) {
                order.get().setStatus("Pagamento Aprovado");
                sendOrderToKitchen(orderFound);
            } else {
                order.get().setStatus("Pagamento Rejeitado");
            }
            orderRepository.save(orderFound);
        }

        return order;
    }

    public void sendOrderToKitchen(Order order) throws Exception {

        URL url = new URL("http://payment.us-east-1.elasticbeanstalk.com/api/kitchen");

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.setRequestMethod("POST");

        connection.setRequestProperty("Content-type", "application/json");

        connection.setDoOutput(true);

        OutputStream os = connection.getOutputStream();

        String input = (new Gson().toJson(order));

        os.write(input.getBytes());

        int responseCode = connection.getResponseCode();

        os.flush();

        os.close();
    }

    CustomerResponseDto getCustomer(String cpf) {
        CustomerResponseDto customerResponseDto = new CustomerResponseDto();
        try {
            URL url = new URL("http://customers.us-east-1.elasticbeanstalk.com/api/customers/" + cpf);

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setRequestMethod("GET");

            int responseCode = connection.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();

                Gson gson = new Gson();

                while ((inputLine = in.readLine()) != null) {
                    customerResponseDto = gson.fromJson(inputLine, CustomerResponseDto.class);

                    response.append(inputLine);
                }
                in.close();

                return customerResponseDto;
            } else {
                System.out.println("API Call Failed. Response Code: " + responseCode);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return customerResponseDto;
    }

    ProductResponseDto getProduct(String productName) {
        ProductResponseDto productResponseDto = new ProductResponseDto();
        try {
            URL url = new URL("http://products.us-east-1.elasticbeanstalk.com/api/products/" + productName);

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setRequestMethod("GET");

            int responseCode = connection.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();

                Gson gson = new Gson();

                while ((inputLine = in.readLine()) != null) {
                    productResponseDto = gson.fromJson(inputLine, ProductResponseDto.class);

                    response.append(inputLine);
                }
                in.close();

                return productResponseDto;
            } else {
                System.out.println("API Call Failed. Response Code: " + responseCode);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return productResponseDto;
    }

    BigDecimal calculateTotalPrice(List<ProductResponseDto> productResponseDtoList) {
        List<BigDecimal> prices = new ArrayList<>();

        productResponseDtoList.forEach(product -> {
            prices.add(product.getPrice());
        });

        BigDecimal totalPrice;

        totalPrice = prices.stream().reduce(BigDecimal.ZERO, BigDecimal::add);

        return totalPrice;
    }

    boolean sendOrderToPayment(Order order) throws Exception {
        var IspaymentOk = false;
        PaymentResponseDto paymentResponseDto = new PaymentResponseDto();
        PaymentsRequestDto paymentsRequestDto = new PaymentsRequestDto();
        paymentsRequestDto.setOrderId(order.getId());

        URL url = new URL("http://payment.us-east-1.elasticbeanstalk.com/api/payments");

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.setRequestMethod("POST");

        connection.setRequestProperty("Content-type", "application/json");

        connection.setDoOutput(true);

        OutputStream os = connection.getOutputStream();

        String input = (new Gson().toJson(paymentsRequestDto));

        os.write(input.getBytes());

        int responseCode = connection.getResponseCode();

        os.flush();

        os.close();

        if (responseCode == HttpURLConnection.HTTP_CREATED) {
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();

            Gson gson = new Gson();

            while ((inputLine = in.readLine()) != null) {
                paymentResponseDto = gson.fromJson(inputLine, PaymentResponseDto.class);

                response.append(inputLine);
            }
            in.close();

            return paymentResponseDto.isPaymentOk();
        } else {
            System.out.println("API Call Failed. Response Code: " + responseCode);
        }

        return IspaymentOk;
    }
}
