package de.espend.idea.php.drupal.registrar;

import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.util.containers.ContainerUtil;
import com.intellij.util.indexing.FileBasedIndex;
import de.espend.idea.php.drupal.DrupalIcons;
import de.espend.idea.php.drupal.DrupalProjectComponent;
import de.espend.idea.php.drupal.index.PermissionIndex;
import fr.adrienbrault.idea.symfony2plugin.codeInsight.GotoCompletionProvider;
import fr.adrienbrault.idea.symfony2plugin.codeInsight.GotoCompletionRegistrar;
import fr.adrienbrault.idea.symfony2plugin.codeInsight.GotoCompletionRegistrarParameter;
import fr.adrienbrault.idea.symfony2plugin.config.yaml.YamlElementPatternHelper;
import fr.adrienbrault.idea.symfony2plugin.stubs.SymfonyProcessors;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.yaml.YAMLUtil;
import org.jetbrains.yaml.psi.YAMLFile;
import org.jetbrains.yaml.psi.YAMLScalar;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.stream.Collectors;

/**
 * @author Daniel Espendiller <daniel@espendiller.net>
 */
public class YamlPermissionGotoCompletion implements GotoCompletionRegistrar {
    @Override
    public void register(GotoCompletionRegistrarParameter registrar) {
        registrar.register(YamlElementPatternHelper.getSingleLineScalarKey("_permission"), psiElement -> {
            if(!DrupalProjectComponent.isEnabled(psiElement)) {
                return null;
            }

            return new MyGotoCompletionProvider(psiElement);
        });
    }

    private static class MyGotoCompletionProvider extends GotoCompletionProvider {
        public MyGotoCompletionProvider(PsiElement psiElement) {
            super(psiElement);
        }

        @NotNull
        @Override
        public Collection<LookupElement> getLookupElements() {

            Collection<LookupElement> lookupElements = new ArrayList<>();

            SymfonyProcessors.CollectProjectUniqueKeys projectUniqueKeys = new SymfonyProcessors.CollectProjectUniqueKeys(getProject(), PermissionIndex.KEY);
            FileBasedIndex.getInstance().processAllKeys(PermissionIndex.KEY, projectUniqueKeys, getProject());
            lookupElements.addAll(projectUniqueKeys.getResult().stream().map(
                s -> LookupElementBuilder.create(s).withIcon(DrupalIcons.DRUPAL)).collect(Collectors.toList())
            );

            return lookupElements;
        }

        @NotNull
        @Override
        public Collection<PsiElement> getPsiTargets(PsiElement psiElement) {
            PsiElement parent = psiElement.getParent();

            if(!(parent instanceof YAMLScalar)) {
                return Collections.emptyList();
            }

            String text = ((YAMLScalar) parent).getTextValue();
            if(StringUtils.isBlank(text)) {
                return Collections.emptyList();
            }

            Collection<PsiElement> targets = new ArrayList<>();

            Collection<VirtualFile> virtualFiles = new ArrayList<>();

            FileBasedIndex.getInstance().getFilesWithKey(PermissionIndex.KEY, new HashSet<>(Collections.singletonList(text)), virtualFile -> {
                virtualFiles.add(virtualFile);
                return true;
            }, GlobalSearchScope.allScope(getProject()));

            for (VirtualFile virtualFile : virtualFiles) {
                PsiFile file = PsiManager.getInstance(getProject()).findFile(virtualFile);
                if(!(file instanceof YAMLFile)) {
                    continue;
                }

                ContainerUtil.addIfNotNull(targets, YAMLUtil.getQualifiedKeyInFile((YAMLFile) file, text));                ;
            }

            return targets;
        }
    }
}
