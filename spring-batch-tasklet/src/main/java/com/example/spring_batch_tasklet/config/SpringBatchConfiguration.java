package com.example.spring_batch_tasklet.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import com.example.spring_batch_tasklet.reader.ItemReaderStep;
import com.example.spring_batch_tasklet.writer.ItemWriterStep;

@Configuration
public class SpringBatchConfiguration {
    
    private static final Logger logger = LoggerFactory.getLogger(SpringBatchConfiguration.class);
	
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
		
    public SpringBatchConfiguration(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        this.jobRepository = jobRepository;
        this.transactionManager = transactionManager;
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
	public Job batchjob() {
		return new JobBuilder("batchjob",jobRepository)
				.incrementer(new RunIdIncrementer())
				.listener(joblistener())
                .start(reader())
                .next(writer())
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

    /*Reader*/
    @Bean
    @JobScope
    public ItemReaderStep readerStep() {
        return new ItemReaderStep();
    }
    
    @Bean
    public Step reader() {
        return new StepBuilder("reader", jobRepository)
                .tasklet(readerStep(), transactionManager)
                .build();
    }
    
    /*Writer*/
    @Bean
    @JobScope
    public ItemWriterStep writerStep() {
    	ItemWriterStep writerStep = new ItemWriterStep();
        writerStep.setData(readerStep().getData());
        return writerStep;
    }


    @Bean
    public Step writer() {
        return new StepBuilder("writer", jobRepository)
                .tasklet(writerStep(), transactionManager)
                .build();
    }
}
