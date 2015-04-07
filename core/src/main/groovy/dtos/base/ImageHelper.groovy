package dtos.base

import dtos.SettingsHelper
import org.testng.ITestContext
import org.testng.Reporter
import org.testng.SkipException

import java.sql.SQLException
import java.text.DateFormat
import java.text.SimpleDateFormat
import static corebase.GlobalConstants.*

/*
PerceptualDiff version 1.1.1, Copyright (C) 2006 Yangli Hector Yee
PerceptualDiff comes with ABSOLUTELY NO WARRANTY;
This is free software, and you are welcome
to redistribute it under certain conditions;
See the GPL page for details: http://www.gnu.org/copyleft/gpl.html

PeceptualDiff image1.tif image2.tif

   Compares image1.tif and image2.tif using a perceptually based image metric
   Options:
	-verbose       : Turns on verbose mode
	-fov deg       : Field of view in degrees (0.1 to 89.9)
	-threshold p	 : #pixels p below which differences are ignored
	-gamma g       : Value to convert rgb into linear space (default 2.2)
	-luminance l   : White luminance (default 100.0 cdm^-2)
	-luminanceonly : Only consider luminance; ignore chroma (color) in the comparison
	-colorfactor   : How much of color to use, 0.0 to 1.0, 0.0 = ignore color.
	-downsample    : How many powers of two to down sample the image.
	-output o.ppm  : Write difference to the file o.ppm

 */


class ImageHelper {
    protected SettingsHelper settingsHelper = SettingsHelper.getInstance()
    protected settings = settingsHelper.settings

    private static counter = 1
    public static String THRESHOLD = "-threshold "
    public static String COLOR_FACTOR = "-colorfactor "
    private static String outputDirectory
    private static String imagesDirectory
    private URL url
    String pkDiffExecutable
    private static String os

    public ImageHelper(ITestContext testContext) {
        String outputDir = testContext.getOutputDirectory()
        outputDirectory = outputDir.substring(0, outputDir.lastIndexOf(File.separator))
        imagesDirectory = "$outputDirectory$IMAGE_DIRECTORY"
        createDir(imagesDirectory)

        switch (OS) {
            case ~/^win.*/:
                os = WIN
                break
            case ~/^mac.*/:
                os = MAC
                break
        }
        url = this.class.getResource(settings.pkdiff[os]);

        if (url != null) {
            try {
                File file = new File(url.toURI());
                pkDiffExecutable = file.absolutePath
            } catch (URISyntaxException e) {
                throw new SkipException(e)
            }
        } else {
            throw new SkipException("Can't find url <settings.pkdiff[os]>")

        }
    }



    public File checkIfImageFilesDiffer(File file1, File file2) {
        return doesFilesDiffers(file1, file2)
    }

    public File checkIfImageFilesDifferByColorFactor(File file1, File file2) {
        return checkIfImageFilesByColorFactor(file1, file2, 0)
    }

    public File checkIfImageFilesByColorFactor(File file1, File file2, int colorFactor) {
        return doesFilesDiffers(file1, file2, COLOR_FACTOR + colorFactor)
    }

    public File checkIfImageFilesByThreshold(File file1, File file2) {
        return checkIfImageFilesByThreshold(file1, file2, 0)
    }


    public File checkIfImageFilesByThreshold(File file1, File file2, int threshold) {
        return doesFilesDiffers(file1, file2, THRESHOLD + threshold)
    }

    private boolean doesFilesDiffer(String fileName1, String fileName2, String option = "") {
        File file1 = new File(fileName1)
        File file2 = new File(fileName2)
        Reporter.log(getHtmlImgTag(file1.getAbsolutePath(), file1.getName()))
        Reporter.log(getHtmlImgTag(file2.getAbsolutePath(), file2.getName()))
        return doesFilesDiffer(file1, file2)
    }

    private boolean doesFilesDiffer(File file1, File file2, String option = "") {
    }

    private File doesFilesDiffers(File file1, File file2, String option = "") {
        boolean filesDiff = false
        try {
            if(!file1.canRead()){
                println ("Can't read file $file1")
                throw new SkipException("Can't read file $file1")
            }
            if(!file2.canRead()){
                println ("Can't read file $file2")
                throw new SkipException("Can't read file $file2")
            }
            //Reporter.log("pkdiff $option $file1 $file2")
            String diffFileName = getFileName("pkDiffFile_", ".jpg")
            String comparisonFileName = "$imagesDirectory/$diffFileName"


            String command = "$pkDiffExecutable $option -output  $comparisonFileName  $file1 $file2"
            Reporter.log("command $command")
            Process process = Runtime.getRuntime().exec(command)
            sleep(1)
            process.waitFor();

            BufferedReader stdInput = new BufferedReader(new
                    InputStreamReader(process.getInputStream()));

            BufferedReader stdError = new BufferedReader(new
                    InputStreamReader(process.getErrorStream()));

            // read the output from the command
//            Reporter.log("Here is the standard output of the command:");
            String fileInput
            if ((fileInput = stdInput.readLine()) != null) {
                Reporter.log("The files differ: $fileInput");
                filesDiff = true
                while ((fileInput = stdInput.readLine()) != null) {
                    Reporter.log(fileInput);
                }
            } else {
                Reporter.log("Files don't differ!")
            }
            if ((fileInput = stdError.readLine()) != null) {
                Reporter.log("Error: $fileInput");
                while ((fileInput = stdError.readLine()) != null) {
                    Reporter.log(fileInput);
                }
                return true
            }
            if (filesDiff) {
                File comparisonFile = new File(comparisonFileName)
                if (comparisonFile.canRead()){
                    Reporter.log(getHtmlImgTag(comparisonFile))
                    return comparisonFile
                }
            }
        }
        catch (IOException e) {
            Reporter.log("exception happened - here's what I know: " + e);
        }
    }

    private String getFileName(String prefix, String fileExtension) {
        DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd-HHmmss-SSS")
        return prefix + dateFormat.format(new Date()) + "_" + counter++ + fileExtension
    }

    public String getHtmlImgTag(File file) {
        if(file != null){
            return getHtmlImgTag(file.getAbsolutePath(), file.getName())
        }else{
            return "File <$file> is null"
        }

    }

    public String addImageToReport(final File file) {
        Reporter.log(getHtmlImgTag(file))
    }

    public String getHtmlImgTag(final String filePath, String fileName) {
        def str = '<br/>screenshot: ' +
                "<a href=\"" + filePath + "\" target=\"_blank\">" +
                fileName + "<br>" +
                "<img src=\"" + filePath + "\" border=\"2\" width=\"88\" height=\"80\" hspace=\"10\" /></a><br/><br/>"
        return str
    }

    private void createDir(String dir) {
        File destinationDir = new File(dir)
        if (!destinationDir.isDirectory()) {
            destinationDir.mkdir()
            Thread.sleep(1)
        }
    }


}
