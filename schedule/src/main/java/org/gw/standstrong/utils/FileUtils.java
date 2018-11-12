package org.gw.standstrong.utils;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

import java.io.IOException;

public class FileUtils {

    public static Resource[] getResources(String pattern) {

        Resource[] resources = new Resource[0];

        ClassPathResource a = new ClassPathResource("input/");
        ResourceLoader rl = new ResourceLoader() {
            @Override
            public Resource getResource(String location) {
                return a;
            }

            @Override
            public ClassLoader getClassLoader() {
                return null;
            }
        };
        ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver(rl);
        try {
            return resolver.getResources(pattern);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return resources;
    }
}
