package org.gw.standstrong;

import lombok.extern.slf4j.Slf4j;
import org.gw.standstrong.importfile.ImportFileService;
import org.gw.standstrong.mother.MotherService;
import org.gw.standstrong.project.Project;
import org.gw.standstrong.project.ProjectRepository;
import org.gw.standstrong.s3.S3Storage;
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
import java.io.IOException;
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

    @Autowired
    ImportFileService importFileService;

    public static void main(String[] args) throws Exception {
        SpringApplication.run(StandStrongScheduler.class, args);
    }

    @Bean
    public JobRegistryBeanPostProcessor jobRegistryBeanPostProcessor(JobRegistry jobRegistry) {
        JobRegistryBeanPostProcessor jobRegistryBeanPostProcessor = new JobRegistryBeanPostProcessor();
        jobRegistryBeanPostProcessor.setJobRegistry(jobRegistry);
        return jobRegistryBeanPostProcessor;
    }

    /*
    Running the hourly from 7AM to 11PM
     */
    @Scheduled(cron="0 0/15 * * * *")
    public void runJobs(){

        try {
            S3Storage.download(projectRepository.findAll().get(0).getInboundFolder());
        } catch (IOException e) {
            e.printStackTrace();
        }


        log.info("--------------------------------------");
        log.info("Beginning of running all the jobs:");
        log.info("--------------------------------------");


        log.info("========Running GPS Job==============================");
        runGpsJob();
        log.info("========End of GPS Job==============================");

        log.info("========Running Proximity Job==============================");
        runProximityJob();
        log.info("========End of Proximity Job==============================");

        log.info("========Running Activity Job==============================");
        runActivityJob();
        log.info("========End of Activity Job==============================");

        log.info("========Running Audio Job==============================");
        runAudioJob();
        log.info("========End of Audio Job==============================");

        log.info("--------------------------------------");
        log.info("End of running all the jobs:");
        log.info("--------------------------------------");
    }

    public void runGpsJob() {

        try {

            List<Project> projects = projectRepository.findAll();

            File[] files = FileUtils.getFiles(projects.get(0).getInboundFolder(), "GPS");

            for (File file : files) {

                if(importFileService.exists(file.getName())){
                    log.info("The file {} exists already. If you would like to reload the file due to errors then fix the status to AUDIT_CANCELLED" , file.getName());
                    continue;
                }


                final Long motherId = motherService.getMotherId(file.getName(), "-");

                if (motherId != null && motherId > 0) {

                    JobParameters jobParameters = new JobParametersBuilder()
                            .addString("JOB_NAME", "importGpsJob")
                            .addString("FILE", file.getName())
                            .addLong("MOTHER_ID", motherId)
                            .addLong("time", System.currentTimeMillis()).toJobParameters();
                    Job job = jobLocator.getJob("importGpsJob");
                    jobLauncher.run(jobRegistry.getJob(job.getName()), jobParameters);

                } else {

                    log.error("Unable to find mother id.");

                }
            }

        } catch (Exception e) {
            log.info(e.getMessage());
        }
    }

    public void runProximityJob() {

        try {

            List<Project> projects = projectRepository.findAll();

            File[] files = FileUtils.getFiles(projects.get(0).getInboundFolder(), "PROXIMITY");

            for (File file : files) {

                if(importFileService.exists(file.getName())){
                    log.info("The file {} exists already. If you would like to reload the file due to errors then fix the status to AUDIT_CANCELLED" , file.getName());
                    continue;
                }

                final Long motherId = motherService.getMotherId(file.getName(), "-");

                if (motherId != null && motherId > 0) {

                    JobParameters jobParameters = new JobParametersBuilder()
                            .addString("JOB_NAME", "importProximityJob")
                            .addString("FILE", file.getName())
                            .addLong("MOTHER_ID", motherId)
                            .addLong("time", System.currentTimeMillis()).toJobParameters();
                    Job job = jobLocator.getJob("importProximityJob");
                    jobLauncher.run(jobRegistry.getJob(job.getName()), jobParameters);

                } else {

                    log.error("Unable to find mother id.");

                }
            }

        } catch (Exception e) {
            log.info(e.getMessage());
        }
    }

    public void runActivityJob() {

        try {

            List<Project> projects = projectRepository.findAll();

            File[] files = FileUtils.getFiles(projects.get(0).getInboundFolder(), "ACTIVITY");

            for (File file : files) {

                if(importFileService.exists(file.getName())){
                    log.info("The file {} exists already. If you would like to reload the file due to errors then fix the status to AUDIT_CANCELLED" , file.getName());
                    continue;
                }


                final Long motherId = motherService.getMotherId(file.getName(), "-");

                if (motherId != null && motherId > 0) {

                    JobParameters jobParameters = new JobParametersBuilder()
                            .addString("JOB_NAME", "importActivityJob")
                            .addString("FILE", file.getName())
                            .addLong("MOTHER_ID", motherId)
                            .addLong("time", System.currentTimeMillis()).toJobParameters();
                    Job job = jobLocator.getJob("importActivityJob");
                    jobLauncher.run(jobRegistry.getJob(job.getName()), jobParameters);

                } else {

                    log.error("Unable to find mother id.");

                }
            }

        } catch (Exception e) {
            log.info(e.getMessage());
        }
    }

    public void runAudioJob() {

        try {

            List<Project> projects = projectRepository.findAll();

            File[] files = FileUtils.getFiles(projects.get(0).getInboundFolder(), "AUDIO");

            for (File file : files) {

                if(importFileService.exists(file.getName())){
                    log.info("The file {} exists already. If you would like to reload the file due to errors then fix the status to AUDIT_CANCELLED" , file.getName());
                    continue;
                }

                final Long motherId = motherService.getMotherId(file.getName(), "-");

                if (motherId != null && motherId > 0) {

                    JobParameters jobParameters = new JobParametersBuilder()
                            .addString("JOB_NAME", "importAudioJob")
                            .addString("FILE", file.getName())
                            .addLong("MOTHER_ID", motherId)
                            .addLong("time", System.currentTimeMillis()).toJobParameters();
                    Job job = jobLocator.getJob("importAudioJob");
                    jobLauncher.run(jobRegistry.getJob(job.getName()), jobParameters);

                } else {

                    log.error("Unable to find mother id.");

                }
            }

        } catch (Exception e) {
            log.info(e.getMessage());
        }
    }
}