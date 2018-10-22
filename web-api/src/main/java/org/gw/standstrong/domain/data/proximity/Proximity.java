package org.gw.standstrong.domain.data.proximity;

import com.itglance.base.BaseEntity;
import lombok.Data;
import org.gw.standstrong.domain.data.importfile.ImportFile;
import org.gw.standstrong.domain.mother.Mother;
import org.gw.standstrong.domain.project.Project;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name="proximity")
public class Proximity extends BaseEntity {

    @Column(name="rssi")
    private double rssi;

    @Column(name="distance")
    private double distance;

    @Column(name="proximity_level")
    private String proximityLevel;

    @Column(name="recorded_date_time")
    private LocalDateTime recordedDateTime;

    @ManyToOne(optional = false)
    @JoinColumn(name = "mother_id")
    private Mother mother;
}
