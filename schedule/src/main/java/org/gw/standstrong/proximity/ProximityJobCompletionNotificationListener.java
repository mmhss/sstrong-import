package org.gw.standstrong.proximity;

import lombok.extern.slf4j.Slf4j;
import org.gw.standstrong.importfile.ImportFile;
import org.gw.standstrong.importfile.ImportFileService;
import org.gw.standstrong.project.Project;
import org.gw.standstrong.project.ProjectRepository;
import org.gw.standstrong.utils.FileUtils;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.listener.JobExecutionListenerSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Component
@Slf4j
public class ProximityJobCompletionNotificationListener extends JobExecutionListenerSupport {

    private final JdbcTemplate jdbcTemplate;
    private final ImportFileService importFileService;
    private final ProjectRepository projectRepository;

    @Autowired
    public ProximityJobCompletionNotificationListener(JdbcTemplate jdbcTemplate, ImportFileService importFileService, ProjectRepository projectRepository) {
        this.jdbcTemplate = jdbcTemplate;
        this.importFileService = importFileService;
        this.projectRepository = projectRepository;
    }

    @Override
    public void beforeJob(JobExecution jobExecution) {
        log.info(jobExecution.getJobInstance().getJobName() + " has started.");

        ImportFile importFile = new ImportFile();
        importFile.setFilename(jobExecution.getJobParameters().getString("FILE"));
        importFile.setFileType(ImportFile.FILE_TYPE_PROXIMITY);
        importFile.setImportDate(new Date());
        importFile.setMotherId(jobExecution.getJobParameters().getLong("MOTHER_ID"));
        importFile.setStatus(ImportFile.FILE_LOADING);
        importFile.setJobExecutionId(jobExecution.getId());
        importFileService.save(importFile);

        log.info("Saved import file");
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        if (jobExecution.getStatus() == BatchStatus.COMPLETED) {
            log.info("!!! JOB FINISHED! Time to verify the results");

            Optional<ImportFile> optionalImportFile = importFileService.find(jobExecution.getId());
            if(optionalImportFile.isPresent()){
                ImportFile importFile = optionalImportFile.get();
                importFile.setStatus(ImportFile.FILE_IMPORTED);
                importFileService.save(importFile);
            }
        }

        List<Project> projects = projectRepository.findAll();

        String filePath = projects.get(0).getInboundFolder();
        File file = new File(filePath + "/" + jobExecution.getJobParameters().getString("FILE"));
        FileUtils.deleteFile(file);
    }
}
