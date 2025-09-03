package com.example.trackings.domain.model;

/**
 * Enum representing the different states of a tracking order.
 * Following DDD principles, this belongs to the domain layer.
 */
public enum TrackingStatus {
    PENDING("Pending"),
    IN_TRANSIT("In Transit"),
    OUT_FOR_DELIVERY("Out for Delivery"),
    DELIVERED("Delivered"),
    FAILED("Failed"),
    CANCELLED("Cancelled");

    private final String description;

    TrackingStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}