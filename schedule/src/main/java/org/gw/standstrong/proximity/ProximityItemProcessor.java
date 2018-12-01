package org.gw.standstrong.proximity;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@JobScope
public class ProximityItemProcessor implements ItemProcessor<Proximity, Proximity> {

    @Value("#{jobParameters['MOTHER_ID']}")
    private Long motherId;

    @Override
    public Proximity process(final Proximity proximity) throws Exception {

        final Proximity transformedProximity = new Proximity( proximity.getCaptureDate()
                , proximity.getAndroidId()
                , proximity.getDataType()
                , proximity.getEvent()
                , proximity.getValue()
                , motherId
        );

        log.info("Converting (" + proximity + ") into (" + transformedProximity + ")");

        return transformedProximity;
    }

}
