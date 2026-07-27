package br.com.nischor.ledgerxbackend.notification.infrastructure.persistence.repository;

import br.com.nischor.ledgerxbackend.notification.domain.model.Notification;
import br.com.nischor.ledgerxbackend.notification.domain.repository.NotificationRepository;
import br.com.nischor.ledgerxbackend.notification.infrastructure.persistence.mapper.NotificationJpaMapper;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Repository;

/**
 * Adapter that implements the domain's {@link NotificationRepository} port on top of Spring
 * Data JPA, delegating persistence to {@link NotificationJpaRepository} and converting between
 * domain and JPA representations via {@link NotificationJpaMapper}.
 */
@Repository
public class NotificationRepositoryAdapter implements NotificationRepository {

    private final NotificationJpaRepository jpaRepository;
    private final NotificationJpaMapper mapper;

    /**
     * Creates the adapter.
     *
     * @param jpaRepository Spring Data JPA repository used for persistence.
     * @param mapper        mapper used to convert between domain and JPA representations.
     */
    public NotificationRepositoryAdapter(NotificationJpaRepository jpaRepository, NotificationJpaMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    /**
     * {@inheritDoc}
     *
     * @param notification the notification to persist.
     * @return the persisted notification.
     */
    @Override
    public Notification save(Notification notification) {
        return mapper.toDomain(jpaRepository.save(mapper.toEntity(notification)));
    }

    /**
     * {@inheritDoc}
     *
     * @param id identifier of the notification to find.
     * @return an {@link Optional} containing the notification, or empty if none was found.
     */
    @Override
    public Optional<Notification> findById(UUID id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    /**
     * {@inheritDoc}
     *
     * @return all notifications, most recent first.
     */
    @Override
    public List<Notification> findAllByOrderByCreatedAtDesc() {
        return jpaRepository.findAllByOrderByCreatedAtDesc().stream().map(mapper::toDomain).toList();
    }

    /**
     * {@inheritDoc}
     *
     * @return unread notifications, most recent first.
     */
    @Override
    public List<Notification> findAllByReadFalseOrderByCreatedAtDesc() {
        return jpaRepository.findAllByReadFalseOrderByCreatedAtDesc().stream().map(mapper::toDomain).toList();
    }
}
