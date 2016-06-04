package de.espend.idea.php.drupal.tests.linemarker;

import de.espend.idea.php.drupal.tests.DrupalLightCodeInsightFixtureTestCase;
import org.jetbrains.yaml.YAMLFileType;

import java.io.File;

/**
 * @author Daniel Espendiller <daniel@espendiller.net>
 * @see de.espend.idea.php.drupal.linemarker.RouteFormLineMarkerProvider
 */
public class RouteFormLineMarkerProviderTest extends DrupalLightCodeInsightFixtureTestCase {

    public void setUp() throws Exception {
        super.setUp();
        myFixture.copyFileToProject("classes.php");
    }

    public String getTestDataPath() {
        return new File(this.getClass().getResource("fixtures").getFile()).getAbsolutePath();
    }

    public void testLinemarkerForForm() {
        assertLineMarker(myFixture.configureByText(YAMLFileType.YML, "" +
            "config.export_full:\n" +
            "  defaults:\n" +
            "    _form: '\\Foo\\FooBar'"
        ), new LineMarker.ToolTipEqualsAssert("Navigate to form"));
    }

}
