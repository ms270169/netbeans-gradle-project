package org.netbeans.gradle.project.properties.ui;

import java.util.concurrent.TimeUnit;
import org.junit.Test;
import org.netbeans.gradle.project.properties.global.CommonGlobalSettings;
import org.netbeans.gradle.project.properties.global.GlobalSettingsPage;
import org.netbeans.gradle.project.util.NbConsumer;
import org.netbeans.gradle.project.util.NbSupplier;


public class GradleDaemonPanelTest {
    private static NbSupplier<GlobalSettingsPage> settingsPageFactory() {
        return new NbSupplier<GlobalSettingsPage>() {
            @Override
            public GlobalSettingsPage get() {
                return GradleDaemonPanel.createSettingsPage();
            }
        };
    }

    @Test
    public void testInitAndReadBack() throws Exception {
        GlobalSettingsPanelTestUtils.testGlobalInitAndReadBack(settingsPageFactory(), new NbConsumer<CommonGlobalSettings>() {
            @Override
            public void accept(CommonGlobalSettings input) {
                input.gradleDaemonTimeoutSec().setValue((int)TimeUnit.MINUTES.toSeconds(37));
            }
        });
    }
}
