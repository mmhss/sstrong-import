package org.gw.standstrong.proximity;

import lombok.extern.slf4j.Slf4j;
import org.gw.standstrong.mother.MotherRepository;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
public class ProximityItemProcessor implements ItemProcessor<Proximity, Proximity> {
    
    @Autowired
    private MotherRepository motherRepository;

    @Override
    public Proximity process(final Proximity proximity) throws Exception {
        final String captureDate = proximity.getCaptureDate();
        final boolean visible = proximity.isVisible();
        final String androidId = proximity.getAndroidId();
        final Long motherId = 1L;

        System.out.println(motherId);

        final Proximity transformedProximity = new Proximity(captureDate, androidId, visible, motherId);

        log.info("Converting (" + proximity + ") into (" + transformedProximity + ")");

        return transformedProximity;
    }

}
