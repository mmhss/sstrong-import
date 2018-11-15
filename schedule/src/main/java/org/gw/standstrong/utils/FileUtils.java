package org.gw.standstrong.utils;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;

public class FileUtils {

    public static File[] getFiles(String folderPath, String identifier) {

        File f = new File(folderPath);
        File[] files = f.listFiles(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.contains(identifier);
            }
        });

        return files;
    }
}
