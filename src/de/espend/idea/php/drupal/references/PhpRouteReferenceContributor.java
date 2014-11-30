package de.espend.idea.php.drupal.references;

import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.*;
import com.intellij.util.ProcessingContext;
import com.jetbrains.php.lang.PhpLanguage;
import com.jetbrains.php.lang.psi.elements.*;
import de.espend.idea.php.drupal.DrupalProjectComponent;
import fr.adrienbrault.idea.symfony2plugin.routing.RouteReference;
import fr.adrienbrault.idea.symfony2plugin.util.MethodMatcher;
import org.jetbrains.annotations.NotNull;

/**
 * @author Daniel Espendiller <daniel@espendiller.net>
 */
public class PhpRouteReferenceContributor extends PsiReferenceContributor {

    public static MethodMatcher.CallToSignature[] GENERATOR_SIGNATURES = new MethodMatcher.CallToSignature[] {
        new MethodMatcher.CallToSignature("\\Drupal\\Core\\Routing\\UrlGeneratorInterface", "getPathFromRoute"), // <- <@TODO: remove: pre Drupal8 beta
        new MethodMatcher.CallToSignature("\\Drupal\\Core\\Routing\\UrlGeneratorInterface", "generateFromRoute"), // <- <@TODO: remove: pre Drupal8 beta
        new MethodMatcher.CallToSignature("\\Drupal\\Core\\Routing\\UrlGenerator", "getPathFromRoute"),
        new MethodMatcher.CallToSignature("\\Drupal\\Core\\Routing\\UrlGenerator", "generateFromRoute"),
        new MethodMatcher.CallToSignature("\\Drupal\\Core\\Url", "fromRoute"),
    };

    @Override
    public void registerReferenceProviders(PsiReferenceRegistrar psiReferenceRegistrar) {

        psiReferenceRegistrar.registerReferenceProvider(
            PlatformPatterns.psiElement(StringLiteralExpression.class).withLanguage(PhpLanguage.INSTANCE),
            new PsiReferenceProvider() {
                @NotNull
                @Override
                public PsiReference[] getReferencesByElement(@NotNull PsiElement psiElement, @NotNull ProcessingContext processingContext) {

                    if(!DrupalProjectComponent.isEnabled(psiElement)) {
                        return new PsiReference[0];
                    }

                    if (MethodMatcher.getMatchedSignatureWithDepth(psiElement, GENERATOR_SIGNATURES) == null) {
                        return new PsiReference[0];
                    }

                    return new PsiReference[]{ new RouteReference((StringLiteralExpression) psiElement) };
                }
            }
        );

        psiReferenceRegistrar.registerReferenceProvider(
            PlatformPatterns.psiElement(StringLiteralExpression.class).withLanguage(PhpLanguage.INSTANCE),
            new PsiReferenceProvider() {
                @NotNull
                @Override
                public PsiReference[] getReferencesByElement(@NotNull PsiElement psiElement, @NotNull ProcessingContext processingContext) {

                    if(!DrupalProjectComponent.isEnabled(psiElement)) {
                        return new PsiReference[0];
                    }

                    if(psiElement instanceof StringLiteralExpression) {
                        PsiElement parameterList = psiElement.getParent();
                        if(parameterList instanceof ParameterList) {
                            PsiElement newExpression = parameterList.getParent();
                            if(newExpression instanceof NewExpression) {
                                ClassReference classReference = ((NewExpression) newExpression).getClassReference();
                                if(classReference != null && "Url".equals(classReference.getName())) {
                                    String fqn = classReference.getFQN();
                                    if(fqn != null && fqn.equals("\\Drupal\\Core\\Url")) {
                                        return new PsiReference[]{ new RouteReference((StringLiteralExpression) psiElement) };
                                    }
                                }
                            }
                        }
                    }

                    return new PsiReference[0];
                }
            }
        );

    }
}
