package com.example.trackings.application.query;

import com.example.trackings.domain.model.TrackingStatus;
import lombok.Builder;
import lombok.Data;

/**
 * Query object for listing tracking orders.
 * Following CQRS principles, this represents a query request in the application layer.
 */
@Data
@Builder
public class ListTrackingOrdersQuery {

    /**
     * Optional filter by tracking status.
     */
    private TrackingStatus status;

    /**
     * Optional filter by customer name (partial match, case-insensitive).
     */
    private String customerName;

    /**
     * Optional limit for the number of results.
     * Default is no limit (null means return all).
     */
    private Integer limit;

    /**
     * Whether to sort by creation date descending (newest first).
     * Default is true.
     */
    @Builder.Default
    private Boolean sortByCreatedAtDesc = true;

    /**
     * Static factory method to create a query for all tracking orders.
     *
     * @return Query to list all tracking orders
     */
    public static ListTrackingOrdersQuery all() {
        return ListTrackingOrdersQuery.builder().build();
    }

    /**
     * Static factory method to create a query filtered by status.
     *
     * @param status the status to filter by
     * @return Query to list tracking orders with the specified status
     */
    public static ListTrackingOrdersQuery byStatus(TrackingStatus status) {
        return ListTrackingOrdersQuery.builder()
                .status(status)
                .build();
    }

    /**
     * Static factory method to create a query filtered by customer name.
     *
     * @param customerName the customer name to filter by
     * @return Query to list tracking orders for the specified customer
     */
    public static ListTrackingOrdersQuery byCustomerName(String customerName) {
        return ListTrackingOrdersQuery.builder()
                .customerName(customerName)
                .build();
    }
}