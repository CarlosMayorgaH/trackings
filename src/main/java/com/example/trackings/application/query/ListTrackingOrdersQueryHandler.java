package com.example.trackings.application.query;

import com.example.trackings.domain.model.TrackingOrder;
import com.example.trackings.infrastructure.repository.TrackingOrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * Query handler for listing tracking orders.
 * Following CQRS principles, this handles the query processing in the application layer.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class ListTrackingOrdersQueryHandler {

    private final TrackingOrderRepository trackingOrderRepository;

    /**
     * Handles the ListTrackingOrdersQuery and returns a reactive stream of tracking orders.
     *
     * @param query the query containing filter criteria
     * @return Flux of TrackingOrder entities
     */
    public Flux<TrackingOrder> handle(ListTrackingOrdersQuery query) {
        log.info("Processing ListTrackingOrdersQuery with filters - status: {}, customerName: {}, limit: {}",
                query.getStatus(), query.getCustomerName(), query.getLimit());

        return Mono.fromCallable(() -> {
            List<TrackingOrder> orders = fetchOrdersBasedOnQuery(query);
            
            // Apply limit if specified
            if (query.getLimit() != null && query.getLimit() > 0) {
                int limit = Math.min(query.getLimit(), orders.size());
                orders = orders.subList(0, limit);
            }
            
            log.info("Retrieved {} tracking orders", orders.size());
            return orders;
        })
        .flatMapMany(Flux::fromIterable)
        .doOnError(error -> log.error("Error processing ListTrackingOrdersQuery", error));
    }

    /**
     * Fetches orders from repository based on query criteria.
     *
     * @param query the query containing filter criteria
     * @return List of TrackingOrder entities
     */
    private List<TrackingOrder> fetchOrdersBasedOnQuery(ListTrackingOrdersQuery query) {
        // Priority: specific filters take precedence over general listing
        
        if (query.getStatus() != null && query.getCustomerName() != null) {
            // Both status and customer name filters
            return filterByCustomerName(
                trackingOrderRepository.findByStatus(query.getStatus()),
                query.getCustomerName()
            );
        }
        
        if (query.getStatus() != null) {
            // Status filter only
            return trackingOrderRepository.findByStatus(query.getStatus());
        }
        
        if (query.getCustomerName() != null) {
            // Customer name filter only
            return trackingOrderRepository.findByCustomerNameContainingIgnoreCase(query.getCustomerName());
        }
        
        // No specific filters - return all orders
        if (Boolean.TRUE.equals(query.getSortByCreatedAtDesc())) {
            return trackingOrderRepository.findAllOrderedByCreatedAtDesc();
        } else {
            return trackingOrderRepository.findAll();
        }
    }

    /**
     * Filters a list of orders by customer name (case-insensitive partial match).
     *
     * @param orders the list of orders to filter
     * @param customerName the customer name to search for
     * @return filtered list of orders
     */
    private List<TrackingOrder> filterByCustomerName(List<TrackingOrder> orders, String customerName) {
        if (customerName == null || customerName.trim().isEmpty()) {
            return orders;
        }
        
        String lowerCaseFilter = customerName.toLowerCase().trim();
        return orders.stream()
                .filter(order -> order.getCustomerName().toLowerCase().contains(lowerCaseFilter))
                .toList();
    }
}