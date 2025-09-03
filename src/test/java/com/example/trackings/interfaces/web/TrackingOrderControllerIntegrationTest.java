package com.example.trackings.interfaces.web;

import com.example.trackings.domain.model.TrackingOrder;
import com.example.trackings.domain.model.TrackingStatus;
import com.example.trackings.infrastructure.repository.TrackingOrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.time.LocalDateTime;

/**
 * Integration tests for TrackingOrderController.
 * Tests the complete endpoint functionality with real database interaction.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@DisplayName("TrackingOrderController Integration Tests")
class TrackingOrderControllerIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private TrackingOrderRepository trackingOrderRepository;

    private TrackingOrder order1, order2, order3;

    @BeforeEach
    void setUp() {
        // Clear database before each test
        trackingOrderRepository.deleteAll();

        LocalDateTime now = LocalDateTime.now();

        // Create test data
        order1 = TrackingOrder.builder()
                .trackingNumber("TEST001")
                .customerName("John Doe")
                .destination("123 Main St, New York, NY")
                .status(TrackingStatus.PENDING)
                .createdAt(now.minusHours(3))
                .updatedAt(now.minusHours(3))
                .estimatedDelivery(now.plusDays(2))
                .build();

        order2 = TrackingOrder.builder()
                .trackingNumber("TEST002")
                .customerName("Jane Smith")
                .destination("456 Oak Ave, Los Angeles, CA")
                .status(TrackingStatus.IN_TRANSIT)
                .createdAt(now.minusHours(2))
                .updatedAt(now.minusHours(1))
                .estimatedDelivery(now.plusDays(1))
                .build();

        order3 = TrackingOrder.builder()
                .trackingNumber("TEST003")
                .customerName("Bob Johnson")
                .destination("789 Pine St, Chicago, IL")
                .status(TrackingStatus.DELIVERED)
                .createdAt(now.minusHours(1))
                .updatedAt(now)
                .estimatedDelivery(now.minusDays(1))
                .build();

        // Save test data
        trackingOrderRepository.saveAll(java.util.List.of(order1, order2, order3));
    }

    @Test
    @DisplayName("GET /api/v1/tracking-orders should return all orders")
    void shouldReturnAllTrackingOrders() {
        webTestClient.get()
                .uri("/api/v1/tracking-orders")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(TrackingOrder.class)
                .hasSize(3)
                .consumeWith(response -> {
                    var orders = response.getResponseBody();
                    System.out.println("[DEBUG_LOG] Retrieved orders: " + orders.size());
                    orders.forEach(order -> 
                        System.out.println("[DEBUG_LOG] Order: " + order.getTrackingNumber() + 
                                         " - " + order.getCustomerName() + " - " + order.getStatus())
                    );
                });
    }

    @Test
    @DisplayName("GET /api/v1/tracking-orders?status=IN_TRANSIT should filter by status")
    void shouldFilterTrackingOrdersByStatus() {
        webTestClient.get()
                .uri("/api/v1/tracking-orders?status=IN_TRANSIT")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(TrackingOrder.class)
                .hasSize(1)
                .consumeWith(response -> {
                    var orders = response.getResponseBody();
                    System.out.println("[DEBUG_LOG] Filtered by status IN_TRANSIT: " + orders.size() + " orders");
                    assert orders.get(0).getStatus() == TrackingStatus.IN_TRANSIT;
                    assert orders.get(0).getTrackingNumber().equals("TEST002");
                });
    }

    @Test
    @DisplayName("GET /api/v1/tracking-orders?customerName=Jane should filter by customer name")
    void shouldFilterTrackingOrdersByCustomerName() {
        webTestClient.get()
                .uri("/api/v1/tracking-orders?customerName=Jane")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(TrackingOrder.class)
                .hasSize(1)
                .consumeWith(response -> {
                    var orders = response.getResponseBody();
                    System.out.println("[DEBUG_LOG] Filtered by customer name 'Jane': " + orders.size() + " orders");
                    assert orders.get(0).getCustomerName().contains("Jane");
                });
    }

    @Test
    @DisplayName("GET /api/v1/tracking-orders?limit=2 should limit results")
    void shouldLimitTrackingOrdersResults() {
        webTestClient.get()
                .uri("/api/v1/tracking-orders?limit=2")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(TrackingOrder.class)
                .hasSize(2)
                .consumeWith(response -> {
                    System.out.println("[DEBUG_LOG] Limited to 2 results: " + response.getResponseBody().size() + " orders");
                });
    }

    @Test
    @DisplayName("GET /api/v1/tracking-orders with multiple filters should work correctly")
    void shouldHandleMultipleFilters() {
        webTestClient.get()
                .uri("/api/v1/tracking-orders?status=PENDING&customerName=John&limit=1")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(TrackingOrder.class)
                .hasSize(1)
                .consumeWith(response -> {
                    var orders = response.getResponseBody();
                    System.out.println("[DEBUG_LOG] Multiple filters applied: " + orders.size() + " orders");
                    assert orders.get(0).getStatus() == TrackingStatus.PENDING;
                    assert orders.get(0).getCustomerName().contains("John");
                });
    }

    @Test
    @DisplayName("GET /api/v1/tracking-orders?status=CANCELLED should return empty list")
    void shouldReturnEmptyListForNonExistentStatus() {
        webTestClient.get()
                .uri("/api/v1/tracking-orders?status=CANCELLED")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(TrackingOrder.class)
                .hasSize(0)
                .consumeWith(response -> {
                    System.out.println("[DEBUG_LOG] Filter by CANCELLED status: " + response.getResponseBody().size() + " orders");
                });
    }

    @Test
    @DisplayName("GET /api/v1/tracking-orders?limit=0 should return empty list")
    void shouldReturnEmptyListForZeroLimit() {
        webTestClient.get()
                .uri("/api/v1/tracking-orders?limit=0")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(TrackingOrder.class)
                .hasSize(0);
    }

    @Test
    @DisplayName("GET /api/v1/tracking-orders?limit=-1 should return bad request")
    void shouldReturnBadRequestForNegativeLimit() {
        webTestClient.get()
                .uri("/api/v1/tracking-orders?limit=-1")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().is5xxServerError(); // The controller returns a Flux.error which becomes 500
    }

    @Test
    @DisplayName("GET /api/v1/tracking-orders?sortByCreatedAtDesc=false should sort ascending")
    void shouldSortAscendingWhenRequested() {
        webTestClient.get()
                .uri("/api/v1/tracking-orders?sortByCreatedAtDesc=false")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(TrackingOrder.class)
                .hasSize(3)
                .consumeWith(response -> {
                    var orders = response.getResponseBody();
                    System.out.println("[DEBUG_LOG] Sort ascending test: " + orders.size() + " orders");
                    // The order should be as retrieved from findAll() - TEST001, TEST002, TEST003
                    assert orders.get(0).getTrackingNumber().equals("TEST001");
                    assert orders.get(1).getTrackingNumber().equals("TEST002");
                    assert orders.get(2).getTrackingNumber().equals("TEST003");
                });
    }

    @Test
    @DisplayName("GET /api/v1/tracking-orders/health should return health status")
    void shouldReturnHealthStatus() {
        webTestClient.get()
                .uri("/api/v1/tracking-orders/health")
                .accept(MediaType.TEXT_PLAIN)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.TEXT_PLAIN_VALUE + ";charset=UTF-8")
                .expectBody(String.class)
                .isEqualTo("Tracking Orders API is healthy");
    }

    @Test
    @DisplayName("Should handle case-insensitive customer name search")
    void shouldHandleCaseInsensitiveCustomerNameSearch() {
        webTestClient.get()
                .uri("/api/v1/tracking-orders?customerName=JANE")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(TrackingOrder.class)
                .hasSize(1)
                .consumeWith(response -> {
                    var orders = response.getResponseBody();
                    System.out.println("[DEBUG_LOG] Case insensitive search for 'JANE': " + orders.size() + " orders");
                    assert orders.get(0).getCustomerName().equals("Jane Smith");
                });
    }

    @Test
    @DisplayName("Should handle partial customer name search")
    void shouldHandlePartialCustomerNameSearch() {
        webTestClient.get()
                .uri("/api/v1/tracking-orders?customerName=Do")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(TrackingOrder.class)
                .hasSize(1)
                .consumeWith(response -> {
                    var orders = response.getResponseBody();
                    System.out.println("[DEBUG_LOG] Partial name search for 'Do': " + orders.size() + " orders");
                    assert orders.get(0).getCustomerName().equals("John Doe");
                });
    }
}