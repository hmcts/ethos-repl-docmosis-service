package uk.gov.hmcts.ethos.replacement.docmosis.test.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LogUtil {

    public static String getDocMosisPayload() throws IOException {
        Pattern pattern = Pattern.compile(".*TornadoService Sending request: (.*)");
        for (String log : getLogs("ethos-repl-docmosis-service")) {
            Matcher matcher = pattern.matcher(log);
            if (matcher.matches()) {
                return matcher.group(1);
            }
        }

        return null;
    }

    public static List<String> getLogs(String containerName) throws IOException {
        List<String> logs = new LinkedList<>();

        // run the Unix "ps -ef" command
        // using the Runtime exec method:
        Process p = Runtime.getRuntime().exec("docker logs --since=10s " + containerName);

        BufferedReader stdInput = new BufferedReader(new
                InputStreamReader(p.getInputStream()));

        // read the output from the command
        String s;
        while ((s = stdInput.readLine()) != null) {
            logs.add(s);
            //System.out.println(s);
        }

        return logs;
    }
}
