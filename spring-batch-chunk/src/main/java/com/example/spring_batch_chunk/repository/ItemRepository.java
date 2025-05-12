package com.example.spring_batch_chunk.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.spring_batch_chunk.model.DataEntity;

@Repository
public interface ItemRepository extends JpaRepository<DataEntity, Long> {

    @Query(value = "SELECT id, name FROM TEST", nativeQuery = true)
    List<DataEntity> findAllList();
}