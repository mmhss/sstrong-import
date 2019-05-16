package org.gw.standstrong.importfile;

import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.Optional;

@Service
public class ImportFileServiceImpl implements ImportFileService {

    private ImportFileRepository importFileRepository;

    @Inject
    public ImportFileServiceImpl(ImportFileRepository importFileRepository) {
        this.importFileRepository = importFileRepository;
    }

    @Override
    public void save(ImportFile importFile) {
        importFileRepository.save(importFile);
    }

    @Override
    public Optional<ImportFile> find(Long jobExecutionId) {
        return importFileRepository.findById(importFileRepository.findByJobExecution(jobExecutionId));
    }

    @Override
    public boolean exists(String filename) {
        return importFileRepository.exists(filename);
    }

}
