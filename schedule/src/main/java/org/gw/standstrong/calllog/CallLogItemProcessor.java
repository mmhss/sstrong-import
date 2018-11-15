package org.gw.standstrong.calllog;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@JobScope
public class CallLogItemProcessor implements ItemProcessor<CallLog, CallLog> {

    @Value("#{jobParameters['MOTHER_ID']}")
    private Long motherId;

    @Override
    public CallLog process(final CallLog callLog) throws Exception {


        final CallLog transformedCallLog = new CallLog( callLog.getCaptureDate()
                , callLog.getAndroidId()
                , callLog.getDataType()
                , callLog.getCallId()
                , callLog.getPhoneNumber()
                , callLog.getDirection()
                , callLog.getActionDate()
                , callLog.getDuration()
                , motherId
        );

        log.info("Converting (" + callLog + ") into (" + transformedCallLog + ")");

        return transformedCallLog;
    }
}

