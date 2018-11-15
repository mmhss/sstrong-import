package org.gw.standstrong.domain.project;


import com.itglance.base.BaseEntity;
import lombok.Data;
import org.gw.standstrong.domain.mother.Mother;
import org.gw.standstrong.domain.setting.Setting;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;


@Data
@Entity
@Table(name="project")
public class Project extends BaseEntity {

    @Column(name="name")
    private String name;

    @Column(name="location")
    private String location;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "completion_date")
    private LocalDate completionDate;

    @OneToOne(mappedBy = "project", cascade = CascadeType.ALL)
    private Setting setting;

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL)
    private List<Mother> mothers;




}
