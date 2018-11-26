package org.gw.standstrong.importfile;

import java.util.Optional;

public interface ImportFileService  {

    void save(ImportFile importFile);
    Optional<ImportFile> find(Long jobExecutionId);
}
