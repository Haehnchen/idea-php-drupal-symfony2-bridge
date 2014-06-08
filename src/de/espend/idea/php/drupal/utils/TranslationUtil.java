package de.espend.idea.php.drupal.utils;

import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.project.Project;
import com.intellij.util.indexing.FileBasedIndex;
import de.espend.idea.php.drupal.DrupalIcons;
import de.espend.idea.php.drupal.index.CollectProjectUniqueKeys;
import de.espend.idea.php.drupal.index.GetTextFileIndex;

public class TranslationUtil {

    public static void attachGetTextLookupKeys(CompletionResultSet completionResultSet, Project project) {

        CollectProjectUniqueKeys ymlProjectProcessor = new CollectProjectUniqueKeys(project, GetTextFileIndex.KEY);
        FileBasedIndex.getInstance().processAllKeys(GetTextFileIndex.KEY, ymlProjectProcessor, project);

        for(String phpClassName: ymlProjectProcessor.getResult()) {
            completionResultSet.addElement(LookupElementBuilder.create(phpClassName).withIcon(DrupalIcons.DRUPAL));
        }

    }

}
