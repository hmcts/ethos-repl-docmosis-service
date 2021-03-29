package uk.gov.hmcts.ethos.replacement.functional.util;

import org.apache.commons.lang3.RandomStringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class FileUtil {

    private FileUtil() {
    }

    public static String downloadFileFromUrl(String strUrl, String authToken) throws IOException {

        URL url = new URL(strUrl.replace("127.0.0.1", "localhost"));
        URLConnection uc = url.openConnection();

        uc.setRequestProperty("Authorization", authToken);
        uc.connect();
        InputStream in = uc.getInputStream();
        String destinationFile = Constants.DOWNLOAD_FOLDER + "/document-" + getFileSuffix() + ".docx";
        Files.copy(in, Paths.get(destinationFile), StandardCopyOption.REPLACE_EXISTING);

        return destinationFile;
    }

    public static String getFileSuffix() {
        return RandomStringUtils.randomAlphanumeric(5);
    }

}
