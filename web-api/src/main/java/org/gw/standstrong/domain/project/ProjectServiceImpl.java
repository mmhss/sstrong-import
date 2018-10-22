package org.gw.standstrong.domain.project;

import com.itglance.base.BaseServiceImpl;
import org.springframework.stereotype.Service;

import javax.inject.Inject;

@Service
public class ProjectServiceImpl extends BaseServiceImpl<Project> implements ProjectService {

    @Inject
    public ProjectServiceImpl(ProjectRepository projectRepository) {
        super(projectRepository);
    }
}
