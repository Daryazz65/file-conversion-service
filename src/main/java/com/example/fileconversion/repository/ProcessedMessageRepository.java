package com.example.fileconversion.repository;

import com.example.fileconversion.domain.ProcessedMessage;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProcessedMessageRepository extends JpaRepository<ProcessedMessage, UUID> {
}