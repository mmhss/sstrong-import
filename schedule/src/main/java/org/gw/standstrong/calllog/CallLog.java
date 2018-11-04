package org.gw.standstrong.jobs.calllog;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class CallLog {

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