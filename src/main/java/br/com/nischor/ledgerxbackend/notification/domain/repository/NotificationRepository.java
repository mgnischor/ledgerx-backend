package br.com.nischor.ledgerxbackend.notification.domain.repository;

import br.com.nischor.ledgerxbackend.notification.domain.model.Notification;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Persistence contract for {@link Notification} aggregates, defined in the domain layer and
 * implemented by the infrastructure layer.
 */
public interface NotificationRepository {

    /**
     * Persists a notification, creating or updating it as needed.
     *
     * @param notification the notification to persist.
     * @return the persisted notification.
     */
    Notification save(Notification notification);

    /**
     * Finds a notification by its identifier.
     *
     * @param id identifier of the notification to find.
     * @return an {@link Optional} containing the notification, or empty if none was found.
     */
    Optional<Notification> findById(UUID id);

    /**
     * Returns all notifications ordered from most recent to oldest.
     *
     * @return all notifications, most recent first.
     */
    List<Notification> findAllByOrderByCreatedAtDesc();

    /**
     * Returns all unread notifications ordered from most recent to oldest.
     *
     * @return unread notifications, most recent first.
     */
    List<Notification> findAllByReadFalseOrderByCreatedAtDesc();
}
