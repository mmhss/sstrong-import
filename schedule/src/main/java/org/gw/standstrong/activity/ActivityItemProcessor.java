package org.gw.standstrong.activity;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@JobScope
public class ActivityItemProcessor implements ItemProcessor<Activity, Activity> {

    @Value("#{jobParameters['MOTHER_ID']}")
    private Long motherId;

    @Override
    public Activity process(final Activity activity) throws Exception {


        final Activity transformedActivity = new Activity( activity.getCaptureDate()
                , activity.getAndroidId()
                , activity.getDataType()
                , activity.getActivityType()
                , activity.getConfidence()
                , motherId
        );

        log.info("Converting (" + activity + ") into (" + transformedActivity + ")");

        return transformedActivity;
    }
}

