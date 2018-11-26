package org.gw.standstrong.calllog;

import lombok.extern.slf4j.Slf4j;
import org.gw.standstrong.importfile.ImportFile;
import org.gw.standstrong.importfile.ImportFileService;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.listener.JobExecutionListenerSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Optional;

@Component
@Slf4j
public class CallLogJobCompletionNotificationListener extends JobExecutionListenerSupport {

    private final JdbcTemplate jdbcTemplate;
    private final ImportFileService importFileService;

    @Autowired
    public CallLogJobCompletionNotificationListener(JdbcTemplate jdbcTemplate, ImportFileService importFileService) {
        this.jdbcTemplate = jdbcTemplate;
        this.importFileService = importFileService;
    }


    @Override
    public void beforeJob(JobExecution jobExecution) {
        log.info(jobExecution.getJobInstance().getJobName() + " has started.");

        ImportFile importFile = new ImportFile();
        importFile.setFilename(jobExecution.getJobParameters().getString("FILE"));
        importFile.setFileType(ImportFile.FILE_TYPE_CALL_LOG);
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
    }
}
