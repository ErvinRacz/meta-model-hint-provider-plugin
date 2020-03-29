package com.ervinracz;

import com.intellij.openapi.project.Project;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.search.GlobalSearchScope;

public class StaticMetamodelAnnotationClass {

    private static final String DEFAULT_METAMODEL_ANNOTATION = "javax.persistence.metamodel.StaticMetamodel";

    private static PsiClass metamodelAnnotationClass = null;

    protected StaticMetamodelAnnotationClass() {
    }

    public static PsiClass getInstance(Project project) {
        if (metamodelAnnotationClass == null) {
            metamodelAnnotationClass = JavaPsiFacade.getInstance(project)
                    .findClass(DEFAULT_METAMODEL_ANNOTATION, GlobalSearchScope.allScope(project));
        }
        return metamodelAnnotationClass;
    }
}
