package de.espend.idea.php.drupal.tests.registrar;

import de.espend.idea.php.drupal.tests.DrupalLightCodeInsightFixtureTestCase;
import org.jetbrains.yaml.YAMLFileType;

import java.io.File;

/**
 * @author Daniel Espendiller <daniel@espendiller.net>
 * @see de.espend.idea.php.drupal.registrar.ControllerCompletion
 */
public class ControllerCompletionTest extends DrupalLightCodeInsightFixtureTestCase {

    public void setUp() throws Exception {
        super.setUp();
        myFixture.copyFileToProject("classes.php");
    }

    public String getTestDataPath() {
        return new File(this.getClass().getResource("fixtures").getFile()).getAbsolutePath();
    }

    public void testThatEntityFormCompletesAndNavigates() {
        assertCompletionContains(YAMLFileType.YML, "" +
                "config.import_full:\n" +
                "  defaults:\n" +
                "    _controller: '<caret>'",
            "\\Drupal\\contact\\Controller\\ContactController::foo"
        );

        assertCompletionNotContains(YAMLFileType.YML, "" +
                "config.import_full:\n" +
                "  defaults:\n" +
                "    _controller: '<caret>'",
            "\\Drupal\\contact\\Controller\\ContactController::privateBar"
        );
    }

}
