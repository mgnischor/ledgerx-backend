package br.com.nischor.ledgerxbackend.notification.infrastructure.persistence.repository;

import br.com.nischor.ledgerxbackend.notification.infrastructure.persistence.entity.NotificationJpaEntity;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Spring Data JPA repository for {@link NotificationJpaEntity}, providing query methods derived
 * from method name conventions.
 */
public interface NotificationJpaRepository extends JpaRepository<NotificationJpaEntity, UUID> {

    /**
     * Returns all notification entities ordered from most recent to oldest.
     *
     * @return all notification entities, most recent first.
     */
    List<NotificationJpaEntity> findAllByOrderByCreatedAtDesc();

    /**
     * Returns all unread notification entities ordered from most recent to oldest.
     *
     * @return unread notification entities, most recent first.
     */
    List<NotificationJpaEntity> findAllByReadFalseOrderByCreatedAtDesc();
}
