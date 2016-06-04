package de.espend.idea.php.drupal.tests.index;

import de.espend.idea.php.drupal.index.ConfigEntityTypeAnnotationIndex;
import de.espend.idea.php.drupal.tests.DrupalLightCodeInsightFixtureTestCase;

import java.io.File;

/**
 * @author Daniel Espendiller <daniel@espendiller.net>
 * @see de.espend.idea.php.drupal.index.ConfigEntityTypeAnnotationIndex
 */
public class ConfigEntityTypeAnnotationIndexTest extends DrupalLightCodeInsightFixtureTestCase {

    public void setUp() throws Exception {
        super.setUp();
        myFixture.copyFileToProject("classes.php");
    }

    public String getTestDataPath() {
        return new File(this.getClass().getResource("fixtures").getFile()).getAbsolutePath();
    }

    public void testThatMappingValueIsInIndex() {
        assertIndexContains(ConfigEntityTypeAnnotationIndex.KEY, "contact_form");

        assertIndexContainsKeyWithValue(ConfigEntityTypeAnnotationIndex.KEY, "contact_form", "Drupal\\Foo\\Entity\\ContactForm");
    }
}
