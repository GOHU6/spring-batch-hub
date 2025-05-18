package com.example.spring_batch_chunk.writer;

import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.lang.NonNull;

import com.example.spring_batch_chunk.model.DataEntity;

public class DataItemWriter implements ItemWriter<DataEntity>{

    @Override
    public void write(@NonNull Chunk<? extends DataEntity> chunk) throws Exception {
        throw new UnsupportedOperationException("Unimplemented method 'write'");
    }
    
}
