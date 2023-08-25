package de.espend.idea.php.drupal.tests.index;

import de.espend.idea.php.drupal.index.PermissionIndex;
import de.espend.idea.php.drupal.tests.DrupalLightCodeInsightFixtureTestCase;

import java.io.File;

/**
 * @author Daniel Espendiller <daniel@espendiller.net>
 * @see de.espend.idea.php.drupal.index.PermissionIndex
 */
public class PermissionIndexTest extends DrupalLightCodeInsightFixtureTestCase {

    public void setUp() throws Exception {
        super.setUp();
        myFixture.copyFileToProject("config.permissions.yml");
    }

    public String getTestDataPath() {
        return new File(this.getClass().getResource("fixtures").getFile()).getAbsolutePath();
    }

    public void testThatMappingValueIsInIndex() {
        assertIndexContains(PermissionIndex.KEY, "import configuration");
    }
}
