package dtos

import org.testng.SkipException

class FileUtilsHelper {

    public File loadResourceFile(String fileName) {
        URL url = this.class.getResource(fileName);
        if (url != null) {
            return new File(url.toURI())
        }
        throw new SkipException("Can't load file $fileName")
    }
}
