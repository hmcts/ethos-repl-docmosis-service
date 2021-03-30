package uk.gov.hmcts.ethos.replacement.functional.util;

import org.apache.commons.lang3.StringUtils;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;
import org.docx4j.wml.Text;
import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;

public class Docx4jUtil {

    private Docx4jUtil() {
    }

    public static List<String> getAllTextElementsFromDocument(File document) throws JAXBException, Docx4JException {
        List<String> result = new LinkedList<>();

        WordprocessingMLPackage wordMLPackage = WordprocessingMLPackage.load(document);

        MainDocumentPart mainDocumentPart = wordMLPackage.getMainDocumentPart();

        String textNodesXPath = "//w:t";

        List<Object> textNodes = mainDocumentPart.getJAXBNodesViaXPath(textNodesXPath, true);

        for (Object obj : textNodes) {
            Text text = (Text) ((JAXBElement) obj).getValue();
            result.add(text.getValue().trim());
        }

        return result;
    }

    public static List<String> getSelectedTextElementsFromDocument(File document, String docVersion)
            throws JAXBException, Docx4JException {
        List<String> result = new LinkedList<>();

        WordprocessingMLPackage wordMLPackage = WordprocessingMLPackage.load(document);

        MainDocumentPart mainDocumentPart = wordMLPackage.getMainDocumentPart();

        String textNodesXPath = "//w:t";

        List<Object> textNodes = mainDocumentPart.getJAXBNodesViaXPath(textNodesXPath, true);

        //Flag to check if the given doc version found
        boolean isElemFound = false;

        for (Object obj : textNodes) {
            Text text = (Text) ((JAXBElement) obj).getValue();

            //Loop ends when last end tag is found
            if (isElemFound && text.getValue().contains("<<es")) {
                break;
            }

            //Loop ends when end tag is found
            if (isElemFound && text.getValue().startsWith("<<else")) {
                break;
            }

            //Condition for <<else>> tag
            if (StringUtils.isEmpty(docVersion) && text.getValue().contains("<<else>>")) {
                isElemFound = true;
            }

            //Flag set to true when given document version found
            if (!StringUtils.isEmpty(docVersion) && text.getValue().contains(docVersion)) {
                isElemFound = true;
            }

            //Elements added if document version found
            if (isElemFound) {
                result.add(text.getValue().trim());
            }
        }

        return result;
    }

    public static List<String> getTagsFromDocument(File document, boolean isScotland)
            throws JAXBException, Docx4JException {
        List<String> result = new LinkedList<>();

        WordprocessingMLPackage wordMLPackage = WordprocessingMLPackage.load(document);

        MainDocumentPart mainDocumentPart = wordMLPackage.getMainDocumentPart();

        String textNodesXPath = "//w:t";

        List<Object> textNodes = mainDocumentPart.getJAXBNodesViaXPath(textNodesXPath, true);

        Pattern patternEng = Pattern.compile(".*([0-9]_[0-9A-Z]+).*");
        Pattern patternScot1 = Pattern.compile(".*_(Scot_[0-9A-Z_]+).*");
        Pattern patternScot2 = Pattern.compile(".*([0-9]{3}+.*)>>.*");
        Pattern patternScot3 = Pattern.compile(".*([0-9]{3}_[0-9A-Z]+)>>.*");
        Matcher matcher;

        for (Object obj : textNodes) {
            Text text = (Text) ((JAXBElement) obj).getValue();
            String textValue = text.getValue().trim();

            if (isScotland) {
                matcher = patternScot1.matcher(textValue);
                if (!matcher.matches()) {
                    matcher = patternScot2.matcher(textValue);
                    if (!matcher.matches()) {
                        matcher = patternScot3.matcher(textValue);
                    }
                }
            } else {
                matcher = patternEng.matcher(textValue);
            }
            if (matcher.matches()) {
                String value = matcher.group(1);
                if (StringUtils.isEmpty(value)) {
                    continue;
                }

                if (isScotland && !value.contains("Scot")) {
                    value = "Scot_" + value;
                }
                result.add(value);

            }
        }

        return result;
    }
}
