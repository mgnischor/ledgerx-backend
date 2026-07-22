package br.com.nischor.ledgerxbackend.accounting.infrastructure.persistence.repository;

import br.com.nischor.ledgerxbackend.accounting.domain.model.Transaction;
import br.com.nischor.ledgerxbackend.accounting.domain.repository.TransactionRepository;
import br.com.nischor.ledgerxbackend.accounting.infrastructure.persistence.mapper.TransactionJpaMapper;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Repository;

@Repository
public class TransactionRepositoryAdapter implements TransactionRepository {

    private final TransactionJpaRepository jpaRepository;
    private final TransactionJpaMapper mapper;

    public TransactionRepositoryAdapter(TransactionJpaRepository jpaRepository, TransactionJpaMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Transaction save(Transaction transaction) {
        return mapper.toDomain(jpaRepository.save(mapper.toEntity(transaction)));
    }

    @Override
    public List<Transaction> findByFinancialAccountIdAndPeriod(UUID financialAccountId, LocalDate from,
            LocalDate to) {
        return jpaRepository.findAllByFinancialAccountIdAndOccurredOnBetween(financialAccountId, from, to).stream()
                .map(mapper::toDomain)
                .toList();
    }
}
