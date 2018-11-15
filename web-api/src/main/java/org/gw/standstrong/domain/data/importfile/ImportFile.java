package org.gw.standstrong.domain.data.importfile;

import com.itglance.base.BaseEntity;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "import_file")
public class ImportFile extends BaseEntity {

    @Column(name = "filename")
    private String filename;

    @Column(name = "status")
    private String status;

    @Column(name = "load_date_time")
    private LocalDateTime loadDateTime;

    @Column(name = "fileType")
    private String fileType;
}
