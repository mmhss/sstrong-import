package org.gw.standstrong.utils;

import java.io.File;
import java.io.FilenameFilter;

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

    public static boolean deleteFile(File file){

        if(file.delete())
        {
            return true;
        }
        else
        {
            return false;
        }
    }
}
