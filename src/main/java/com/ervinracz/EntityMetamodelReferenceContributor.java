package com.ervinracz;

import com.intellij.codeInsight.completion.CompletionUtilCore;
import com.intellij.openapi.util.TextRange;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.*;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class EntityMetamodelReferenceContributor extends PsiReferenceContributor {

    private static final String EL_VARIABLE = "ELVariableImpl";

    private enum ELVarParts {
        METAMODEL_CLASS, METAMODEL_ATTRIBUTE
    }

    private static Map<ELVarParts, String> processElVariable(PsiElement elReferenceElement, String actual) {
        Map<ELVarParts, String> parts = new HashMap<>();
        PsiElement prevSibling = elReferenceElement.getPrevSibling();

        if (prevSibling != null && prevSibling.textMatches(".") && actual.matches("[A-Za-z0-9_]*")) {
            parts.put(ELVarParts.METAMODEL_ATTRIBUTE, actual);
            Optional.ofNullable(prevSibling.getPrevSibling()).ifPresent(elem ->
                    parts.put(ELVarParts.METAMODEL_CLASS, elem.getText()));
        } else if (actual.matches("[A-Za-z0-9_]+")) {
            parts.put(ELVarParts.METAMODEL_CLASS, elReferenceElement.getText());
        }
        return parts;
    }

    private static boolean isInFieldTypeAttribute(PsiElement elReferenceElement) {
        PsiElement attribute;
        PsiElement prevSibling = elReferenceElement.getPrevSibling();

        if (prevSibling != null && prevSibling.textMatches(".")) {
            attribute = elReferenceElement.getParent().getParent().getParent().getPrevSibling().getPrevSibling();
        } else {
            attribute = elReferenceElement.getParent().getParent().getPrevSibling().getPrevSibling();
        }
        return attribute != null
                && (attribute.textMatches("filterBy")
                || attribute.textMatches("field")
                || attribute.textMatches("sortBy"));
    }

    @Override
    public void registerReferenceProviders(@NotNull PsiReferenceRegistrar registrar) {
        // todo rcz: use pattern https://intellij-support.jetbrains.com/hc/en-us/community/posts/360000044290-My-PsiReferenceContributor-unintentional-prevent-renaming-of-Java-methods-
        registrar.registerReferenceProvider(PlatformPatterns.psiElement(PsiElement.class),
                new PsiReferenceProvider() {

                    @NotNull
                    @Override
                    public PsiReference[] getReferencesByElement(@NotNull PsiElement element, @NotNull ProcessingContext context) {
                        if (element.getClass().getSimpleName().equals(EL_VARIABLE) && isInFieldTypeAttribute(element)) {
                            String refExpr = element.getText();
                            if (refExpr != null) {
                                TextRange textRange;
                                if (refExpr.contains(CompletionUtilCore.DUMMY_IDENTIFIER_TRIMMED)) {
                                    textRange = new TextRange(0, refExpr.indexOf(CompletionUtilCore.DUMMY_IDENTIFIER_TRIMMED));
                                    refExpr = refExpr.substring(0, refExpr.indexOf(CompletionUtilCore.DUMMY_IDENTIFIER_TRIMMED));
                                } else {
                                    textRange = new TextRange(0, refExpr.length());
                                }

                                Map<ELVarParts, String> refExprProps = processElVariable(element, refExpr);
                                if (refExprProps.get(ELVarParts.METAMODEL_CLASS) != null
                                        && refExprProps.get(ELVarParts.METAMODEL_ATTRIBUTE) != null) {
                                    return new PsiReference[]{
                                            new MetamodelAttributeReference(
                                                    element,
                                                    textRange,
                                                    refExprProps.get(ELVarParts.METAMODEL_CLASS))
                                    };
                                } else if (refExprProps.get(ELVarParts.METAMODEL_CLASS) != null) {
                                    return new PsiReference[]{new MetamodelClassReference(element, textRange)};
                                }
                            }
                        }

                        return new PsiReference[0];
                    }
                });
    }

}
