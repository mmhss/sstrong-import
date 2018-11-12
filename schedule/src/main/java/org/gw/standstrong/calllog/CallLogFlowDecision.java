package org.gw.standstrong.calllog;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.job.flow.FlowExecutionStatus;
import org.springframework.batch.core.job.flow.JobExecutionDecider;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.LinkedBlockingQueue;

@Component("decider")
@Slf4j
public class CallLogFlowDecision implements JobExecutionDecider {

    public static final String COMPLETED = "COMPLETED";
    public static final String FAILED = "FAILED";

    public static final String INPUT_FILE = "input.file";
    public static final String INPUT_FILES = "input.files";
    public static final String DELIMITER = ",";

    private Queue<String> inputFiles;

    @Override
    public FlowExecutionStatus decide(JobExecution jobExecution,StepExecution stepExecution) {
        //check if the jobExecution has the input.file in it's context
        if (!jobExecution.getExecutionContext().containsKey(INPUT_FILE)) {
            //build the queue
            inputFiles = new LinkedBlockingQueue<String>(Arrays.asList("input/SS1001-Call_Log-20181028.txt"));
        }//end if
        //pop and add
        String file = inputFiles.poll();
        if (file != null) {
            jobExecution.getExecutionContext().put(INPUT_FILE, file);
            return FlowExecutionStatus.UNKNOWN;
        }//end if
        //return 'done'
        return FlowExecutionStatus.COMPLETED;
    }
}