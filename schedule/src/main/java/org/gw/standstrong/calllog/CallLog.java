package org.gw.standstrong.calllog;

import com.itglance.base.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class CallLog extends BaseEntity {

    private String captureDate;
    private String androidId;
    private String dataType;
    private String callId;
    private String phoneNumber;
    private String direction;
    private String actionDate;
    private int duration;
    private Long motherId;
}