package com.example.lucas.tech_challenge_04_orders.bdd;

import com.example.lucas.tech_challenge_04_orders.entity.Order;
import com.example.lucas.tech_challenge_04_orders.entity.dtos.CreateOrderDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class StepDefinition {
    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private ObjectMapper objectMapper;
    private MockMvc mockMvc;
    private MvcResult mvcResult;
    private CreateOrderDto createOrderDto;

    @Given("a order payload with cpf {string}")
    public void a_order_payload_with_cpf(String string) {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        createOrderDto = new CreateOrderDto("12345678910", new ArrayList<>());
    }
    @When("the client requests to create a order")
    public void the_client_requests_to_create_a_order() throws Exception{
        mvcResult = mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createOrderDto)))
                .andExpect(status().isCreated())
                .andReturn();
    }
    @Then("the response should contain the order's ID and details")
    public void the_response_should_contain_the_order_s_id_and_details() throws Exception{
        String content = mvcResult.getResponse().getContentAsString();
        Order createdOrder = objectMapper.readValue(content, Order.class);
        assertThat(createdOrder.getId()).isNotNull();
    }

}
