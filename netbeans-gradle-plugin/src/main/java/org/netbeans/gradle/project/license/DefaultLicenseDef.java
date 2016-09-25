package org.netbeans.gradle.project.license;

import java.nio.file.Path;
import java.security.SecureRandom;
import java.util.Random;
import org.jtrim.utils.ExceptionHelper;
import org.netbeans.gradle.project.util.NbFileUtils;

public final class DefaultLicenseDef implements LicenseDef {
    private static final Random RND = new SecureRandom();

    private final Path src;
    private final String name;
    private final String licenseId;
    private final String displayName;

    public DefaultLicenseDef(Path src, String name, String displayName) {
        ExceptionHelper.checkNotNullArgument(src, "src");
        ExceptionHelper.checkNotNullArgument(name, "name");
        ExceptionHelper.checkNotNullArgument(displayName, "displayName");

        this.src = src;
        this.name = name;
        this.displayName = displayName;

        String safeName = NbFileUtils.toSafeFileName(name);
        String randomStr = Long.toHexString(RND.nextLong()) + "-" + Long.toHexString(RND.nextLong());
        this.licenseId = "nb-gradle-" + safeName + "-" + randomStr;
    }

    public Path getSrc() {
        return src;
    }

    public String getName() {
        return name;
    }

    @Override
    public String getLicenseId() {
        return licenseId;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return "License " + displayName + " at " + src;
    }
}
