package org.netbeans.gradle.model.java

import org.gradle.api.*;
import org.netbeans.gradle.model.ProjectInfoBuilder;

public enum JarOutputsModelBuilder
implements
        ProjectInfoBuilder<JarOutputsModel> {

    INSTANCE;

    private static Class<?> getApiClass(Project project, String className) {
        return project.getClass().classLoader.loadClass(className);
    }

    public JarOutputsModel getProjectInfo(Project project) {
        if (!project.plugins.hasPlugin('java')) {
            return null;
        }

        Class jarClass = project.jar.getClass();

        List result = new LinkedList();
        project.tasks.withType(jarClass, { jar ->
            result.add(new JarOutput(jar.name, jar.archivePath));
        });

        return new JarOutputsModel(result);
    }
}
