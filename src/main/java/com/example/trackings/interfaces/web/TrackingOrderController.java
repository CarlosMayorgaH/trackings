package com.example.trackings.interfaces.web;

import com.example.trackings.application.query.ListTrackingOrdersQuery;
import com.example.trackings.application.query.ListTrackingOrdersQueryHandler;
import com.example.trackings.domain.model.TrackingOrder;
import com.example.trackings.domain.model.TrackingStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

/**
 * REST Controller for tracking orders.
 * Following DDD principles, this is part of the interfaces layer (web).
 * Uses reactive programming with WebFlux.
 */
@RestController
@RequestMapping("/api/v1/tracking-orders")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Tracking Orders", description = "API for managing and querying tracking orders")
public class TrackingOrderController {

    private final ListTrackingOrdersQueryHandler listTrackingOrdersQueryHandler;

    /**
     * Lists tracking orders with optional filtering.
     *
     * @param status Optional filter by tracking status
     * @param customerName Optional filter by customer name (partial match, case-insensitive)
     * @param limit Optional limit for the number of results
     * @param sortByCreatedAtDesc Whether to sort by creation date descending (default: true)
     * @return Reactive stream of tracking orders
     */
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
        summary = "List tracking orders",
        description = "Retrieves a list of tracking orders with optional filtering by status, customer name, and result limits. Results are sorted by creation date in descending order by default."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Successfully retrieved tracking orders",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = TrackingOrder.class)
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid request parameters",
            content = @Content
        ),
        @ApiResponse(
            responseCode = "500",
            description = "Internal server error",
            content = @Content
        )
    })
    public Flux<TrackingOrder> listTrackingOrders(
            @Parameter(description = "Filter by tracking status", example = "IN_TRANSIT")
            @RequestParam(required = false) TrackingStatus status,
            
            @Parameter(description = "Filter by customer name (partial match, case-insensitive)", example = "John")
            @RequestParam(required = false) String customerName,
            
            @Parameter(description = "Maximum number of results to return", example = "10")
            @RequestParam(required = false) Integer limit,
            
            @Parameter(description = "Sort by creation date in descending order (newest first)", example = "true")
            @RequestParam(required = false, defaultValue = "true") Boolean sortByCreatedAtDesc) {

        log.info("GET /api/v1/tracking-orders - status: {}, customerName: {}, limit: {}, sortByCreatedAtDesc: {}",
                status, customerName, limit, sortByCreatedAtDesc);

        // Validate limit parameter
        if (limit != null && limit <= 0) {
            log.warn("Invalid limit parameter: {}", limit);
            return Flux.error(new IllegalArgumentException("Limit must be greater than 0"));
        }

        // Build query object
        ListTrackingOrdersQuery query = ListTrackingOrdersQuery.builder()
                .status(status)
                .customerName(customerName)
                .limit(limit)
                .sortByCreatedAtDesc(sortByCreatedAtDesc)
                .build();

        // Process query through handler
        return listTrackingOrdersQueryHandler.handle(query)
                .doOnComplete(() -> log.info("Successfully completed listing tracking orders"))
                .doOnError(error -> log.error("Error listing tracking orders", error));
    }

    /**
     * Health check endpoint for the tracking orders API.
     *
     * @return Simple health status
     */
    @GetMapping("/health")
    @Operation(
        summary = "Health check",
        description = "Simple health check endpoint for the tracking orders API"
    )
    @ApiResponse(
        responseCode = "200",
        description = "API is healthy",
        content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE)
    )
    public String health() {
        return "Tracking Orders API is healthy";
    }
}