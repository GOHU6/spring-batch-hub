package com.example.spring_batch_chunk.config;


import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemReader;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import com.example.spring_batch_chunk.model.DataEntity;
import com.example.spring_batch_chunk.reader.ItemReaderStep;
import com.example.spring_batch_chunk.repository.ItemRepository;
import com.example.spring_batch_chunk.writer.ItemWriterStep;

@Configuration
public class SpringBatchConfiguration{
    
    private static final Logger logger = LoggerFactory.getLogger(SpringBatchConfiguration.class);
	
    ItemRepository repository;

	@Bean
	public ApplicationRunner jobRunner(JobLauncher jobLauncher, Job job) {
	    return args -> {
	        JobParameters jobParameters = new JobParametersBuilder()
	            .addLong("timestamp", System.currentTimeMillis())
	            .toJobParameters();

	        jobLauncher.run(job, jobParameters);
	    };
	}

    @Bean
	public Job batchjob(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
		return new JobBuilder("batchjob",jobRepository)
				.incrementer(new RunIdIncrementer())
				.listener(joblistener())
                .start(stepDataBuilder(jobRepository, transactionManager))
                .build();
	}

    @Bean
	public JobExecutionListener joblistener() {
		return new JobExecutionListener() {

			@Override
			public void beforeJob(JobExecution jobExecution) {
				logger.info("~~~~ Job has started ~~~~\n");
				logger.info("Job started at: "+ jobExecution.getStartTime());
				logger.info("Status of the Job: "+jobExecution.getStatus());
			}

			@Override
			public void afterJob(JobExecution jobExecution) {
				logger.info("~~~~ Finishing the job ~~~~\n");
				logger.info("Job Ended at: "+ jobExecution.getEndTime());
				logger.info("Status of the Job: "+jobExecution.getStatus());
				
				if (jobExecution.getStatus().isUnsuccessful()) {
	                System.exit(1);
	                logger.info("~~~~ Error in the job ~~~~");
	            } else {
	            	logger.info("~~~~ Exit ~~~~");
	            	System.exit(0);
	            }
			}

		};
	}

    @Bean
	public Step stepDataBuilder(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
		return new StepBuilder("stepDataBuilder",jobRepository)
				.<DataEntity, DataEntity> chunk(10 ,transactionManager)
				.allowStartIfComplete(true)
				.reader(readerStep())
				.writer(writerStep())
				.build();
	}

    /*Reader*/
    @Bean
    @StepScope
    public ItemReader<DataEntity> readerStep() {
        List<DataEntity> items = new ArrayList<>();
		items = repository.findAllList();
        return new ItemReaderStep(items);
    }
    
    /*Writer*/
    @Bean
    @StepScope
    public ItemWriterStep writerStep() {
        return new ItemWriterStep();
    }
}
