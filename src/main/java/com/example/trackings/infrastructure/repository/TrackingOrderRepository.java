package com.example.trackings.infrastructure.repository;

import com.example.trackings.domain.model.TrackingOrder;
import com.example.trackings.domain.model.TrackingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for TrackingOrder entity.
 * Following DDD principles, this is part of the infrastructure layer.
 */
@Repository
public interface TrackingOrderRepository extends JpaRepository<TrackingOrder, Long> {

    /**
     * Find tracking order by tracking number.
     *
     * @param trackingNumber the tracking number to search for
     * @return Optional containing the tracking order if found
     */
    Optional<TrackingOrder> findByTrackingNumber(String trackingNumber);

    /**
     * Find all tracking orders by status.
     *
     * @param status the tracking status to filter by
     * @return List of tracking orders with the specified status
     */
    List<TrackingOrder> findByStatus(TrackingStatus status);

    /**
     * Find all tracking orders ordered by created date descending.
     *
     * @return List of all tracking orders sorted by creation date (newest first)
     */
    @Query("SELECT t FROM TrackingOrder t ORDER BY t.createdAt DESC")
    List<TrackingOrder> findAllOrderedByCreatedAtDesc();

    /**
     * Find tracking orders by customer name (case-insensitive).
     *
     * @param customerName the customer name to search for
     * @return List of tracking orders for the specified customer
     */
    @Query("SELECT t FROM TrackingOrder t WHERE LOWER(t.customerName) LIKE LOWER(CONCAT('%', :customerName, '%'))")
    List<TrackingOrder> findByCustomerNameContainingIgnoreCase(@Param("customerName") String customerName);
}