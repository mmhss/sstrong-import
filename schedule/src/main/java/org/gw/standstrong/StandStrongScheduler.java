package org.gw.standstrong;

import lombok.extern.slf4j.Slf4j;
import org.gw.standstrong.calllog.CallLogFlowDecision;
import org.gw.standstrong.utils.FileUtils;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.configuration.JobLocator;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.support.JobRegistryBeanPostProcessor;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.SimpleJobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.support.MapJobRepositoryFactoryBean;
import org.springframework.batch.support.transaction.ResourcelessTransactionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import javax.annotation.PropertyKey;

@SpringBootApplication
@EnableScheduling
@EnableBatchProcessing
@Slf4j
public class StandStrongScheduler {

    @Autowired
    JobRegistry jobRegistry;

    @Autowired
    JobLauncher jobLauncher;

    @Autowired
    JobLocator jobLocator;



    public static void main(String[] args) throws Exception {
        SpringApplication.run(StandStrongScheduler.class, args);
    }

    @Bean
    public JobRegistryBeanPostProcessor jobRegistryBeanPostProcessor(JobRegistry jobRegistry) {
        JobRegistryBeanPostProcessor jobRegistryBeanPostProcessor = new JobRegistryBeanPostProcessor();
        jobRegistryBeanPostProcessor.setJobRegistry(jobRegistry);
        return jobRegistryBeanPostProcessor;
    }

    @Scheduled(fixedDelay = 100000)
    public void runJobs(){

        try {

            JobParameters jobParameters = new JobParametersBuilder().addString("JOB_NAME", "importProximityJob")
                    .addLong("time", System.currentTimeMillis())
                    .toJobParameters();
            Job job = jobLocator.getJob("importProximityJob");
            jobLauncher.run(job, jobParameters);
        } catch (Exception e) {
            log.info(e.getMessage());
        }


        try {

            Resource[] resources= FileUtils.getResources("*.txt");

            for (Resource resource: resources) {
                JobParameters jobParameters = new JobParametersBuilder()
                        .addString("JOB_NAME", "importCallLogJob")
                        .addString("FILE",resource.getFilename() )
                        .addLong("time", System.currentTimeMillis()).toJobParameters();
                Job job = jobLocator.getJob("importCallLogJob");
                jobLauncher.run(jobRegistry.getJob(job.getName()), jobParameters);
            }

        } catch (Exception e) {
            log.info(e.getMessage());
        }

    }

}