package br.com.nischor.ledgerxbackend.notification.infrastructure.persistence.repository;

import br.com.nischor.ledgerxbackend.notification.domain.model.Notification;
import br.com.nischor.ledgerxbackend.notification.domain.repository.NotificationRepository;
import br.com.nischor.ledgerxbackend.notification.infrastructure.persistence.mapper.NotificationJpaMapper;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Repository;

@Repository
public class NotificationRepositoryAdapter implements NotificationRepository {

    private final NotificationJpaRepository jpaRepository;
    private final NotificationJpaMapper mapper;

    public NotificationRepositoryAdapter(NotificationJpaRepository jpaRepository, NotificationJpaMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Notification save(Notification notification) {
        return mapper.toDomain(jpaRepository.save(mapper.toEntity(notification)));
    }

    @Override
    public Optional<Notification> findById(UUID id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<Notification> findAllByOrderByCreatedAtDesc() {
        return jpaRepository.findAllByOrderByCreatedAtDesc().stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<Notification> findAllByReadFalseOrderByCreatedAtDesc() {
        return jpaRepository.findAllByReadFalseOrderByCreatedAtDesc().stream().map(mapper::toDomain).toList();
    }
}
