package com.example.spring_batch_tasklet.reader;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import com.example.spring_batch_tasklet.model.DataEntity;
import com.example.spring_batch_tasklet.repository.ItemRepository;

public class ItemReaderStep implements Tasklet{

   	private static final Logger logger = LoggerFactory.getLogger(ItemReaderStep.class);
    
    @Autowired
    ItemRepository repository;
    
    private List<DataEntity> data = new ArrayList<>();

    public List<DataEntity> getData() {return data;}

    @Override
    @Nullable
    public RepeatStatus execute(@NonNull StepContribution contribution, @NonNull ChunkContext chunkContext) throws Exception {
        
        try {

			data = repository.findAll();
            logger.info("Successfully read {} rows", data.size());
            
            // For debug
            int index = 1;
            for (DataEntity entity : data) {
                logger.info("#{} - {}", index++, entity.toString());
            }

		} catch (Exception e) {
			logger.error("Error while reading data", e);
			throw e;
		}
				
		return RepeatStatus.FINISHED;
    }
    
}
