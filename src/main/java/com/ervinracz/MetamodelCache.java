package com.ervinracz;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MetamodelCache {
    private static MetamodelCache metamodelCache;
    private Map<Project, Map<String, String>> cache = new HashMap<>();

    private MetamodelCache() {
    }

    public static MetamodelCache getInstance() {
        if (metamodelCache == null) {
            metamodelCache = new MetamodelCache();
        }
        return metamodelCache;
    }

//    static Map<String, String> parse(List<PsiFile> files) {
//        Map<String, String> result = new HashMap<>();
//        for (PsiFile file : files) {
//            PropertiesFile propFile = (PropertiesFile) file;
//            propFile.getProperties().forEach(prop -> result.put(prop.getKey(), prop.getValue()));
//        }
//        return result;
//    }
//
//    public Map<String, String> collectProperties(String fileName, Project project, boolean useCache) {
//        Map<String, String> result;
//        if (useCache) {
//            result = cache.get(project);
//            if (result != null) {
//                return result;
//            }
//        }
//        GlobalSearchScope scope = GlobalSearchScope.allScope(project);
//        PsiFile[] files = FilenameIndex.getFilesByName(project, fileName, scope);
//        if (files.length != 0) {
//            result = parse(Arrays.asList(files));
//            cache.put(project, result);
//            return result;
//        }
//        return new HashMap<>();
//    }
}
