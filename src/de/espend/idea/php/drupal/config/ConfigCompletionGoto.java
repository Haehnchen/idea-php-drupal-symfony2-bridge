package de.espend.idea.php.drupal.config;

import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.Processor;
import com.intellij.util.indexing.FileBasedIndex;
import com.intellij.util.indexing.FileBasedIndexImpl;
import com.jetbrains.php.lang.PhpLanguage;
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression;
import de.espend.idea.php.drupal.DrupalProjectComponent;
import de.espend.idea.php.drupal.index.CollectProjectUniqueKeys;
import de.espend.idea.php.drupal.index.ConfigSchemaIndex;
import fr.adrienbrault.idea.symfony2plugin.Symfony2Icons;
import fr.adrienbrault.idea.symfony2plugin.codeInsight.GotoCompletionContributor;
import fr.adrienbrault.idea.symfony2plugin.codeInsight.GotoCompletionProvider;
import fr.adrienbrault.idea.symfony2plugin.codeInsight.GotoCompletionRegistrar;
import fr.adrienbrault.idea.symfony2plugin.codeInsight.GotoCompletionRegistrarParameter;
import fr.adrienbrault.idea.symfony2plugin.util.MethodMatcher;
import fr.adrienbrault.idea.symfony2plugin.util.PsiElementUtils;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.yaml.YAMLFileType;
import org.jetbrains.yaml.psi.YAMLDocument;
import org.jetbrains.yaml.psi.YAMLKeyValue;

import java.util.*;

/**
 * @author Daniel Espendiller <daniel@espendiller.net>
 */
public class ConfigCompletionGoto implements GotoCompletionRegistrar {

    private static MethodMatcher.CallToSignature[] CONFIG = new MethodMatcher.CallToSignature[] {
        new MethodMatcher.CallToSignature("\\Drupal\\Core\\Config\\ConfigFactory", "get"),
    };

    @Override
    public void register(GotoCompletionRegistrarParameter registrar) {
        registrar.register(PlatformPatterns.psiElement().withParent(StringLiteralExpression.class).withLanguage(PhpLanguage.INSTANCE), new GotoCompletionContributor() {
            @Nullable
            public GotoCompletionProvider getProvider(@NotNull PsiElement psiElement) {

                if(!DrupalProjectComponent.isEnabled(psiElement)) {
                    return null;
                }

                PsiElement parent = psiElement.getParent();
                if(parent == null) {
                    return null;
                }

                MethodMatcher.MethodMatchParameter methodMatchParameter = MethodMatcher.getMatchedSignatureWithDepth(parent, CONFIG);
                if(methodMatchParameter == null) {
                    return null;
                }

                return new FormReferenceCompletionProvider(parent);

            }
        });
    }

    private static class FormReferenceCompletionProvider extends GotoCompletionProvider {

        public FormReferenceCompletionProvider(PsiElement element) {
            super(element);
        }

        @NotNull
        @Override
        public Collection<LookupElement> getLookupElements() {

            CollectProjectUniqueKeys ymlProjectProcessor = new CollectProjectUniqueKeys(getProject(), ConfigSchemaIndex.KEY);
            FileBasedIndex.getInstance().processAllKeys(ConfigSchemaIndex.KEY, ymlProjectProcessor, getProject());

            Collection<LookupElement> lookupElements = new ArrayList<LookupElement>();
            for(String config: ymlProjectProcessor.getResult()) {
                lookupElements.add(LookupElementBuilder.create(config).withIcon(Symfony2Icons.CONFIG_VALUE));
            }

            return lookupElements;
        }

        @NotNull
        @Override
        public Collection<PsiElement> getPsiTargets(PsiElement psiElement) {

            PsiElement element = psiElement.getParent();
            if(!(element instanceof StringLiteralExpression)) {
                return Collections.emptyList();
            }

            final String contents = ((StringLiteralExpression) element).getContents();
            if(StringUtils.isBlank(contents)) {
                return Collections.emptyList();
            }

            final Collection<PsiElement> psiElements = new ArrayList<PsiElement>();
            FileBasedIndexImpl.getInstance().getFilesWithKey(ConfigSchemaIndex.KEY, new HashSet<String>(Arrays.asList(contents)), new Processor<VirtualFile>() {
                @Override
                public boolean process(VirtualFile virtualFile) {

                    PsiFile psiFile = PsiManager.getInstance(getProject()).findFile(virtualFile);
                    if(psiFile == null) {
                        return true;
                    }

                    YAMLDocument yamlDocument = PsiTreeUtil.getChildOfType(psiFile, YAMLDocument.class);
                    if(yamlDocument == null) {
                        return true;
                    }

                    for(YAMLKeyValue yamlKeyValue: PsiTreeUtil.getChildrenOfType(yamlDocument, YAMLKeyValue.class)) {
                        String keyText = PsiElementUtils.trimQuote(yamlKeyValue.getKeyText());
                        if(StringUtils.isNotBlank(keyText) && keyText.equals(contents)) {
                            psiElements.add(yamlKeyValue);
                        }
                    }

                    return true;
                }
            }, GlobalSearchScope.getScopeRestrictedByFileTypes(GlobalSearchScope.allScope(getProject()), YAMLFileType.YML));

            return psiElements;
        }
    }

}
