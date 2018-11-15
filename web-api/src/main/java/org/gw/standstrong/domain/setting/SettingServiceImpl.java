package org.gw.standstrong.domain.setting;

import com.itglance.base.BaseServiceImpl;
import org.springframework.stereotype.Service;

import javax.inject.Inject;

@Service
public class SettingServiceImpl extends BaseServiceImpl<Setting> implements SettingService {

    @Inject
    public SettingServiceImpl(SettingRepository settingRepository){
        super(settingRepository);
    }
}
