package uk.gov.hmcts.ethos.replacement.docmosis.test.util;

import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;
import org.docx4j.wml.Text;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import java.io.File;
import java.util.LinkedList;
import java.util.List;

public class Docx4jUtil {

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
}
