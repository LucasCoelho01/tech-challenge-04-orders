package com.example.lucas.tech_challenge_04_orders.service;

import com.example.lucas.tech_challenge_04_orders.TechChallenge04OrdersApplication;
import com.example.lucas.tech_challenge_04_orders.entity.CustomerResponse;
import com.google.gson.Gson;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

@Service
public class OrderService {

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
}
