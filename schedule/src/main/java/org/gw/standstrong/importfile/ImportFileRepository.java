package org.gw.standstrong.importfile;

import com.itglance.base.BaseRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ImportFileRepository extends BaseRepository<ImportFile> {

    @Query(value="SELECT id FROM import_file where job_execution_id= :job_execution_id", nativeQuery = true)
    Long findByJobExecution(@Param("job_execution_id") Long jobExecutionId);

    @Query(value = "SELECT CASE WHEN (COUNT(importFile) > 0) THEN TRUE ELSE FALSE END FROM ImportFile importFile WHERE importFile.filename = :filename AND importFile.status <> 'AUDIT_CANCELLED'")
    boolean exists(@Param("filename") String filename);
}
