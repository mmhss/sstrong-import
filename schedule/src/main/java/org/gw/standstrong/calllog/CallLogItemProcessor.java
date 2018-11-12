package org.gw.standstrong.calllog;


import lombok.extern.slf4j.Slf4j;
import org.gw.standstrong.mother.MotherRepository;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
public class CallLogItemProcessor implements ItemProcessor<CallLog, CallLog> {

    @Autowired
    private MotherRepository motherRepository;

    @Override
    public CallLog process(final CallLog callLog) throws Exception {


        final Long motherId = 1L;

        System.out.println(motherId);

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

