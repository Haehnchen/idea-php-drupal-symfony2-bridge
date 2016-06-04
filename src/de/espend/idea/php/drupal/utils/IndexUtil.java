package de.espend.idea.php.drupal.utils;

import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.CachedValue;
import com.intellij.util.indexing.FileBasedIndex;
import com.intellij.util.indexing.FileBasedIndexImpl;
import com.intellij.util.indexing.ID;
import com.jetbrains.php.lang.psi.elements.PhpClass;
import de.espend.idea.php.drupal.DrupalIcons;
import de.espend.idea.php.drupal.index.ConfigEntityTypeAnnotationIndex;
import fr.adrienbrault.idea.symfony2plugin.stubs.ServiceIndexUtil;
import fr.adrienbrault.idea.symfony2plugin.stubs.SymfonyProcessors;
import fr.adrienbrault.idea.symfony2plugin.stubs.cache.FileIndexCaches;
import fr.adrienbrault.idea.symfony2plugin.stubs.indexes.ContainerParameterStubIndex;
import fr.adrienbrault.idea.symfony2plugin.stubs.indexes.ServicesTagStubIndex;
import fr.adrienbrault.idea.symfony2plugin.util.PhpElementsUtil;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Daniel Espendiller <daniel@espendiller.net>
 */
public class IndexUtil {

    @NotNull
    public static Collection<PhpClass> getFormClassForId(@NotNull Project project, @NotNull String id)  {

        SymfonyProcessors.CollectProjectUniqueKeys keys = new SymfonyProcessors.CollectProjectUniqueKeys(project, ConfigEntityTypeAnnotationIndex.KEY);
        FileBasedIndexImpl.getInstance().processAllKeys(ConfigEntityTypeAnnotationIndex.KEY, keys, project);

        Collection<PhpClass> phpClasses = new ArrayList<>();

        for (String key : keys.getResult()) {
            if(!id.equals(key)) {
                continue;
            }

            for (String value : FileBasedIndex.getInstance().getValues(ConfigEntityTypeAnnotationIndex.KEY, key, GlobalSearchScope.allScope(project))) {
                phpClasses.addAll(PhpElementsUtil.getClassesInterface(project, value));
            }
        }

        return phpClasses;
    }

    @NotNull
    public static Collection<LookupElement> getIndexedKeyLookup(@NotNull Project project, @NotNull ID<String, ?> var1) {
        Collection<LookupElement> lookupElements = new ArrayList<>();

        SymfonyProcessors.CollectProjectUniqueKeys projectUniqueKeys = new SymfonyProcessors.CollectProjectUniqueKeys(project, var1);
        FileBasedIndex.getInstance().processAllKeys(var1, projectUniqueKeys, project);

        lookupElements.addAll(projectUniqueKeys.getResult().stream().map(
            s -> LookupElementBuilder.create(s).withIcon(DrupalIcons.DRUPAL)).collect(Collectors.toList())
        );

        return lookupElements;
    }
}
