package uk.gov.hmcts.ethos.replacement.docmosis.test.util;

import org.apache.commons.lang3.RandomStringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class FileUtil {

    public static String downloadFileFromUrl(String strUrl, String authToken) throws IOException {
        String destinationFile = Constants.DOWNLOAD_FOLDER + "document-" + getFileSuffix() + ".docx";

        strUrl = strUrl.replace("127.0.0.1", "localhost");

        URL url = new URL(strUrl);
        URLConnection uc = url.openConnection();

        uc.setRequestProperty ("Authorization", authToken);
        InputStream in = uc.getInputStream();
        Files.copy(in, Paths.get(destinationFile), StandardCopyOption.REPLACE_EXISTING);

        return destinationFile;
    }

    private static String getFileSuffix() {
        return RandomStringUtils.randomAlphanumeric(5);
    }
}
