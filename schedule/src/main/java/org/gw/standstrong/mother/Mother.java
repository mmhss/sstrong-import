package org.gw.standstrong.mother;

import com.itglance.base.BaseEntity;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Data
@Entity
@Table(name = "mother")
public class Mother extends BaseEntity {

    @Column(name="identification_number")
    private String identificationNumber;
}
