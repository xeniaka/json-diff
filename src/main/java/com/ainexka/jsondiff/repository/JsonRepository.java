package com.ainexka.jsondiff.repository;

import com.ainexka.jsondiff.entity.DataPosition;
import com.ainexka.jsondiff.entity.JsonData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JsonRepository extends JpaRepository<JsonData, Long> {
    List<JsonData> findByIdentifier(String identifier);
    JsonData findByIdentifierAndPosition(String identifier, DataPosition position);
}
