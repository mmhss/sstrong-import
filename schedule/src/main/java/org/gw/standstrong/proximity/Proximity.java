package org.gw.standstrong.proximity;

import com.itglance.base.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Proximity extends BaseEntity {

    private String captureDate;
    private String androidId;
    private String dataType;
    private String event;
    private int value;
    private Long motherId;

}