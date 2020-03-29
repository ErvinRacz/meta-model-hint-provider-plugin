package com.ervinracz;

import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.ide.highlighter.JavaFileType;
import com.intellij.model.SymbolResolveResult;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.psi.search.FileTypeIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.searches.AnnotatedElementsSearch;
import com.intellij.util.IncorrectOperationException;
import com.intellij.util.Processor;
import com.intellij.util.Query;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

public class MetamodelClassReference extends PsiReferenceBase<PsiElement> implements PsiPolyVariantReference {

    private Project project;
    private String searchKey;

    public MetamodelClassReference(@NotNull PsiElement element, TextRange rangeInElement) {
        super(element, rangeInElement);
        this.searchKey = element.getText().substring(rangeInElement.getStartOffset(), rangeInElement.getEndOffset());
        this.project = element.getProject();
    }

    public static List<PsiClass> findClasses(Project project, @NotNull String searchKey) {
        PsiClass nameAnnotation = StaticMetamodelAnnotationClass.getInstance(project);
        List<PsiClass> result = new ArrayList<>();
        AnnotatedElementsSearch.searchPsiClasses(nameAnnotation, GlobalSearchScope.projectScope(project)).forEach(psiClass -> {
            if (searchKey.equals(psiClass.getName())) {
                result.add(psiClass);
            }
            return true;
        });
        return result;
    }

    @NotNull
    @Override
    public ResolveResult[] multiResolve(boolean incompleteCode) {
        return findClasses(project, searchKey)
                .stream()
                .map(PsiElementResolveResult::new)
                .toArray(ResolveResult[]::new);
    }


    @Nullable
    @Override
    public PsiElement resolve() {
        ResolveResult[] resolveResults = multiResolve(false);
        return resolveResults.length == 1 ? resolveResults[0].getElement() : null;
    }

    @NotNull
    @Override
    public Object[] getVariants() {
        PsiClass nameAnnotation = StaticMetamodelAnnotationClass.getInstance(project);
        // todo rcz: parametrize the annotation name

        if (nameAnnotation != null) {
            return AnnotatedElementsSearch.searchPsiClasses(nameAnnotation, GlobalSearchScope.projectScope(project))
                    .findAll().stream().map(LookupElementBuilder::create).toArray(LookupElement[]::new);
        }

        return new LookupElement[]{};
    }

    @NotNull
    @Override
    public Collection<? extends SymbolResolveResult> resolveReference() {
        return null;
    }

    @Override
    public PsiElement handleElementRename(@NotNull String newElementName) throws IncorrectOperationException {
        return getElement();
    }
}
