package org.gw.standstrong.activity;

import com.itglance.base.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Activity extends BaseEntity {

    private String captureDate;
    private String androidId;
    private String dataType;
    private String activityType;
    private double confidence;
    private Long motherId;
}