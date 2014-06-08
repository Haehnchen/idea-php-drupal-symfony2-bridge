package de.espend.idea.php.drupal.utils;

import com.intellij.patterns.PlatformPatterns;
import com.intellij.patterns.PsiElementPattern;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiWhiteSpace;
import com.jetbrains.php.lang.parser.PhpElementTypes;
import com.jetbrains.php.lang.psi.elements.ArrayHashElement;
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression;
import fr.adrienbrault.idea.symfony2plugin.util.PhpElementsUtil;

public class DrupalPattern {

    /**
     * Migrate from isAfterArrayKey and make working!
     */
    public static PsiElementPattern.Capture<PsiElement> getArrayRouteName() {

        return PlatformPatterns.psiElement().withParent(PlatformPatterns.psiElement(StringLiteralExpression.class).withParent(
            PlatformPatterns.psiElement(PhpElementTypes.ARRAY_VALUE).afterLeafSkipping(
                PlatformPatterns.or(
                    PlatformPatterns.psiElement(PhpElementTypes.HASH_ARRAY_ELEMENT),
                    PlatformPatterns.psiElement(PhpElementTypes.WHITE_SPACE),
                    PlatformPatterns.psiElement(PsiWhiteSpace.class)
                ),
                PlatformPatterns.psiElement(PhpElementTypes.ARRAY_KEY)
            )
        ));

    }

    public static boolean isAfterArrayKey(PsiElement psiElement, String arrayKeyName) {

        PsiElement literal = psiElement.getContext();
        if(!(literal instanceof StringLiteralExpression)) {
            return false;
        }

        PsiElement arrayValue = literal.getParent();
        if(arrayValue.getNode().getElementType() != PhpElementTypes.ARRAY_VALUE) {
            return false;
        }

        PsiElement arrayHashElement = arrayValue.getParent();
        if(!(arrayHashElement instanceof ArrayHashElement)) {
            return false;
        }

        PsiElement arrayKey = ((ArrayHashElement) arrayHashElement).getKey();
        String keyString = PhpElementsUtil.getStringValue(arrayKey);
        if(!arrayKeyName.equals(keyString)) {
            return false;
        }

        return true;
    }


    /*

.withParent(
            PlatformPatterns.psiElement(PhpElementTypes.ARRAY_VALUE).afterLeafSkipping(
                PlatformPatterns.or(
                    PlatformPatterns.psiElement(PhpElementTypes.HASH_ARRAY_ELEMENT),
                    PlatformPatterns.psiElement(PsiWhiteSpace.class)
                ),
                PlatformPatterns.psiElement(PhpElementTypes.ARRAY_KEY)
            )
        )
     */

}
