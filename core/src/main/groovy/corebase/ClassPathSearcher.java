package corebase;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Map;
import java.util.TreeMap;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class ClassPathSearcher {
    private static Log log = LogFactory.getLog(ClassPathSearcher.class);
    private boolean show;

    public Map<String, InputStream> findFilesInClassPath(String fileNamePattern) {
        return findFilesInClassPath(fileNamePattern, true);
    }
    public Map<String, InputStream> findFilesInClassPath(String fileNamePattern, boolean show) {
        this.show = show;
        Map<String, InputStream> result = new TreeMap<String, InputStream>();
        String classPath = System.getProperty("java.class.path");
        String[] pathElements = classPath.split(System
                .getProperty("path.separator"));
        for (String element : pathElements) {
            if(show){
                log.debug(element);
            }
            try {
                File newFile = new File(element);
                if (newFile.isDirectory()) {
                    result.putAll(findResourceInDirectory(newFile,
                            fileNamePattern));
                } else {
                    result.putAll(findResourceInFile(newFile, fileNamePattern));
                }
            } catch (IOException e) {
                log.error("Exception:", e);
            }
        }
        return result;
    }

    private Map<String, InputStream> findResourceInFile(File resourceFile,
                                                        String fileNamePattern) throws IOException {
        Map<String, InputStream> result = new TreeMap<String, InputStream>();
        if (resourceFile.canRead()
                && resourceFile.getAbsolutePath().endsWith(".jar")) {
            if(show){
                log.debug("jar file found: " + resourceFile.getAbsolutePath());
            }
            JarFile jarFile = new JarFile(resourceFile);
            Enumeration<JarEntry> entries = jarFile.entries();
            while (entries.hasMoreElements()) {
                JarEntry singleEntry = entries.nextElement();
                if(show){
                    log.debug("jar entry: " + singleEntry.getName());
                }
                if (singleEntry.getName().matches(fileNamePattern)) {
                    result.put(jarFile.getName() + "/" + singleEntry.getName(),
                            jarFile.getInputStream(singleEntry));
                }
            }
        }
        return result;
    }

    private Map<String, InputStream> findResourceInDirectory(File directory,
                                                             String fileNamePattern) throws IOException {
        Map<String, InputStream> result = new TreeMap<String, InputStream>();
        File[] files = directory.listFiles();
        for (File currentFile : files) {
            if(show){
                log.debug("current file name: " + currentFile.getAbsolutePath());
            }
            if (currentFile.isDirectory()) {

                result.putAll(findResourceInDirectory(currentFile,
                        fileNamePattern));
            } else if (currentFile.getAbsolutePath().matches(fileNamePattern)) {
                result.put(currentFile.getAbsolutePath(), new FileInputStream(
                        currentFile));
            } else {
                result.putAll(findResourceInFile(currentFile, fileNamePattern));
            }
        }
        return result;
    }
}
