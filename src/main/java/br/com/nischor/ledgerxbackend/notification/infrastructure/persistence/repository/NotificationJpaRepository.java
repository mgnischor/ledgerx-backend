package br.com.nischor.ledgerxbackend.notification.infrastructure.persistence.repository;

import br.com.nischor.ledgerxbackend.notification.infrastructure.persistence.entity.NotificationJpaEntity;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationJpaRepository extends JpaRepository<NotificationJpaEntity, UUID> {

    List<NotificationJpaEntity> findAllByOrderByCreatedAtDesc();

    List<NotificationJpaEntity> findAllByReadFalseOrderByCreatedAtDesc();
}
