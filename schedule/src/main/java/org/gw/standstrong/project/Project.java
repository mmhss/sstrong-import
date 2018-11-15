package org.gw.standstrong.project;

import com.itglance.base.BaseEntity;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Data
@Entity
@Table(name = "project")
public class Project extends BaseEntity {

    @Column(name="inbound_folder")
    private String inboundFolder;

    @Column(name="backup_folder")
    private String backupFolder;
}
