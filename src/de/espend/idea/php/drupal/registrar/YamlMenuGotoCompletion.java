package de.espend.idea.php.drupal.registrar;

import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.PsiElement;
import de.espend.idea.php.drupal.DrupalProjectComponent;
import de.espend.idea.php.drupal.index.MenuIndex;
import de.espend.idea.php.drupal.registrar.utils.YamlRegistrarUtil;
import de.espend.idea.php.drupal.utils.IndexUtil;
import fr.adrienbrault.idea.symfony2plugin.codeInsight.GotoCompletionProvider;
import fr.adrienbrault.idea.symfony2plugin.codeInsight.GotoCompletionRegistrar;
import fr.adrienbrault.idea.symfony2plugin.codeInsight.GotoCompletionRegistrarParameter;
import fr.adrienbrault.idea.symfony2plugin.config.yaml.YamlElementPatternHelper;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

/**
 * @author Daniel Espendiller <daniel@espendiller.net>
 */
public class YamlMenuGotoCompletion implements GotoCompletionRegistrar {
    @Override
    public void register(GotoCompletionRegistrarParameter registrar) {
        registrar.register(PlatformPatterns.and(PlatformPatterns.psiFile().withName(PlatformPatterns.string().endsWith(".menu.yml")), YamlElementPatternHelper.getSingleLineScalarKey("parent")), psiElement -> {
            if(!DrupalProjectComponent.isEnabled(psiElement)) {
                return null;
            }

            return new ParentMenu(psiElement);
        });
    }

    private static class ParentMenu extends GotoCompletionProvider {
        ParentMenu(PsiElement psiElement) {
            super(psiElement);
        }

        @NotNull
        @Override
        public Collection<LookupElement> getLookupElements() {
            return IndexUtil.getIndexedKeyLookup(getProject(), MenuIndex.KEY);
        }

        @NotNull
        @Override
        public Collection<PsiElement> getPsiTargets(PsiElement psiElement) {
            String text = YamlRegistrarUtil.getYamlScalarKey(psiElement);
            if(text == null) {
                return Collections.emptyList();
            }

            return new ArrayList<>(IndexUtil.getMenuForId(getProject(), text));
        }
    }
}
