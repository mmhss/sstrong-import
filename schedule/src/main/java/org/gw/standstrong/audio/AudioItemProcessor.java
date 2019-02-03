package org.gw.standstrong.audio;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@JobScope
public class AudioItemProcessor implements ItemProcessor<Audio, Audio> {

    @Value("#{jobParameters['MOTHER_ID']}")
    private Long motherId;

    @Override
    public Audio process(final Audio audio) throws Exception {


        final Audio transformedAudio = new Audio(audio.getCaptureDate()
                , audio.getAudioType()
                , audio.getFilename()
                , motherId
        );

        log.info("Converting (" + audio + ") into (" + transformedAudio + ")");

        return transformedAudio;
    }
}

