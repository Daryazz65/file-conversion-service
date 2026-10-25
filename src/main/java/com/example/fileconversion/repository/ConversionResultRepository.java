package com.example.fileconversion.repository;

import com.example.fileconversion.domain.ConversionResult;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConversionResultRepository extends JpaRepository<ConversionResult, UUID> {
}