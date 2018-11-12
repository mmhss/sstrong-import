package org.gw.standstrong.proximity;

import lombok.extern.slf4j.Slf4j;
import org.gw.standstrong.mother.MotherRepository;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
public class ProximityItemProcesser implements ItemProcessor<Proximity, Proximity> {
    
    @Autowired
    private MotherRepository motherRepository;

    @Override
    public Proximity process(final Proximity proximity) throws Exception {
        final double rssi = proximity.getRssi();
        final String recordedDateTime = proximity.getRecordedDateTime();
        final String motherIdentificationNumber = proximity.getMotherIdentificationNumber();
        final Long motherId = 1L;

        System.out.println(motherId);

        final Proximity transformedProximity = new Proximity(rssi, recordedDateTime, motherIdentificationNumber, motherId);

        log.info("Converting (" + proximity + ") into (" + transformedProximity + ")");

        return transformedProximity;
    }

}
