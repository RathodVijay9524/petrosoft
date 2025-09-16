package com.vijay.petrosoft.repository;

import com.vijay.petrosoft.domain.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    
    List<Notification> findByPumpId(Long pumpId);
    
    List<Notification> findByUserId(Long userId);
    
    List<Notification> findByStatus(Notification.NotificationStatus status);
    
    List<Notification> findByType(Notification.NotificationType type);
    
    List<Notification> findByRecipientEmail(String email);
    
    @Query("SELECT n FROM Notification n WHERE n.status = 'PENDING' AND (n.scheduledAt IS NULL OR n.scheduledAt <= :currentTime)")
    List<Notification> findPendingNotifications(@Param("currentTime") LocalDateTime currentTime);
    
    @Query("SELECT n FROM Notification n WHERE n.status = 'FAILED' AND n.retryCount < n.maxRetries")
    List<Notification> findFailedNotificationsForRetry();
    
    @Query("SELECT n FROM Notification n WHERE n.pumpId = :pumpId AND n.createdAt >= :startDate AND n.createdAt <= :endDate")
    List<Notification> findNotificationsByPumpIdAndDateRange(@Param("pumpId") Long pumpId, 
                                                            @Param("startDate") LocalDateTime startDate, 
                                                            @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT n FROM Notification n WHERE n.type = :type AND n.status = 'SENT' AND n.sentAt >= :startDate")
    List<Notification> findSentNotificationsByTypeAndDate(@Param("type") Notification.NotificationType type, 
                                                         @Param("startDate") LocalDateTime startDate);
    
    long countByStatus(Notification.NotificationStatus status);
    
    long countByType(Notification.NotificationType type);
    
    @Query("SELECT COUNT(n) FROM Notification n WHERE n.pumpId = :pumpId AND n.status = 'SENT'")
    long countSentNotificationsByPumpId(@Param("pumpId") Long pumpId);
}
