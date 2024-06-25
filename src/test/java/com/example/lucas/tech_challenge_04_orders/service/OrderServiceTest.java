package com.example.lucas.tech_challenge_04_orders.service;

import com.example.lucas.tech_challenge_04_orders.entity.Order;
import com.example.lucas.tech_challenge_04_orders.entity.dtos.*;
import com.example.lucas.tech_challenge_04_orders.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private OrderService orderService;

    private CreateOrderDto createOrderDto;
    private Order order;
    private CustomerResponseDto customerResponseDto;
    private ProductResponseDto productResponseDto;
    private PaymentResponseDto paymentResponseDto;

    @BeforeEach
    void setUp() {
        createOrderDto = new CreateOrderDto("12345678900", List.of("product1", "product2"));

        customerResponseDto = new CustomerResponseDto();
        customerResponseDto.setId(UUID.randomUUID());
        customerResponseDto.setUsername("Lucas");
        customerResponseDto.setCpf("12345678910");
        customerResponseDto.setEmail("lucas@gmail.com");

        productResponseDto = new ProductResponseDto();
        productResponseDto.setName("testProduct");
        productResponseDto.setPrice(BigDecimal.valueOf(100.00));

        paymentResponseDto = new PaymentResponseDto();
        paymentResponseDto.setPaymentOk(true);

        order = new Order();
        order.setId("1");
        order.setCustomer("testCustomer");
        order.setProducts(List.of("testProduct"));
        order.setStatus("Aguardando pagamento");
        order.setTimestamp(LocalDateTime.now().toString());
        order.setTotalPrice(BigDecimal.valueOf(200.00));
    }

    /*@Test
    void createOrder_success() throws Exception {
            when(orderRepository.save(any(Order.class))).thenReturn(order);

            when(orderService.getCustomer(anyString())).thenReturn(customerResponseDto);
            when(orderService.getProduct(anyString())).thenReturn(productResponseDto);
            doNothing().when(orderRepository).save(any(Order.class));
            when(orderService.sendOrderToPayment(any(Order.class))).thenReturn(true);
            doNothing().when(orderService).sendOrderToKitchen(any(Order.class));

            Order createdOrder = orderService.createOrder(createOrderDto);

            assertNotNull(createdOrder);
            assertEquals("testCustomer", createdOrder.getCustomer());
            assertEquals(1, createdOrder.getProducts().size());
            assertEquals(BigDecimal.valueOf(200.00), createdOrder.getTotalPrice());
            assertEquals("Pagamento Aprovado", createdOrder.getStatus());

            verify(orderRepository, times(1)).save(any(Order.class));
    }*/

    @Test
    void getAllOrders_success() {
        when(orderRepository.findAll()).thenReturn(List.of(order));

        List<Order> orders = orderService.getAllOrders();

        assertNotNull(orders);
        assertFalse(orders.isEmpty());
        assertEquals(1, orders.size());
        verify(orderRepository, times(1)).findAll();
    }

    @Test
    void getOrderById_success() {
        when(orderRepository.findById(anyString())).thenReturn(Optional.of(order));

        Optional<Order> foundOrder = orderService.getOrderById("1");

        assertTrue(foundOrder.isPresent());
        assertEquals(order, foundOrder.get());
        verify(orderRepository, times(1)).findById(anyString());
    }

/*    @Test
    void updateOrderStatus_success() throws Exception {
        when(orderRepository.findById(anyString())).thenReturn(Optional.of(order));

        Optional<Order> updatedOrder = orderService.updateOrderStatus("1", true);

        assertTrue(updatedOrder.isPresent());
        assertEquals("Pagamento Aprovado", updatedOrder.get().getStatus());
        verify(orderRepository, times(1)).findById(anyString());
        verify(orderRepository, times(1)).save(any(Order.class));
    }*/

    @Test
    void updateOrderStatus_paymentRejected() throws Exception {
        when(orderRepository.findById(anyString())).thenReturn(Optional.of(order));

        Optional<Order> updatedOrder = orderService.updateOrderStatus("1", false);

        assertTrue(updatedOrder.isPresent());
        assertEquals("Pagamento Rejeitado", updatedOrder.get().getStatus());
        verify(orderRepository, times(1)).findById(anyString());
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    void calculateTotalPrice_success() {
        List<ProductResponseDto> productResponseDtos = List.of(productResponseDto, productResponseDto);

        BigDecimal totalPrice = orderService.calculateTotalPrice(productResponseDtos);

        assertEquals(BigDecimal.valueOf(200.00), totalPrice);
    }
}
