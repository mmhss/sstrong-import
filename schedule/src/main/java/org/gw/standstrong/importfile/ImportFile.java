package org.gw.standstrong.importfile;

import com.itglance.base.BaseEntity;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;

@Data
@Entity
@Table(name="import_file")
public class ImportFile extends BaseEntity {

    public static final String FILE_LOADING = "LOADING";
    public static final String FILE_IMPORTED = "IMPORTED";
    public static final String FILE_FAILED = "FAIL";

    public static final String FILE_TYPE_ACTIVITY = "ACTIVITY";
    public static final String FILE_TYPE_AUDIO = "AUDIO";
    public static final String FILE_TYPE_CALL_LOG = "CALL_LOG";
    public static final String FILE_TYPE_GPS = "GPS";
    public static final String FILE_TYPE_PROXIMITY = "PROXIMITY";
    public static final String FILE_TYPE_SMS_LOG = "SMS_LOG";

    @Column(name="filename", nullable = false)
    private String filename;

    @Column(name="status", nullable = false)
    private String status;

    @Column(name="import_date", nullable = false)
    private Date importDate;

    @Column(name="file_type", nullable = false)
    private String fileType;

    @Column(name="mother_id", nullable = false)
    private Long motherId;

    @Column(name="job_execution_id", nullable = false)
    private Long jobExecutionId;
}
