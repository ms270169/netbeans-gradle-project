package org.netbeans.gradle.project.properties;

import org.netbeans.gradle.project.NbStrings;

public enum GradleLocationDefault implements GradleLocation {
    INSTANCE;

    public static final String UNIQUE_TYPE_NAME = "DEFAULT";

    @Override
    public void applyLocation(Applier applier) {
        applier.applyDefault();
    }

    @Override
    public String asString() {
        return null;
    }

    @Override
    public String getUniqueTypeName() {
        return UNIQUE_TYPE_NAME;
    }

    @Override
    public String toLocalizedString() {
        return NbStrings.getGradleLocationDefault();
    }
}
