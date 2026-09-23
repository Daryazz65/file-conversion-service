package com.example.fileconversion.repository;

import com.example.fileconversion.domain.OutboxMessage;
import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

public interface OutboxMessageRepository extends JpaRepository<OutboxMessage, UUID> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            select o
            from OutboxMessage o
            where o.publishedAt is null
            order by o.createdAt
            """)
    List<OutboxMessage> findUnpublishedForUpdate(Pageable pageable);
}