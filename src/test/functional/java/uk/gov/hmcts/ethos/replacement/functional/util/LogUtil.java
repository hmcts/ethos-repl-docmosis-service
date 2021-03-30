package uk.gov.hmcts.ethos.replacement.functional.util;

import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LogUtil {

    private LogUtil() {
    }

    public static String getDocMosisPayload() throws IOException {
        Pattern pattern = Pattern.compile(".*TornadoService Sending request: (.*)");

        List<String> logs = getLogs("ethos-repl-docmosis-service");

        if (logs.size() == 0) {
            return null;
        }

        for (int i = logs.size() - 1; i > 0; i--) {
            String log = logs.get(i);
            Matcher matcher = pattern.matcher(log);
            if (matcher.matches()) {
                return matcher.group(1);
            }
        }

        return null;
    }

    public static List<String> getLogs(String containerName) throws IOException {
        // run the Unix "ps -ef" command
        // using the Runtime exec method:
        Process p = Runtime.getRuntime().exec("docker logs --since=60s " + containerName);

        BufferedReader stdInput = new BufferedReader(new
                InputStreamReader(p.getInputStream()));

        // read the output from the command
        String s;
        StringBuilder stringBuilder = new StringBuilder();
        while ((s = stdInput.readLine()) != null) {

            if (StringUtils.isEmpty(s)) {
                continue;
            }

            stringBuilder.append(s);
        }

        stdInput.close();

        return Arrays.asList(stringBuilder.toString().split("[\\d]{4}-[\\d]{2}-[\\d]{2}T"));
    }
}
