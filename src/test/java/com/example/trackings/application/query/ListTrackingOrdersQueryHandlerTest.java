package com.example.trackings.application.query;

import com.example.trackings.domain.model.TrackingOrder;
import com.example.trackings.domain.model.TrackingStatus;
import com.example.trackings.infrastructure.repository.TrackingOrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

/**
 * Unit tests for ListTrackingOrdersQueryHandler.
 * Tests the query processing logic with mocked dependencies.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("ListTrackingOrdersQueryHandler Tests")
class ListTrackingOrdersQueryHandlerTest {

    @Mock
    private TrackingOrderRepository trackingOrderRepository;

    @InjectMocks
    private ListTrackingOrdersQueryHandler queryHandler;

    private List<TrackingOrder> sampleOrders;
    private TrackingOrder order1, order2, order3;

    @BeforeEach
    void setUp() {
        LocalDateTime now = LocalDateTime.now();
        
        order1 = TrackingOrder.builder()
                .id(1L)
                .trackingNumber("TRK001")
                .customerName("John Doe")
                .destination("123 Main St")
                .status(TrackingStatus.PENDING)
                .createdAt(now.minusHours(2))
                .updatedAt(now.minusHours(2))
                .build();

        order2 = TrackingOrder.builder()
                .id(2L)
                .trackingNumber("TRK002")
                .customerName("Jane Smith")
                .destination("456 Oak Ave")
                .status(TrackingStatus.IN_TRANSIT)
                .createdAt(now.minusHours(1))
                .updatedAt(now.minusHours(1))
                .build();

        order3 = TrackingOrder.builder()
                .id(3L)
                .trackingNumber("TRK003")
                .customerName("Bob Johnson")
                .destination("789 Pine St")
                .status(TrackingStatus.DELIVERED)
                .createdAt(now)
                .updatedAt(now)
                .build();

        sampleOrders = Arrays.asList(order1, order2, order3);
    }

    @Test
    @DisplayName("Should return all orders when no filters are applied")
    void shouldReturnAllOrdersWithNoFilters() {
        // Arrange
        ListTrackingOrdersQuery query = ListTrackingOrdersQuery.all();
        when(trackingOrderRepository.findAllOrderedByCreatedAtDesc()).thenReturn(sampleOrders);

        // Act
        Flux<TrackingOrder> result = queryHandler.handle(query);

        // Assert
        StepVerifier.create(result)
                .expectNext(order1)
                .expectNext(order2)
                .expectNext(order3)
                .verifyComplete();
    }

    @Test
    @DisplayName("Should filter orders by status")
    void shouldFilterOrdersByStatus() {
        // Arrange
        ListTrackingOrdersQuery query = ListTrackingOrdersQuery.byStatus(TrackingStatus.IN_TRANSIT);
        when(trackingOrderRepository.findByStatus(TrackingStatus.IN_TRANSIT)).thenReturn(List.of(order2));

        // Act
        Flux<TrackingOrder> result = queryHandler.handle(query);

        // Assert
        StepVerifier.create(result)
                .expectNext(order2)
                .verifyComplete();
    }

    @Test
    @DisplayName("Should filter orders by customer name")
    void shouldFilterOrdersByCustomerName() {
        // Arrange
        ListTrackingOrdersQuery query = ListTrackingOrdersQuery.byCustomerName("Jane");
        when(trackingOrderRepository.findByCustomerNameContainingIgnoreCase("Jane")).thenReturn(List.of(order2));

        // Act
        Flux<TrackingOrder> result = queryHandler.handle(query);

        // Assert
        StepVerifier.create(result)
                .expectNext(order2)
                .verifyComplete();
    }

    @Test
    @DisplayName("Should apply limit to results")
    void shouldApplyLimitToResults() {
        // Arrange
        ListTrackingOrdersQuery query = ListTrackingOrdersQuery.builder()
                .limit(2)
                .build();
        when(trackingOrderRepository.findAllOrderedByCreatedAtDesc()).thenReturn(sampleOrders);

        // Act
        Flux<TrackingOrder> result = queryHandler.handle(query);

        // Assert
        StepVerifier.create(result)
                .expectNext(order1)
                .expectNext(order2)
                .verifyComplete();
    }

    @Test
    @DisplayName("Should handle combination of status and customer name filters")
    void shouldHandleCombinedFilters() {
        // Arrange
        ListTrackingOrdersQuery query = ListTrackingOrdersQuery.builder()
                .status(TrackingStatus.IN_TRANSIT)
                .customerName("Jane")
                .build();
        when(trackingOrderRepository.findByStatus(TrackingStatus.IN_TRANSIT)).thenReturn(List.of(order2));

        // Act
        Flux<TrackingOrder> result = queryHandler.handle(query);

        // Assert
        StepVerifier.create(result)
                .expectNext(order2)
                .verifyComplete();
    }

    @Test
    @DisplayName("Should return empty flux when no orders found")
    void shouldReturnEmptyFluxWhenNoOrdersFound() {
        // Arrange
        ListTrackingOrdersQuery query = ListTrackingOrdersQuery.byStatus(TrackingStatus.CANCELLED);
        when(trackingOrderRepository.findByStatus(TrackingStatus.CANCELLED)).thenReturn(Collections.emptyList());

        // Act
        Flux<TrackingOrder> result = queryHandler.handle(query);

        // Assert
        StepVerifier.create(result)
                .verifyComplete();
    }

    @Test
    @DisplayName("Should handle repository exception gracefully")
    void shouldHandleRepositoryException() {
        // Arrange
        ListTrackingOrdersQuery query = ListTrackingOrdersQuery.all();
        when(trackingOrderRepository.findAllOrderedByCreatedAtDesc())
                .thenThrow(new RuntimeException("Database connection failed"));

        // Act
        Flux<TrackingOrder> result = queryHandler.handle(query);

        // Assert
        StepVerifier.create(result)
                .expectErrorMatches(throwable -> 
                    throwable instanceof RuntimeException && 
                    throwable.getMessage().contains("Database connection failed"))
                .verify();
    }

    @Test
    @DisplayName("Should respect sortByCreatedAtDesc = false")
    void shouldRespectSortByCreatedAtDescFalse() {
        // Arrange
        ListTrackingOrdersQuery query = ListTrackingOrdersQuery.builder()
                .sortByCreatedAtDesc(false)
                .build();
        when(trackingOrderRepository.findAll()).thenReturn(sampleOrders);

        // Act
        Flux<TrackingOrder> result = queryHandler.handle(query);

        // Assert
        StepVerifier.create(result)
                .expectNext(order1)
                .expectNext(order2)
                .expectNext(order3)
                .verifyComplete();
    }

    @Test
    @DisplayName("Should handle zero limit")
    void shouldHandleZeroLimit() {
        // Arrange
        ListTrackingOrdersQuery query = ListTrackingOrdersQuery.builder()
                .limit(0)
                .build();
        when(trackingOrderRepository.findAllOrderedByCreatedAtDesc()).thenReturn(sampleOrders);

        // Act
        Flux<TrackingOrder> result = queryHandler.handle(query);

        // Assert
        StepVerifier.create(result)
                .verifyComplete();
    }
}