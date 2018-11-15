package org.gw.standstrong.domain.data.proximity;

import com.itglance.base.BaseServiceImpl;
import org.springframework.stereotype.Service;

import javax.inject.Inject;

@Service
public class ProximityServiceImpl extends BaseServiceImpl<Proximity> implements ProximityService {

    @Inject
    public ProximityServiceImpl(ProximityRepository proximityRepository) {
        super(proximityRepository);
    }
}
