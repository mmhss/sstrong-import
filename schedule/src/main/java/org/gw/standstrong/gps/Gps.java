package org.gw.standstrong.gps;

import com.itglance.base.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Gps extends BaseEntity {

    private String captureDate;
    private String androidId;
    private String dataType;
    private String latitude;
    private String longitude;
    private String accuracy;
    private String altitude;
    private Long motherId;
}