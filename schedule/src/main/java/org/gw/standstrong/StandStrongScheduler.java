package org.gw.standstrong;

import lombok.extern.slf4j.Slf4j;
import org.gw.standstrong.mother.MotherService;
import org.gw.standstrong.project.Project;
import org.gw.standstrong.project.ProjectRepository;
import org.gw.standstrong.utils.FileUtils;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.configuration.JobLocator;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.support.JobRegistryBeanPostProcessor;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.io.File;
import java.util.List;

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

    @Autowired
    ProjectRepository projectRepository;

    @Autowired
    MotherService motherService;

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

            List<Project> projects  = projectRepository.findAll();

            File[] files= FileUtils.getFiles(projects.get(0).getInboundFolder(),"Call");

            for (File file: files) {

                final Long motherId = motherService.getMotherId(file.getName(), "-");

                if(motherId !=null && motherId > 0) {

                    JobParameters jobParameters = new JobParametersBuilder()
                            .addString("JOB_NAME", "importCallLogJob")
                            .addString("FILE", file.getName())
                            .addLong("MOTHER_ID", motherId)
                            .addLong("time", System.currentTimeMillis()).toJobParameters();
                    Job job = jobLocator.getJob("importCallLogJob");
                    jobLauncher.run(jobRegistry.getJob(job.getName()), jobParameters);

                }else{

                    log.error("Unable to find mother id.");

                }
            }

        } catch (Exception e) {
            log.info(e.getMessage());
        }

    }



}