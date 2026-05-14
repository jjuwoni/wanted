package com.wanted.springasync.repository.notification;

import com.wanted.springasync.domain.notification.NotificationLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationLogRepository extends JpaRepository<NotificationLog, Long> {
}
