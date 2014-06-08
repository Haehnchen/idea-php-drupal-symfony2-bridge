package de.espend.idea.php.drupal.completion;


import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.project.Project;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.PsiElement;
import com.intellij.util.ProcessingContext;
import com.intellij.util.indexing.FileBasedIndex;
import com.jetbrains.php.lang.psi.elements.FunctionReference;
import com.jetbrains.php.lang.psi.elements.ParameterList;
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression;
import de.espend.idea.php.drupal.DrupalIcons;
import de.espend.idea.php.drupal.index.CollectProjectUniqueKeys;
import de.espend.idea.php.drupal.index.GetTextFileIndex;
import de.espend.idea.php.drupal.utils.TranslationUtil;
import fr.adrienbrault.idea.symfony2plugin.Symfony2ProjectComponent;
import fr.adrienbrault.idea.symfony2plugin.TwigHelper;
import org.jetbrains.annotations.NotNull;

public class TwigCompletionContributor extends CompletionContributor {

    public TwigCompletionContributor() {

        // ''|t;
        extend(CompletionType.BASIC, TwigHelper.getTranslationPattern("t"), new CompletionProvider<CompletionParameters>() {

            @Override
            protected void addCompletions(@NotNull CompletionParameters completionParameters, ProcessingContext processingContext, @NotNull CompletionResultSet completionResultSet) {

                PsiElement psiElement = completionParameters.getOriginalPosition();
                if (psiElement == null || !Symfony2ProjectComponent.isEnabled(psiElement)) {
                    return;
                }

                TranslationUtil.attachGetTextLookupKeys(completionResultSet, psiElement.getProject());
            }

        });

    }



}
