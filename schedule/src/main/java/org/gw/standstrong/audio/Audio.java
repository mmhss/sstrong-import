package org.gw.standstrong.audio;

import com.itglance.base.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Audio extends BaseEntity {

    private String captureDate;
    private String audioType;
    private double accuracy;
    private String filename;
    private Long motherId;

}
