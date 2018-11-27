package org.gw.standstrong.gps;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@JobScope
public class GpsItemProcessor implements ItemProcessor<Gps, Gps> {

    @Value("#{jobParameters['MOTHER_ID']}")
    private Long motherId;

    @Override
    public Gps process(final Gps gps) throws Exception {


        final Gps transformedGps = new Gps( gps.getCaptureDate()
                , gps.getAndroidId()
                , gps.getDataType()
                , gps.getLatitude()
                , gps.getLongitude()
                , gps.getAccuracy()
                , gps.getAltitude()
                , motherId
        );

        log.info("Converting (" + gps + ") into (" + transformedGps + ")");

        return transformedGps;
    }
}

