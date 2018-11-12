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
public class Proximity {

    private String captureDate;
    private String androidId;
    private boolean visible;
    private Long motherId;

}