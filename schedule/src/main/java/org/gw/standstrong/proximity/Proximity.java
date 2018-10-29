package org.gw.standstrong.proximity;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Proximity {

    private double rssi;
    private String recordedDateTime;
    private String motherIdentificationNumber;
    private Long motherId;
}