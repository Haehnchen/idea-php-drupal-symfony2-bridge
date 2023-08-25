package de.espend.idea.php.drupal.tests.index;

import de.espend.idea.php.drupal.index.MenuIndex;
import de.espend.idea.php.drupal.tests.DrupalLightCodeInsightFixtureTestCase;

import java.io.File;

/**
 * @author Daniel Espendiller <daniel@espendiller.net>
 * @see de.espend.idea.php.drupal.index.MenuIndex
 */
public class MenuIndexTest extends DrupalLightCodeInsightFixtureTestCase {

    public void setUp() throws Exception {
        super.setUp();
        myFixture.copyFileToProject("search.menu.yml");
    }

    public String getTestDataPath() {
        return new File(this.getClass().getResource("fixtures").getFile()).getAbsolutePath();
    }

    public void testThatMappingValueIsInIndex() {
        assertIndexContains(MenuIndex.KEY, "search.view");
        assertIndexContains(MenuIndex.KEY, "entity.search_page.collection");
    }
}
