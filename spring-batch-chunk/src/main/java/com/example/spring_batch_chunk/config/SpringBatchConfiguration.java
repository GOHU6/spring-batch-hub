package com.example.spring_batch_chunk.config;

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
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import com.example.spring_batch_chunk.model.DataEntity;
import com.example.spring_batch_chunk.reader.DataItemReader;
import com.example.spring_batch_chunk.repository.ItemRepository;
import com.example.spring_batch_chunk.writer.DataItemWriter;

@Configuration
public class SpringBatchConfiguration {

    private static final Logger logger = LoggerFactory.getLogger(SpringBatchConfiguration.class);

    @Autowired
	private List<DataEntity> data;

	@Bean
	@StepScope
	protected ItemReader<DataEntity> itemReader(ItemRepository repository) {
		data = repository.findAll();
		logger.info("Sending {} data", data.size());
		return new DataItemReader(data);
	}

	@Bean
	protected ItemWriter<DataEntity> itemWriter() {
		return new DataItemWriter();
	}

	@Bean
	public Step stepDataBuilder(JobRepository jobRepository, PlatformTransactionManager transactionManager, ItemRepository repository) {
		return new StepBuilder("stepDataBuilder",jobRepository)
				.<DataEntity, DataEntity> chunk(2 ,transactionManager)
				.allowStartIfComplete(true)
				.reader(itemReader(repository))
				.writer(itemWriter())
				.build();
	}

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
	public Job batchjob(JobRepository jobRepository, PlatformTransactionManager transactionManager, ItemRepository repository) {
		return new JobBuilder("batchjob",jobRepository)
				.incrementer(new RunIdIncrementer())
				.listener(joblistener())
                .start(stepDataBuilder(jobRepository, transactionManager, repository))
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
}
