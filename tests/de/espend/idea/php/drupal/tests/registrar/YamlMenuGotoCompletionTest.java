package de.espend.idea.php.drupal.tests.registrar;

import com.intellij.patterns.PlatformPatterns;
import de.espend.idea.php.drupal.tests.DrupalLightCodeInsightFixtureTestCase;

import java.io.File;

/**
 * @author Daniel Espendiller <daniel@espendiller.net>
 * @see de.espend.idea.php.drupal.registrar.YamlMenuGotoCompletion
 */
public class YamlMenuGotoCompletionTest extends DrupalLightCodeInsightFixtureTestCase {

    public void setUp() throws Exception {
        super.setUp();
        myFixture.copyFileToProject("search.menu.yml");
    }

    public String getTestDataPath() {
        return new File(this.getClass().getResource("fixtures").getFile()).getAbsolutePath();
    }

    public void testCompletesAndNavigates() {
        assertCompletionContains("foo.menu.yml", "" +
            "config.import_full:\n" +
            "  parent: '<caret>'" +
            "search.view"
        );

        assertCompletionContains("foo.menu.yml", "" +
            "config.import_full:\n" +
            "  parent: <caret>" +
            "search.view"
        );
    }
}
