package org.gw.standstrong.mother;

import com.itglance.base.BaseRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MotherRepository extends BaseRepository<Mother> {

    @Query(value="SELECT id FROM mother where mother.identification_number = :identificationNumber", nativeQuery = true)
    Long findId(@Param("identificationNumber")String identificationNumber);
}
