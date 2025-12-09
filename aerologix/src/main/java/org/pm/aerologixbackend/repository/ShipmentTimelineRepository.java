package org.pm.aerologixbackend.repository;

import org.pm.aerologixbackend.entity.ShipmentTimeline;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShipmentTimelineRepository extends JpaRepository<ShipmentTimeline, Long> {
    
    @Query("SELECT st FROM ShipmentTimeline st WHERE st.shipment.id = :shipmentId ORDER BY st.timestamp ASC")
    List<ShipmentTimeline> findByShipmentIdOrderByTimestampAsc(@Param("shipmentId") Long shipmentId);
    
    @Query("SELECT st FROM ShipmentTimeline st WHERE st.shipment.trackingNumber = :trackingNumber ORDER BY st.timestamp ASC")
    List<ShipmentTimeline> findByTrackingNumberOrderByTimestampAsc(@Param("trackingNumber") String trackingNumber);
}
