package com.example.backendtemplate.repository.customRepositories.Impl;

import com.example.backendtemplate.entities.Transaction;
import com.example.backendtemplate.enums.FilterTypes;
import com.example.backendtemplate.repository.customRepositories.TransactionCustomRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Repository
public class TransactionCustomRepositoryImpl implements TransactionCustomRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Page<Transaction> searchTransactions(String type, Double minAmount, Double maxAmount, String dateType, Pageable pageable) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();

        // Main query
        CriteriaQuery<Transaction> cq = cb.createQuery(Transaction.class);
        Root<Transaction> root = cq.from(Transaction.class);
        List<Predicate> predicates = new ArrayList<>();

        if (type.isEmpty()) {
            predicates.add(cb.equal(root.get(type), FilterTypes.ALL));
        } else {
            predicates.add(cb.equal(root.get(type), type));
        }

        if (minAmount != null) {
            predicates.add(cb.greaterThanOrEqualTo(root.get("amount"), minAmount));
        }
        if (maxAmount != null) {
            predicates.add(cb.lessThanOrEqualTo(root.get("amount"), maxAmount));
        }

        // Date filtering based on dateType
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startDate = null;
        LocalDateTime endDate = now;
        if (dateType != null) {
            try {
                FilterTypes filterType = FilterTypes.valueOf(dateType);
                switch (filterType) {
                    case LAST_7_DAYS:
                        startDate = now.minusDays(7);
                        break;
                    case LAST_MONTHS:
                        startDate = now.minusMonths(1).withDayOfMonth(1);
                        break;
                    case LAST_3_MONTHS:
                        startDate = now.minusMonths(3).withDayOfMonth(1);
                        break;
                    default:
                        break;
                }
            } catch (IllegalArgumentException e) {
                // handle invalid dateType string if needed
            }
        }
        if (startDate != null) {
            predicates.add(cb.greaterThanOrEqualTo(root.get("createdDateTime"), startDate));
        }
        if (endDate != null) {
            predicates.add(cb.lessThanOrEqualTo(root.get("createdDateTime"), endDate));
        }

        cq.where(cb.and(predicates.toArray(new Predicate[0])));
        cq.orderBy(cb.desc(root.get("transactionDate")));

        TypedQuery<Transaction> query = entityManager.createQuery(cq);
        query.setFirstResult((int) pageable.getOffset());
        query.setMaxResults(pageable.getPageSize());

        // Count a query
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<Transaction> countRoot = countQuery.from(Transaction.class);
        countQuery.select(cb.count(countRoot));
        countQuery.where(cb.and(predicates.toArray(new Predicate[0])));
        Long total = entityManager.createQuery(countQuery).getSingleResult();

        return new PageImpl<>(query.getResultList(), pageable, total);
    }
}
