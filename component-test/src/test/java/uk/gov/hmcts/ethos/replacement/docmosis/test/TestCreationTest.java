package uk.gov.hmcts.ethos.replacement.docmosis.test;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import uk.gov.hmcts.ethos.replacement.docmosis.test.util.Constants;
import uk.gov.hmcts.ethos.replacement.docmosis.test.util.Docx4jUtil;

import java.io.File;
import java.util.List;

@Category(ComponentTest.class)
@Ignore
public class TestCreationTest {


    @Before
    public void setUp() {
    }

    @Test
    public void generateTestScripts() throws Exception {

        List<String> tags;
        boolean isScotland = true;
        String topLevel = "16";

        if (isScotland) {
            tags = Docx4jUtil.getTagsFromDocument(new File(Constants.TEMPLATE_PATH_SCOT.replace("#VERSION#", topLevel)), isScotland);
        } else {
            tags = Docx4jUtil.getTagsFromDocument(new File(Constants.TEMPLATE_PATH_ENG.replace("#VERSION#", topLevel)), isScotland);
        }

        String testScript = "";
        for (String tag : tags) {
            String[] elements = tag.replace("Scot_", "").split("_");

            if (elements.length >= 1) {

                testScript += "   @Test\n";

                if (isScotland) {
                    testScript += "    public void generateDocument_Part_" + tag + "() throws Exception {\n";
                    if (elements.length == 1) {
                        testScript += "        testUtil.executeGenerateDocumentTest(\"" + elements[0] + "\", \"\", true);\n";
                    } else if (elements.length == 2) {
                        testScript += "        testUtil.executeGenerateDocumentTest(\"" + elements[0] + "\", \"" + elements[1] + "\", true);\n";
                    }
                } else {
                    testScript += "    public void generateDocument_Part" + tag + "() throws Exception {\n";
                    testScript += "        testUtil.executeGenerateDocumentTest(\"" + elements[0] + "\", \"" + elements[1] + "\");\n";
                }

                testScript += "    }\n\n";
            }
        }

        System.out.println("//-----------------------------------------------------------------------");
        System.out.println(testScript);
        System.out.println("//-----------------------------------------------------------------------");
    }

    @After
    public void tearDown() {

    }
}
