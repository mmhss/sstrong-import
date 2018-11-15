package org.gw.standstrong.domain.data.Gps;

import com.itglance.base.BaseEntity;
import lombok.Data;
import org.gw.standstrong.domain.mother.Mother;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name="gps")
public class Gps extends BaseEntity {


    @Column(name="longitude")
    private double longitude;

    @Column(name="latitude")
    private double latitude;

    @Column(name="altitude")
    private double altitude;

    @Column(name="accuracy")
    private double accuracy;

    @Column(name="recorded_date_time")
    private LocalDateTime recordedDateTime;

    @ManyToOne(optional = false)
    @JoinColumn(name = "mother_id")
    private Mother mother;
}
