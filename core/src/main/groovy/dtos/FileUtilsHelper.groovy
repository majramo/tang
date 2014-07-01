package dtos

import org.testng.SkipException

class FileUtilsHelper {

    public File loadResourceFile(String fileName, boolean failOnError = true) {
        println fileName
        URL url = this.class.getResource(fileName);
        println url
        if (url != null) {
            return new File(url.toURI())
        }
        if(failOnError){
            throw new SkipException("Can't load file $fileName")
        }
    }

    public File loadResourceFileIfExists(String fileName) {
        loadResourceFile(fileName, false)
    }
}
