package com.example.spring_batch_tasklet.writer;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import com.example.spring_batch_tasklet.model.DataEntity;

public class ItemWriterStep implements Tasklet{

   	private static final Logger logger = LoggerFactory.getLogger(ItemWriterStep.class);

    private List<DataEntity> data;

    public List<DataEntity> setData(List<DataEntity> data) {return this.data= data;};
    
    @Override
    @Nullable
    public RepeatStatus execute(@NonNull StepContribution contribution, @NonNull ChunkContext chunkContext) throws Exception {
    
        try {
            logger.info("~~~~ ItemWriterStep ~~~~\n");

            // For debug
            int index = 1;
            for (DataEntity entity : data) {
                logger.info("#{} - {}", index++, entity.toString());
            }

        } catch (Exception e) {
			logger.error("Error while writing data", e);
			throw e;
        }
        
        return RepeatStatus.FINISHED;
    }
}