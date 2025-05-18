package com.example.spring_batch_chunk.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.spring_batch_chunk.model.DataEntity;

public interface ItemRepository extends JpaRepository<DataEntity, Long>{
    
}
