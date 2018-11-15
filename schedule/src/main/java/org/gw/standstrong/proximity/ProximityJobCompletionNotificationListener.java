package org.gw.standstrong.proximity;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.listener.JobExecutionListenerSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ProximityJobCompletionNotificationListener extends JobExecutionListenerSupport {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public ProximityJobCompletionNotificationListener(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        if(jobExecution.getStatus() == BatchStatus.COMPLETED) {
            log.info("!!! JOB FINISHED! Time to verify the results");

            jdbcTemplate.query("SELECT capture_date, android_id, visible, mother_id FROM proximity" +
                            " inner join mother on mother.id= proximity.mother_id",
                (rs, row) -> new Proximity(
                        rs.getString(1),
                        rs.getString(2),
                        rs.getBoolean(3),
                        rs.getLong(4))
            ).forEach(proximity -> log.info("Found <" + proximity + "> in the database."));
        }
    }
}
