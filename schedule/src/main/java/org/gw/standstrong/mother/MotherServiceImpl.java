package org.gw.standstrong.mother;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.inject.Inject;

@Service
public class MotherServiceImpl implements MotherService {

    private MotherRepository motherRepository;

    @Inject
    public MotherServiceImpl(MotherRepository motherRepository) {
        this.motherRepository = motherRepository;
    }

    public Long getMotherId(String filename, String delimiter) {

        final String[] parts = StringUtils.split(filename, delimiter);

        if (parts.length > 0) {
            return motherRepository.findId(parts[0]);
        }

        return null;
    }
}
