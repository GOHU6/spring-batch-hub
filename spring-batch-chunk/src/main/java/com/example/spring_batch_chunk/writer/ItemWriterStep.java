package com.example.spring_batch_chunk.writer;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;

import com.example.spring_batch_chunk.model.DataEntity;

public class ItemWriterStep implements ItemWriter<DataEntity>{
    
    private static final Logger logger = LoggerFactory.getLogger(ItemWriterStep.class);
  
    private List<DataEntity> data = new ArrayList<DataEntity>();

    @Override
	public void write(Chunk<? extends DataEntity> chunk) throws Exception {
       
        try {

            logger.info("~~~~ ItemWriterStep ~~~~\n");

            // For debug
			this.data.addAll(chunk.getItems());

            int index = 1;
            for (DataEntity entity : data) {
                logger.info("#{} - {}", index++, entity.toString());
            }

        } catch (Exception e) {
			logger.error("Error while writing data", e);
			throw e;
        }
    }
}
