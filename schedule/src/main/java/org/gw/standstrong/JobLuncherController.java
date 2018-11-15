package org.gw.standstrong;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.configuration.JobLocator;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class JobLuncherController {

    @Autowired
    JobLauncher jobLauncher;

    @Autowired
    JobLocator jobLocator;

    @RequestMapping("/launchjob1")
    public String launchjob1() throws Exception {
        try {
            JobParameters jobParameters = new JobParametersBuilder().addString("JOB_NAME", "importProximityJob").addLong("time", System.currentTimeMillis()).toJobParameters();
            Job job = jobLocator.getJob("importProximityJob");
            jobLauncher.run(job, jobParameters);
        } catch (Exception e) {
            log.info(e.getMessage());
        }

        return "Done";
    }

    @RequestMapping("/launchjob2")
    public String launchjob2() throws Exception {
        try {
            JobParameters jobParameters = new JobParametersBuilder().addString("JOB_NAME", "importCallLogJob").addLong("time", System.currentTimeMillis()).toJobParameters();
            Job job = jobLocator.getJob("importCallLogJob");
            jobLauncher.run(job, jobParameters);
        } catch (Exception e) {
            log.info(e.getMessage());
        }

        return "Done";
    }
}
