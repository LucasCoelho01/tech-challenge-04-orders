Feature: Order Service

  Scenario: Create a new order
    Given a order payload with cpf "12345678910"
    When the client requests to create a order
    Then the response should contain the order's ID and details