package org.gw.standstrong.domain.mother;

import com.itglance.base.BaseServiceImpl;

import javax.inject.Inject;

public class MotherServiceImpl extends BaseServiceImpl<Mother> implements MotherService {

    @Inject
    public MotherServiceImpl(MotherRepository motherRepository){
        super(motherRepository);
    }
}
