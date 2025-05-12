package com.example.spring_batch_tasklet.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.spring_batch_tasklet.model.DataEntity;

@Repository
public interface ItemRepository extends JpaRepository<DataEntity, Long>{

}
