package br.com.nischor.ledgerxbackend.notification.domain.repository;

import br.com.nischor.ledgerxbackend.notification.domain.model.Notification;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface NotificationRepository {

    Notification save(Notification notification);

    Optional<Notification> findById(UUID id);

    List<Notification> findAllByOrderByCreatedAtDesc();

    List<Notification> findAllByReadFalseOrderByCreatedAtDesc();
}
